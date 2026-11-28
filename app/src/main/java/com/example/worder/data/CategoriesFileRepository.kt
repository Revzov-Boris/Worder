package com.example.worder.data

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import com.example.worder.service.CategoryApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import com.google.gson.GsonBuilder

/** Обычный .txt в Downloads + резервная копия в приватном filesDir. */
data class CategoriesFileInfo(val name: String, val location: String, val bytes: Long, val created: Long)
data class CategoriesFilesState(val publicFile: CategoriesFileInfo?, val backup: CategoriesFileInfo?)

class CategoriesFileRepository(context: Context) {
    private val appContext = context.applicationContext
    private val resolver = appContext.contentResolver
    private val metadata = appContext.getSharedPreferences("categories_file_metadata", Context.MODE_PRIVATE)
    private val backupFile = File(appContext.filesDir, "categories_backup.txt")
    private val downloadCollection: Uri
        get() = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)

    var fileName: String
        get() = metadata.getString("export_filename", "0_categories.txt") ?: "0_categories.txt"
        set(value) { metadata.edit().putString("export_filename", sanitizeName(value)).apply() }

    private fun sanitizeName(value: String): String {
        val clean = value.trim().replace(Regex("[^\\p{L}\\p{N}_ .-]"), "_").take(80)
        require(clean.isNotBlank() && clean != "." && clean != "..") { "Укажи имя файла" }
        return if (clean.endsWith(".txt", ignoreCase = true)) clean else "$clean.txt"
    }

    private data class StoredFile(val uri: Uri, val info: CategoriesFileInfo)

    private fun findPublic(name: String): StoredFile? {
        val cols = arrayOf(
            MediaStore.Downloads._ID,
            MediaStore.Downloads.DISPLAY_NAME,
            MediaStore.Downloads.SIZE,
            MediaStore.Downloads.DATE_ADDED,
            MediaStore.Downloads.RELATIVE_PATH
        )
        resolver.query(
            downloadCollection, cols,
            "${MediaStore.Downloads.DISPLAY_NAME}=? AND ${MediaStore.Downloads.RELATIVE_PATH}=?",
            arrayOf(name, "${Environment.DIRECTORY_DOWNLOADS}/"), null
        )?.use { c ->
            if (c.moveToFirst()) {
                val id = c.getLong(c.getColumnIndexOrThrow(MediaStore.Downloads._ID))
                val displayName = c.getString(c.getColumnIndexOrThrow(MediaStore.Downloads.DISPLAY_NAME))
                val size = c.getLong(c.getColumnIndexOrThrow(MediaStore.Downloads.SIZE))
                val created = c.getLong(c.getColumnIndexOrThrow(MediaStore.Downloads.DATE_ADDED)) * 1000
                return StoredFile(
                    ContentUris.withAppendedId(downloadCollection, id),
                    CategoriesFileInfo(displayName, "Downloads/$displayName", size, created)
                )
            }
        }
        return null
    }

    suspend fun getState(): CategoriesFilesState = withContext(Dispatchers.IO) {
        val backupName = metadata.getString("backup_filename", "0_categories.txt") ?: "0_categories.txt"
        CategoriesFilesState(
            publicFile = findPublic(fileName)?.info,
            backup = if (backupFile.isFile) CategoriesFileInfo(
                backupName, "Внутреннее хранилище приложения/files/categories_backup.txt",
                backupFile.length(), backupFile.lastModified()
            ) else null
        )
    }

    private fun createPublic(name: String, bytes: ByteArray) {
        check(findPublic(name) == null) { "Файл $name уже существует в Downloads" }
        val values = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, name)
            put(MediaStore.Downloads.MIME_TYPE, "text/plain")
            put(MediaStore.Downloads.RELATIVE_PATH, "${Environment.DIRECTORY_DOWNLOADS}/")
            put(MediaStore.Downloads.IS_PENDING, 1)
        }
        val uri = resolver.insert(downloadCollection, values) ?: throw IOException("Не удалось создать файл")
        try {
            resolver.openOutputStream(uri, "w")?.use { it.write(bytes) }
                ?: throw IOException("Не удалось открыть файл для записи")
            resolver.update(uri, ContentValues().apply { put(MediaStore.Downloads.IS_PENDING, 0) }, null, null)
        } catch (e: Exception) {
            resolver.delete(uri, null, null)
            throw e
        }
    }

    suspend fun exportCategories(language: String): CategoriesFilesState =
        withContext(Dispatchers.IO) {
            val name = fileName

            check(findPublic(name) == null) {
                "Файл уже существует. Удали его перед повторным экспортом."
            }

            val categories = mutableListOf<CategoryDto>()
            var page = 0
            var finished: Boolean

            do {
                val result = CategoryApiClient.categoryApi.getCategories(
                    language.lowercase(),
                    page,
                    10
                )

                categories.addAll(result.content)
                page++

                finished = result.last || page >= result.totalPages
            } while (!finished && page < 100)

            val gson = GsonBuilder()
                .setPrettyPrinting()
                .create()

            val json = gson.toJson(categories)

            createPublic(name, json.toByteArray(Charsets.UTF_8))

            getState()
        }

    suspend fun deleteWithBackup(): CategoriesFilesState = withContext(Dispatchers.IO) {
        val source = findPublic(fileName) ?: throw IOException("Файл в Downloads не найден")
        // Сначала записываем резервную копию; удаляем внешний файл только после успеха.
        val temp = File(appContext.filesDir, "categories_backup.tmp")
        try {
            resolver.openInputStream(source.uri)?.use { input ->
                temp.outputStream().use { output -> input.copyTo(output) }
            } ?: throw IOException("Не удалось прочитать исходный файл")
            check(temp.length() == source.info.bytes) { "Резервная копия записана не полностью" }
            // Для небольшого .txt используем копирование вместо ненадёжного переименования.
            temp.copyTo(backupFile, overwrite = true)
            check(backupFile.length() == temp.length()) { "Не удалось проверить резервную копию" }
            metadata.edit().putString("backup_filename", source.info.name).commit().also {
                check(it) { "Не удалось сохранить имя резервной копии" }
            }
            check(resolver.delete(source.uri, null, null) > 0) { "Не удалось удалить файл в Downloads" }
        } finally {
            temp.delete()
        }
        getState()
    }

    suspend fun restoreBackup(): CategoriesFilesState = withContext(Dispatchers.IO) {
        check(backupFile.isFile) { "Резервной копии нет" }
        val name = metadata.getString("backup_filename", "0_categories.txt") ?: "0_categories.txt"
        createPublic(name, backupFile.readBytes())
        fileName = name
        // удаляем резервную копию после успешного восстановления
        check(backupFile.delete()) {
            "Файл восстановлен, но не удалось удалить бэкап"
        }
        getState()
    }
}

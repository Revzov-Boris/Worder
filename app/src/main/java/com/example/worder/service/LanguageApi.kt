package com.example.worder.service

import android.content.Context
import com.example.worder.data.LanguageDto
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.net.Proxy

interface LanguageApi {
    @GET("api/languages")
    suspend fun getLanguages(): List<LanguageDto>
}

// Один клиент на всё приложение
object ApiClient {
    private const val URL = "http://10.0.2.2:8080/"

    private const val JSON = """
    [
    {"id":1, "title": "белорусский", "flagCode": "BY"},
    {"id":2, "title": "английский", "flagCode": "US"},
    {"id":3, "title": "испанский",  "flagCode": "ES"},
    {"id":4, "title": "немецкий",  "flagCode": "DE"},
    {"id":5, "title": "каталанский",  "flagCode": "AD"},
    {"id":6, "title": "португальский",  "flagCode": "PR"},
    {"id":7, "title": "итальянский",  "flagCode": "IT"},
    {"id":8, "title": "китайский",  "flagCode": "CN"}
    ]
    """

//    val languageApi: LanguageApi by lazy {
//        Retrofit.Builder()
//            .baseUrl(URL)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//            .create(LanguageApi::class.java)
//    }

    private val fakeApi: LanguageApi = object : LanguageApi {
        override suspend fun getLanguages(): List<LanguageDto> {
            val type = object : TypeToken<List<LanguageDto>>() {}.type
            return Gson().fromJson(JSON, type)
        }
    }

    fun loadFromAssets(context: Context): List<LanguageDto> {
        val json = context.assets.open("languages.json")
            .bufferedReader()
            .use { it.readText() }

        val type = object : TypeToken<List<LanguageDto>>() {}.type
        return Gson().fromJson(json, type)
    }

    val languageApi: LanguageApi get() = fakeApi
}
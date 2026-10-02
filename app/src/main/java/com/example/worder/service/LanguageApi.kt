package com.example.worder.service

import com.example.worder.data.LanguageDto
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
interface LanguageApi {
    @GET("api/languages")
    suspend fun getLanguages(): List<LanguageDto>
}

// Один клиент на всё приложение
object ApiClient {
    private const val URL = "http://localhost:8080/"

    private const val JSON = """
    [
      {
        "id": 2,
        "title": "немецкий",
        "flagCode": "DE"
      },
      {
        "id": 30,
        "title": "казахский",
        "flagCode": "KZ"
      },
      {
        "id": 31,
        "title": "китайский",
        "flagCode": "CN"
      },
      {
        "id": 32,
        "title": "хинди",
        "flagCode": "IN"
      },
      {
        "id": 33,
        "title": "персидский",
        "flagCode": "IR"
      },
      {
        "id": 34,
        "title": "иврит",
        "flagCode": "IL"
      },
      {
        "id": 1,
        "title": "английский",
        "flagCode": "GB-ENG"
      }
    ]
    """

    val reaLlanguageApi: LanguageApi by lazy {
        Retrofit.Builder()
            .baseUrl(URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(LanguageApi::class.java)
    }

    // Захардкоженный JSON для тестов
//    private val fakeApi: LanguageApi = object : LanguageApi {
//        override suspend fun getLanguages(): List<LanguageDto> {
//            val type = object : TypeToken<List<LanguageDto>>() {}.type
//            return Gson().fromJson(JSON, type)
//        }
//    }

    val languageApi: LanguageApi get() = reaLlanguageApi
}
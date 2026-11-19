package com.example.worder.service

import com.example.worder.data.CategoriesPageDto
import com.google.gson.Gson
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface CategoryApi {
    @GET("api/categories")
    suspend fun getCategories(
        @Query("languageTitle") languageTitle: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): CategoriesPageDto
}

// Отдельный клиент, чтобы не трогать ApiClient для языков
object CategoryApiClient {
    private const val URL = "http://localhost:8080/"

    // Захардкоженный JSON для тестов (3 категории на страницу)
    private const val JSON_PAGE_0 = """
    {
      "content": [
        {
          "id": 8,
          "name": "неправильные глаголы",
          "description": "глаголы с нестандартной 2-й или 3-й формой",
          "countOfWord": 41
        },
        {
          "id": 9,
          "name": "проблемные двойки",
          "description": "существительные с нестандартной формой множественного числа",
          "countOfWord": 7
        },
        {
          "id": 12,
          "name": "странные прилагательные",
          "description": "прилагательные с нестандартной превосходной или сравнительной степенью",
          "countOfWord": 4
        }
      ],
      "totalElements": 6,
      "totalPages": 2,
      "number": 0,
      "size": 3,
      "last": false
    }
    """

    private const val JSON_PAGE_1 = """
    {
      "content": [
        {
          "id": 15,
          "name": "фразовые глаголы",
          "description": "глагол + предлог/наречие с новым значением",
          "countOfWord": 25
        },
        {
          "id": 16,
          "name": "идиомы",
          "description": "устойчивые выражения",
          "countOfWord": 18
        },
        {
          "id": 17,
          "name": "предлоги места",
          "description": "in, on, at и другие",
          "countOfWord": 12
        }
      ],
      "totalElements": 6,
      "totalPages": 2,
      "number": 1,
      "size": 3,
      "last": true
    }
    """

    val realCategoryApi: CategoryApi by lazy {
        Retrofit.Builder()
            .baseUrl(URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CategoryApi::class.java)
    }

    private val fakeApi: CategoryApi = object : CategoryApi {
        override suspend fun getCategories(
            languageTitle: String,
            page: Int,
            size: Int
        ): CategoriesPageDto {
            val json = when (page) {
                0 -> JSON_PAGE_0
                else -> JSON_PAGE_1
            }
            return Gson().fromJson(json, CategoriesPageDto::class.java)
        }
    }

    val categoryApi: CategoryApi get() = realCategoryApi
}
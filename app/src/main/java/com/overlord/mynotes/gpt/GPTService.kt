package com.overlord.mynotes.gpt

import com.overlord.mynotes.model.GPTApiResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

class GPTService(private val accessToken: String) {

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://your-gpt-api-url.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val gptApi = retrofit.create(GPTApi::class.java)

    suspend fun generateResponse(input: String): String {
        // Отправка запроса к GPT с использованием токена доступа
        val response = gptApi.generateResponse("Bearer $accessToken", input)
        return response.body()?.response ?: "Default response"
    }
}

interface GPTApi {
    @POST("generate")
    suspend fun generateResponse(
        @Header("Authorization") authorization: String,
        @Body input: String
    ): Response<GPTApiResponse>
}

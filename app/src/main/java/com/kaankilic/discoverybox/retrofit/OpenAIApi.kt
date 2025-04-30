package com.kaankilic.discoverybox.retrofit

import com.kaankilic.discoverybox.entitiy.ImageRequest
import com.kaankilic.discoverybox.entitiy.ImageResponse
import com.kaankilic.discoverybox.entitiy.TTSRequest
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Streaming
import java.util.concurrent.TimeUnit

interface OpenAIApi {
    @POST("audio/speech")
    @Headers("Content-Type: application/json")
    @Streaming
    suspend fun generateSpeech(
        @Body request: TTSRequest,
        @Header("Authorization") auth: String
    ): Response<ResponseBody>

    @POST("images/generations")
    @Headers("Content-Type: application/json")
    suspend fun generateImage(
        @Body request: ImageRequest,
        @Header("Authorization") auth: String
    ): Response<ImageResponse>
}

val client = OkHttpClient.Builder()
    .connectTimeout(120, TimeUnit.SECONDS)
    .readTimeout(120, TimeUnit.SECONDS)
    .writeTimeout(120, TimeUnit.SECONDS)
    .build()

val retrofit = Retrofit.Builder()
    .baseUrl("https://api.openai.com/v1/")
    .client(client) // burası önemli
    .addConverterFactory(GsonConverterFactory.create())
    .build()

val api = retrofit.create(OpenAIApi::class.java)
// data/remote/groq/GroqApiService.kt
package com.mykaradainam.data.remote.groq

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface GroqApiService {
    @POST("chat/completions")
    suspend fun chatCompletion(@Body request: ChatRequest): ChatResponse

    @Multipart
    @POST("audio/transcriptions")
    suspend fun transcribeAudio(
        @Part file: MultipartBody.Part,
        @Part("model") model: RequestBody,
        @Part("language") language: RequestBody
    ): TranscriptionResponse
}

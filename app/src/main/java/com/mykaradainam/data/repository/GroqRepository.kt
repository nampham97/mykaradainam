// data/repository/GroqRepository.kt
package com.mykaradainam.data.repository

import android.graphics.Bitmap
import android.util.Base64
import com.google.gson.Gson
import com.mykaradainam.data.remote.groq.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GroqRepository @Inject constructor(
    private val api: GroqApiService,
    private val gson: Gson
) {
    suspend fun processInvoiceImage(bitmap: Bitmap): InvoiceParseResult {
        val base64 = bitmapToBase64(bitmap)
        val request = ChatRequest(
            model = "meta-llama/llama-4-scout-17b-16e-instruct",
            messages = listOf(
                ChatMessage(
                    role = "system",
                    content = "Bạn là trợ lý đọc hóa đơn karaoke. Trích xuất thông tin từ ảnh hóa đơn và trả về JSON với format: {\"items\": [{\"name\": string, \"quantity\": int, \"unitPrice\": int}], \"totalAmount\": int, \"confidence\": \"high\"|\"medium\"|\"low\", \"warnings\": [string]}. Chỉ trả về JSON, không giải thích."
                ),
                ChatMessage(
                    role = "user",
                    content = listOf(
                        ContentPart(type = "text", text = "Đọc hóa đơn này:"),
                        ContentPart(
                            type = "image_url",
                            imageUrl = ImageUrl("data:image/jpeg;base64,$base64")
                        )
                    )
                )
            ),
            temperature = 0.1
        )

        val response = api.chatCompletion(request)
        val json = response.choices.first().message.content
            .trim().removePrefix("```json").removeSuffix("```").trim()
        return gson.fromJson(json, InvoiceParseResult::class.java)
    }

    suspend fun processVoiceAudio(audioFile: File): InvoiceParseResult {
        // Step 1: Whisper transcription
        val filePart = MultipartBody.Part.createFormData(
            "file", audioFile.name,
            audioFile.asRequestBody("audio/m4a".toMediaTypeOrNull())
        )
        val modelPart = "whisper-large-v3".toRequestBody("text/plain".toMediaTypeOrNull())
        val langPart = "vi".toRequestBody("text/plain".toMediaTypeOrNull())

        val transcription = api.transcribeAudio(filePart, modelPart, langPart)

        // Step 2: Orchestrator structures the text
        val request = ChatRequest(
            model = "openai/gpt-oss-120b",
            messages = listOf(
                ChatMessage(
                    role = "system",
                    content = "Bạn là trợ lý xử lý hóa đơn karaoke. Nhận mô tả bằng giọng nói (tiếng Việt) về hóa đơn và trả về JSON: {\"items\": [{\"name\": string, \"quantity\": int, \"unitPrice\": int}], \"totalAmount\": int, \"confidence\": \"high\"|\"medium\"|\"low\", \"warnings\": [string]}. Nếu không rõ giá, đặt unitPrice=0 và thêm warning. Chỉ trả về JSON."
                ),
                ChatMessage(role = "user", content = transcription.text)
            ),
            temperature = 0.1
        )

        val response = api.chatCompletion(request)
        val json = response.choices.first().message.content
            .trim().removePrefix("```json").removeSuffix("```").trim()
        return gson.fromJson(json, InvoiceParseResult::class.java)
    }

    suspend fun getAiAdvisorResponse(systemPrompt: String, userPrompt: String): String {
        val request = ChatRequest(
            model = "openai/gpt-oss-120b",
            messages = listOf(
                ChatMessage(role = "system", content = systemPrompt),
                ChatMessage(role = "user", content = userPrompt)
            ),
            temperature = 0.3
        )
        val response = api.chatCompletion(request)
        return response.choices.first().message.content
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        // Resize to max 1280px on longest edge
        val maxDim = 1280
        val scale = if (bitmap.width > bitmap.height) {
            maxDim.toFloat() / bitmap.width
        } else {
            maxDim.toFloat() / bitmap.height
        }
        val resized = if (scale < 1f) {
            Bitmap.createScaledBitmap(
                bitmap,
                (bitmap.width * scale).toInt(),
                (bitmap.height * scale).toInt(),
                true
            )
        } else bitmap

        val stream = ByteArrayOutputStream()
        resized.compress(Bitmap.CompressFormat.JPEG, 80, stream)
        return Base64.encodeToString(stream.toByteArray(), Base64.NO_WRAP)
    }
}

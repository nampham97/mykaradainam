// data/remote/groq/GroqModels.kt
package com.mykaradainam.data.remote.groq

import com.google.gson.annotations.SerializedName

// Chat Completion
data class ChatRequest(
    val model: String,
    val messages: List<ChatMessage>,
    val temperature: Double = 0.1
)

data class ChatMessage(
    val role: String,
    val content: Any // String or List<ContentPart>
)

data class ContentPart(
    val type: String,
    @SerializedName("text") val text: String? = null,
    @SerializedName("image_url") val imageUrl: ImageUrl? = null
)

data class ImageUrl(val url: String)

data class ChatResponse(
    val choices: List<Choice>
)

data class Choice(
    val message: MessageContent
)

data class MessageContent(
    val content: String
)

// Whisper Transcription response
data class TranscriptionResponse(
    val text: String
)

// Parsed invoice from AI
data class InvoiceParseResult(
    val items: List<ParsedItem>,
    val totalAmount: Long,
    val confidence: String = "high",
    val warnings: List<String> = emptyList()
)

data class ParsedItem(
    val name: String,
    val quantity: Int,
    val unitPrice: Long
)

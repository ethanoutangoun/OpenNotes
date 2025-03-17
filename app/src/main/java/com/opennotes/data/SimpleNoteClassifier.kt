package com.opennotes.data

import com.opennotes.ui.Category
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

// Retrofit service interface
interface OpenAIService {
    @POST("v1/chat/completions")
    suspend fun getChatCompletion(
        @Header("Authorization") authorization: String,
        @Body request: ChatRequest
    ): ChatResponse
}

// Data classes for the API
data class ChatRequest(
    val model: String = "gpt-4o",
    val messages: List<Message>,
    val temperature: Double = 0.5
)

data class Message(
    val role: String,
    val content: String
)

data class ChatResponse(
    val choices: List<Choice>
)

data class Choice(
    val message: Message
)

class SimpleNoteClassifier {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.openai.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val service = retrofit.create(OpenAIService::class.java)

    /**
     * Simple function to get a category for a note
     */
    suspend fun categorizeSingleNote(
        apiKey: String,
        noteContent: String,
        existingCategories: List<Category>
    ): Pair<String, String> = withContext(Dispatchers.IO) {
        // Convert categories to a simple text list
        val categoriesText = existingCategories.joinToString("\n") {
            "${it.id}: ${it.name}"
        }

        val prompt = """
            Analyze this note and tell me which category it belongs to.
            If it fits into an existing category, just return the category name and a random color, separated by a space.
            If it is new category, return the category name along with a color that goes well with it.
            Ensure color hex values account for the alpha as the first two characters. Must always be 8 characters long.
            
            Example Output:
            Journaling 0xFFFFFFFF
            
            Existing categories:
            $categoriesText
            
            Note Content: $noteContent
        """.trimIndent()

        val request = ChatRequest(
            messages = listOf(
                Message("system", "You are a note categorization assistant."),
                Message("user", prompt)
            )
        )

        val response = service.getChatCompletion("Bearer $apiKey", request)
        val answer = response.choices.first().message.content.trim()


        // Use existing category
        val parts = answer.split(" ")
        val categoryName = parts.firstOrNull()?.trim() ?: ""
        val categoryColor = parts.getOrNull(1)?.trim() ?: ""

        Pair(categoryName, categoryColor)

    }

}
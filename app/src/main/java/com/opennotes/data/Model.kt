package com.opennotes.data

import android.util.Log
import androidx.compose.ui.graphics.Color
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
    val functions: List<FunctionDefinition>? = null,
    val temperature: Double = 0.01
)

data class FunctionDefinition(
    val name: String,
    val description: String,
    val parameters: Map<String, Any>
)

data class Message(
    val role: String,
    val content: String?,
    val function_call: FunctionCall? = null
)

data class FunctionCall(
    val name: String,
    val arguments: String
)

data class ChatResponse(
    val choices: List<Choice>
)

data class Choice(
    val message: Message
)

class Model {

    private fun createDeleteNoteFunction(): FunctionDefinition {
        return FunctionDefinition(
            name = "delete_note",
            description = "Delete a note by its ID",
            parameters = mapOf(
                "type" to "object",
                "properties" to mapOf(
                    "note_id" to mapOf(
                        "type" to "string",
                        "description" to "The ID of the note to delete"
                    )
                ),
                "required" to listOf("note_id")
            )
        )
    }


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
    ): Pair<String, Color> = withContext(Dispatchers.IO) {
        // Convert categories to a simple text list
        val categoriesText = existingCategories.joinToString("\n") {
            "${it.id}: ${it.name}"
        }

        val prompt = """
            
            Existing categories: $categoriesText
                    
            Note Content: $noteContent
            
            Analyze this note and categorize it. If not many categories, make a new one, even if it kind of fits.
            If it fits VERY WELL into an existing category, just return the category name and a random color, separated by a space.
            If it could benefit from adding another category, return the new category name along with a color that goes well with it.
            Ensure color hex values are always 6 characters long (RRGGBB) without an alpha channel. Colors chosen should stand out from a white background.
           
            Example Outputs:
            Journaling 0xFFFFFF
            Category2 0xE6E6E6
        """.trimIndent()

        val request = ChatRequest(
            messages = listOf(
                Message("system", "You are a note categorization assistant."),
                Message("user", prompt)
            )
        )

        val response = service.getChatCompletion("Bearer $apiKey", request)
        val answer = response.choices.first().message.content?.trim() ?: ""

        // Use existing category
        val parts = answer.split(" ")
        val categoryName = parts.firstOrNull()?.trim() ?: ""
        val categoryColor = parts.getOrNull(1)?.trim() ?: ""

        // Parse the color and handle the "0x" prefix
        val color = parseColor(categoryColor)

        Pair(categoryName, color)
    }

    /**
     * Function to parse a hex color string to a Color object.
     */
    private fun parseColor(colorString: String): Color {
        return try {
            val cleanedColorString = colorString.removePrefix("0x")

            // Ensure it is exactly 6 characters (RGB format)
            if (cleanedColorString.length == 6 && cleanedColorString.matches(Regex("^[0-9A-Fa-f]+$"))) {
                // Prepend "FF" for full opacity
                val argb = "FF$cleanedColorString"
                Color(android.graphics.Color.parseColor("#$argb"))
            } else {
                Color.Black // Default fallback color
            }
        } catch (e: Exception) {
            Color.Black
        }
    }

    suspend fun queryWithContext(
        apiKey: String,
        query: String,
        noteContext: String
    ): Map<String, Any> = withContext(Dispatchers.IO) {
        // Create a prompt for the query along with the provided note context
        val prompt = """
    You are a helpful assistant designed to help users efficiently query/manage their saved notes. 
    If the user wants to delete a note, call the 'delete_note' function with the correct note ID.
    Otherwise, answer normally.
    Return answers in a concise and useful format.
    Here is some context about a note: 
    $noteContext
    
    Based on this context, answer the following query:
    $query
    """.trimIndent()

        val request = ChatRequest(
            messages = listOf(
                Message("system", "You are a helpful assistant."),
                Message("user", prompt)
            ),
            functions = listOf(createDeleteNoteFunction())
        )

        // Get the response from OpenAI API
        val response = service.getChatCompletion("Bearer $apiKey", request)
        val responseMessage = response.choices.first().message


        // Check if we have a function call
        return@withContext if (responseMessage.function_call != null) {
            // Function call detected, return function name and arguments
            mapOf(
                "response" to "Function call: ${responseMessage.function_call.name} with arguments: ${responseMessage.function_call.arguments}",
                "type" to "function",
                "function_name" to responseMessage.function_call.name,
                "params" to responseMessage.function_call.arguments
            )
        } else {
            // Regular message response
            mapOf(
                "response" to (responseMessage.content?.trim() ?: "No response content"),
                "type" to "message"
            )
        }
    }
}
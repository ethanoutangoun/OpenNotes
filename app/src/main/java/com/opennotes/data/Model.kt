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
            name = "delete_notes",
            description = "Delete multiple notes by providing a list of IDs",
            parameters = mapOf(
                "type" to "object",
                "properties" to mapOf(
                    "note_ids" to mapOf(
                        "type" to "array",
                        "description" to "An array of note IDs to delete",
                        "items" to mapOf(
                            "type" to "string"
                        )
                    )
                ),
                "required" to listOf("note_ids")
            )
        )
    }


    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.openai.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val service = retrofit.create(OpenAIService::class.java)


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
            Pick colors that aren't neutral (black/white/gray), ensure it is able to stand out against a white/black background.
            Ensure color hex values are always 6 characters long (RRGGBB) without an alpha channel. 
           
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

        val color = parseColor(categoryColor)

        Pair(categoryName, color)
    }

    /**
     * Function to parse a hex color string to a Color object.
     */
    private fun parseColor(colorString: String): Color {

        val defaultColor = Color.Magenta
        return try {
            val cleanedColorString = colorString.removePrefix("0x")

            if (cleanedColorString.length == 6 && cleanedColorString.matches(Regex("^[0-9A-Fa-f]+$"))) {
                val argb = "FF$cleanedColorString"
                Color(android.graphics.Color.parseColor("#$argb"))
            } else {
                Log.d("Model", "Color does not match regex, defaulting to $defaultColor")
                defaultColor
            }
        } catch (e: Exception) {
            Log.d("Model", "Color parsing failed, defaulting to $defaultColor")
            defaultColor
        }
    }

    suspend fun summarizeNote(
        apiKey: String,
        query: String,
    ): String = withContext(Dispatchers.IO) {

        val prompt = """
    Note content:
    $query
    """.trimIndent()

        val request = ChatRequest(
            messages = listOf(
                Message("system", "You are a helpful assistant designed to condense the content of a note into a concise title. Return just the title name"),
                Message("user", prompt)
            )
        )

        val response = service.getChatCompletion("Bearer $apiKey", request)
        val responseMessage = response.choices.first().message

        responseMessage.content?.trim() ?: "No response content"
    }


    suspend fun queryPostFunction(
        apiKey: String,
        query: String,
        context: String
    ): String = withContext(Dispatchers.IO) {
        val prompt = """
    You are a helpful assistant designed to help users efficiently query/manage their saved notes. 
    
    Here is context to the conversation:
    $context
   
    This function has just been executed: 
    $query
    
    Let the user know what you did and that the operation succeeded.
    """.trimIndent()

        val request = ChatRequest(
            messages = listOf(
                Message("system", "You are a helpful assistant."),
                Message("user", prompt)
            )
        )

        // Get the response from OpenAI API
        val response = service.getChatCompletion("Bearer $apiKey", request)
        val responseMessage = response.choices.first().message

        // Simply return the content as a string
        responseMessage.content?.trim() ?: "No response content"
    }

    suspend fun queryWithContext(
        apiKey: String,
        query: String,
        noteContext: String
    ): Map<String, Any> = withContext(Dispatchers.IO) {
        // Create a prompt for the query along with the provided note context
        val prompt = """
    You are a helpful assistant designed to help users efficiently query/manage their saved notes. 
    You will receive context on the user's notes, use the contents of the notes to provide helpful answers.
    If the user wants to delete notes, call the 'delete_notes' function with the correct note IDs. 
    If the user has stated a task from a note has been completed, delete that note.
    Ensure you only delete notes that exactly fit their request, otherwise do not call "delete_notes".
    Otherwise, answer normally.
    For deletion requests, include all relevant note IDs that should be deleted based on the user's query.
    Return answers in a concise and useful format, in plaintext not markdown.
    
    Here is the context for the user's notes: 
    $noteContext
    """.trimIndent()

        val message = """          
            Based on the user's context, answer the following query:
            $query
        """.trimIndent()

        val request = ChatRequest(
            messages = listOf(
                Message("system", prompt),
                Message("user", message)
            ),
            functions = listOf(createDeleteNoteFunction())
        )

        val response = service.getChatCompletion("Bearer $apiKey", request)
        val responseMessage = response.choices.first().message


        return@withContext if (responseMessage.function_call != null) {
            mapOf(
                "response" to "Function call: ${responseMessage.function_call.name} with arguments: ${responseMessage.function_call.arguments}",
                "type" to "function",
                "function_name" to responseMessage.function_call.name,
                "params" to responseMessage.function_call.arguments
            )
        } else {
            mapOf(
                "response" to (responseMessage.content?.trim() ?: "No response content"),
                "type" to "message"
            )
        }
    }
}
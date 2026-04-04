package org.example.project.data

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.example.project.model.UserAccount
import kotlin.random.Random

/**
 * Gestionează comunicarea cu Firebase pentru operațiuni pe tabela `users`.
 */
object AuthRepository {

    private const val BASE_URL = FirebaseConfig.DATABASE_URL
    
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(json)
        }
    }

    var currentUser: UserAccount? = null
        private set

    /**
     * Creează un cont nou pe Firebase dacă username-ul nu e luat.
     */
    suspend fun register(username: String, passwordHash: String): Result<UserAccount> {
        return try {
            val endpoint = "$BASE_URL/users/$username.json"
            val checkResponse = client.get(endpoint)
            val body = checkResponse.bodyAsText()
            
            // Firebase returnează "null" la chei invalide
            if (checkResponse.status.isSuccess() && body != "null" && body.isNotBlank()) {
                return Result.failure(Exception("Username already exists."))
            }

            val newUser = UserAccount(
                username = username,
                passwordHash = passwordHash,
                memberId = generateId()
            )

            client.put(endpoint) {
                contentType(ContentType.Application.Json)
                setBody(json.encodeToString(UserAccount.serializer(), newUser))
            }.bodyAsText()
            
            currentUser = newUser
            Result.success(newUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Autentifică un utilizator validându-i parola stocată la `/users/{username}`.
     */
    suspend fun login(username: String, passwordHash: String): Result<UserAccount> {
        return try {
            val endpoint = "$BASE_URL/users/$username.json"
            val response = client.get(endpoint)
            val body = response.bodyAsText()

            if (!response.status.isSuccess() || body == "null" || body.isBlank()) {
                return Result.failure(Exception("User not found."))
            }

            val fetchedUser = json.decodeFromString(UserAccount.serializer(), body)
            if (fetchedUser.passwordHash != passwordHash) {
                return Result.failure(Exception("Incorrect password."))
            }

            currentUser = fetchedUser
            Result.success(fetchedUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun joinCircle(inviteCode: String, groupName: String): Result<Unit> {
        return try {
            val user = currentUser ?: return Result.failure(Exception("No logged in user"))
            
            val updatedJoinedGroups = user.joinedGroups.toMutableMap()
            updatedJoinedGroups[inviteCode] = groupName
            
            val updatedUser = user.copy(
                joinedGroups = updatedJoinedGroups,
                lastActiveGroup = inviteCode
            )
            
            val endpoint = "$BASE_URL/users/${user.username}.json"
            client.put(endpoint) {
                contentType(ContentType.Application.Json)
                setBody(json.encodeToString(UserAccount.serializer(), updatedUser))
            }.bodyAsText()
            currentUser = updatedUser
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun setActiveCircle(inviteCode: String): Result<Unit> {
        return try {
            val user = currentUser ?: return Result.failure(Exception("No logged in user"))
            
            val updatedUser = user.copy(lastActiveGroup = inviteCode)
            
            val endpoint = "$BASE_URL/users/${user.username}.json"
            client.put(endpoint) {
                contentType(ContentType.Application.Json)
                setBody(json.encodeToString(UserAccount.serializer(), updatedUser))
            }.bodyAsText()
            currentUser = updatedUser
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        currentUser = null
    }

    private fun generateId(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        return (1..12).map { chars[Random.nextInt(chars.length)] }.joinToString("")
    }
}

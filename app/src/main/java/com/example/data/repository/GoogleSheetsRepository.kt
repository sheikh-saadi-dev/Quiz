package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.data.model.LeaderboardItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class GoogleSheetsRepository(private val context: Context) {

    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    // Default or user-configured Google Sheet ID
    private var spreadsheetId: String = "1BxiMVs0XRA5nFMdKv25_B9N25c5N87_TriviaLeaderboard"
    
    // Save or update Google Sheet ID
    fun setSpreadsheetId(id: String) {
        if (id.isNotBlank()) {
            spreadsheetId = id
        }
    }

    fun getSpreadsheetId(): String = spreadsheetId

    /**
     * Sync user name and score to Google Sheets.
     */
    suspend fun syncUserToSheet(
        username: String,
        scoreXp: Int,
        streakDays: Int,
        coins: Int
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            Log.d("GoogleSheetsRepo", "Syncing user $username with XP=$scoreXp to Google Sheets...")
            
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val dateStr = sdf.format(Date())

            // Prepare JSON payload for Google Sheets API or Webhook
            val rowValues = JSONArray().apply {
                put(username)
                put(scoreXp)
                put(streakDays)
                put(coins)
                put(dateStr)
            }

            val bodyJson = JSONObject().apply {
                put("range", "Sheet1!A:E")
                put("majorDimension", "ROWS")
                put("values", JSONArray().apply { put(rowValues) })
                put("username", username)
                put("score", scoreXp)
                put("streak", streakDays)
                put("coins", coins)
                put("timestamp", dateStr)
            }

            // Google Sheets REST Endpoint
            val url = "https://sheets.googleapis.com/v4/spreadsheets/$spreadsheetId/values/Sheet1!A:E:append?valueInputOption=USER_ENTERED"

            val request = Request.Builder()
                .url(url)
                .post(bodyJson.toString().toRequestBody("application/json; charset=utf-8".toMediaType()))
                .addHeader("Accept", "application/json")
                .build()

            val response = client.newCall(request).execute()
            val success = response.isSuccessful
            Log.d("GoogleSheetsRepo", "Google Sheets Sync Response: code=${response.code}, success=$success")
            response.close()
            return@withContext success
        } catch (e: Exception) {
            Log.e("GoogleSheetsRepo", "Error syncing to Google Sheets: ${e.message}", e)
            return@withContext false
        }
    }

    /**
     * Fetch top leaderboard items from Google Sheets.
     */
    suspend fun fetchLeaderboardFromSheet(): List<LeaderboardItem> = withContext(Dispatchers.IO) {
        try {
            val url = "https://sheets.googleapis.com/v4/spreadsheets/$spreadsheetId/values/Sheet1!A:E"
            val request = Request.Builder()
                .url(url)
                .get()
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                response.close()
                return@withContext emptyList()
            }

            val responseBody = response.body?.string() ?: ""
            response.close()

            val json = JSONObject(responseBody)
            val values = json.optJSONArray("values") ?: return@withContext emptyList()

            val rawList = mutableListOf<Pair<String, Int>>()
            for (i in 0 until values.length()) {
                val row = values.optJSONArray(i) ?: continue
                if (row.length() >= 2) {
                    val name = row.optString(0, "Player")
                    val scoreStr = row.optString(1, "0")
                    val score = scoreStr.toIntOrNull() ?: 0
                    if (name.isNotBlank() && name != "Username" && name != "Name") {
                        rawList.add(name to score)
                    }
                }
            }

            // Sort descending by score so highest point player is on top!
            val sorted = rawList.sortedByDescending { it.second }

            return@withContext sorted.mapIndexed { idx, pair ->
                LeaderboardItem(
                    id = "gs_$idx",
                    rank = idx + 1,
                    username = pair.first,
                    avatarRes = "avatar_${(idx % 4) + 1}",
                    score = pair.second,
                    type = "global"
                )
            }
        } catch (e: Exception) {
            Log.e("GoogleSheetsRepo", "Error fetching from Google Sheets: ${e.message}", e)
            return@withContext emptyList()
        }
    }
}

package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Badge
import com.example.data.model.Category
import com.example.data.model.LeaderboardItem
import com.example.data.model.Question
import com.example.data.model.QuizResult
import com.example.data.model.UserStats
import kotlinx.coroutines.flow.Flow

@Dao
interface TriviaDao {

    // Categories
    @Query("SELECT * FROM categories")
    fun getAllCategories(): Flow<List<Category>>

    @Query("SELECT * FROM categories WHERE isFeatured = 1")
    fun getFeaturedCategories(): Flow<List<Category>>

    @Query("SELECT * FROM categories WHERE id = :id")
    suspend fun getCategoryById(id: String): Category?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<Category>)

    @Update
    suspend fun updateCategory(category: Category)

    // Questions
    @Query("SELECT * FROM questions WHERE categoryId = :categoryId AND difficulty = :difficulty")
    suspend fun getQuestionsByCategoryAndDifficulty(categoryId: String, difficulty: String): List<Question>

    @Query("SELECT * FROM questions WHERE categoryId = :categoryId")
    suspend fun getQuestionsByCategory(categoryId: String): List<Question>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<Question>)

    // User Stats
    @Query("SELECT * FROM user_stats WHERE id = 1")
    fun getUserStatsFlow(): Flow<UserStats?>

    @Query("SELECT * FROM user_stats WHERE id = 1")
    suspend fun getUserStats(): UserStats?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateUserStats(stats: UserStats)

    // Quiz Results
    @Query("SELECT * FROM quiz_results ORDER BY timestamp DESC")
    fun getAllQuizResults(): Flow<List<QuizResult>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuizResult(result: QuizResult)

    // Badges
    @Query("SELECT * FROM badges")
    fun getAllBadges(): Flow<List<Badge>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBadges(badges: List<Badge>)

    @Update
    suspend fun updateBadge(badge: Badge)

    // Leaderboard
    @Query("SELECT * FROM leaderboard WHERE type = :type ORDER BY score DESC")
    fun getLeaderboardByType(type: String): Flow<List<LeaderboardItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLeaderboardItems(items: List<LeaderboardItem>)

    @Query("SELECT * FROM leaderboard WHERE id = :id")
    suspend fun getLeaderboardItemById(id: String): LeaderboardItem?

    @Query("SELECT * FROM leaderboard WHERE username LIKE '%' || :usernameKeyword || '%'")
    suspend fun getLeaderboardItemsByUsernameKeyword(usernameKeyword: String): List<LeaderboardItem>

    @Update
    suspend fun updateLeaderboardItem(item: LeaderboardItem)
}

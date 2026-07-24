package com.example.data.repository

import com.example.data.db.TriviaDao
import com.example.data.model.Badge
import com.example.data.model.Category
import com.example.data.model.LeaderboardItem
import com.example.data.model.Question
import com.example.data.model.QuizResult
import com.example.data.model.UserStats
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class TriviaRepository(
    private val dao: TriviaDao,
    private val googleSheetsRepository: GoogleSheetsRepository? = null
) {

    val allCategories: Flow<List<Category>> = dao.getAllCategories()
    val featuredCategories: Flow<List<Category>> = dao.getFeaturedCategories()
    val userStats: Flow<UserStats?> = dao.getUserStatsFlow()
    val allBadges: Flow<List<Badge>> = dao.getAllBadges()
    val quizResults: Flow<List<QuizResult>> = dao.getAllQuizResults()

    suspend fun updateUsername(newName: String) {
        val currentStats = dao.getUserStats() ?: UserStats()
        val updatedStats = currentStats.copy(username = newName)
        dao.insertOrUpdateUserStats(updatedStats)

        updateUserInLeaderboard(newName, updatedStats.xp)
        googleSheetsRepository?.syncUserToSheet(
            username = newName,
            scoreXp = updatedStats.xp,
            streakDays = updatedStats.streakDays,
            coins = updatedStats.coins
        )
    }

    private suspend fun updateUserInLeaderboard(username: String, xp: Int) {
        val displayName = if (username.contains("(You)")) username else "$username (You)"
        
        // Find existing "(You)" entries in leaderboard
        val youItems = dao.getLeaderboardItemsByUsernameKeyword("(You)")
        if (youItems.isNotEmpty()) {
            for (item in youItems) {
                dao.updateLeaderboardItem(item.copy(username = displayName, score = xp))
            }
        } else {
            // Insert new leaderboard entries for this user
            val newItems = listOf(
                LeaderboardItem("g_user_you", 1, displayName, "avatar_1", xp, "global"),
                LeaderboardItem("w_user_you", 1, displayName, "avatar_1", xp, "this_week"),
                LeaderboardItem("f_user_you", 1, displayName, "avatar_1", xp, "friends")
            )
            dao.insertLeaderboardItems(newItems)
        }
    }

    fun getLeaderboard(type: String): Flow<List<LeaderboardItem>> {
        return dao.getLeaderboardByType(type)
    }

    suspend fun getQuestionsForQuiz(categoryId: String, difficulty: String): List<Question> {
        val questions = dao.getQuestionsByCategoryAndDifficulty(categoryId, difficulty)
        return if (questions.isNotEmpty()) {
            questions.shuffled().take(10)
        } else {
            // Fallback to any questions in this category
            dao.getQuestionsByCategory(categoryId).shuffled().take(10)
        }
    }

    suspend fun recordQuizResult(
        categoryId: String,
        categoryTitle: String,
        difficulty: String,
        score: Int,
        totalQuestions: Int
    ): QuizResult {
        val baseMultiplier = when (difficulty) {
            "easy" -> 10
            "medium" -> 15
            "hard" -> 25
            else -> 10
        }

        val xpEarned = score * baseMultiplier + if (score >= 8) 50 else 0
        val coinsEarned = score * 2 + if (score == 10) 20 else 0

        val result = QuizResult(
            categoryId = categoryId,
            categoryTitle = categoryTitle,
            difficulty = difficulty,
            score = score,
            totalQuestions = totalQuestions,
            xpEarned = xpEarned,
            coinsEarned = coinsEarned
        )

        dao.insertQuizResult(result)

        // Update User Stats
        val currentStats = dao.getUserStats() ?: UserStats()
        val newXp = currentStats.xp + xpEarned
        val newCoins = currentStats.coins + coinsEarned

        // Check streak
        val lastTimestamp = currentStats.lastQuizTimestamp
        val oneDayMillis = 86400000L
        val diff = System.currentTimeMillis() - lastTimestamp
        val newStreak = if (diff < oneDayMillis * 2) {
            if (diff >= oneDayMillis) currentStats.streakDays + 1 else currentStats.streakDays
        } else {
            1
        }

        val updatedStats = currentStats.copy(
            xp = newXp,
            coins = newCoins,
            streakDays = newStreak,
            lastQuizTimestamp = System.currentTimeMillis()
        )
        dao.insertOrUpdateUserStats(updatedStats)

        // Sync score to leaderboard items and Google Sheets
        updateUserInLeaderboard(updatedStats.username, updatedStats.xp)
        googleSheetsRepository?.syncUserToSheet(
            username = updatedStats.username,
            scoreXp = updatedStats.xp,
            streakDays = updatedStats.streakDays,
            coins = updatedStats.coins
        )

        // Update Category Completed Count
        val cat = dao.getCategoryById(categoryId)
        if (cat != null) {
            val updatedCat = cat.copy(completedCount = (cat.completedCount + 1).coerceAtMost(cat.totalQuestions))
            dao.updateCategory(updatedCat)
        }

        // Check Badges
        checkBadges(updatedStats, score)

        return result
    }

    private suspend fun checkBadges(stats: UserStats, lastScore: Int) {
        val badges = dao.getAllBadges().firstOrNull() ?: return
        for (badge in badges) {
            if (badge.isUnlocked) continue
            var unlock = false
            when (badge.id) {
                "b1" -> unlock = true // Completed first quiz
                "b2" -> if (stats.streakDays >= 5) unlock = true
                "b3" -> if (lastScore == 10) unlock = true
                "b6" -> if (stats.xp >= 2000) unlock = true
            }
            if (unlock) {
                dao.updateBadge(badge.copy(isUnlocked = true, unlockedAt = System.currentTimeMillis()))
            }
        }
    }

    suspend fun consumeLife(): Boolean {
        val stats = dao.getUserStats() ?: return false
        if (stats.lives > 0) {
            dao.insertOrUpdateUserStats(stats.copy(lives = stats.lives - 1))
            return true
        }
        return false
    }

    suspend fun addLife(amount: Int = 1) {
        val stats = dao.getUserStats() ?: return
        val newLives = (stats.lives + amount).coerceAtMost(stats.maxLives)
        dao.insertOrUpdateUserStats(stats.copy(lives = newLives))
    }

    suspend fun doubleXP(resultId: Int) {
        val stats = dao.getUserStats() ?: return
        // Give bonus 100 XP
        dao.insertOrUpdateUserStats(stats.copy(xp = stats.xp + 100))
    }

    suspend fun toggleDarkMode(enabled: Boolean) {
        val stats = dao.getUserStats() ?: return
        dao.insertOrUpdateUserStats(stats.copy(darkThemeEnabled = enabled))
    }

    suspend fun toggleSound(enabled: Boolean) {
        val stats = dao.getUserStats() ?: return
        dao.insertOrUpdateUserStats(stats.copy(soundEnabled = enabled))
    }

    suspend fun toggleHaptics(enabled: Boolean) {
        val stats = dao.getUserStats() ?: return
        dao.insertOrUpdateUserStats(stats.copy(hapticsEnabled = enabled))
    }

    suspend fun removeAds() {
        val stats = dao.getUserStats() ?: return
        dao.insertOrUpdateUserStats(stats.copy(isAdsRemoved = true))
    }

    suspend fun setOnboardingCompleted() {
        val stats = dao.getUserStats() ?: return
        dao.insertOrUpdateUserStats(stats.copy(isOnboardingCompleted = true))
    }
}

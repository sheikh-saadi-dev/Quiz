package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val iconName: String,
    val colorHex: String,
    val totalQuestions: Int = 10,
    val completedCount: Int = 0,
    val isFeatured: Boolean = false
)

@Entity(tableName = "questions")
data class Question(
    @PrimaryKey val id: String,
    val categoryId: String,
    val difficulty: String, // "easy", "medium", "hard"
    val questionText: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String = ""
)

@Entity(tableName = "user_stats")
data class UserStats(
    @PrimaryKey val id: Int = 1,
    val username: String = "Trivia Explorer",
    val coins: Int = 150,
    val xp: Int = 1250,
    val streakDays: Int = 3,
    val lastQuizTimestamp: Long = System.currentTimeMillis(),
    val lives: Int = 3,
    val maxLives: Int = 3,
    val isAdsRemoved: Boolean = false,
    val darkThemeEnabled: Boolean = false,
    val soundEnabled: Boolean = true,
    val hapticsEnabled: Boolean = true,
    val isOnboardingCompleted: Boolean = false
)

@Entity(tableName = "quiz_results")
data class QuizResult(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val categoryId: String,
    val categoryTitle: String,
    val difficulty: String,
    val score: Int,
    val totalQuestions: Int,
    val xpEarned: Int,
    val coinsEarned: Int,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "badges")
data class Badge(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val iconName: String,
    val isUnlocked: Boolean = false,
    val unlockedAt: Long? = null
)

@Entity(tableName = "leaderboard")
data class LeaderboardItem(
    @PrimaryKey val id: String,
    val rank: Int,
    val username: String,
    val avatarRes: String,
    val score: Int,
    val type: String // "global", "friends", "this_week"
)

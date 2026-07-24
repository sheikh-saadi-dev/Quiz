package com.example.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.admob.AdManager
import com.example.data.db.TriviaDatabase
import com.example.data.model.Badge
import com.example.data.model.Category
import com.example.data.model.LeaderboardItem
import com.example.data.model.Question
import com.example.data.model.QuizResult
import com.example.data.model.UserStats
import com.example.data.repository.GoogleSheetsRepository
import com.example.data.repository.TriviaRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class QuizViewModel(application: Application) : AndroidViewModel(application) {

    val googleSheetsRepository = GoogleSheetsRepository(application)
    private val db = TriviaDatabase.getDatabase(application, viewModelScope)
    private val repository = TriviaRepository(db.triviaDao(), googleSheetsRepository)
    val adManager = AdManager.getInstance(application)

    fun setPlayerName(name: String) {
        viewModelScope.launch {
            if (name.isNotBlank()) {
                repository.updateUsername(name.trim())
            }
        }
    }

    val userStats: StateFlow<UserStats?> = repository.userStats
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val categories: StateFlow<List<Category>> = repository.allCategories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val featuredCategories: StateFlow<List<Category>> = repository.featuredCategories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val badges: StateFlow<List<Badge>> = repository.allBadges
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active Quiz Gameplay State
    private val _currentCategory = MutableStateFlow<Category?>(null)
    val currentCategory: StateFlow<Category?> = _currentCategory.asStateFlow()

    private val _selectedDifficulty = MutableStateFlow("medium")
    val selectedDifficulty: StateFlow<String> = _selectedDifficulty.asStateFlow()

    private val _quizQuestions = MutableStateFlow<List<Question>>(emptyList())
    val quizQuestions: StateFlow<List<Question>> = _quizQuestions.asStateFlow()

    private val _currentQuestionIndex = MutableStateFlow(0)
    val currentQuestionIndex: StateFlow<Int> = _currentQuestionIndex.asStateFlow()

    private val _score = MutableStateFlow(0)
    val score: StateFlow<Int> = _score.asStateFlow()

    private val _lives = MutableStateFlow(3)
    val lives: StateFlow<Int> = _lives.asStateFlow()

    private val _selectedOptionIndex = MutableStateFlow<Int?>(null)
    val selectedOptionIndex: StateFlow<Int?> = _selectedOptionIndex.asStateFlow()

    private val _isAnswerSubmitted = MutableStateFlow(false)
    val isAnswerSubmitted: StateFlow<Boolean> = _isAnswerSubmitted.asStateFlow()

    private val _timerSeconds = MutableStateFlow(15)
    val timerSeconds: StateFlow<Int> = _timerSeconds.asStateFlow()

    private val _showOutOfLivesModal = MutableStateFlow(false)
    val showOutOfLivesModal: StateFlow<Boolean> = _showOutOfLivesModal.asStateFlow()

    private val _showInterstitial = MutableStateFlow(false)
    val showInterstitial: StateFlow<Boolean> = _showInterstitial.asStateFlow()

    private val _lastResult = MutableStateFlow<QuizResult?>(null)
    val lastResult: StateFlow<QuizResult?> = _lastResult.asStateFlow()

    private val _leaderboardType = MutableStateFlow("global")
    val leaderboardType: StateFlow<String> = _leaderboardType.asStateFlow()

    private var timerJob: Job? = null

    fun selectCategory(category: Category) {
        _currentCategory.value = category
    }

    fun setDifficulty(difficulty: String) {
        _selectedDifficulty.value = difficulty
    }

    fun startQuiz() {
        val cat = _currentCategory.value ?: return
        val diff = _selectedDifficulty.value

        viewModelScope.launch {
            val questions = repository.getQuestionsForQuiz(cat.id, diff)
            _quizQuestions.value = questions
            _currentQuestionIndex.value = 0
            _score.value = 0
            _lives.value = userStats.value?.lives ?: 3
            _selectedOptionIndex.value = null
            _isAnswerSubmitted.value = false
            _showOutOfLivesModal.value = false
            startQuestionTimer()
        }
    }

    private fun startQuestionTimer() {
        timerJob?.cancel()
        _timerSeconds.value = 15
        timerJob = viewModelScope.launch {
            while (_timerSeconds.value > 0 && !_isAnswerSubmitted.value) {
                delay(1000)
                _timerSeconds.value--
            }
            if (_timerSeconds.value == 0 && !_isAnswerSubmitted.value) {
                // Time's up - count as wrong answer
                handleOptionSelected(-1)
            }
        }
    }

    fun handleOptionSelected(optionIndex: Int) {
        if (_isAnswerSubmitted.value) return

        timerJob?.cancel()
        _selectedOptionIndex.value = optionIndex
        _isAnswerSubmitted.value = true

        val currentQ = _quizQuestions.value.getOrNull(_currentQuestionIndex.value) ?: return
        val isCorrect = optionIndex == currentQ.correctIndex

        if (isCorrect) {
            _score.value++
        } else {
            _lives.value--
            viewModelScope.launch {
                repository.consumeLife()
            }
        }

        viewModelScope.launch {
            delay(1500) // Brief delay to show correct/wrong highlight
            if (_lives.value <= 0) {
                _showOutOfLivesModal.value = true
            } else {
                nextQuestionOrFinish()
            }
        }
    }

    fun watchAdForLife() {
        _showOutOfLivesModal.value = false
        _lives.value = 1
        viewModelScope.launch {
            repository.addLife(1)
            nextQuestionOrFinish()
        }
    }

    fun dismissOutOfLivesModalAndFinish() {
        _showOutOfLivesModal.value = false
        finishQuiz()
    }

    private fun nextQuestionOrFinish() {
        if (_currentQuestionIndex.value < _quizQuestions.value.size - 1) {
            _currentQuestionIndex.value++
            _selectedOptionIndex.value = null
            _isAnswerSubmitted.value = false
            startQuestionTimer()
        } else {
            finishQuiz()
        }
    }

    private fun finishQuiz() {
        val cat = _currentCategory.value ?: return
        viewModelScope.launch {
            val result = repository.recordQuizResult(
                categoryId = cat.id,
                categoryTitle = cat.title,
                difficulty = _selectedDifficulty.value,
                score = _score.value,
                totalQuestions = _quizQuestions.value.size
            )
            _lastResult.value = result

            // Check if we can show interstitial ad on results screen load
            if (adManager.canShowInterstitial()) {
                _showInterstitial.value = true
                adManager.recordInterstitialShown()
            }
        }
    }

    fun dismissInterstitial() {
        _showInterstitial.value = false
    }

    fun watchAdForDoubleXP() {
        val res = _lastResult.value ?: return
        viewModelScope.launch {
            repository.doubleXP(res.id)
            _lastResult.value = res.copy(xpEarned = res.xpEarned * 2)
        }
    }

    fun setLeaderboardType(type: String) {
        _leaderboardType.value = type
    }

    fun getLeaderboardFlow(): StateFlow<List<LeaderboardItem>> {
        return repository.getLeaderboard(_leaderboardType.value)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

    fun toggleDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            repository.toggleDarkMode(enabled)
        }
    }

    fun toggleSound(enabled: Boolean) {
        viewModelScope.launch {
            repository.toggleSound(enabled)
        }
    }

    fun toggleHaptics(enabled: Boolean) {
        viewModelScope.launch {
            repository.toggleHaptics(enabled)
        }
    }

    fun removeAds() {
        viewModelScope.launch {
            repository.removeAds()
            adManager.setAdsRemoved(true)
        }
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            repository.setOnboardingCompleted()
        }
    }
}

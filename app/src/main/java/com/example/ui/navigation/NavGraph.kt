package com.example.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.CategoryDetailScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LeaderboardScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.QuizScreen
import com.example.ui.screens.QuizViewModel
import com.example.ui.screens.ResultScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.theme.PrimaryPurple

sealed class Screen(val route: String, val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector?) {
    object Splash : Screen("splash", "Splash", null)
    object Onboarding : Screen("onboarding", "Onboarding", null)
    object Main : Screen("main", "Main", null)
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Leaderboard : Screen("leaderboard", "Leaderboard", Icons.Default.EmojiEvents)
    object Profile : Screen("profile", "Profile", Icons.Default.Person)
    object CategoryDetail : Screen("category_detail", "Category Detail", null)
    object Quiz : Screen("quiz", "Quiz", null)
    object Result : Screen("result", "Result", null)
}

@Composable
fun AppNavGraph(viewModel: QuizViewModel) {
    val navController = rememberNavController()

    val userStats by viewModel.userStats.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val featuredCategories by viewModel.featuredCategories.collectAsState()
    val currentCategory by viewModel.currentCategory.collectAsState()
    val selectedDifficulty by viewModel.selectedDifficulty.collectAsState()
    val quizQuestions by viewModel.quizQuestions.collectAsState()
    val currentQuestionIndex by viewModel.currentQuestionIndex.collectAsState()
    val lives by viewModel.lives.collectAsState()
    val score by viewModel.score.collectAsState()
    val selectedOptionIndex by viewModel.selectedOptionIndex.collectAsState()
    val isAnswerSubmitted by viewModel.isAnswerSubmitted.collectAsState()
    val timerSeconds by viewModel.timerSeconds.collectAsState()
    val showOutOfLivesModal by viewModel.showOutOfLivesModal.collectAsState()
    val showInterstitial by viewModel.showInterstitial.collectAsState()
    val lastResult by viewModel.lastResult.collectAsState()
    val badges by viewModel.badges.collectAsState()
    val leaderboardType by viewModel.leaderboardType.collectAsState()
    val leaderboardItems by viewModel.getLeaderboardFlow().collectAsState()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                isOnboardingCompleted = userStats?.isOnboardingCompleted ?: false,
                onNavigateNext = { route ->
                    navController.navigate(route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onFinishOnboarding = { playerName ->
                    if (playerName.isNotBlank()) {
                        viewModel.setPlayerName(playerName)
                    }
                    viewModel.completeOnboarding()
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Main.route) {
            MainContainerScreen(
                viewModel = viewModel,
                onCategorySelected = { category ->
                    viewModel.selectCategory(category)
                    navController.navigate(Screen.CategoryDetail.route)
                }
            )
        }

        composable(Screen.CategoryDetail.route) {
            CategoryDetailScreen(
                category = currentCategory,
                selectedDifficulty = selectedDifficulty,
                isAdsRemoved = userStats?.isAdsRemoved ?: false,
                onDifficultySelected = { diff -> viewModel.setDifficulty(diff) },
                onStartQuiz = {
                    viewModel.startQuiz()
                    navController.navigate(Screen.Quiz.route)
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Quiz.route) {
            QuizScreen(
                category = currentCategory,
                questions = quizQuestions,
                currentQuestionIndex = currentQuestionIndex,
                lives = lives,
                score = score,
                selectedOptionIndex = selectedOptionIndex,
                isAnswerSubmitted = isAnswerSubmitted,
                timerSeconds = timerSeconds,
                showOutOfLivesModal = showOutOfLivesModal,
                onOptionSelected = { idx -> viewModel.handleOptionSelected(idx) },
                onWatchAdForLife = { viewModel.watchAdForLife() },
                onDismissOutOfLives = {
                    viewModel.dismissOutOfLivesModalAndFinish()
                    navController.navigate(Screen.Result.route) {
                        popUpTo(Screen.CategoryDetail.route)
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Result.route) {
            ResultScreen(
                result = lastResult,
                showInterstitial = showInterstitial,
                onDismissInterstitial = { viewModel.dismissInterstitial() },
                onPlayAgain = {
                    viewModel.startQuiz()
                    navController.navigate(Screen.Quiz.route) {
                        popUpTo(Screen.Result.route) { inclusive = true }
                    }
                },
                onDoubleXP = { viewModel.watchAdForDoubleXP() },
                onBackHome = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Main.route) { inclusive = true }
                    }
                }
            )
        }
    }
}

@Composable
fun MainContainerScreen(
    viewModel: QuizViewModel,
    onCategorySelected: (com.example.data.model.Category) -> Unit
) {
    var selectedBottomTab by remember { mutableStateOf("home") }

    val userStats by viewModel.userStats.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val featuredCategories by viewModel.featuredCategories.collectAsState()
    val badges by viewModel.badges.collectAsState()
    val leaderboardType by viewModel.leaderboardType.collectAsState()
    val leaderboardItems by viewModel.getLeaderboardFlow().collectAsState()

    Scaffold(
        bottomBar = {
            NavigationBar(
                modifier = Modifier.testTag("main_bottom_nav"),
                containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surface
            ) {
                val navItems = listOf(Screen.Home, Screen.Leaderboard, Screen.Profile)
                navItems.forEach { screen ->
                    val isSelected = selectedBottomTab == screen.route
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { selectedBottomTab = screen.route },
                        icon = {
                            screen.icon?.let { icon ->
                                Icon(imageVector = icon, contentDescription = screen.title)
                            }
                        },
                        label = { Text(text = screen.title) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PrimaryPurple,
                            selectedTextColor = PrimaryPurple,
                            indicatorColor = PrimaryPurple.copy(alpha = 0.15f)
                        ),
                        modifier = Modifier.testTag("nav_item_${screen.route}")
                    )
                }
            }
        }
    ) { innerPadding ->
        androidx.compose.foundation.layout.Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedBottomTab) {
                "home" -> HomeScreen(
                    userStats = userStats,
                    categories = categories,
                    featuredCategories = featuredCategories,
                    onCategorySelected = onCategorySelected,
                    onNavigateProfile = { selectedBottomTab = "profile" }
                )
                "leaderboard" -> LeaderboardScreen(
                    leaderboardType = leaderboardType,
                    leaderboardItems = leaderboardItems,
                    isAdsRemoved = userStats?.isAdsRemoved ?: false,
                    onTypeSelected = { type -> viewModel.setLeaderboardType(type) }
                )
                "profile" -> ProfileScreen(
                    userStats = userStats,
                    badges = badges,
                    onToggleDarkMode = { enabled -> viewModel.toggleDarkMode(enabled) },
                    onToggleSound = { enabled -> viewModel.toggleSound(enabled) },
                    onToggleHaptics = { enabled -> viewModel.toggleHaptics(enabled) },
                    onRemoveAds = { viewModel.removeAds() },
                    onUpdateName = { name -> viewModel.setPlayerName(name) }
                )
            }
        }
    }
}

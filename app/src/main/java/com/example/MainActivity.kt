package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.ui.navigation.AppNavGraph
import com.example.ui.screens.QuizViewModel
import com.example.ui.theme.TriviaQuizTheme

class MainActivity : ComponentActivity() {

    private val viewModel: QuizViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val userStats by viewModel.userStats.collectAsState()
            val darkTheme = userStats?.darkThemeEnabled ?: false

            TriviaQuizTheme(darkTheme = darkTheme) {
                AppNavGraph(viewModel = viewModel)
            }
        }
    }
}

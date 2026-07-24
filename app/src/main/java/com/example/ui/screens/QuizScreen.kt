package com.example.ui.screens

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.admob.AdMobRewardedModal
import com.example.data.model.Category
import com.example.data.model.Question
import com.example.ui.components.TimerRing
import com.example.ui.theme.CorrectGreen
import com.example.ui.theme.PrimaryPurple
import com.example.ui.theme.WrongRed
import kotlin.math.roundToInt

@Composable
fun QuizScreen(
    category: Category?,
    questions: List<Question>,
    currentQuestionIndex: Int,
    lives: Int,
    score: Int,
    selectedOptionIndex: Int?,
    isAnswerSubmitted: Boolean,
    timerSeconds: Int,
    showOutOfLivesModal: Boolean,
    onOptionSelected: (Int) -> Unit,
    onWatchAdForLife: () -> Unit,
    onDismissOutOfLives: () -> Unit,
    onBack: () -> Unit
) {
    val currentQuestion = questions.getOrNull(currentQuestionIndex)
    val context = LocalContext.current

    // Shake animation offset for wrong answers
    val shakeOffset = remember { Animatable(0f) }

    LaunchedEffect(isAnswerSubmitted) {
        if (isAnswerSubmitted && selectedOptionIndex != null) {
            val isCorrect = selectedOptionIndex == currentQuestion?.correctIndex
            triggerHapticFeedback(context, isCorrect)

            if (!isCorrect) {
                // Shake animation (2 cycles, 200ms)
                shakeOffset.animateTo(20f, tween(50))
                shakeOffset.animateTo(-20f, tween(50))
                shakeOffset.animateTo(10f, tween(50))
                shakeOffset.animateTo(0f, tween(50))
            }
        }
    }

    if (showOutOfLivesModal) {
        AdMobRewardedModal(
            title = "Out of Lives!",
            rewardText = "Watch a short video ad to gain +1 Life and continue this quiz session?",
            onRewardEarned = onWatchAdForLife,
            onDismiss = onDismissOutOfLives
        )
    }

    Scaffold(
        topBar = {
            // Top Gameplay Bar (Back button, Question Progress, Timer Ring, Lives)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("quiz_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Question ${currentQuestionIndex + 1} of ${questions.size}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { ((currentQuestionIndex + 1).toFloat() / questions.size.coerceAtLeast(1).toFloat()) },
                        modifier = Modifier
                            .width(100.dp)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = PrimaryPurple,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }

                TimerRing(secondsRemaining = timerSeconds, totalSeconds = 15)

                // Lives Indicator (3 heart icons)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    repeat(3) { index ->
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Life",
                            tint = if (index < lives) WrongRed else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                            modifier = Modifier
                                .size(20.dp)
                                .padding(horizontal = 1.dp)
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        if (currentQuestion == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Loading Quiz Questions...")
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
                .testTag("quiz_screen"),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Question Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
                    .offset { IntOffset(shakeOffset.value.roundToInt(), 0) },
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        color = PrimaryPurple.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.padding(bottom = 12.dp)
                    ) {
                        Text(
                            text = category?.title?.uppercase() ?: "TRIVIA",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryPurple,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    Text(
                        text = currentQuestion.questionText,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        lineHeight = 26.sp
                    )
                }
            }

            // Answer Options Stack
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                currentQuestion.options.forEachIndexed { index, optionText ->
                    AnswerOptionButton(
                        optionIndex = index,
                        optionText = optionText,
                        correctIndex = currentQuestion.correctIndex,
                        selectedIndex = selectedOptionIndex,
                        isSubmitted = isAnswerSubmitted,
                        onClick = { onOptionSelected(index) }
                    )
                }
            }
        }
    }
}

@Composable
private fun AnswerOptionButton(
    optionIndex: Int,
    optionText: String,
    correctIndex: Int,
    selectedIndex: Int?,
    isSubmitted: Boolean,
    onClick: () -> Unit
) {
    val isSelected = selectedIndex == optionIndex
    val isCorrect = optionIndex == correctIndex

    val targetBgColor = when {
        isSubmitted && isCorrect -> CorrectGreen
        isSubmitted && isSelected && !isCorrect -> WrongRed
        isSelected -> PrimaryPurple.copy(alpha = 0.2f)
        else -> MaterialTheme.colorScheme.surface
    }

    val animatedBgColor by animateColorAsState(
        targetValue = targetBgColor,
        animationSpec = tween(250),
        label = "option_bg"
    )

    val textColor = when {
        isSubmitted && (isCorrect || (isSelected && !isCorrect)) -> Color.White
        else -> MaterialTheme.colorScheme.onSurface
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable(enabled = !isSubmitted) { onClick() }
            .testTag("quiz_option_$optionIndex"),
        shape = RoundedCornerShape(14.dp),
        color = animatedBgColor,
        border = androidx.compose.foundation.BorderStroke(
            1.5.dp,
            if (isSubmitted && isCorrect) CorrectGreen
            else if (isSubmitted && isSelected && !isCorrect) WrongRed
            else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(
                            if (isSubmitted && isCorrect) Color.White.copy(alpha = 0.3f)
                            else if (isSubmitted && isSelected && !isCorrect) Color.White.copy(alpha = 0.3f)
                            else MaterialTheme.colorScheme.surfaceVariant
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = ('A' + optionIndex).toString(),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = optionText,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = textColor
                )
            }

            if (isSubmitted) {
                if (isCorrect) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Correct",
                        tint = Color.White
                    )
                } else if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Wrong",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

private fun triggerHapticFeedback(context: Context, isCorrect: Boolean) {
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            val vibrator = vibratorManager.defaultVibrator
            val effect = if (isCorrect) {
                VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
            } else {
                VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK)
            }
            vibrator.vibrate(effect)
        } else {
            @Suppress("DEPRECATION")
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if (isCorrect) {
                vibrator.vibrate(50)
            } else {
                vibrator.vibrate(150)
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

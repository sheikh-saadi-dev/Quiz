package com.example.ui.screens

import android.content.Intent
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.admob.AdMobInterstitialDialog
import com.example.admob.AdMobRewardedModal
import com.example.data.model.QuizResult
import com.example.ui.components.ConfettiBurst
import com.example.ui.theme.CoinYellow
import com.example.ui.theme.CorrectGreen
import com.example.ui.theme.PrimaryPurple
import com.example.ui.theme.SecondaryTeal

@Composable
fun ResultScreen(
    result: QuizResult?,
    showInterstitial: Boolean,
    onDismissInterstitial: () -> Unit,
    onPlayAgain: () -> Unit,
    onDoubleXP: () -> Unit,
    onBackHome: () -> Unit
) {
    if (result == null) return

    val context = LocalContext.current
    val percentage = (result.score * 100) / result.totalQuestions.coerceAtLeast(1)
    val isHighPerformance = percentage >= 80

    var animatedScore by remember { mutableIntStateOf(0) }
    var showDoubleXpRewardedModal by remember { mutableStateOf(false) }

    val displayScore by animateIntAsState(
        targetValue = animatedScore,
        animationSpec = tween(1000),
        label = "score_count_up"
    )

    LaunchedEffect(Unit) {
        animatedScore = result.score
    }

    if (showInterstitial) {
        AdMobInterstitialDialog(onDismiss = onDismissInterstitial)
    }

    if (showDoubleXpRewardedModal) {
        AdMobRewardedModal(
            title = "Double Your XP!",
            rewardText = "Watch a short video ad to double your XP earned for this quiz session?",
            onRewardEarned = {
                showDoubleXpRewardedModal = false
                onDoubleXP()
            },
            onDismiss = { showDoubleXpRewardedModal = false }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("result_screen")
    ) {
        if (isHighPerformance) {
            ConfettiBurst()
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = if (isHighPerformance) "AMAZING JOB! 🎉" else "QUIZ COMPLETED!",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    text = result.categoryTitle,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Circular Score Display
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .clip(CircleShape)
                        .background(
                            if (isHighPerformance) CorrectGreen.copy(alpha = 0.15f)
                            else PrimaryPurple.copy(alpha = 0.15f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$displayScore/${result.totalQuestions}",
                            fontSize = 36.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isHighPerformance) CorrectGreen else PrimaryPurple
                        )
                        Text(
                            text = "$percentage% Score",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Rewards Earned Cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(PrimaryPurple.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "XP",
                                    tint = PrimaryPurple,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(text = "+${result.xpEarned} XP", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text(text = "Earned", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }

                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(CoinYellow.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MonetizationOn,
                                    contentDescription = "Coins",
                                    tint = CoinYellow,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(text = "+${result.coinsEarned}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text(text = "Coins", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Non-blocking optional Rewarded Ad card to Double XP
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SecondaryTeal.copy(alpha = 0.15f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Surface(
                                color = Color(0xFFF1C40F),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "REWARDED AD",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Double Your XP Reward",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "Watch a video ad to earn +${result.xpEarned} extra XP!",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Button(
                            onClick = { showDoubleXpRewardedModal = true },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple)
                        ) {
                            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "2x XP", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Action Buttons Stack
                Button(
                    onClick = onPlayAgain,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("play_again_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple)
                ) {
                    Icon(imageVector = Icons.Default.Replay, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "PLAY AGAIN", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "I scored ${result.score}/${result.totalQuestions} in the ${result.categoryTitle} quiz on Trivia Quiz! Can you beat my score?"
                            )
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share Quiz Score"))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("share_score_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "SHARE SCORE", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = onBackHome,
                    modifier = Modifier.testTag("back_to_categories_button")
                ) {
                    Text(
                        text = "Back to Categories",
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

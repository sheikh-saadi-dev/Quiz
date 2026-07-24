package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.PrimaryPurple
import com.example.ui.theme.SecondaryTeal

private data class OnboardingSlide(
    val title: String,
    val description: String,
    val imageRes: Int
)

@Composable
fun OnboardingScreen(
    onFinishOnboarding: (String) -> Unit
) {
    val slides = listOf(
        OnboardingSlide(
            title = "Test Your Knowledge",
            description = "Explore hundreds of curated trivia questions across US history, science, pop culture, and sports.",
            imageRes = R.drawable.img_onboarding_quiz_1784836966130
        ),
        OnboardingSlide(
            title = "Earn XP & Build Streaks",
            description = "Maintain daily quiz streaks, collect rare trophy badges, and double your XP rewards.",
            imageRes = R.drawable.img_app_icon_1784836942041
        ),
        OnboardingSlide(
            title = "Climb Global Leaderboards",
            description = "Compete against general trivia lovers across the USA and prove who reigns supreme!",
            imageRes = R.drawable.img_featured_quiz_1784836954126
        )
    )

    var currentSlideIndex by remember { mutableIntStateOf(0) }
    var playerNameInput by remember { mutableStateOf("") }
    val isLastSlide = currentSlideIndex == slides.size - 1
    val currentSlide = slides[currentSlideIndex]

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp)
            .testTag("onboarding_screen")
    ) {
        // Skip Button
        TextButton(
            onClick = { onFinishOnboarding(if (playerNameInput.isNotBlank()) playerNameInput else "Trivia Master") },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .testTag("skip_onboarding_button")
        ) {
            Text(
                text = "Skip",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
        }

        // Main Slide Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 48.dp, bottom = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = currentSlide.imageRes),
                contentDescription = currentSlide.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(200.dp)
                    .clip(RoundedCornerShape(24.dp))
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = currentSlide.title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = currentSlide.description,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Player Name Input field always visible
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                OutlinedTextField(
                    value = playerNameInput,
                    onValueChange = { playerNameInput = it },
                    label = { Text("What is your name?") },
                    placeholder = { Text("e.g. Alex_Trivia") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = PrimaryPurple
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryPurple,
                        focusedLabelColor = PrimaryPurple
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("player_name_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = SecondaryTeal.copy(alpha = 0.12f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.TableChart,
                            contentDescription = null,
                            tint = SecondaryTeal,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Your name and quiz scores will be automatically saved in Google Sheet and ranked on the Leaderboard!",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Indicator Dots
            Row(horizontalArrangement = Arrangement.Center) {
                slides.forEachIndexed { index, _ ->
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .height(8.dp)
                            .width(if (index == currentSlideIndex) 24.dp else 8.dp)
                            .clip(CircleShape)
                            .background(
                                if (index == currentSlideIndex) PrimaryPurple else Color.LightGray
                            )
                    )
                }
            }
        }

        // Bottom Action Button
        Button(
            onClick = {
                if (currentSlideIndex < slides.size - 1) {
                    currentSlideIndex++
                } else {
                    val nameToUse = if (playerNameInput.isNotBlank()) playerNameInput.trim() else "Trivia Master"
                    onFinishOnboarding(nameToUse)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .align(Alignment.BottomCenter)
                .testTag("next_onboarding_button"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple)
        ) {
            Text(
                text = if (isLastSlide) "START PLAYING" else "NEXT",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null
            )
        }
    }
}

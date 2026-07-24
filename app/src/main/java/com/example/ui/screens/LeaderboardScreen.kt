package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.admob.AdMobBannerView
import com.example.admob.AdMobNativeCard
import com.example.data.model.LeaderboardItem
import com.example.ui.theme.CoinYellow
import com.example.ui.theme.PrimaryPurple
import com.example.ui.theme.SecondaryTeal

@Composable
fun LeaderboardScreen(
    leaderboardType: String,
    leaderboardItems: List<LeaderboardItem>,
    isAdsRemoved: Boolean,
    onTypeSelected: (String) -> Unit
) {
    Scaffold(
        bottomBar = {
            AdMobBannerView(isAdsRemoved = isAdsRemoved)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .testTag("leaderboard_screen")
        ) {
            // Screen Title
            Text(
                text = "Leaderboard",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
            )

            // Segmented Control Tabs (Global / Friends / This Week)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(4.dp)
            ) {
                val tabs = listOf(
                    "global" to "Global",
                    "this_week" to "This Week",
                    "friends" to "Friends"
                )

                tabs.forEach { (typeKey, label) ->
                    val isSelected = leaderboardType == typeKey
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onTypeSelected(typeKey) }
                            .testTag("leaderboard_tab_$typeKey"),
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) PrimaryPurple else Color.Transparent
                    ) {
                        Box(
                            modifier = Modifier.padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Leaderboard List with Native Ad after every 6th entry
            val sortedItems = leaderboardItems.sortedByDescending { it.score }

            LazyColumn(
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                itemsIndexed(sortedItems) { index, item ->
                    // Check if we should insert Native Ad (every 6th real row)
                    if (index > 0 && index % 6 == 0 && !isAdsRemoved) {
                        AdMobNativeCard()
                    }

                    LeaderboardRowCard(item = item, displayRank = index + 1)
                }
            }
        }
    }
}

@Composable
private fun LeaderboardRowCard(item: LeaderboardItem, displayRank: Int) {
    val isTop3 = displayRank <= 3
    val rankBadgeColor = when (displayRank) {
        1 -> Color(0xFFFFD15C) // Gold
        2 -> Color(0xFFC0C0C0) // Silver
        3 -> Color(0xFFCD7F32) // Bronze
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("leaderboard_row_${item.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (item.username.contains("(You)")) PrimaryPurple.copy(alpha = 0.12f)
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Rank Badge
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(rankBadgeColor),
                    contentAlignment = Alignment.Center
                ) {
                    if (isTop3) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = "Rank",
                            tint = if (displayRank == 1) Color.Black else Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    } else {
                        Text(
                            text = "$displayRank",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Avatar
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(SecondaryTeal.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Avatar",
                        tint = SecondaryTeal,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Username
                Text(
                    text = item.username,
                    fontSize = 15.sp,
                    fontWeight = if (item.username.contains("(You)")) FontWeight.ExtraBold else FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Score Badge
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "XP",
                    tint = PrimaryPurple,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${item.score} XP",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryPurple
                )
            }
        }
    }
}

package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.admob.AdMobBannerView
import com.example.data.model.Badge
import com.example.data.model.UserStats
import com.example.ui.theme.CoinYellow
import com.example.ui.theme.CorrectGreen
import com.example.ui.theme.PrimaryPurple
import com.example.ui.theme.SecondaryTeal
import com.example.ui.theme.StreakOrange

import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults

@Composable
fun ProfileScreen(
    userStats: UserStats?,
    badges: List<Badge>,
    onToggleDarkMode: (Boolean) -> Unit,
    onToggleSound: (Boolean) -> Unit,
    onToggleHaptics: (Boolean) -> Unit,
    onRemoveAds: () -> Unit,
    onUpdateName: (String) -> Unit = {}
) {
    var showAboutDialog by remember { mutableStateOf(false) }
    var showEditNameDialog by remember { mutableStateOf(false) }
    var editedName by remember { mutableStateOf(userStats?.username ?: "") }

    if (showEditNameDialog) {
        Dialog(onDismissRequest = { showEditNameDialog = false }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Edit Player Name",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = editedName,
                        onValueChange = { editedName = it },
                        label = { Text("Name") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("edit_name_input")
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = { showEditNameDialog = false },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray.copy(alpha = 0.2f))
                        ) {
                            Text("Cancel", color = MaterialTheme.colorScheme.onSurface)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (editedName.isNotBlank()) {
                                    onUpdateName(editedName.trim())
                                }
                                showEditNameDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple)
                        ) {
                            Text("Save & Sync")
                        }
                    }
                }
            }
        }
    }

    if (showAboutDialog) {
        Dialog(onDismissRequest = { showAboutDialog = false }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = PrimaryPurple,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "About Trivia Quiz",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Version 1.0.0\nBuilt for USA trivia and general knowledge enthusiasts. AdMob compliance verified.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = { showAboutDialog = false },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple)
                    ) {
                        Text(text = "Close")
                    }
                }
            }
        }
    }

    Scaffold(
        bottomBar = {
            AdMobBannerView(isAdsRemoved = userStats?.isAdsRemoved ?: false)
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .testTag("profile_screen"),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // Profile Header Card
            item {
                ProfileHeaderCard(
                    userStats = userStats,
                    onEditName = { showEditNameDialog = true }
                )
            }

            // Badges Grid Section
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Trophy Badges",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )

                Column(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    badges.forEach { badge ->
                        BadgeRowCard(badge = badge)
                    }
                }
            }

            // Settings Section
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Settings & Preferences",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        // Dark Theme Toggle
                        SettingToggleRow(
                            icon = Icons.Default.DarkMode,
                            title = "Dark Theme",
                            checked = userStats?.darkThemeEnabled ?: false,
                            onCheckedChange = onToggleDarkMode,
                            testTag = "dark_mode_switch"
                        )

                        HorizontalDivider(modifier = Modifier.padding(horizontal = 12.dp), color = MaterialTheme.colorScheme.outlineVariant)

                        // Sound Toggle
                        SettingToggleRow(
                            icon = Icons.Default.VolumeUp,
                            title = "Sound Effects",
                            checked = userStats?.soundEnabled ?: true,
                            onCheckedChange = onToggleSound,
                            testTag = "sound_switch"
                        )

                        HorizontalDivider(modifier = Modifier.padding(horizontal = 12.dp), color = MaterialTheme.colorScheme.outlineVariant)

                        // Haptics Toggle
                        SettingToggleRow(
                            icon = Icons.Default.Vibration,
                            title = "Haptic Vibration",
                            checked = userStats?.hapticsEnabled ?: true,
                            onCheckedChange = onToggleHaptics,
                            testTag = "haptics_switch"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Remove Ads / Premium IAP Button
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (userStats?.isAdsRemoved == true) CorrectGreen.copy(alpha = 0.15f)
                        else PrimaryPurple.copy(alpha = 0.15f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (userStats?.isAdsRemoved == true) "Ads Removed Active" else "Remove All Ads",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                text = if (userStats?.isAdsRemoved == true) "Enjoy ad-free trivia everywhere!" else "One-time purchase for zero ads forever",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        if (userStats?.isAdsRemoved == true) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Active",
                                tint = CorrectGreen,
                                modifier = Modifier.size(28.dp)
                            )
                        } else {
                            Button(
                                onClick = onRemoveAds,
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
                                modifier = Modifier.testTag("remove_ads_button")
                            ) {
                                Text(text = "\$2.99", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // About & Privacy Policy Button
                Surface(
                    onClick = { showAboutDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = PrimaryPurple)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = "About & Privacy Policy", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileHeaderCard(
    userStats: UserStats?,
    onEditName: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(PrimaryPurple.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Avatar",
                    tint = PrimaryPurple,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = userStats?.username ?: "Trivia Master",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(6.dp))
                androidx.compose.material3.IconButton(
                    onClick = onEditName,
                    modifier = Modifier.size(24.dp).testTag("edit_name_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Name",
                        tint = PrimaryPurple,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Surface(
                color = SecondaryTeal.copy(alpha = 0.15f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.TableChart,
                        contentDescription = null,
                        tint = SecondaryTeal,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Synced with Google Sheet",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SecondaryTeal
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Level ${((userStats?.xp ?: 0) / 500) + 1} Trivia Master",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Stats Summary Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "${userStats?.xp ?: 0}", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = PrimaryPurple)
                    Text(text = "Total XP", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "${userStats?.streakDays ?: 1}", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = StreakOrange)
                    Text(text = "Streak Days", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "${userStats?.coins ?: 0}", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = CoinYellow)
                    Text(text = "Coins", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
private fun BadgeRowCard(badge: Badge) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("badge_${badge.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (badge.isUnlocked) MaterialTheme.colorScheme.surface
            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        if (badge.isUnlocked) PrimaryPurple.copy(alpha = 0.2f)
                        else Color.Gray.copy(alpha = 0.2f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getBadgeIcon(badge.iconName),
                    contentDescription = badge.title,
                    tint = if (badge.isUnlocked) PrimaryPurple else Color.Gray,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = badge.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (badge.isUnlocked) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
                Text(
                    text = badge.description,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (badge.isUnlocked) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Unlocked",
                    tint = CorrectGreen,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun SettingToggleRow(
    icon: ImageVector,
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = title, tint = PrimaryPurple)
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.testTag(testTag),
            colors = SwitchDefaults.colors(checkedThumbColor = PrimaryPurple)
        )
    }
}

fun getBadgeIcon(iconName: String): ImageVector {
    return when (iconName) {
        "emoji_events" -> Icons.Default.EmojiEvents
        "local_fire_department" -> Icons.Default.LocalFireDepartment
        "star" -> Icons.Default.Star
        "bolt" -> Icons.Default.Bolt
        "history_edu" -> Icons.Default.HistoryEdu
        else -> Icons.Default.MilitaryTech
    }
}

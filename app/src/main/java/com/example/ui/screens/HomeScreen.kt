package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Rocket
import androidx.compose.material.icons.filled.SportsBasketball
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.admob.AdMobBannerView
import com.example.data.model.Category
import com.example.data.model.UserStats
import com.example.ui.components.StreakBounce
import com.example.ui.theme.CoinYellow
import com.example.ui.theme.PrimaryPurple
import com.example.ui.theme.SecondaryTeal

@Composable
fun HomeScreen(
    userStats: UserStats?,
    categories: List<Category>,
    featuredCategories: List<Category>,
    onCategorySelected: (Category) -> Unit,
    onNavigateProfile: () -> Unit
) {
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
                .testTag("home_screen"),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // Header Section
            item {
                HomeHeader(
                    userStats = userStats,
                    onAvatarClick = onNavigateProfile
                )
            }

            // Featured Categories Section
            item {
                Text(
                    text = "Featured Quizzes",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
                )

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(featuredCategories) { category ->
                        FeaturedCategoryCard(
                            category = category,
                            onClick = { onCategorySelected(category) }
                        )
                    }
                }
            }

            // All Categories Header
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "All Categories",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )
            }

            // All Categories 2-Column Grid Simulation in LazyColumn
            items(categories.chunked(2)) { rowCategories ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowCategories.forEach { category ->
                        CategoryGridCard(
                            category = category,
                            onClick = { onCategorySelected(category) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (rowCategories.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun HomeHeader(
    userStats: UserStats?,
    onAvatarClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { onAvatarClick() }
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(PrimaryPurple.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "User Avatar",
                    tint = PrimaryPurple,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = "Welcome back!",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
                Text(
                    text = userStats?.username ?: "Trivia Explorer",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            StreakBounce(streakDays = userStats?.streakDays ?: 1)

            Spacer(modifier = Modifier.width(10.dp))

            Surface(
                color = CoinYellow.copy(alpha = 0.2f),
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.MonetizationOn,
                        contentDescription = "Coins",
                        tint = CoinYellow,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${userStats?.coins ?: 0}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = CoinYellow
                    )
                }
            }
        }
    }
}

@Composable
fun FeaturedCategoryCard(
    category: Category,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(260.dp)
            .height(140.dp)
            .clickable { onClick() }
            .testTag("featured_category_${category.id}"),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = R.drawable.img_featured_quiz_1784836954126),
                contentDescription = category.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Text(
                    text = category.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = category.description,
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.8f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun CategoryGridCard(
    category: Category,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progress = (category.completedCount.toFloat() / category.totalQuestions.toFloat()).coerceIn(0f, 1f)

    Card(
        modifier = modifier
            .clickable { onClick() }
            .testTag("category_card_${category.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(PrimaryPurple.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getCategoryIcon(category.iconName),
                    contentDescription = category.title,
                    tint = PrimaryPurple,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = category.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${category.completedCount}/${category.totalQuestions} Completed",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                color = SecondaryTeal,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }
    }
}

fun getCategoryIcon(iconName: String): ImageVector {
    return when (iconName) {
        "flag" -> Icons.Default.Flag
        "rocket" -> Icons.Default.Rocket
        "movie" -> Icons.Default.Movie
        "map" -> Icons.Default.Map
        "sports" -> Icons.Default.SportsBasketball
        "restaurant" -> Icons.Default.Restaurant
        "book" -> Icons.Default.Book
        else -> Icons.Default.Lightbulb
    }
}

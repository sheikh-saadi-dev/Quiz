package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.Badge
import com.example.data.model.Category
import com.example.data.model.Converters
import com.example.data.model.LeaderboardItem
import com.example.data.model.Question
import com.example.data.model.QuizResult
import com.example.data.model.UserStats
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Category::class,
        Question::class,
        UserStats::class,
        QuizResult::class,
        Badge::class,
        LeaderboardItem::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class TriviaDatabase : RoomDatabase() {

    abstract fun triviaDao(): TriviaDao

    companion object {
        @Volatile
        private var INSTANCE: TriviaDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): TriviaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TriviaDatabase::class.java,
                    "trivia_quiz_database"
                )
                    .addCallback(TriviaDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class TriviaDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateInitialData(database.triviaDao())
                }
            }
        }

        private suspend fun populateInitialData(dao: TriviaDao) {
            // Initial User Stats
            dao.insertOrUpdateUserStats(
                UserStats(
                    id = 1,
                    username = "Trivia Master",
                    coins = 200,
                    xp = 1850,
                    streakDays = 5,
                    lives = 3,
                    maxLives = 3,
                    isAdsRemoved = false,
                    darkThemeEnabled = false,
                    soundEnabled = true,
                    hapticsEnabled = true,
                    isOnboardingCompleted = false
                )
            )

            // Initial Categories
            val categories = listOf(
                Category("us_history", "US History", "Presidents, founding era, and historic USA events", "flag", "#6C5CE7", 10, 4, isFeatured = true),
                Category("science_tech", "Science & Tech", "Space, computing, inventions, and physics", "rocket", "#00D2D3", 10, 6, isFeatured = true),
                Category("pop_culture", "Pop Culture", "Movies, music hits, celebrities, and television", "movie", "#FF7675", 10, 2, isFeatured = true),
                Category("geography", "Geography", "US states, world capitals, and natural landmarks", "map", "#00B894", 10, 5, isFeatured = false),
                Category("sports", "Sports & Fitness", "Super Bowls, NBA legends, and Olympic records", "sports", "#FDCB6E", 10, 3, isFeatured = false),
                Category("food_dining", "Food & Dining", "Cuisines, culinary history, and iconic dishes", "restaurant", "#E17055", 10, 1, isFeatured = false),
                Category("literature", "Arts & Literature", "Famous authors, classic novels, and mythology", "book", "#A29BFE", 10, 0, isFeatured = false),
                Category("general_knowledge", "General Knowledge", "Fun everyday facts and mind-bending trivia", "lightbulb", "#FD79A8", 10, 7, isFeatured = true)
            )
            dao.insertCategories(categories)

            // Initial Badges
            val badges = listOf(
                Badge("b1", "Quiz Beginner", "Complete your first trivia quiz", "emoji_events", isUnlocked = true, unlockedAt = System.currentTimeMillis() - 86400000),
                Badge("b2", "Streak Master", "Maintain a 5-day quiz streak", "local_fire_department", isUnlocked = true, unlockedAt = System.currentTimeMillis()),
                Badge("b3", "Perfect 10", "Score 100% on any 10-question quiz", "star", isUnlocked = false),
                Badge("b4", "Speed Demon", "Answer a question in under 3 seconds", "bolt", isUnlocked = false),
                Badge("b5", "History Buff", "Score 80%+ in 3 US History quizzes", "history_edu", isUnlocked = false),
                Badge("b6", "Trivia Master", "Earn 2,000 Total XP", "military_tech", isUnlocked = false)
            )
            dao.insertBadges(badges)

            // Initial Leaderboards
            val leaderboards = mutableListOf<LeaderboardItem>()
            val globalUsers = listOf(
                Triple("1", "Sarah_US", 9850),
                Triple("2", "Alex_Trivia", 9420),
                Triple("3", "Marcus_99", 8900),
                Triple("4", "Chloe_W", 8450),
                Triple("5", "Dave_Quiz", 8100),
                Triple("6", "Trivia Master (You)", 1850),
                Triple("7", "Emily_R", 1600),
                Triple("8", "Jason_K", 1450),
                Triple("9", "Jessica_M", 1300),
                Triple("10", "Brian_P", 1100)
            )
            globalUsers.forEachIndexed { idx, item ->
                leaderboards.add(LeaderboardItem("g_${item.first}", idx + 1, item.second, "avatar_${(idx % 4) + 1}", item.third, "global"))
            }

            val weekUsers = listOf(
                Triple("1", "Alex_Trivia", 2400),
                Triple("2", "Trivia Master (You)", 1850),
                Triple("3", "Sarah_US", 1750),
                Triple("4", "Marcus_99", 1500),
                Triple("5", "Chloe_W", 1200),
                Triple("6", "Jessica_M", 950),
                Triple("7", "Dave_Quiz", 800)
            )
            weekUsers.forEachIndexed { idx, item ->
                leaderboards.add(LeaderboardItem("w_${item.first}", idx + 1, item.second, "avatar_${(idx % 4) + 1}", item.third, "this_week"))
            }

            val friendUsers = listOf(
                Triple("1", "Alex_Trivia", 9420),
                Triple("2", "Trivia Master (You)", 1850),
                Triple("3", "Chloe_W", 8450),
                Triple("4", "Dave_Quiz", 8100)
            )
            friendUsers.forEachIndexed { idx, item ->
                leaderboards.add(LeaderboardItem("f_${item.first}", idx + 1, item.second, "avatar_${(idx % 4) + 1}", item.third, "friends"))
            }
            dao.insertLeaderboardItems(leaderboards)

            // Initial Questions Set
            val questions = mutableListOf<Question>()

            // US History Questions
            questions.addAll(
                listOf(
                    Question(
                        "q_ush_1", "us_history", "easy",
                        "Who was the 16th President of the United States?",
                        listOf("Abraham Lincoln", "George Washington", "Thomas Jefferson", "James Madison"),
                        0, "Abraham Lincoln served as the 16th President from 1861 until his assassination in 1865."
                    ),
                    Question(
                        "q_ush_2", "us_history", "medium",
                        "In which year was the US Declaration of Independence adopted?",
                        listOf("1776", "1789", "1812", "1765"),
                        0, "The Declaration of Independence was officially adopted on July 4, 1776."
                    ),
                    Question(
                        "q_ush_3", "us_history", "hard",
                        "Which constitutional amendment granted women the right to vote in the US?",
                        listOf("19th Amendment", "15th Amendment", "21st Amendment", "14th Amendment"),
                        0, "The 19th Amendment was ratified in 1920, guaranteeing women the right to vote."
                    ),
                    Question(
                        "q_ush_4", "us_history", "easy",
                        "What is the capital of the United States?",
                        listOf("Washington, D.C.", "New York City", "Philadelphia", "Boston"),
                        0, "Washington, D.C. became the permanent capital of the United States in 1800."
                    ),
                    Question(
                        "q_ush_5", "us_history", "medium",
                        "Which US state was bought from Russia in 1867?",
                        listOf("Alaska", "Hawaii", "California", "Oregon"),
                        0, "The US purchased Alaska from Russia in 1867 for $7.2 million."
                    ),
                    Question(
                        "q_ush_6", "us_history", "easy",
                        "Who was the first President of the United States?",
                        listOf("George Washington", "John Adams", "Alexander Hamilton", "Benjamin Franklin"),
                        0, "George Washington served as the first US President from 1789 to 1797."
                    ),
                    Question(
                        "q_ush_7", "us_history", "medium",
                        "Which ocean borders the West Coast of the United States?",
                        listOf("Pacific Ocean", "Atlantic Ocean", "Indian Ocean", "Arctic Ocean"),
                        0, "The Pacific Ocean borders the West Coast of the US, including California, Oregon, and Washington."
                    ),
                    Question(
                        "q_ush_8", "us_history", "hard",
                        "Who was the main author of the United States Declaration of Independence?",
                        listOf("Thomas Jefferson", "Benjamin Franklin", "John Adams", "James Madison"),
                        0, "Thomas Jefferson draft the Declaration of Independence in June 1776."
                    ),
                    Question(
                        "q_ush_9", "us_history", "easy",
                        "How many stars are on the current United States flag?",
                        listOf("50", "48", "52", "13"),
                        0, "The 50 stars represent the 50 states of the USA."
                    ),
                    Question(
                        "q_ush_10", "us_history", "medium",
                        "Which famous speech began with 'Four score and seven years ago'?",
                        listOf("Gettysburg Address", "I Have a Dream", "Inaugural Address", "Farewell Address"),
                        0, "Abraham Lincoln delivered the Gettysburg Address during the American Civil War in 1863."
                    )
                )
            )

            // Science & Tech Questions
            questions.addAll(
                listOf(
                    Question(
                        "q_st_1", "science_tech", "easy",
                        "What planet in our solar system is known as the Red Planet?",
                        listOf("Mars", "Venus", "Jupiter", "Saturn"),
                        0, "Mars gets its reddish color from iron oxide (rust) on its surface."
                    ),
                    Question(
                        "q_st_2", "science_tech", "medium",
                        "What element has the chemical symbol 'Au'?",
                        listOf("Gold", "Silver", "Copper", "Aluminum"),
                        0, "Au comes from 'Aurum', the Latin word for Gold."
                    ),
                    Question(
                        "q_st_3", "science_tech", "hard",
                        "Which company introduced the first commercial microprocessor, the 4004, in 1971?",
                        listOf("Intel", "IBM", "AMD", "Apple"),
                        0, "Intel released the 4004, a 4-bit central processing unit."
                    ),
                    Question(
                        "q_st_4", "science_tech", "easy",
                        "What is the hardest natural substance on Earth?",
                        listOf("Diamond", "Quartz", "Granite", "Titanium"),
                        0, "Diamond is a solid form of carbon with a 10 on the Mohs hardness scale."
                    ),
                    Question(
                        "q_st_5", "science_tech", "medium",
                        "What unit measures electric resistance?",
                        listOf("Ohm", "Watt", "Volt", "Ampere"),
                        0, "The Ohm is named after German physicist Georg Simon Ohm."
                    ),
                    Question(
                        "q_st_6", "science_tech", "easy",
                        "What is the main gas that makes up the Earth's atmosphere?",
                        listOf("Nitrogen", "Oxygen", "Carbon Dioxide", "Hydrogen"),
                        0, "Nitrogen makes up roughly 78% of Earth's atmosphere."
                    ),
                    Question(
                        "q_st_7", "science_tech", "medium",
                        "Which organ in the human body consumes the most energy?",
                        listOf("Brain", "Heart", "Liver", "Kidney"),
                        0, "The brain consumes around 20% of the body's energy despite being only 2% of total weight."
                    ),
                    Question(
                        "q_st_8", "science_tech", "hard",
                        "In computer science, what does 'HTTP' stand for?",
                        listOf("Hypertext Transfer Protocol", "High Transfer Tech Program", "Hyperlink Text Process", "Home Tool Transfer System"),
                        0, "HTTP stands for Hypertext Transfer Protocol."
                    ),
                    Question(
                        "q_st_9", "science_tech", "easy",
                        "How many bones are in the adult human body?",
                        listOf("206", "180", "250", "300"),
                        0, "Adult humans have 206 bones in their skeleton."
                    ),
                    Question(
                        "q_st_10", "science_tech", "medium",
                        "What is the speed of light in a vacuum approximately?",
                        listOf("300,000 km/s", "150,000 km/s", "1,000,000 km/s", "50,000 km/s"),
                        0, "The speed of light in a vacuum is approximately 299,792 kilometers per second."
                    )
                )
            )

            // Pop Culture Questions
            questions.addAll(
                listOf(
                    Question(
                        "q_pop_1", "pop_culture", "easy",
                        "Who played the character of Tony Stark / Iron Man in the Marvel Cinematic Universe?",
                        listOf("Robert Downey Jr.", "Chris Evans", "Chris Hemsworth", "Mark Ruffalo"),
                        0, "Robert Downey Jr. inaugurated the MCU in 2008 as Iron Man."
                    ),
                    Question(
                        "q_pop_2", "pop_culture", "medium",
                        "Which animated movie features the hit song 'Let It Go'?",
                        listOf("Frozen", "Moana", "Tangled", "Brave"),
                        0, "Frozen (2013) features 'Let It Go', sung by Idina Menzel."
                    ),
                    Question(
                        "q_pop_3", "pop_culture", "hard",
                        "Which band released the iconic 1975 song 'Bohemian Rhapsody'?",
                        listOf("Queen", "The Beatles", "Led Zeppelin", "Pink Floyd"),
                        0, "Queen released 'Bohemian Rhapsody' on their album A Night at the Opera."
                    ),
                    Question(
                        "q_pop_4", "pop_culture", "easy",
                        "What fictional world is the setting for The Lord of the Rings?",
                        listOf("Middle-earth", "Narnia", "Westeros", "Hogwarts"),
                        0, "J.R.R. Tolkien set his epic Legendarium in Middle-earth."
                    ),
                    Question(
                        "q_pop_5", "pop_culture", "medium",
                        "In Friends, what is the name of Joey's bedtime penguin toy?",
                        listOf("Hugsy", "Pingu", "Waddles", "Snowball"),
                        0, "Joey's beloved bedtime penguin pal is named Hugsy."
                    ),
                    Question(
                        "q_pop_6", "pop_culture", "easy",
                        "Which artist sang 'Thriller' and 'Billie Jean'?",
                        listOf("Michael Jackson", "Prince", "Stevie Wonder", "Bruno Mars"),
                        0, "Michael Jackson released 'Thriller' in 1982."
                    ),
                    Question(
                        "q_pop_7", "pop_culture", "medium",
                        "What is the name of the wizarding school attended by Harry Potter?",
                        listOf("Hogwarts", "Ilvermorny", "Beauxbatons", "Durmstrang"),
                        0, "Harry Potter attends Hogwarts School of Witchcraft and Wizardry."
                    ),
                    Question(
                        "q_pop_8", "pop_culture", "hard",
                        "Which director made Jurassic Park, Jaws, and E.T.?",
                        listOf("Steven Spielberg", "George Lucas", "James Cameron", "Christopher Nolan"),
                        0, "Steven Spielberg directed all three classic cinematic blockbusters."
                    ),
                    Question(
                        "q_pop_9", "pop_culture", "easy",
                        "What color are the Simpson family characters?",
                        listOf("Yellow", "Blue", "Green", "Pink"),
                        0, "Matt Groening chose yellow to make the Simpsons stand out on television."
                    ),
                    Question(
                        "q_pop_10", "pop_culture", "medium",
                        "Which video game franchise features a plumber named Mario?",
                        listOf("Super Mario", "Sonic the Hedgehog", "Zelda", "Pokémon"),
                        0, "Nintendo created Mario in 1981."
                    )
                )
            )

            // General Knowledge Questions
            questions.addAll(
                listOf(
                    Question(
                        "q_gk_1", "general_knowledge", "easy",
                        "How many days are there in a standard leap year?",
                        listOf("366", "365", "364", "360"),
                        0, "A leap year adds February 29th, giving a total of 366 days."
                    ),
                    Question(
                        "q_gk_2", "general_knowledge", "medium",
                        "Which continent is the largest by land area?",
                        listOf("Asia", "Africa", "North America", "Europe"),
                        0, "Asia covers roughly 44.5 million square kilometers."
                    ),
                    Question(
                        "q_gk_3", "general_knowledge", "hard",
                        "What is the official national currency of Japan?",
                        listOf("Yen", "Won", "Yuan", "Ringgit"),
                        0, "The Japanese Yen (¥) is the currency of Japan."
                    ),
                    Question(
                        "q_gk_4", "general_knowledge", "easy",
                        "What color is created when you mix Blue and Yellow paint?",
                        listOf("Green", "Purple", "Orange", "Brown"),
                        0, "Blue and yellow combine to produce green."
                    ),
                    Question(
                        "q_gk_5", "general_knowledge", "medium",
                        "Which instrument has 88 keys?",
                        listOf("Piano", "Organ", "Accordion", "Harpsichord"),
                        0, "A standard modern piano features 88 keys (52 white, 36 black)."
                    ),
                    Question(
                        "q_gk_6", "general_knowledge", "easy",
                        "What is the freezing point of water in Fahrenheit?",
                        listOf("32°F", "0°F", "100°F", "212°F"),
                        0, "Water freezes at 32 degrees Fahrenheit (0°C)."
                    ),
                    Question(
                        "q_gk_7", "general_knowledge", "medium",
                        "How many sides does a hexagon have?",
                        listOf("6", "5", "8", "7"),
                        0, "A hexagon is a polygon with 6 sides and 6 angles."
                    ),
                    Question(
                        "q_gk_8", "general_knowledge", "hard",
                        "Which company manufactures the iPhone?",
                        listOf("Apple", "Samsung", "Google", "Microsoft"),
                        0, "Apple Inc. introduced the first iPhone in 2007."
                    ),
                    Question(
                        "q_gk_9", "general_knowledge", "easy",
                        "What primary color is a stop sign?",
                        listOf("Red", "Yellow", "Blue", "Green"),
                        0, "Octagonal stop signs are red with white letters."
                    ),
                    Question(
                        "q_gk_10", "general_knowledge", "medium",
                        "Which mammal is famous for laying eggs?",
                        listOf("Platypus", "Kangaroo", "Dolphin", "Koala"),
                        0, "The platypus and echidna are monotremes—egg-laying mammals."
                    )
                )
            )

            dao.insertQuestions(questions)
        }
    }
}

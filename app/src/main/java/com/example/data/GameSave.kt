package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class MovieGenre(val displayName: String, val typicalBudget: Double, val fanAppeal: Double, val vfxNeed: Double, val scriptNeed: Double) {
    ACTION("Action Blockbuster", 8_000_000.0, 0.9, 0.8, 0.4),
    COMEDY("Indie Comedy", 1_500_000.0, 0.7, 0.1, 0.8),
    DRAMA("Oscar Drama", 2_500_000.0, 0.5, 0.1, 0.95),
    SCIFI("Sci-Fi Odyssey", 12_000_000.0, 0.95, 0.95, 0.7),
    HORROR("Cult Horror", 1_000_000.0, 0.8, 0.3, 0.5),
    ROMANCE("Romantic Comedy", 2_000_000.0, 0.6, 0.1, 0.75),
    THRILLER("Suspense Thriller", 3_000_000.0, 0.75, 0.2, 0.85)
}

enum class ActiveProjectStage {
    NONE,
    WRITING,
    PRE_PRODUCTION, // Casting and budgeting
    SHOOTING,
    POST_PRODUCTION,
    REVIEWS,
    RELEASE_RUN
}

data class Screenwriter(
    val id: String,
    val name: String,
    val skill: Float, // 0.1 to 1.0
    val cost: Double,
    val speed: Float, // 1.0 (normal) to 2.0 (fast)
    val favoriteGenre: MovieGenre,
    val bio: String
) {
    companion object {
        val ALL = listOf(
            Screenwriter("w1", "Chuck Palahni-fake", 0.65f, 150_000.0, 1.2f, MovieGenre.THRILLER, "Writes dark psychological thrillers with twists."),
            Screenwriter("w2", "George R.R. Delay", 0.85f, 450_000.0, 0.5f, MovieGenre.DRAMA, "Brilliant writing, but notoriously slow."),
            Screenwriter("w3", "Nora Ephr-off", 0.70f, 200_000.0, 1.4f, MovieGenre.ROMANCE, "Charming dialogues, rapid writing speed."),
            Screenwriter("w4", "Quentin Tarantin-no", 0.95f, 900_000.0, 0.9f, MovieGenre.ACTION, "Master dialogist. Extremely expensive."),
            Screenwriter("w5", "Indie McScreen", 0.40f, 40_000.0, 1.6f, MovieGenre.COMEDY, "Fresh out of film school. Cheap but erratic.")
        )
    }
}

data class Director(
    val id: String,
    val name: String,
    val skill: Float, // 0.1 to 1.0
    val cost: Double,
    val style: String,
    val favoriteGenre: MovieGenre
) {
    companion object {
        val ALL = listOf(
            Director("d1", "Steven Spielbug", 0.95f, 1_200_000.0, "Visionary & Heartfelt", MovieGenre.SCIFI),
            Director("d2", "Greta Gerwig-out", 0.88f, 750_000.0, "Artistic & Character-focused", MovieGenre.DRAMA),
            Director("d3", "Michael Blay", 0.60f, 600_000.0, "High Explosions & Fast Editing", MovieGenre.ACTION),
            Director("d4", "Alfred Hitch-cough", 0.90f, 950_000.0, "Nail-biting Suspense", MovieGenre.THRILLER),
            Director("d5", "Wes Anders-off", 0.80f, 500_000.0, "Strictly Symmetrical & Quirky", MovieGenre.COMEDY),
            Director("d6", "B-Movie Bill", 0.35f, 80_000.0, "Cheap, Fast & Cheesy", MovieGenre.HORROR)
        )
    }
}

data class Actor(
    val id: String,
    val name: String,
    val skill: Float, // 0.1 to 1.0
    val starPower: Float, // 0.1 to 1.0 (hype / box office multiplier)
    val cost: Double,
    val favoriteGenre: MovieGenre,
    val roleType: String
) {
    companion object {
        val ALL = listOf(
            Actor("a1", "Leonardo DiCaprio-car", 0.98f, 0.95f, 2_500_000.0, MovieGenre.DRAMA, "Intense Drama Lead"),
            Actor("a2", "Tom Croos-control", 0.80f, 0.98f, 3_000_000.0, MovieGenre.ACTION, "Stunt Master Action Icon"),
            Actor("a3", "Scarlett Johansen-sen", 0.88f, 0.88f, 1_800_000.0, MovieGenre.SCIFI, "Versatile Leading Actress"),
            Actor("a4", "Adam Sand-castle", 0.55f, 0.82f, 1_200_000.0, MovieGenre.COMEDY, "High-Appeal Comedian"),
            Actor("a5", "Florence P-ew", 0.92f, 0.70f, 850_000.0, MovieGenre.THRILLER, "Acclaimed Rising Star"),
            Actor("a6", "Bruce Camp-bell", 0.50f, 0.45f, 120_000.0, MovieGenre.HORROR, "Cult-horror B-movie Legend"),
            Actor("a7", "Extras Ethan", 0.20f, 0.10f, 15_000.0, MovieGenre.DRAMA, "Undiscovered Talent / Extra")
        )
    }
}

@Entity(tableName = "studio_save")
data class StudioSave(
    @PrimaryKey val id: Int = 1,
    val name: String,
    val money: Double,
    val fans: Int,
    val reputation: Float, // 0 to 1
    val completedMoviesCount: Int = 0,
    val totalEarnings: Double = 0.0,
    val timelineWeek: Int = 1,
    // Active project state fully saved inside the entity as JSON or split properties
    val currentStage: ActiveProjectStage = ActiveProjectStage.NONE,
    
    // Draft / Script stage
    val activeTitle: String = "",
    val activeGenre: MovieGenre = MovieGenre.DRAMA,
    val activeWriterId: String = "",
    val scriptWriteProgress: Float = 0f,
    val screenplayQuality: Float = 0f, // 0 to 100
    
    // Casting and pre-production selections
    val activeDirectorId: String = "",
    val activeLeadActorId: String = "",
    val activeSupportingActorId: String = "",
    
    // Budget configurations
    val budgetVfx: Double = 0.0,
    val budgetProduction: Double = 0.0,
    val budgetMarketing: Double = 0.0,
    
    // Shooting progression
    val shootProgressWeek: Int = 0,
    val shootTotalWeeks: Int = 4,
    val accumulatedFilmQuality: Float = 0f, // starts from screenplay quality, modified by shoot events
    val accumulatedHype: Float = 0f, // modified by marketing and events
    
    // Release configuration
    val selectedReleaseTier: String = "Wide", // Wide, Limited, Festival, Streaming
    val selectedReleaseSeason: String = "Summer", // Summer, Holiday, Oscar, Dump
    
    // Current Review and Release details for Active Run
    val finalMovieQuality: Int = 0,
    val finalMovieReviewScore: Int = 0,
    val finalMovieImdb: Float = 5.0f,
    val criticCommentSummary: String = "",
    
    // Box office play details
    val boxOfficeRunWeek: Int = 0,
    val boxOfficeTotalWeeks: Int = 5,
    val boxOfficeWeeklyGrossHistoryJson: String = "[]", // Serialized list of floats
    val boxOfficeRunningCumulative: Double = 0.0,
    val boxOfficeScreens: Int = 2000
)

@Entity(tableName = "released_movies")
data class ReleasedMovie(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val genre: String,
    val directorName: String,
    val leadActorName: String,
    val writerName: String,
    val totalBudget: Double,
    val filmQuality: Int,
    val reviewScore: Int,
    val imdbRating: Float,
    val criticReview: String,
    val finalBoxOffice: Double,
    val totalProfit: Double,
    val releaseSeason: String,
    val awardNominated: Boolean = false,
    val awardWon: Boolean = false,
    val releaseWeekIndex: Int = 0
)

package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

class GameViewModel(application: Application) : AndroidViewModel(application) {
    private val gameDb = GameDatabase.getDatabase(application)
    private val repository = GameRepository(gameDb.gameDao())

    val studioState: StateFlow<StudioSave?> = repository.studioSave
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val releasedMovies: StateFlow<List<ReleasedMovie>> = repository.releasedMovies
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Setup / Reset the game
    fun startNewStudio(studioName: String) {
        viewModelScope.launch {
            repository.restartGame(studioName)
        }
    }

    // Step 1: Start screenplay drafting
    fun selectWriterAndNewMovie(title: String, genre: MovieGenre, writerId: String) {
        val activeSave = studioState.value ?: return
        val writer = Screenwriter.ALL.firstOrNull { it.id == writerId } ?: return
        if (activeSave.money < writer.cost) return // Check budget cover

        viewModelScope.launch {
            val updated = activeSave.copy(
                currentStage = ActiveProjectStage.WRITING,
                activeTitle = title.ifBlank { "Untitled Masterpiece" },
                activeGenre = genre,
                activeWriterId = writerId,
                scriptWriteProgress = 0f,
                money = activeSave.money - writer.cost, // Pay writer immediately
                screenplayQuality = 0f
            )
            repository.saveStudioState(updated)
        }
    }

    // Advance Script Writing (clicks / task progress)
    fun advanceScriptWriting() {
        val activeSave = studioState.value ?: return
        if (activeSave.currentStage != ActiveProjectStage.WRITING) return

        val writer = Screenwriter.ALL.firstOrNull { it.id == activeSave.activeWriterId } ?: return
        val increment = 25f * writer.speed // normal writer speed multiplier
        val newProgress = min(100f, activeSave.scriptWriteProgress + increment)

        viewModelScope.launch {
            var finalQuality = activeSave.screenplayQuality
            var newStage = activeSave.currentStage
            var currentMoney = activeSave.money

            // When screenplay writing is done
            if (newProgress >= 100f) {
                // Calculate screenplay quality base
                val base = writer.skill * 100f
                val genreBonus = if (writer.favoriteGenre == activeSave.activeGenre) 10f else 0f
                val randomOffset = Random.nextInt(-8, 9)
                finalQuality = (base + genreBonus + randomOffset).coerceIn(10f, 100f)
                newStage = ActiveProjectStage.PRE_PRODUCTION
            }

            var updated = activeSave.copy(
                scriptWriteProgress = newProgress,
                screenplayQuality = finalQuality,
                currentStage = newStage,
                money = currentMoney
            )

            // Random screenplay drafting obstacle event at 50%
            if (activeSave.scriptWriteProgress < 50f && newProgress >= 50f) {
                // Generate automatic plot roadblock
                val hasBudget = currentMoney >= 50_000.0
                if (hasBudget) {
                    // Option: Consultant
                    currentMoney -= 40_000.0
                    finalQuality = (finalQuality + 8f).coerceAtMost(100f)
                } else {
                    finalQuality = (finalQuality - 5f).coerceAtLeast(10f)
                }
                updated = updated.copy(
                    money = currentMoney,
                    screenplayQuality = finalQuality
                )
            }

            repository.saveStudioState(updated)
        }
    }

    // Step 3: Set Casting and Budget variables
    fun finalizeCastingAndBudget(
        directorId: String,
        leadActorId: String,
        supportingActorId: String,
        vfxAlloc: Double,
        prodAlloc: Double,
        mktAlloc: Double
    ) {
        val activeSave = studioState.value ?: return
        if (activeSave.currentStage != ActiveProjectStage.PRE_PRODUCTION) return

        val director = Director.ALL.firstOrNull { it.id == directorId } ?: return
        val lead = Actor.ALL.firstOrNull { it.id == leadActorId } ?: return
        val support = Actor.ALL.firstOrNull { it.id == supportingActorId } ?: return

        val totalStaffCost = director.cost + lead.cost + support.cost
        val totalConfiguredProdCost = totalStaffCost + vfxAlloc + prodAlloc + mktAlloc

        if (activeSave.money < totalConfiguredProdCost) return // Check solvency

        viewModelScope.launch {
            val updated = activeSave.copy(
                currentStage = ActiveProjectStage.SHOOTING,
                activeDirectorId = directorId,
                activeLeadActorId = leadActorId,
                activeSupportingActorId = supportingActorId,
                budgetVfx = vfxAlloc,
                budgetProduction = prodAlloc,
                budgetMarketing = mktAlloc,
                money = activeSave.money - totalConfiguredProdCost, // pay entire production costs
                shootProgressWeek = 0,
                shootTotalWeeks = 4,
                // Shooting quality baseline initialized from Screenplay quality
                accumulatedFilmQuality = activeSave.screenplayQuality,
                accumulatedHype = (mktAlloc / 10_000.0).toFloat().coerceIn(10f, 100f)
            )
            repository.saveStudioState(updated)
        }
    }

    // Step 4: Advancing Shooting Weeks with Set Event decisions
    fun handleShootingWeekOptionSelected(optionIndex: Int) {
        val activeSave = studioState.value ?: return
        if (activeSave.currentStage != ActiveProjectStage.SHOOTING) return

        val nextWeek = activeSave.shootProgressWeek + 1
        var newStage = activeSave.currentStage
        var currentQuality = activeSave.accumulatedFilmQuality
        var currentHype = activeSave.accumulatedHype
        var currentMoney = activeSave.money

        // Process Weekly Event choice outcome
        when (activeSave.shootProgressWeek) {
            0 -> { // Week 1 Event: Actor tantrum
                if (optionIndex == 0) { // Pamper star (-$75k, +10 hype, +5 quality)
                    currentMoney -= 75_000.0
                    currentQuality += 5f
                    currentHype += 10f
                } else { // Crack the whip (free, -10 quality, +5 hype)
                    currentQuality -= 10f
                    currentHype += 5f
                }
            }
            1 -> { // Week 2 Event: Stunt accident / rewrite
                if (optionIndex == 0) { // Safety rewrite (free, -5 quality)
                    currentQuality -= 5f
                } else { // Hire safety coordinator & better gear (-$120k, +15 quality, +15 hype)
                    currentMoney -= 120_000.0
                    currentQuality += 15f
                    currentHype += 15f
                }
            }
            2 -> { // Week 3 Event: Film visual leak
                if (optionIndex == 0) { // Spin as marketing buzz (free, +25 hype, -5 quality)
                    currentHype += 25f
                    currentQuality -= 5f
                } else { // Secure the set legally (-$50k, +10 quality)
                    currentMoney -= 50_000.0
                    currentQuality += 10f
                }
            }
            3 -> { // Week 4 Event: Editing disagreement
                if (optionIndex == 0) { // Let director have final cut (free, +12 quality, -5 hype)
                    currentQuality += 12f
                    currentHype -= 5f
                } else { // Studio edit override (-$40k sound effects, +5 quality, +15 hype)
                    currentMoney -= 40_000.0
                    currentQuality += 5f
                    currentHype += 15f
                }
            }
        }

        // Clamp values
        currentQuality = currentQuality.coerceIn(5f, 100f)
        currentHype = currentHype.coerceIn(5f, 100f)

        if (nextWeek >= activeSave.shootTotalWeeks) {
            newStage = ActiveProjectStage.POST_PRODUCTION
        }

        viewModelScope.launch {
            val updated = activeSave.copy(
                shootProgressWeek = nextWeek,
                currentStage = newStage,
                accumulatedFilmQuality = currentQuality,
                accumulatedHype = currentHype,
                money = currentMoney
            )
            repository.saveStudioState(updated)
        }
    }

    // Step 5: Post Production choices & release deployment
    fun deployMovieToTheaters(season: String, tier: String, extraPostEditPolishLevel: Int) {
        val activeSave = studioState.value ?: return
        if (activeSave.currentStage != ActiveProjectStage.POST_PRODUCTION) return

        val director = Director.ALL.firstOrNull { it.id == activeSave.activeDirectorId } ?: return
        val lead = Actor.ALL.firstOrNull { it.id == activeSave.activeLeadActorId } ?: return
        val support = Actor.ALL.firstOrNull { it.id == activeSave.activeSupportingActorId } ?: return

        var postCost = 0.0
        var extraQualityBoost = 0f
        when (extraPostEditPolishLevel) {
            1 -> { // Deluxe CGI & Sound Polish ($250k)
                postCost = 250_000.0
                extraQualityBoost = 6f
            }
            2 -> { // IMAX conversion & Legendary Composer ($750k)
                postCost = 750_000.0
                extraQualityBoost = 15f
            }
        }

        if (activeSave.money < postCost) return // safety check

        // Final Movie Quality calculations
        val baseScore = activeSave.accumulatedFilmQuality + extraQualityBoost
        // Staff contribution multipliers
        val directorCoeff = director.skill * 10f
        val castingSkillCoeff = (lead.skill * 0.6f + support.skill * 0.4f) * 10f
        
        // Genre alignment checks
        val writer = Screenwriter.ALL.firstOrNull { it.id == activeSave.activeWriterId }
        val genreMatchBonus = (if (director.favoriteGenre == activeSave.activeGenre) 5f else 0f) +
                             (if (writer?.favoriteGenre == activeSave.activeGenre) 5f else 0f)

        val finalScore = (baseScore + directorCoeff + castingSkillCoeff + genreMatchBonus).coerceIn(10f, 100f).toInt()
        
        // Critic and RT calculations
        val reviewScore = (finalScore + Random.nextInt(-10, 11)).coerceIn(5, 100)
        val imdbRating = (reviewScore / 10f + Random.nextFloat() * 0.8f - 0.4f).coerceIn(1.0f, 10.0f)

        // Pre-calculated screens by tier
        val screens = when (tier) {
            "Wide" -> 3000
            "Limited" -> 500
            "Festival" -> 50
            else -> 100 // Direct to Steam/Streaming
        }

        // Generate critical comment summary procedurally
        val comment = generateProceduralCriticComment(finalScore, activeSave.activeTitle, lead.name)

        viewModelScope.launch {
            val updated = activeSave.copy(
                currentStage = ActiveProjectStage.REVIEWS,
                money = activeSave.money - postCost,
                selectedReleaseSeason = season,
                selectedReleaseTier = tier,
                finalMovieQuality = finalScore,
                finalMovieReviewScore = reviewScore,
                finalMovieImdb = String.format("%.1f", imdbRating).toFloat(),
                criticCommentSummary = comment,
                boxOfficeScreens = screens
            )
            repository.saveStudioState(updated)
        }
    }

    // Launch theatrical weekly run
    fun proceedToTheatricalRun() {
        val activeSave = studioState.value ?: return
        if (activeSave.currentStage != ActiveProjectStage.REVIEWS) return

        viewModelScope.launch {
            val updated = activeSave.copy(
                currentStage = ActiveProjectStage.RELEASE_RUN,
                boxOfficeRunWeek = 0,
                boxOfficeRunningCumulative = 0.0,
                boxOfficeWeeklyGrossHistoryJson = "0.0,0.0,0.0,0.0,0.0" // initialize empty list
            )
            repository.saveStudioState(updated)
        }
    }

    // Run next week's box office play
    fun advanceTheatricalWeeklyPlay() {
        val activeSave = studioState.value ?: return
        if (activeSave.currentStage != ActiveProjectStage.RELEASE_RUN) return

        val currentRunningWeek = activeSave.boxOfficeRunWeek
        val totalWeeks = activeSave.boxOfficeTotalWeeks

        // Balance variables
        val tierMult = when (activeSave.selectedReleaseTier) {
            "Wide" -> 1.5
            "Limited" -> 0.4
            "Festival" -> 0.1
            else -> 0.25
        }

        val seasonMult = when (activeSave.selectedReleaseSeason) {
            "Summer" -> 1.8 // Blockbuster high highs, but risky if movie is garbage
            "Holiday" -> 1.4
            "Oscar" -> 1.0
            else -> 0.6 // Dump month - low highs
        }

        // Evaluate formula for Opening Box Office vs standard runs
        val weeklyGross = if (currentRunningWeek == 0) {
            // Week 1 - Opening Weekend
            val qualityFactor = (activeSave.finalMovieQuality / 50f)
            val baseHypeEarnings = activeSave.accumulatedHype * 125_000.0
            val potentialEarner = baseHypeEarnings * qualityFactor * tierMult * seasonMult
            potentialEarner.coerceAtLeast(10_000.0) + Random.nextDouble(5_000.0, 50_000.0)
        } else {
            // Drop-off logic based on Movie Quality
            // Quality under 40 has terrible leg drops (70% drops). Quality over 80 has AMAZING word-of-mouth (15%-25% drops).
            val typicalDrop = if (activeSave.finalMovieQuality >= 80) {
                Random.nextDouble(0.18, 0.28)
            } else if (activeSave.finalMovieQuality >= 60) {
                Random.nextDouble(0.35, 0.45)
            } else {
                Random.nextDouble(0.58, 0.68)
            }

            // Parse previous week's gross
            val hist = activeSave.boxOfficeWeeklyGrossHistoryJson.split(",")
            val lastWeeklyGross = hist.getOrNull(currentRunningWeek - 1)?.toDoubleOrNull() ?: 10_000.0
            val nextWeeklyGross = lastWeeklyGross * (1.0 - typicalDrop)
            nextWeeklyGross.coerceAtLeast(1000.0)
        }

        // Update weekly gross array
        val grossList = activeSave.boxOfficeWeeklyGrossHistoryJson.split(",").toMutableList()
        if (currentRunningWeek < grossList.size) {
            grossList[currentRunningWeek] = weeklyGross.toString()
        } else {
            grossList.add(weeklyGross.toString())
        }
        val nextHistoryJson = grossList.joinToString(",")

        val cumulativeGross = activeSave.boxOfficeRunningCumulative + weeklyGross
        val nextWeekIndex = currentRunningWeek + 1

        viewModelScope.launch {
            if (nextWeekIndex >= totalWeeks) {
                // Movie Has Finished Its Theater Play!
                // Calculate Net Studios Revenue Share (Standard studio share of box office gross is 50%)
                val netStudioRev = cumulativeGross * 0.50
                val lead = Actor.ALL.firstOrNull { it.id == activeSave.activeLeadActorId }
                val devTotalBudget = activeSave.budgetProduction + activeSave.budgetVfx + activeSave.budgetMarketing
                val totalProjectCost = devTotalBudget + (lead?.cost ?: 0.0)
                val totalProfit = netStudioRev - totalProjectCost

                // Feed back loop: Add fans & Reputation adjustments
                var fanBonus = (cumulativeGross / 100.0).toInt()
                if (activeSave.finalMovieQuality >= 80) {
                    fanBonus = (fanBonus * 1.5).toInt()
                }
                val newFans = max(1000, activeSave.fans + fanBonus)
                
                // Adjustment multiplier for reputation
                val reputationShift = if (activeSave.finalMovieQuality >= 75) 0.05f else if (activeSave.finalMovieQuality < 45) -0.06f else 0.01f
                val newReputation = (activeSave.reputation + reputationShift).coerceIn(0.1f, 1.0f)

                // Check for Award Nominations (highly likely during Oscar Season or for 88+ IMDb quality)
                val isOscarSeason = activeSave.selectedReleaseSeason == "Oscar"
                val finalRating = activeSave.finalMovieImdb
                val awardNominated = finalRating >= 8.2f || (isOscarSeason && finalRating >= 7.2f)
                val awardWon = awardNominated && Random.nextInt(0, 100) > (if (isOscarSeason) 40 else 70)

                // Find staff names
                val writer = Screenwriter.ALL.firstOrNull { it.id == activeSave.activeWriterId }
                val director = Director.ALL.firstOrNull { it.id == activeSave.activeDirectorId }

                // Insert into History database
                val movieReport = ReleasedMovie(
                    title = activeSave.activeTitle,
                    genre = activeSave.activeGenre.displayName,
                    writerName = writer?.name ?: "Unknown Writer",
                    directorName = director?.name ?: "Unknown Director",
                    leadActorName = lead?.name ?: "Solo Actor",
                    totalBudget = totalProjectCost,
                    filmQuality = activeSave.finalMovieQuality,
                    reviewScore = activeSave.finalMovieReviewScore,
                    imdbRating = finalRating,
                    criticReview = activeSave.criticCommentSummary,
                    finalBoxOffice = cumulativeGross,
                    totalProfit = totalProfit,
                    releaseSeason = activeSave.selectedReleaseSeason,
                    awardNominated = awardNominated,
                    awardWon = awardWon,
                    releaseWeekIndex = activeSave.timelineWeek
                )
                repository.saveReleasedMovie(movieReport)

                // Reset Active movie parameters, update studio stats
                val finishedSaveState = activeSave.copy(
                    currentStage = ActiveProjectStage.NONE,
                    money = activeSave.money + netStudioRev, // add studio net collection share to funds
                    reputation = newReputation,
                    fans = newFans,
                    completedMoviesCount = activeSave.completedMoviesCount + 1,
                    totalEarnings = activeSave.totalEarnings + cumulativeGross,
                    timelineWeek = activeSave.timelineWeek + 5, // advance internal calendar week
                    activeTitle = "",
                    scriptWriteProgress = 0f
                )
                repository.saveStudioState(finishedSaveState)
            } else {
                // Save and continue theatrical run
                val updated = activeSave.copy(
                    boxOfficeRunWeek = nextWeekIndex,
                    boxOfficeRunningCumulative = cumulativeGross,
                    boxOfficeWeeklyGrossHistoryJson = nextHistoryJson
                )
                repository.saveStudioState(updated)
            }
        }
    }

    // Weekly Marketing Boost choice during release run to nudge ticket sales (+Hype)
    fun runMiniReleaseMarketingHypePush() {
        val activeSave = studioState.value ?: return
        if (activeSave.currentStage != ActiveProjectStage.RELEASE_RUN) return
        if (activeSave.money < 75_000.0) return // cost threshold

        viewModelScope.launch {
            // Pay $75k for an active social media trend campaign which boots cumulative gross slightly
            val currentHist = activeSave.boxOfficeWeeklyGrossHistoryJson.split(",").toMutableList()
            val currentWeekIdx = activeSave.boxOfficeRunWeek
            if (currentWeekIdx < currentHist.size) {
                val weeklyVal = currentHist[currentWeekIdx].toDoubleOrNull() ?: 1000.0
                val boostedVal = weeklyVal * 1.35 // instant 35% weekly boost!
                currentHist[currentWeekIdx] = boostedVal.toString()
            }
            val runningAddon = (activeSave.boxOfficeRunningCumulative * 0.05) // 5% flat bonus

            val updated = activeSave.copy(
                money = activeSave.money - 75_000.0,
                boxOfficeWeeklyGrossHistoryJson = currentHist.joinToString(","),
                boxOfficeRunningCumulative = activeSave.boxOfficeRunningCumulative + runningAddon
            )
            repository.saveStudioState(updated)
        }
    }

    // Procedural hilarious critic review text generator
    private fun generateProceduralCriticComment(quality: Int, title: String, starName: String): String {
        val superGood = listOf(
            "An absolute landmark masterpiece! $starName delivers the performance of a century, leaving cinema forever transformed.",
            "Visual poetry combined with a flawless screen narrative. One of the finest movies ever constructed.",
            "Spectacular in scale and intimate in heart. Will dominate the upcoming awards season sweepstakes!"
        )
        val good = listOf(
            "An incredibly enjoyable cinema piece that perfectly matches its genre expectations. $starName shines.",
            "Witty, well-crafted, and superbly paced. This is highly recommended big screen entertainment.",
            "Stunning visuals paired with solid core performances. Definitely worth the ticket admission."
        )
        val mid = listOf(
            "A comfortable but mostly forgettable venture. It has fun moments but safe, uninventive direction.",
            "Perfectly average. It doesn't break any cinematic boundaries but makes for an okay popcorn watch.",
            "Has plenty of style but struggles with narrative consistency. Hits the middle of the board."
        )
        val bad = listOf(
            "A disjointed experience that crumbles under high-concept over-ambition. Skip this one.",
            "An unfortunate waste of talented cast members. $starName looks incredibly confused in most scenes.",
            "Slow, dreary, and entirely lacking in the entertainment spark. Better left direct-to-streaming."
        )

        return when {
            quality >= 85 -> superGood.random()
            quality >= 65 -> good.random()
            quality >= 45 -> mid.random()
            else -> bad.random()
        }
    }
}

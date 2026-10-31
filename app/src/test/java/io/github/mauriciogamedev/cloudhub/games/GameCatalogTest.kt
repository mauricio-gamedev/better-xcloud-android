package io.github.mauriciogamedev.cloudhub.games

import io.github.mauriciogamedev.cloudhub.providers.ProviderCatalog
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GameCatalogTest {
    @Test
    fun catalogContainsMultipleVerifiedFreeToPlayCloudGames() {
        assertTrue(GameCatalog.all.size >= 10)
        assertTrue(GameCatalog.all.all { it.accessType == GameAccessType.FREE_TO_PLAY })
        assertTrue(GameCatalog.all.map { it.id }.containsAll(
            listOf(
                "fortnite",
                "apex-legends",
                "destiny-2",
                "rocket-league",
                "genshin-impact",
                "honkai-star-rail",
                "wuthering-waves",
                "zenless-zone-zero",
                "marvel-rivals",
                "counter-strike-2",
                "warframe",
                "dota-2"
            )
        ))
    }

    @Test
    fun fortniteXboxLinkUsesTheVerifiedXboxProductId() {
        val link = GameCatalog.all.single { it.id == "fortnite" }
            .providerLaunchUrls[ProviderCatalog.XBOX_CLOUD_ID]
        assertEquals(
            "https://www.xbox.com/pt-br/play/games/fortnite/bt5p2x999vh2",
            link
        )
    }

    @Test
    fun onlyFortniteIsSeededForXboxFreeCloudAccess() {
        val xboxGames = GameCatalog.all.filter {
            ProviderCatalog.XBOX_CLOUD_ID in it.providerLaunchUrls
        }
        assertEquals(listOf("fortnite"), xboxGames.map { it.id })
    }

    @Test
    fun everyOtherSeededFreeGameUsesGeForceNowBrazilCatalog() {
        val games = GameCatalog.all.filter { it.id != "fortnite" }
        assertTrue(games.all {
            ProviderCatalog.GEFORCE_NOW_ID in it.providerLaunchUrls &&
                it.sourceUrls.any { source -> source.contains("akillibrasil.com") }
        })
    }

    @Test
    fun everyGameHasHttpsLaunchAndVerificationSources() {
        assertTrue(GameCatalog.all.all { game ->
            game.providerLaunchUrls.values.all { it.startsWith("https://") } &&
                game.sourceUrls.isNotEmpty() &&
                game.sourceUrls.all { it.startsWith("https://") }
        })
    }

    @Test
    fun freeOnlyFilterExcludesSubscriptionAndPaidGames() {
        val results = GameCatalog.filtered(GameFilter.FREE_ONLY)
        assertTrue(results.all {
            it.accessType == GameAccessType.FREE_TO_PLAY ||
                it.accessType == GameAccessType.FREE_PROMOTION
        })
    }

    @Test
    fun freeToPlayGamesSortBeforeSubscriptionAndPaidGames() {
        val samples = listOf(
            sample("paid", GameAccessType.PAID_PURCHASE),
            sample("subscription", GameAccessType.SUBSCRIPTION_INCLUDED),
            sample("free", GameAccessType.FREE_TO_PLAY),
            sample("promo", GameAccessType.FREE_PROMOTION)
        )

        val sortedIds = GameCatalog.sortedFreeFirst(samples).map { it.id }
        assertEquals(listOf("free", "promo", "subscription", "paid"), sortedIds)
    }

    @Test
    fun freeFilterDoesNotIncludeSubscriptionOnlyGames() {
        val mixed = listOf(
            sample("subscription-only", GameAccessType.SUBSCRIPTION_INCLUDED),
            sample("paid-only", GameAccessType.PAID_PURCHASE),
            sample("free-base", GameAccessType.FREE_TO_PLAY)
        )
        val filtered = GameCatalog.filtered(GameFilter.FREE_ONLY, mixed).map { it.id }
        assertEquals(listOf("free-base"), filtered)
        assertFalse(filtered.contains("subscription-only"))
    }

    private fun sample(id: String, type: GameAccessType) = CloudGame(
        id = id,
        title = id,
        description = "",
        accessType = type,
        providerLaunchUrls = mapOf("provider" to "https://example.com/$id"),
        sourceUrls = listOf("https://example.com/source"),
        verifiedOn = "2026-10-31"
    )
}

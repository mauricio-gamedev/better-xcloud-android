package io.github.mauriciogamedev.cloudhub.games

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GameCatalogTest {
    @Test
    fun seededCatalogContainsVerifiedFreeToPlayFortnite() {
        val fortnite = GameCatalog.all.single { it.id == "fortnite" }
        assertEquals(GameAccessType.FREE_TO_PLAY, fortnite.accessType)
        assertTrue(fortnite.providerLaunchUrls.keys.containsAll(setOf("xbox-cloud", "geforce-now")))
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

package io.github.mauriciogamedev.cloudhub.providers

import java.net.URI
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ProviderCatalogTest {
    @Test
    fun providerIdsAreUnique() {
        val ids = ProviderCatalog.all.map { it.id }
        assertEquals(ids.size, ids.distinct().size)
    }

    @Test
    fun everyProviderUsesAnAbsoluteHttpsUrl() {
        ProviderCatalog.all.forEach { provider ->
            val uri = URI(provider.launchUrl)
            assertEquals("https", uri.scheme)
            assertTrue(uri.host.isNullOrBlank().not())
        }
    }

    @Test
    fun initialCatalogContainsTheFourPlannedServices() {
        val ids = ProviderCatalog.all.map { it.id }.toSet()
        assertTrue(ids.containsAll(setOf("xbox-cloud", "geforce-now", "boosteroid", "blacknut")))
    }

    @Test
    fun xboxAndGeForceNowExposeFreeAccessOptions() {
        assertEquals(
            ProviderAccessModel.FREE_GAMES_WITH_FREE_ACCOUNT,
            ProviderCatalog.findById(ProviderCatalog.XBOX_CLOUD_ID)?.accessModel
        )
        assertEquals(
            ProviderAccessModel.FREE_TIER_AVAILABLE,
            ProviderCatalog.findById(ProviderCatalog.GEFORCE_NOW_ID)?.accessModel
        )
    }

    @Test
    fun browserPlayerProvidersAreMarkedExperimental() {
        listOf(
            ProviderCatalog.GEFORCE_NOW_ID,
            ProviderCatalog.BOOSTEROID_ID,
            ProviderCatalog.BLACKNUT_ID
        ).forEach { id ->
            assertEquals(
                ProviderIntegrationStage.EXPERIMENTAL_IN_APP,
                ProviderCatalog.findById(id)?.integrationStage
            )
        }
    }

    @Test
    fun paidProvidersAreNotMislabelledAsFree() {
        assertEquals(
            ProviderAccessModel.PAID_SUBSCRIPTION,
            ProviderCatalog.findById(ProviderCatalog.BOOSTEROID_ID)?.accessModel
        )
        assertEquals(
            ProviderAccessModel.PAID_SUBSCRIPTION_WITH_TRIAL_WHERE_AVAILABLE,
            ProviderCatalog.findById(ProviderCatalog.BLACKNUT_ID)?.accessModel
        )
    }

    @Test
    fun findByIdReturnsTheMatchingProvider() {
        assertNotNull(ProviderCatalog.findById(ProviderCatalog.XBOX_CLOUD_ID))
    }
}

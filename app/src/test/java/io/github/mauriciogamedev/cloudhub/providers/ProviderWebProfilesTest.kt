package io.github.mauriciogamedev.cloudhub.providers

import java.net.URI
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ProviderWebProfilesTest {
    @Test
    fun eachWebProviderAllowsItsOfficialEntryPoint() {
        ProviderWebProfiles.experimentalProviders.forEach { profile ->
            assertTrue(profile.providerId, profile.allows(URI(profile.entryUrl)))
        }
    }

    @Test
    fun serviceProfilesAllowTheirOwnAuthenticationAndStoreDomains() {
        val gfn = ProviderWebProfiles.forProvider(ProviderCatalog.GEFORCE_NOW_ID)!!
        val boosteroid = ProviderWebProfiles.forProvider(ProviderCatalog.BOOSTEROID_ID)!!
        val blacknut = ProviderWebProfiles.forProvider(ProviderCatalog.BLACKNUT_ID)!!
        assertTrue(gfn.allows(URI("https://login.nvidia.com/")))
        assertTrue(gfn.allows(URI("https://store.steampowered.com/")))
        assertTrue(boosteroid.allows(URI("https://accounts.google.com/")))
        assertTrue(blacknut.allows(URI("https://profile.blacknut.com/")))
    }

    @Test
    fun onlyGeForceNowUsesTheAndroidWebViewCompatibilityUserAgent() {
        assertTrue(
            ProviderWebProfiles.forProvider(ProviderCatalog.GEFORCE_NOW_ID)
                ?.normalizeAndroidWebViewUserAgent == true
        )
        assertFalse(
            ProviderWebProfiles.forProvider(ProviderCatalog.BOOSTEROID_ID)
                ?.normalizeAndroidWebViewUserAgent == true
        )
        assertFalse(
            ProviderWebProfiles.forProvider(ProviderCatalog.BLACKNUT_ID)
                ?.normalizeAndroidWebViewUserAgent == true
        )
    }

    @Test
    fun profilesRejectLookalikeDomainsAndUnlistedHosts() {
        ProviderWebProfiles.experimentalProviders.forEach { profile ->
            val host = URI(profile.entryUrl).host
            assertFalse(profile.providerId, profile.allows(URI("https://${host}.attacker.example/")))
        }
        assertFalse(ProviderWebProfiles.forProvider(ProviderCatalog.GEFORCE_NOW_ID)!!.allows(
            URI("https://example.com/")
        ))
    }

    @Test
    fun profilesRejectCleartextAndCredentialsInUrl() {
        val gfn = ProviderWebProfiles.forProvider(ProviderCatalog.GEFORCE_NOW_ID)!!
        assertFalse(gfn.allows(URI("http://play.geforcenow.com/")))
        assertFalse(gfn.allows(URI("https://user:pass@play.geforcenow.com/")))
    }

    @Test
    fun unknownProvidersHaveNoEmbeddedProfile() {
        assertNotNull(ProviderWebProfiles.forProvider(ProviderCatalog.GEFORCE_NOW_ID))
        assertTrue(ProviderWebProfiles.forProvider("other-cloud") == null)
        assertTrue(ProviderWebProfiles.experimentalProviders.size == 3)
    }
}

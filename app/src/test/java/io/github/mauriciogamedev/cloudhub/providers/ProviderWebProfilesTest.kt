package io.github.mauriciogamedev.cloudhub.providers

import java.net.URI
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ProviderWebProfilesTest {
    @Test
    fun geforceNowProfileAllowsOfficialServiceAndAuthenticationHosts() {
        val profile = ProviderWebProfiles.forProvider(ProviderCatalog.GEFORCE_NOW_ID)
        assertNotNull(profile)
        assertTrue(profile!!.allows(URI("https://play.geforcenow.com/")))
        assertTrue(profile.allows(URI("https://login.nvidia.com/")))
        assertTrue(profile.allows(URI("https://store.steampowered.com/")))
    }

    @Test
    fun profileRejectsLookalikeDomainsAndUnlistedHosts() {
        val profile = ProviderWebProfiles.forProvider(ProviderCatalog.GEFORCE_NOW_ID)!!
        assertFalse(profile.allows(URI("https://geforcenow.com.attacker.example/")))
        assertFalse(profile.allows(URI("https://example.com/")))
    }

    @Test
    fun profileRejectsCleartextAndCredentialsInUrl() {
        val profile = ProviderWebProfiles.forProvider(ProviderCatalog.GEFORCE_NOW_ID)!!
        assertFalse(profile.allows(URI("http://play.geforcenow.com/")))
        assertFalse(profile.allows(URI("https://user:pass@play.geforcenow.com/")))
    }

    @Test
    fun onlyProvidersWithAnExplicitWebProfileAreReturned() {
        assertNotNull(ProviderWebProfiles.forProvider(ProviderCatalog.GEFORCE_NOW_ID))
        assertTrue(ProviderWebProfiles.forProvider("boosteroid") == null)
        assertTrue(ProviderWebProfiles.forProvider("blacknut") == null)
    }
}

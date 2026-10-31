package io.github.mauriciogamedev.cloudhub.providers

import java.net.URI

data class ProviderWebProfile(
    val providerId: String,
    val displayName: String,
    val entryUrl: String,
    val trustedHosts: Set<String>
) {
    fun allows(uri: URI): Boolean {
        if (!uri.scheme.equals("https", ignoreCase = true)) return false
        if (uri.userInfo != null) return false
        val host = uri.host?.lowercase() ?: return false
        return trustedHosts.any { trusted ->
            host == trusted || host.endsWith("." + trusted)
        }
    }

    fun allows(rawUrl: String): Boolean =
        runCatching { allows(URI(rawUrl)) }.getOrDefault(false)
}

object ProviderWebProfiles {
    private val geforceNow = ProviderWebProfile(
        providerId = ProviderCatalog.GEFORCE_NOW_ID,
        displayName = "GeForce NOW",
        entryUrl = "https://play.geforcenow.com/",
        trustedHosts = setOf(
            "play.geforcenow.com",
            "nvidia.com",
            "geforcenow.com",
            "nvda.ws",
            "steampowered.com",
            "steamcommunity.com",
            "epicgames.com",
            "accounts.google.com",
            "google.com",
            "ubisoft.com",
            "xbox.com",
            "xboxlive.com",
            "microsoft.com",
            "live.com"
        )
    )

    fun forProvider(providerId: String): ProviderWebProfile? =
        when (providerId) {
            ProviderCatalog.GEFORCE_NOW_ID -> geforceNow
            else -> null
        }

    val experimentalProviders: List<ProviderWebProfile>
        get() = listOf(geforceNow)
}

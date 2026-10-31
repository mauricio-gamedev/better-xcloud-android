package io.github.mauriciogamedev.cloudhub.providers

import java.net.URI

data class ProviderWebProfile(
    val providerId: String,
    val displayName: String,
    val entryUrl: String,
    val trustedHosts: Set<String>,
    val normalizeAndroidWebViewUserAgent: Boolean = false
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
    private val commonStoreAndIdentityHosts = setOf(
        "google.com",
        "steampowered.com",
        "steamcommunity.com",
        "epicgames.com",
        "ubisoft.com",
        "discord.com"
    )

    private val geforceNow = ProviderWebProfile(
        providerId = ProviderCatalog.GEFORCE_NOW_ID,
        displayName = "GeForce NOW",
        entryUrl = "https://play.geforcenow.com/",
        trustedHosts = commonStoreAndIdentityHosts + setOf(
            "play.geforcenow.com", "nvidia.com", "geforcenow.com", "nvda.ws",
            "xbox.com", "xboxlive.com", "microsoft.com", "live.com"
        ),
        normalizeAndroidWebViewUserAgent = true
    )

    private val boosteroid = ProviderWebProfile(
        providerId = ProviderCatalog.BOOSTEROID_ID,
        displayName = "Boosteroid",
        entryUrl = "https://boosteroid.com/",
        trustedHosts = commonStoreAndIdentityHosts + setOf(
            "boosteroid.com", "boosteroid.net"
        )
    )

    private val blacknut = ProviderWebProfile(
        providerId = ProviderCatalog.BLACKNUT_ID,
        displayName = "Blacknut",
        entryUrl = "https://app.blacknut.com/",
        trustedHosts = commonStoreAndIdentityHosts + setOf("blacknut.com")
    )

    fun forProvider(providerId: String): ProviderWebProfile? =
        when (providerId) {
            ProviderCatalog.GEFORCE_NOW_ID -> geforceNow
            ProviderCatalog.BOOSTEROID_ID -> boosteroid
            ProviderCatalog.BLACKNUT_ID -> blacknut
            else -> null
        }

    val experimentalProviders: List<ProviderWebProfile>
        get() = listOf(geforceNow, boosteroid, blacknut)
}

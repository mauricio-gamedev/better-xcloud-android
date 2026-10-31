package io.github.mauriciogamedev.cloudhub.providers

/**
 * A provider entry describes how this app can hand off to a cloud-gaming service.
 *
 * EXTERNAL_HANDOFF is deliberately distinct from an in-app integration. It means
 * the app opens the provider's official HTTPS entry point and leaves login and
 * streaming to the provider's own app or supported browser.
 */
enum class ProviderIntegrationStage {
    EXTERNAL_HANDOFF,
    IN_APP_INTEGRATION_PLANNED
}

data class CloudProvider(
    val id: String,
    val name: String,
    val description: String,
    val launchUrl: String,
    val integrationStage: ProviderIntegrationStage,
    val integrationNote: String
)

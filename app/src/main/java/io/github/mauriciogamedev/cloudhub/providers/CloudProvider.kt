package io.github.mauriciogamedev.cloudhub.providers

enum class ProviderIntegrationStage {
    EXPERIMENTAL_IN_APP,
    EXTERNAL_HANDOFF
}

/**
 * Describes the cost of accessing the streaming service itself.
 * This is intentionally separate from whether an individual game is free.
 */
enum class ProviderAccessModel {
    FREE_GAMES_WITH_FREE_ACCOUNT,
    FREE_TIER_AVAILABLE,
    PAID_SUBSCRIPTION,
    PAID_SUBSCRIPTION_WITH_TRIAL_WHERE_AVAILABLE
}

data class CloudProvider(
    val id: String,
    val name: String,
    val description: String,
    val launchUrl: String,
    val integrationStage: ProviderIntegrationStage,
    val accessModel: ProviderAccessModel,
    val accessNote: String,
    val integrationNote: String
)

package io.github.mauriciogamedev.cloudhub.providers

enum class ProviderIntegrationStage {
    EXPERIMENTAL_IN_APP,
    EXTERNAL_HANDOFF
}

/**
 * Describes the cost of accessing the streaming service itself.
 * This is intentionally separate from whether an individual game is free.
 */
enum class ProviderAccessModel(val displayName: String) {
    FREE_GAMES_WITH_FREE_ACCOUNT("ACESSO GRÁTIS A TÍTULOS SELECIONADOS"),
    FREE_TIER_AVAILABLE("PLANO GRATUITO DISPONÍVEL"),
    PAID_SUBSCRIPTION("ASSINATURA PAGA"),
    PAID_SUBSCRIPTION_WITH_TRIAL_WHERE_AVAILABLE("ASSINATURA • TESTE PODE VARIAR")
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

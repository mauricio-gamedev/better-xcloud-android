package io.github.mauriciogamedev.cloudhub.providers

import java.net.URI

object ProviderCatalog {
    const val XBOX_CLOUD_ID = "xbox-cloud"
    const val GEFORCE_NOW_ID = "geforce-now"

    val all: List<CloudProvider> = listOf(
        CloudProvider(
            id = XBOX_CLOUD_ID,
            name = "Xbox Cloud Gaming",
            description = "Alguns jogos gratuitos podem ser transmitidos com uma conta Microsoft gratuita.",
            launchUrl = "https://www.xbox.com/pt-BR/play",
            integrationStage = ProviderIntegrationStage.EXPERIMENTAL_IN_APP,
            accessModel = ProviderAccessModel.FREE_GAMES_WITH_FREE_ACCOUNT,
            accessNote = "Catálogo gratuito limitado; a maior parte da biblioteca exige um plano Game Pass elegível.",
            integrationNote = "Better xCloud 6.7.12 está integrado no WebView experimental. Login e streaming ainda precisam de validação no A06."
        ),
        CloudProvider(
            id = GEFORCE_NOW_ID,
            name = "GeForce NOW",
            description = "Transmita jogos compatíveis das suas bibliotecas de PC.",
            launchUrl = "https://play.geforcenow.com/",
            integrationStage = ProviderIntegrationStage.EXTERNAL_HANDOFF,
            accessModel = ProviderAccessModel.FREE_TIER_AVAILABLE,
            accessNote = "A ABYA oferece plano gratuito no Brasil, com limitações de fila e sessão. Muitos jogos também exigem que você os possua.",
            integrationNote = "Abertura externa para o serviço oficial; login e streaming permanecem sob controle do provedor."
        ),
        CloudProvider(
            id = "boosteroid",
            name = "Boosteroid",
            description = "Acesse sua biblioteca de jogos de PC por streaming.",
            launchUrl = "https://boosteroid.com/",
            integrationStage = ProviderIntegrationStage.EXTERNAL_HANDOFF,
            accessModel = ProviderAccessModel.PAID_SUBSCRIPTION,
            accessNote = "Requer assinatura ativa. Os jogos podem exigir compra separada em uma loja compatível.",
            integrationNote = "Abertura externa; integração interna não implementada."
        ),
        CloudProvider(
            id = "blacknut",
            name = "Blacknut",
            description = "Biblioteca de jogos por streaming em uma única assinatura.",
            launchUrl = "https://www.blacknut.com/pt-br",
            integrationStage = ProviderIntegrationStage.EXTERNAL_HANDOFF,
            accessModel = ProviderAccessModel.PAID_SUBSCRIPTION_WITH_TRIAL_WHERE_AVAILABLE,
            accessNote = "Requer assinatura; períodos de teste podem variar por país e oferta.",
            integrationNote = "Abertura externa; integração interna não implementada."
        )
    )

    init {
        require(all.map { it.id }.distinct().size == all.size) {
            "Provider IDs must be unique."
        }

        all.forEach { provider ->
            val uri = URI(provider.launchUrl)
            require(uri.scheme == "https" && !uri.host.isNullOrBlank()) {
                "Provider ${provider.id} must use an absolute HTTPS URL."
            }
        }
    }

    fun findById(id: String): CloudProvider? = all.firstOrNull { it.id == id }
}

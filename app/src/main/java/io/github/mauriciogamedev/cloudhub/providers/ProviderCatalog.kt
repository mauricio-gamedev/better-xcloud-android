package io.github.mauriciogamedev.cloudhub.providers

import java.net.URI

object ProviderCatalog {
    val all: List<CloudProvider> = listOf(
        CloudProvider(
            id = "xbox-cloud",
            name = "Xbox Cloud Gaming",
            description = "Jogue títulos compatíveis por streaming na nuvem.",
            launchUrl = "https://www.xbox.com/play",
            integrationStage = ProviderIntegrationStage.EXTERNAL_HANDOFF,
            integrationNote = "A integração do núcleo Better xCloud será desenvolvida em uma etapa posterior."
        ),
        CloudProvider(
            id = "geforce-now",
            name = "GeForce NOW",
            description = "Acesse sua biblioteca de jogos compatíveis por streaming.",
            launchUrl = "https://play.geforcenow.com/",
            integrationStage = ProviderIntegrationStage.EXTERNAL_HANDOFF,
            integrationNote = "A autenticação e a sessão permanecem sob controle do serviço oficial."
        ),
        CloudProvider(
            id = "boosteroid",
            name = "Boosteroid",
            description = "Acesse o serviço de jogos em nuvem da Boosteroid.",
            launchUrl = "https://boosteroid.com/",
            integrationStage = ProviderIntegrationStage.EXTERNAL_HANDOFF,
            integrationNote = "O Android poderá encaminhar para um app compatível ou navegador."
        ),
        CloudProvider(
            id = "blacknut",
            name = "Blacknut",
            description = "Acesse o catálogo de jogos em nuvem da Blacknut.",
            launchUrl = "https://www.blacknut.com/",
            integrationStage = ProviderIntegrationStage.EXTERNAL_HANDOFF,
            integrationNote = "A compatibilidade e a disponibilidade devem ser verificadas no dispositivo e região."
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

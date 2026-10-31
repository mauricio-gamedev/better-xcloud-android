package io.github.mauriciogamedev.cloudhub.providers

import java.net.URI

object ProviderCatalog {
    const val XBOX_CLOUD_ID = "xbox-cloud"
    const val GEFORCE_NOW_ID = "geforce-now"
    const val BOOSTEROID_ID = "boosteroid"
    const val BLACKNUT_ID = "blacknut"
    const val STEAM_LINK_ID = "steam-link"

    val all: List<CloudProvider> = listOf(
        CloudProvider(
            id = XBOX_CLOUD_ID,
            name = "Xbox Cloud Gaming",
            description = "Alguns jogos gratuitos podem ser transmitidos com uma conta Microsoft gratuita.",
            launchUrl = "https://www.xbox.com/pt-BR/play",
            integrationStage = ProviderIntegrationStage.EXPERIMENTAL_IN_APP,
            accessModel = ProviderAccessModel.FREE_GAMES_WITH_FREE_ACCOUNT,
            accessNote = "Apenas títulos selecionados são grátis na nuvem; a maior parte da biblioteca exige um plano Game Pass elegível.",
            integrationNote = "Better xCloud 6.7.12 integrado. O Cloud Hub oferece ajustes touch e perfil 720p para estabilidade no A06."
        ),
        CloudProvider(
            id = GEFORCE_NOW_ID,
            name = "GeForce NOW",
            description = "Transmita jogos compatíveis de suas bibliotecas de PC e uma seleção de jogos free-to-play.",
            launchUrl = "https://play.geforcenow.com/",
            integrationStage = ProviderIntegrationStage.EXPERIMENTAL_IN_APP,
            accessModel = ProviderAccessModel.FREE_TIER_AVAILABLE,
            accessNote = "A ABYA oferece plano grátis no Brasil sem método de pagamento. Jogos pagos ainda exigem posse e filas/sessões podem ter limites.",
            integrationNote = "Player interno experimental com modo de compatibilidade do user-agent Android, diagnóstico e atalho para o app oficial."
        ),
        CloudProvider(
            id = BOOSTEROID_ID,
            name = "Boosteroid",
            description = "Acesse sua biblioteca de jogos de PC por streaming.",
            launchUrl = "https://boosteroid.com/",
            integrationStage = ProviderIntegrationStage.EXPERIMENTAL_IN_APP,
            accessModel = ProviderAccessModel.PAID_SUBSCRIPTION,
            accessNote = "Requer assinatura ativa. Os jogos podem exigir compra separada em uma loja compatível.",
            integrationNote = "Player WebView experimental; a compatibilidade real precisa ser validada no A06."
        ),
        CloudProvider(
            id = BLACKNUT_ID,
            name = "Blacknut",
            description = "Biblioteca de jogos por streaming em uma única assinatura.",
            launchUrl = "https://app.blacknut.com/",
            integrationStage = ProviderIntegrationStage.EXPERIMENTAL_IN_APP,
            accessModel = ProviderAccessModel.PAID_SUBSCRIPTION_WITH_TRIAL_WHERE_AVAILABLE,
            accessNote = "Requer assinatura; períodos de teste podem variar por país e oferta.",
            integrationNote = "Aplicativo web oficial no player interno experimental; autenticação e streaming precisam de validação."
        ),
        CloudProvider(
            id = STEAM_LINK_ID,
            name = "Steam Link • PC na rede local",
            description = "Transmita para o A06 jogos que estão rodando em um computador com Steam.",
            launchUrl = "https://play.google.com/store/apps/details?id=com.valvesoftware.steamlink",
            integrationStage = ProviderIntegrationStage.EXTERNAL_HANDOFF,
            accessModel = ProviderAccessModel.LOCAL_NETWORK_FREE,
            accessNote = "Steam Link é gratuito. O jogo precisa estar instalado e em execução num PC anfitrião; Steam Cloud sincroniza saves, não executa jogos.",
            integrationNote = "Abre o app oficial Steam Link. Para melhor resultado, PC por Ethernet e A06 conectado ao Wi-Fi de 5 GHz na mesma rede."
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

package io.github.mauriciogamedev.cloudhub.games

enum class GameAccessType(val displayName: String, internal val sortPriority: Int) {
    FREE_TO_PLAY("GRÁTIS PARA JOGAR", 0),
    FREE_PROMOTION("GRATUITO POR PROMOÇÃO", 1),
    SUBSCRIPTION_INCLUDED("INCLUÍDO EM ASSINATURA", 2),
    PAID_PURCHASE("COMPRA NECESSÁRIA", 3),
    UNKNOWN("CUSTO NÃO VERIFICADO", 4)
}

enum class GameFilter {
    ALL,
    FREE_ONLY
}

data class CloudGame(
    val id: String,
    val title: String,
    val description: String,
    val accessType: GameAccessType,
    val providerLaunchUrls: Map<String, String>,
    val sourceUrls: List<String>,
    val verifiedOn: String,
    val availabilityNote: String = ""
)

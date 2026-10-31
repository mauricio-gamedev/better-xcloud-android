package io.github.mauriciogamedev.cloudhub.games

import io.github.mauriciogamedev.cloudhub.providers.ProviderCatalog
import java.net.URI

object GameCatalog {
    val all: List<CloudGame> = listOf(
        CloudGame(
            id = "fortnite",
            title = "Fortnite",
            description = "Battle Royale e Zero Build. O jogo é gratuito; o serviço de streaming pode ter filas, limites ou condições próprias.",
            accessType = GameAccessType.FREE_TO_PLAY,
            providerLaunchUrls = mapOf(
                ProviderCatalog.XBOX_CLOUD_ID to "https://www.xbox.com/pt-BR/play/games/fortnite",
                ProviderCatalog.GEFORCE_NOW_ID to "https://play.geforcenow.com/"
            ),
            sourceUrls = listOf(
                "https://www.xbox.com/pt-BR/cloud-gaming",
                "https://www.nvidia.com/en-us/geforce-now/fortnite-mobile/"
            ),
            verifiedOn = "2026-10-31"
        )
    )

    init {
        require(all.map { it.id }.distinct().size == all.size) {
            "Game IDs must be unique."
        }

        all.forEach { game ->
            require(game.providerLaunchUrls.isNotEmpty()) {
                "Game ${game.id} must have at least one verified provider."
            }
            game.providerLaunchUrls.values.forEach { rawUrl ->
                val uri = URI(rawUrl)
                require(uri.scheme == "https" && !uri.host.isNullOrBlank()) {
                    "Game ${game.id} contains a non-HTTPS provider URL."
                }
            }
        }
    }

    fun sortedFreeFirst(games: Iterable<CloudGame> = all): List<CloudGame> =
        games.sortedWith(
            compareBy<CloudGame> { it.accessType.sortPriority }
                .thenBy { it.title.lowercase() }
        )

    fun filtered(filter: GameFilter, games: Iterable<CloudGame> = all): List<CloudGame> {
        val candidates = when (filter) {
            GameFilter.ALL -> games
            GameFilter.FREE_ONLY -> games.filter {
                it.accessType == GameAccessType.FREE_TO_PLAY ||
                    it.accessType == GameAccessType.FREE_PROMOTION
            }
        }
        return sortedFreeFirst(candidates)
    }
}

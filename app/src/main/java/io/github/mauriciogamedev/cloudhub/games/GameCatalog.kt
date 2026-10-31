package io.github.mauriciogamedev.cloudhub.games

import io.github.mauriciogamedev.cloudhub.providers.ProviderCatalog
import java.net.URI

object GameCatalog {
    private const val GFN_URL = "https://play.geforcenow.com/"
    // Regional directory's visible snapshot is dated 2026-09-13; provider catalogues can change.
    private const val GFN_BRAZIL_CATALOG = "https://games.akillibrasil.com/lista-de-jogos-geforce-now/"
    private const val GFN_REGION_NOTE =
        "GeForce NOW Brasil: listado no snapshot regional de 13/09/2026. A disponibilidade pode mudar; confirme no serviço antes de jogar."

    val all: List<CloudGame> = listOf(
        CloudGame(
            id = "fortnite",
            title = "Fortnite",
            description = "Battle Royale e Zero Build. Grátis no Xbox Cloud e GeForce NOW; itens opcionais.",
            accessType = GameAccessType.FREE_TO_PLAY,
            providerLaunchUrls = mapOf(
                ProviderCatalog.XBOX_CLOUD_ID to "https://www.xbox.com/pt-br/play/games/fortnite/bt5p2x999vh2",
                ProviderCatalog.GEFORCE_NOW_ID to GFN_URL
            ),
            sourceUrls = listOf(
                "https://www.xbox.com/pt-BR/cloud-gaming",
                "https://www.fortnite.com/mobile/xbox-cloud-gaming?lang=pt-BR",
                "https://www.fortnite.com/mobile/geforce-now?lang=pt-BR"
            ),
            verifiedOn = "2026-10-31",
            availabilityNote = "Xbox Cloud: título gratuito selecionado. GeForce NOW: confirme a disponibilidade no catálogo atual."
        ),
        CloudGame(
            id = "apex-legends",
            title = "Apex Legends",
            description = "Hero shooter battle royale. Jogo-base gratuito; compras opcionais.",
            accessType = GameAccessType.FREE_TO_PLAY,
            providerLaunchUrls = mapOf(ProviderCatalog.GEFORCE_NOW_ID to GFN_URL),
            sourceUrls = listOf(
                "https://store.steampowered.com/app/1172470/Apex_Legends/?l=brazilian",
                "https://play.google.com/store/apps/details?id=com.nvidia.geforcenow&hl=pt&gl=BR",
                GFN_BRAZIL_CATALOG
            ),
            verifiedOn = "2026-10-31",
            availabilityNote = GFN_REGION_NOTE
        ),
        CloudGame(
            id = "destiny-2",
            title = "Destiny 2",
            description = "FPS de ação e ficção científica. O jogo-base é gratuito; expansões são pagas.",
            accessType = GameAccessType.FREE_TO_PLAY,
            providerLaunchUrls = mapOf(ProviderCatalog.GEFORCE_NOW_ID to GFN_URL),
            sourceUrls = listOf(
                "https://store.steampowered.com/app/1085660/Destiny_2/",
                "https://play.google.com/store/apps/details?id=com.nvidia.geforcenow&hl=pt&gl=BR",
                GFN_BRAZIL_CATALOG
            ),
            verifiedOn = "2026-10-31",
            availabilityNote = GFN_REGION_NOTE
        ),
        CloudGame(
            id = "rocket-league",
            title = "Rocket League",
            description = "Futebol arcade com carros. Jogo-base gratuito; itens cosméticos opcionais.",
            accessType = GameAccessType.FREE_TO_PLAY,
            providerLaunchUrls = mapOf(ProviderCatalog.GEFORCE_NOW_ID to GFN_URL),
            sourceUrls = listOf(
                "https://store.epicgames.com/p/rocket-league?lang=pt-BR",
                GFN_BRAZIL_CATALOG
            ),
            verifiedOn = "2026-10-31",
            availabilityNote = GFN_REGION_NOTE
        ),
        CloudGame(
            id = "genshin-impact",
            title = "Genshin Impact",
            description = "RPG de ação em mundo aberto. Gratuito; compras opcionais.",
            accessType = GameAccessType.FREE_TO_PLAY,
            providerLaunchUrls = mapOf(ProviderCatalog.GEFORCE_NOW_ID to GFN_URL),
            sourceUrls = listOf(
                "https://store.epicgames.com/p/genshin-impact?lang=pt-BR",
                GFN_BRAZIL_CATALOG
            ),
            verifiedOn = "2026-10-31",
            availabilityNote = GFN_REGION_NOTE
        ),
        CloudGame(
            id = "honkai-star-rail",
            title = "Honkai: Star Rail",
            description = "RPG de fantasia espacial por turnos. Gratuito; compras opcionais.",
            accessType = GameAccessType.FREE_TO_PLAY,
            providerLaunchUrls = mapOf(ProviderCatalog.GEFORCE_NOW_ID to GFN_URL),
            sourceUrls = listOf(
                "https://store.epicgames.com/pt-BR/p/honkai-star-rail",
                GFN_BRAZIL_CATALOG
            ),
            verifiedOn = "2026-10-31",
            availabilityNote = GFN_REGION_NOTE
        ),
        CloudGame(
            id = "wuthering-waves",
            title = "Wuthering Waves",
            description = "RPG de ação em mundo aberto. Gratuito; compras opcionais.",
            accessType = GameAccessType.FREE_TO_PLAY,
            providerLaunchUrls = mapOf(ProviderCatalog.GEFORCE_NOW_ID to GFN_URL),
            sourceUrls = listOf(
                "https://store.epicgames.com/p/wuthering-waves-76ebc5?lang=pt-BR",
                GFN_BRAZIL_CATALOG
            ),
            verifiedOn = "2026-10-31",
            availabilityNote = GFN_REGION_NOTE
        ),
        CloudGame(
            id = "zenless-zone-zero",
            title = "Zenless Zone Zero",
            description = "RPG de ação urbano. Gratuito; compras opcionais.",
            accessType = GameAccessType.FREE_TO_PLAY,
            providerLaunchUrls = mapOf(ProviderCatalog.GEFORCE_NOW_ID to GFN_URL),
            sourceUrls = listOf(
                "https://store.epicgames.com/p/zenless-zone-zero-faq-b735dd?lang=pt",
                GFN_BRAZIL_CATALOG
            ),
            verifiedOn = "2026-10-31",
            availabilityNote = GFN_REGION_NOTE
        ),
        CloudGame(
            id = "marvel-rivals",
            title = "Marvel Rivals",
            description = "Hero shooter em equipes. Jogo-base gratuito; itens opcionais.",
            accessType = GameAccessType.FREE_TO_PLAY,
            providerLaunchUrls = mapOf(ProviderCatalog.GEFORCE_NOW_ID to GFN_URL),
            sourceUrls = listOf(
                "https://store.steampowered.com/app/2767030/Marvel_Rivals/?l=brazilian",
                GFN_BRAZIL_CATALOG
            ),
            verifiedOn = "2026-10-31",
            availabilityNote = GFN_REGION_NOTE
        ),
        CloudGame(
            id = "counter-strike-2",
            title = "Counter-Strike 2",
            description = "FPS competitivo tático. Grátis para jogar; Prime Status é opcional e pago.",
            accessType = GameAccessType.FREE_TO_PLAY,
            providerLaunchUrls = mapOf(ProviderCatalog.GEFORCE_NOW_ID to GFN_URL),
            sourceUrls = listOf(
                "https://store.steampowered.com/app/730/CounterStrike2/",
                GFN_BRAZIL_CATALOG
            ),
            verifiedOn = "2026-10-31",
            availabilityNote = GFN_REGION_NOTE
        ),
        CloudGame(
            id = "warframe",
            title = "Warframe",
            description = "Ação cooperativa sci-fi. Gratuito; compras opcionais.",
            accessType = GameAccessType.FREE_TO_PLAY,
            providerLaunchUrls = mapOf(ProviderCatalog.GEFORCE_NOW_ID to GFN_URL),
            sourceUrls = listOf(
                "https://store.steampowered.com/app/230410/Warframe/?l=brazilian",
                GFN_BRAZIL_CATALOG
            ),
            verifiedOn = "2026-10-31",
            availabilityNote = GFN_REGION_NOTE
        ),
        CloudGame(
            id = "dota-2",
            title = "Dota 2",
            description = "MOBA competitivo. Grátis para jogar; compras cosméticas opcionais.",
            accessType = GameAccessType.FREE_TO_PLAY,
            providerLaunchUrls = mapOf(ProviderCatalog.GEFORCE_NOW_ID to GFN_URL),
            sourceUrls = listOf(
                "https://store.steampowered.com/app/570/Dota_2/?cc=br&l=brazilian",
                GFN_BRAZIL_CATALOG
            ),
            verifiedOn = "2026-10-31",
            availabilityNote = GFN_REGION_NOTE
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
            require(game.sourceUrls.isNotEmpty()) {
                "Game ${game.id} must include at least one verification source."
            }
            game.sourceUrls.forEach { rawUrl ->
                val uri = URI(rawUrl)
                require(uri.scheme == "https" && !uri.host.isNullOrBlank()) {
                    "Game ${game.id} contains a non-HTTPS source URL."
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

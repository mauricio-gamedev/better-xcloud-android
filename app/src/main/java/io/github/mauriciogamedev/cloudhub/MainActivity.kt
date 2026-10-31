package io.github.mauriciogamedev.cloudhub

import android.app.Activity
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowInsets
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import io.github.mauriciogamedev.cloudhub.games.GameCatalog
import io.github.mauriciogamedev.cloudhub.games.GameFilter
import io.github.mauriciogamedev.cloudhub.providers.CloudProvider
import io.github.mauriciogamedev.cloudhub.providers.ProviderAdapterRegistry
import io.github.mauriciogamedev.cloudhub.providers.ProviderCatalog
import io.github.mauriciogamedev.cloudhub.providers.ProviderIntegrationStage

class MainActivity : Activity() {
    private val backgroundColor = Color.rgb(11, 16, 32)
    private val cardColor = Color.rgb(24, 31, 52)
    private val primaryTextColor = Color.rgb(244, 246, 255)
    private val secondaryTextColor = Color.rgb(174, 184, 210)
    private val accentColor = Color.rgb(139, 156, 255)
    private var selectedGameFilter = GameFilter.FREE_ONLY
    private lateinit var gamesContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = backgroundColor
        window.navigationBarColor = backgroundColor

        val page = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(backgroundColor)
            setPadding(dp(20), dp(28), dp(20), dp(24))
        }

        val scroll = ScrollView(this).apply {
            isFillViewport = true
            clipToPadding = false
            addView(page)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
            scroll.setOnApplyWindowInsetsListener { _, insets ->
                val bars = insets.getInsets(WindowInsets.Type.systemBars())
                page.setPadding(
                    dp(20),
                    bars.top + dp(24),
                    dp(20),
                    bars.bottom + dp(24)
                )
                insets
            }
        }

        page.addView(textView("CLOUD HUB", 13f, accentColor, bold = true), matchWidth())
        page.addView(
            textView("Seus jogos em nuvem, em um só lugar.", 27f, primaryTextColor, bold = true),
            withMargins(bottom = 8)
        )
        page.addView(
            textView(
                "Uma biblioteca única, com prioridade para jogos gratuitos e serviços com plano grátis.",
                14f,
                secondaryTextColor
            ),
            withMargins(bottom = 24)
        )

        page.addView(textView("JOGOS • GRATUITOS PRIMEIRO", 12f, accentColor, bold = true), withMargins(bottom = 8))
        page.addView(
            textView(
                "12 jogos free-to-play no catálogo inicial. O custo do jogo e o do serviço são diferentes; a lista regional do GFN pode mudar.",
                12f,
                secondaryTextColor
            ),
            withMargins(bottom = 10)
        )

        val filters = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.START
        }
        filters.addView(filterButton("Só grátis", GameFilter.FREE_ONLY), wrapMargins(end = 8))
        filters.addView(filterButton("Todos", GameFilter.ALL), wrapMargins())
        page.addView(filters, withMargins(bottom = 12))

        gamesContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }
        page.addView(gamesContainer, matchWidth())
        renderGames()

        page.addView(
            textView("SERVIÇOS", 12f, accentColor, bold = true),
            withMargins(top = 18, bottom = 12)
        )

        ProviderCatalog.all.forEach { provider ->
            page.addView(createProviderCard(provider), withMargins(bottom = 12))
        }

        page.addView(
            textView(
                "Protótipo 0.3.0 • Xbox integrado em modo experimental; outros serviços abrem seus apps ou sites oficiais.",
                12f,
                secondaryTextColor
            ),
            withMargins(top = 10)
        )

        setContentView(scroll)
    }

    private fun filterButton(label: String, filter: GameFilter): Button =
        Button(this).apply {
            text = label
            isAllCaps = false
            textSize = 13f
            setTextColor(Color.WHITE)
            backgroundTintList = android.content.res.ColorStateList.valueOf(
                if (selectedGameFilter == filter) accentColor else cardColor
            )
            setOnClickListener {
                selectedGameFilter = filter
                renderGames()
                (parent as? LinearLayout)?.let { row ->
                    for (index in 0 until row.childCount) {
                        val button = row.getChildAt(index) as? Button ?: continue
                        button.backgroundTintList = android.content.res.ColorStateList.valueOf(
                            if ((index == 0 && selectedGameFilter == GameFilter.FREE_ONLY) ||
                                (index == 1 && selectedGameFilter == GameFilter.ALL)
                            ) accentColor else cardColor
                        )
                    }
                }
            }
        }

    private fun renderGames() {
        gamesContainer.removeAllViews()
        val games = GameCatalog.filtered(selectedGameFilter)

        if (games.isEmpty()) {
            gamesContainer.addView(
                textView(
                    "Ainda não há jogos verificados nesta categoria. Vamos ampliar o catálogo sem inventar compatibilidade.",
                    13f,
                    secondaryTextColor
                ),
                withMargins(bottom = 12)
            )
            return
        }

        games.forEach { game ->
            val card = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(dp(16), dp(15), dp(16), dp(14))
                background = roundedBackground(cardColor, dp(18).toFloat())
            }

            card.addView(textView(game.title, 18f, primaryTextColor, bold = true), matchWidth())
            card.addView(
                textView(game.accessType.displayName, 11f, accentColor, bold = true),
                withMargins(top = 5)
            )
            card.addView(
                textView(game.description, 13f, secondaryTextColor),
                withMargins(top = 5, bottom = 10)
            )

            game.providerLaunchUrls.forEach { (providerId, launchUrl) ->
                val provider = ProviderCatalog.findById(providerId) ?: return@forEach
                val openButton = Button(this).apply {
                    text = if (provider.id == ProviderCatalog.GEFORCE_NOW_ID) {
                        "Abrir catálogo no GeForce NOW"
                    } else {
                        "Abrir jogo via ${provider.name}"
                    }
                    isAllCaps = false
                    textSize = 13f
                    setTextColor(Color.WHITE)
                    backgroundTintList = android.content.res.ColorStateList.valueOf(accentColor)
                    setOnClickListener {
                        ProviderAdapterRegistry.forProvider(provider).launch(this@MainActivity, launchUrl)
                    }
                }
                card.addView(openButton, withMargins(top = 4))
            }

            card.addView(
                textView("Verificação: ${game.verifiedOn}", 10f, secondaryTextColor),
                withMargins(top = 8)
            )
            if (game.availabilityNote.isNotBlank()) {
                card.addView(
                    textView(game.availabilityNote, 10f, secondaryTextColor),
                    withMargins(top = 4)
                )
            }
            gamesContainer.addView(card, withMargins(bottom = 12))
        }
    }

    private fun createProviderCard(provider: CloudProvider): View {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(16), dp(16), dp(14))
            background = roundedBackground(cardColor, dp(18).toFloat())
        }

        card.addView(textView(provider.name, 18f, primaryTextColor, bold = true), matchWidth())
        card.addView(textView(provider.description, 13f, secondaryTextColor), withMargins(top = 6))

        val integrationLabel = when (provider.integrationStage) {
            ProviderIntegrationStage.EXPERIMENTAL_IN_APP -> "IN-APP • EXPERIMENTAL"
            ProviderIntegrationStage.EXTERNAL_HANDOFF -> "ACESSO EXTERNO"
        }
        card.addView(textView(integrationLabel, 10f, accentColor, bold = true), withMargins(top = 12))

        card.addView(
            textView(provider.accessModel.displayName, 12f, primaryTextColor, bold = true),
            withMargins(top = 8)
        )
        card.addView(textView(provider.accessNote, 12f, secondaryTextColor), withMargins(top = 4))
        card.addView(textView(provider.integrationNote, 12f, secondaryTextColor), withMargins(top = 6))

        val button = Button(this).apply {
            text = if (provider.integrationStage == ProviderIntegrationStage.EXPERIMENTAL_IN_APP) {
                "Abrir Xbox Cloud"
            } else {
                "Abrir serviço"
            }
            isAllCaps = false
            setTextColor(Color.WHITE)
            textSize = 14f
            backgroundTintList = android.content.res.ColorStateList.valueOf(accentColor)
            setOnClickListener {
                ProviderAdapterRegistry.forProvider(provider).launch(this@MainActivity, provider.launchUrl)
            }
        }
        card.addView(button, withMargins(top = 8))
        return card
    }

    private fun textView(
        value: String,
        sizeSp: Float,
        color: Int,
        bold: Boolean = false
    ): TextView = TextView(this).apply {
        text = value
        textSize = sizeSp
        setTextColor(color)
        gravity = Gravity.START
        includeFontPadding = false
        if (bold) setTypeface(typeface, Typeface.BOLD)
    }

    private fun roundedBackground(color: Int, radius: Float): GradientDrawable =
        GradientDrawable().apply {
            setColor(color)
            cornerRadius = radius
            setStroke(dp(1), Color.rgb(43, 53, 79))
        }

    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()

    private fun matchWidth(): LinearLayout.LayoutParams =
        LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

    private fun wrapMargins(end: Int = 0): LinearLayout.LayoutParams =
        LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            marginEnd = dp(end)
        }

    private fun withMargins(
        top: Int = 0,
        bottom: Int = 0,
        end: Int = 0
    ): LinearLayout.LayoutParams =
        LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            topMargin = dp(top)
            bottomMargin = dp(bottom)
            marginEnd = dp(end)
        }
}

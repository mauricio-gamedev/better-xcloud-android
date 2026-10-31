package io.github.mauriciogamedev.cloudhub

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowInsets
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import io.github.mauriciogamedev.cloudhub.providers.CloudProvider
import io.github.mauriciogamedev.cloudhub.providers.ProviderCatalog
import io.github.mauriciogamedev.cloudhub.providers.ProviderIntegrationStage

class MainActivity : Activity() {
    private val backgroundColor = Color.rgb(11, 16, 32)
    private val cardColor = Color.rgb(24, 31, 52)
    private val primaryTextColor = Color.rgb(244, 246, 255)
    private val secondaryTextColor = Color.rgb(174, 184, 210)
    private val accentColor = Color.rgb(139, 156, 255)

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

        page.addView(
            textView("CLOUD HUB", 13f, accentColor, bold = true),
            matchWidth()
        )
        page.addView(
            textView("Seus jogos em nuvem, em um só lugar.", 27f, primaryTextColor, bold = true),
            withMargins(bottom = 10)
        )
        page.addView(
            textView(
                "Escolha um serviço. Nesta versão inicial, o acesso é encaminhado ao app ou navegador compatível do próprio provedor.",
                14f,
                secondaryTextColor
            ),
            withMargins(bottom = 24)
        )

        page.addView(
            textView("SERVIÇOS", 12f, accentColor, bold = true),
            withMargins(bottom = 12)
        )

        ProviderCatalog.all.forEach { provider ->
            page.addView(createProviderCard(provider), withMargins(bottom = 12))
        }

        page.addView(
            textView(
                "Protótipo 0.1.0 • Integrações profundas serão adicionadas e testadas individualmente.",
                12f,
                secondaryTextColor
            ),
            withMargins(top = 10)
        )

        setContentView(scroll)
    }

    private fun createProviderCard(provider: CloudProvider): View {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(16), dp(16), dp(14))
            background = roundedBackground(cardColor, dp(18).toFloat())
        }

        card.addView(
            textView(provider.name, 18f, primaryTextColor, bold = true),
            matchWidth()
        )

        card.addView(
            textView(provider.description, 13f, secondaryTextColor),
            withMargins(top = 6)
        )

        val stateText = when (provider.integrationStage) {
            ProviderIntegrationStage.EXTERNAL_HANDOFF -> "ACESSO EXTERNO"
            ProviderIntegrationStage.IN_APP_INTEGRATION_PLANNED -> "INTEGRAÇÃO PLANEJADA"
        }
        card.addView(
            textView(stateText, 10f, accentColor, bold = true),
            withMargins(top = 12)
        )

        card.addView(
            textView(provider.integrationNote, 12f, secondaryTextColor),
            withMargins(top = 4)
        )

        val button = Button(this).apply {
            text = "Abrir serviço"
            isAllCaps = false
            setTextColor(Color.WHITE)
            textSize = 14f
            backgroundTintList = android.content.res.ColorStateList.valueOf(accentColor)
            setOnClickListener { openProvider(provider) }
        }
        card.addView(button, withMargins(top = 8))
        return card
    }

    private fun openProvider(provider: CloudProvider) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(provider.launchUrl))
        try {
            startActivity(intent)
        } catch (_: ActivityNotFoundException) {
            Toast.makeText(this, "Nenhum app ou navegador compatível foi encontrado.", Toast.LENGTH_LONG).show()
        } catch (_: SecurityException) {
            Toast.makeText(this, "O Android bloqueou a abertura deste serviço.", Toast.LENGTH_LONG).show()
        }
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

    private fun withMargins(
        top: Int = 0,
        bottom: Int = 0
    ): LinearLayout.LayoutParams =
        LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            topMargin = dp(top)
            bottomMargin = dp(bottom)
        }
}

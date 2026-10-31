package io.github.mauriciogamedev.cloudhub

import android.app.Activity
import android.app.AlertDialog
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
import android.view.WindowInsetsController
import android.view.WindowManager
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.webkit.WebViewCompat
import androidx.webkit.WebViewFeature
import org.json.JSONObject

class XboxCloudActivity : Activity() {
    companion object {
        const val EXTRA_START_URL = "cloudhub.extra.XBOX_START_URL"
        private const val DEFAULT_START_URL = "https://www.xbox.com/pt-BR/play"
        private const val SCRIPT_ASSET = "scripts/better-xcloud.user.js"
        private const val STRICT_MODE_MARKER = "\"use strict\";"
        private const val XBOX_ORIGIN = "https://www.xbox.com"
        private const val BACKGROUND = 0xFF0B1020.toInt()
        private const val TEXT = 0xFFF4F6FF.toInt()
        private const val MUTED = 0xFFB0BAD2.toInt()
        private const val ACCENT = 0xFF8B9CFF.toInt()
        private const val PREFS = "cloudhub_xbox_player"
    }

    private var cloudWebView: WebView? = null
    private var startScriptHandler: androidx.webkit.ScriptHandler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applyImmersiveMode()
        val startUrl = validatedStartUrl(intent.getStringExtra(EXTRA_START_URL))
        if (!WebViewFeature.isFeatureSupported(WebViewFeature.DOCUMENT_START_SCRIPT)) {
            showFallback("Este Android System WebView não oferece injeção segura no início da página.") {
                openExternal(startUrl)
            }
            return
        }

        val script = try {
            assets.open(SCRIPT_ASSET).bufferedReader(Charsets.UTF_8).use { it.readText() }
        } catch (_: Exception) {
            showFallback("O userscript Better xCloud não foi encontrado no APK.") { openExternal(startUrl) }
            return
        }
        val strictIndex = script.indexOf(STRICT_MODE_MARKER)
        if (strictIndex < 0) {
            showFallback("A versão incluída do Better xCloud não passou na verificação de integridade.") {
                openExternal(startUrl)
            }
            return
        }

        val injectedScript = buildString(script.length + 1000) {
            append(script, 0, strictIndex + STRICT_MODE_MARKER.length)
            append("\nif (window.top !== window) { throw new Error('[Cloud Hub] Better xCloud is limited to the top-level Xbox page.'); }\n")
            append(buildBetterXcloudSettingsBootstrap())
            append('\n')
            append(script.substring(strictIndex + STRICT_MODE_MARKER.length))
        }

        val webView = WebView(this)
        cloudWebView = webView
        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            allowFileAccess = false
            allowContentAccess = false
            mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
            mediaPlaybackRequiresUserGesture = false
            javaScriptCanOpenWindowsAutomatically = false
            setSupportMultipleWindows(false)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) safeBrowsingEnabled = true
        }
        CookieManager.getInstance().setAcceptCookie(true)

        try {
            startScriptHandler = WebViewCompat.addDocumentStartJavaScript(
                webView,
                injectedScript,
                setOf(XBOX_ORIGIN)
            )
        } catch (_: Exception) {
            cloudWebView = null
            webView.destroy()
            showFallback("Não foi possível preparar a integração do Better xCloud neste WebView.") {
                openExternal(startUrl)
            }
            return
        }

        webView.webChromeClient = WebChromeClient()
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean =
                routeNavigation(request.url)

            @Suppress("DEPRECATION")
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean =
                routeNavigation(Uri.parse(url))

            override fun onReceivedError(
                view: WebView,
                request: WebResourceRequest,
                error: android.webkit.WebResourceError
            ) {
                super.onReceivedError(view, request, error)
                if (request.isForMainFrame) Toast.makeText(
                    this@XboxCloudActivity,
                    "Falha ao carregar o Xbox Cloud. Verifique a conexão e tente novamente.",
                    Toast.LENGTH_LONG
                ).show()
            }

            override fun onRenderProcessGone(
                view: WebView,
                detail: android.webkit.RenderProcessGoneDetail
            ): Boolean {
                cloudWebView = null
                view.destroy()
                showFallback("O processo do WebView foi encerrado pelo Android. Isso pode ocorrer por limite de memória.") {
                    openExternal(startUrl)
                }
                return true
            }
        }

        val root = FrameLayout(this)
        root.addView(webView, FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        ))
        val settingsButton = TextView(this).apply {
            text = "⚙"
            textSize = 21f
            gravity = Gravity.CENTER
            setTextColor(Color.WHITE)
            alpha = 0.78f
            background = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(0xCC182034.toInt())
                setStroke(dp(1), 0x887F91D9.toInt())
            }
            contentDescription = "Configurações do player Xbox"
            setOnClickListener { showPlayerSettings() }
        }
        root.addView(settingsButton, FrameLayout.LayoutParams(
            dp(44), dp(44), Gravity.TOP or Gravity.END
        ).apply {
            topMargin = dp(10)
            marginEnd = dp(10)
        })
        setContentView(root)
        applyImmersiveMode()
        webView.loadUrl(startUrl)
    }

    private fun buildBetterXcloudSettingsBootstrap(): String {
        val prefs = getSharedPreferences(PREFS, MODE_PRIVATE)
        val globalSettings = JSONObject()
            .put("touchController.mode", prefs.getString("touch_mode", "all"))
            .put("touchController.autoOff", false)
            .put("touchController.opacity.default", prefs.getInt("touch_opacity", 80))
            .put("touchController.style.standard", prefs.getString("touch_style", "default"))
            .put("stream.video.resolution", prefs.getString("stream_resolution", "720p"))
        val streamSettings = JSONObject()
            .put("stats.showWhenPlaying", prefs.getBoolean("stream_stats", false))

        return """
            (function () {
              try {
                const globalKey = 'BetterXcloud';
                const globalSettings = JSON.parse(localStorage.getItem(globalKey) || '{}');
                Object.assign(globalSettings, __CLOUD_HUB_GLOBAL__);
                localStorage.setItem(globalKey, JSON.stringify(globalSettings));

                const streamKey = 'BetterXcloud.Stream';
                const streamSettings = JSON.parse(localStorage.getItem(streamKey) || '{}');
                Object.assign(streamSettings, __CLOUD_HUB_STREAM__);
                localStorage.setItem(streamKey, JSON.stringify(streamSettings));
              } catch (error) {
                console.warn('[Cloud Hub] Could not apply saved player preferences', error);
              }
            })();
        """.trimIndent()
            .replace("__CLOUD_HUB_GLOBAL__", globalSettings.toString())
            .replace("__CLOUD_HUB_STREAM__", streamSettings.toString())
    }

    private fun showPlayerSettings() {
        val prefs = getSharedPreferences(PREFS, MODE_PRIVATE)
        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(8), dp(20), dp(4))
        }

        fun addLabel(value: String) {
            content.addView(TextView(this).apply {
                text = value
                textSize = 13f
                setTextColor(TEXT)
                setTypeface(typeface, Typeface.BOLD)
                setPadding(0, dp(10), 0, dp(5))
            })
        }

        fun spinner(labels: Array<String>, selected: Int): Spinner =
            Spinner(this).apply {
                adapter = ArrayAdapter(
                    this@XboxCloudActivity,
                    android.R.layout.simple_spinner_dropdown_item,
                    labels
                )
                setSelection(selected)
            }

        addLabel("Controles touch")
        val touchModes = arrayOf("Automático do jogo", "Sempre disponível", "Desativado")
        val touchModeValues = arrayOf("default", "all", "off")
        val touchModeSpinner = spinner(
            touchModes,
            touchModeValues.indexOf(prefs.getString("touch_mode", "all")).coerceAtLeast(0)
        )
        content.addView(touchModeSpinner)

        addLabel("Opacidade dos controles")
        val opacityLabel = TextView(this).apply {
            text = "${prefs.getInt("touch_opacity", 80)}%"
            textSize = 13f
            setTextColor(MUTED)
        }
        content.addView(opacityLabel)
        val opacitySeek = SeekBar(this).apply {
            max = 90
            progress = (prefs.getInt("touch_opacity", 80) - 10).coerceIn(0, 90)
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    opacityLabel.text = "${progress + 10}%"
                }
                override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
                override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
            })
        }
        content.addView(opacitySeek)

        addLabel("Estilo dos botões")
        val touchStyles = arrayOf("Padrão", "Suave", "Branco")
        val touchStyleValues = arrayOf("default", "muted", "white")
        val touchStyleSpinner = spinner(
            touchStyles,
            touchStyleValues.indexOf(prefs.getString("touch_style", "default")).coerceAtLeast(0)
        )
        content.addView(touchStyleSpinner)

        addLabel("Qualidade de transmissão")
        val resolutions = arrayOf("720p • recomendado para fluidez", "1080p", "1080p HQ", "Automático")
        val resolutionValues = arrayOf("720p", "1080p", "1080p-hq", "auto")
        val resolutionSpinner = spinner(
            resolutions,
            resolutionValues.indexOf(prefs.getString("stream_resolution", "720p")).coerceAtLeast(0)
        )
        content.addView(resolutionSpinner)

        val statsCheck = CheckBox(this).apply {
            text = "Mostrar informações do stream durante o jogo"
            setTextColor(TEXT)
            isChecked = prefs.getBoolean("stream_stats", false)
        }
        content.addView(statsCheck)

        AlertDialog.Builder(this)
            .setTitle("Xbox Cloud • controles e desempenho")
            .setView(content)
            .setNegativeButton("Cancelar", null)
            .setNeutralButton("Padrões recomendados") { _, _ ->
                prefs.edit()
                    .putString("touch_mode", "all")
                    .putInt("touch_opacity", 80)
                    .putString("touch_style", "default")
                    .putString("stream_resolution", "720p")
                    .putBoolean("stream_stats", false)
                    .apply()
                cloudWebView?.reload()
            }
            .setPositiveButton("Salvar e recarregar") { _, _ ->
                prefs.edit()
                    .putString("touch_mode", touchModeValues[touchModeSpinner.selectedItemPosition])
                    .putInt("touch_opacity", opacitySeek.progress + 10)
                    .putString("touch_style", touchStyleValues[touchStyleSpinner.selectedItemPosition])
                    .putString("stream_resolution", resolutionValues[resolutionSpinner.selectedItemPosition])
                    .putBoolean("stream_stats", statsCheck.isChecked)
                    .apply()
                cloudWebView?.reload()
            }
            .show()
    }

    private fun applyImmersiveMode() {
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
            window.insetsController?.let { controller ->
                controller.systemBarsBehavior =
                    WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                controller.hide(WindowInsets.Type.systemBars())
            }
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes = window.attributes.apply {
                layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) applyImmersiveMode()
    }

    private fun validatedStartUrl(rawUrl: String?): String {
        if (rawUrl.isNullOrBlank()) return DEFAULT_START_URL
        val uri = Uri.parse(rawUrl)
        val path = uri.path?.lowercase().orEmpty()
        val validPath = path.startsWith("/pt-br/play") || path.startsWith("/en-us/play")
        return if (
            uri.scheme == "https" &&
            uri.host.equals("www.xbox.com", ignoreCase = true) &&
            validPath
        ) uri.toString() else DEFAULT_START_URL
    }

    private fun routeNavigation(uri: Uri): Boolean {
        if (uri.scheme != "https") return true
        val host = uri.host?.lowercase().orEmpty()
        if (isTrustedMicrosoftHost(host)) return false
        openExternal(uri.toString())
        return true
    }

    private fun isTrustedMicrosoftHost(host: String): Boolean {
        val trustedDomains = listOf(
            "xbox.com", "xboxlive.com", "live.com", "microsoft.com",
            "microsoftonline.com", "msauth.net", "msftauth.net"
        )
        return trustedDomains.any { host == it || host.endsWith(".$it") }
    }

    private fun openExternal(url: String) {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        } catch (_: ActivityNotFoundException) {
            Toast.makeText(this, "Nenhum navegador compatível foi encontrado.", Toast.LENGTH_LONG).show()
        } catch (_: SecurityException) {
            Toast.makeText(this, "O Android bloqueou a abertura do link.", Toast.LENGTH_LONG).show()
        }
    }

    private fun showFallback(message: String, onOpenBrowser: () -> Unit) {
        val page = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(28, 28, 28, 28)
            setBackgroundColor(BACKGROUND)
        }
        page.addView(TextView(this).apply {
            text = "Xbox Cloud • modo externo"
            textSize = 21f
            setTextColor(TEXT)
        })
        page.addView(TextView(this).apply {
            text = message
            textSize = 14f
            setTextColor(MUTED)
            setPadding(0, 16, 0, 20)
        })
        page.addView(Button(this).apply {
            text = "Abrir no navegador"
            setTextColor(Color.WHITE)
            setBackgroundTintList(android.content.res.ColorStateList.valueOf(ACCENT))
            setOnClickListener { onOpenBrowser() }
        })
        setContentView(page)
    }

    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()

    @Suppress("DEPRECATION")
    override fun onBackPressed() {
        val webView = cloudWebView
        if (webView?.canGoBack() == true) webView.goBack() else super.onBackPressed()
    }

    override fun onDestroy() {
        startScriptHandler?.remove()
        startScriptHandler = null
        cloudWebView?.let { webView ->
            webView.stopLoading()
            webView.webChromeClient = null
            webView.webViewClient = WebViewClient()
            webView.loadUrl("about:blank")
            webView.clearHistory()
            webView.removeAllViews()
            webView.destroy()
        }
        cloudWebView = null
        super.onDestroy()
    }
}

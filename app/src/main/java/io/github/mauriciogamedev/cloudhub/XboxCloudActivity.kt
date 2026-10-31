package io.github.mauriciogamedev.cloudhub

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.webkit.WebViewCompat
import androidx.webkit.WebViewFeature

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
    }

    private var cloudWebView: WebView? = null
    private var startScriptHandler: androidx.webkit.ScriptHandler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = BACKGROUND
        window.navigationBarColor = BACKGROUND

        val startUrl = validatedStartUrl(intent.getStringExtra(EXTRA_START_URL))
        if (!WebViewFeature.isFeatureSupported(WebViewFeature.DOCUMENT_START_SCRIPT)) {
            showFallback(
                "Este Android System WebView não oferece injeção segura no início da página. Atualize o WebView ou abra o Xbox no navegador compatível."
            ) { openExternal(startUrl) }
            return
        }

        val script = try {
            assets.open(SCRIPT_ASSET).bufferedReader(Charsets.UTF_8).use { it.readText() }
        } catch (_: Exception) {
            showFallback("O userscript Better xCloud não foi encontrado no APK.") {
                openExternal(startUrl)
            }
            return
        }

        val strictIndex = script.indexOf(STRICT_MODE_MARKER)
        if (strictIndex < 0) {
            showFallback("A versão incluída do Better xCloud não passou na verificação de integridade.") {
                openExternal(startUrl)
            }
            return
        }

        // Keep the original userscript intact, but prevent it from initializing in embedded frames.
        val injectedScript = buildString(script.length + 180) {
            append(script, 0, strictIndex + STRICT_MODE_MARKER.length)
            append("\nif (window.top !== window) { throw new Error('[Cloud Hub] Better xCloud is limited to the top-level Xbox page.'); }\n")
            append(script.substring(strictIndex + STRICT_MODE_MARKER.length))
        }

        val webView = WebView(this)
        cloudWebView = webView

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

        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            mediaPlaybackRequiresUserGesture = false
            javaScriptCanOpenWindowsAutomatically = false
            setSupportMultipleWindows(false)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                safeBrowsingEnabled = true
            }
        }

        // Keep the default WebView cookie policy. No JavaScript-to-native bridge is exposed.
        CookieManager.getInstance().setAcceptCookie(true)

        webView.webChromeClient = WebChromeClient()
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest
            ): Boolean = routeNavigation(request.url)

            @Suppress("DEPRECATION")
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean =
                routeNavigation(Uri.parse(url))

            override fun onReceivedError(
                view: WebView,
                request: WebResourceRequest,
                error: android.webkit.WebResourceError
            ) {
                super.onReceivedError(view, request, error)
                if (request.isForMainFrame) {
                    Toast.makeText(
                        this@XboxCloudActivity,
                        "Falha ao carregar o Xbox Cloud. Verifique a conexão e tente novamente.",
                        Toast.LENGTH_LONG
                    ).show()
                }
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

        setContentView(webView)
        webView.loadUrl(startUrl)
    }

    private fun validatedStartUrl(rawUrl: String?): String {
        if (rawUrl.isNullOrBlank()) return DEFAULT_START_URL

        val uri = Uri.parse(rawUrl)
        val validPath = uri.path?.startsWith("/pt-BR/play") == true ||
            uri.path?.startsWith("/en-US/play") == true
        return if (
            uri.scheme == "https" &&
            uri.host == "www.xbox.com" &&
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
            "xbox.com",
            "xboxlive.com",
            "live.com",
            "microsoft.com",
            "microsoftonline.com",
            "msauth.net",
            "msftauth.net"
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

    @Suppress("DEPRECATION")
    override fun onBackPressed() {
        val webView = cloudWebView
        if (webView?.canGoBack() == true) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
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

package io.github.mauriciogamedev.cloudhub

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.webkit.CookieManager
import android.webkit.ConsoleMessage
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import io.github.mauriciogamedev.cloudhub.providers.ProviderWebProfile
import io.github.mauriciogamedev.cloudhub.providers.ProviderWebProfiles
import java.net.URI

class CloudPlayerActivity : Activity() {
    companion object {
        const val EXTRA_PROVIDER_ID = "cloudhub.extra.PLAYER_PROVIDER_ID"
        const val EXTRA_START_URL = "cloudhub.extra.PLAYER_START_URL"
        const val EXTRA_GAME_TITLE = "cloudhub.extra.PLAYER_GAME_TITLE"
        private const val BACKGROUND = 0xFF0B1020.toInt()
        private const val CARD = 0xFF182034.toInt()
        private const val TEXT = 0xFFF4F6FF.toInt()
        private const val MUTED = 0xFFB0BAD2.toInt()
        private const val ACCENT = 0xFF8B9CFF.toInt()
        private const val GFN_PACKAGE = "com.nvidia.geforcenow"
        private const val TAG = "CloudHub.Player"
    }

    private var profile: ProviderWebProfile? = null
    private var playerWebView: WebView? = null
    private var currentStartUrl: String = ""
    private var customView: View? = null
    private var customViewCallback: WebChromeClient.CustomViewCallback? = null
    private lateinit var root: FrameLayout
    private lateinit var content: LinearLayout
    private lateinit var progress: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = BACKGROUND
        window.navigationBarColor = BACKGROUND

        val providerId = intent.getStringExtra(EXTRA_PROVIDER_ID).orEmpty()
        profile = ProviderWebProfiles.forProvider(providerId)
        val activeProfile = profile
        if (activeProfile == null) {
            showFallback("Este provedor ainda não possui um perfil de player interno validado.")
            return
        }

        val requestedUrl = intent.getStringExtra(EXTRA_START_URL) ?: activeProfile.entryUrl
        currentStartUrl = if (activeProfile.allows(requestedUrl)) requestedUrl else activeProfile.entryUrl
        buildShell(activeProfile)
        createPlayerWebView(activeProfile)
        playerWebView?.loadUrl(currentStartUrl)
    }

    private fun buildShell(activeProfile: ProviderWebProfile) {
        root = FrameLayout(this)
        content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(BACKGROUND)
        }
        val toolbar = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(6), dp(3), dp(6), dp(3))
            setBackgroundColor(CARD)
        }
        toolbar.addView(actionButton("‹") {
            val view = playerWebView
            if (view?.canGoBack() == true) view.goBack() else finish()
        })

        val titleColumn = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(6), 0, dp(4), 0)
        }
        titleColumn.addView(TextView(this).apply {
            text = activeProfile.displayName
            textSize = 14f
            setTextColor(TEXT)
            maxLines = 1
            ellipsize = android.text.TextUtils.TruncateAt.END
        })
        val gameTitle = intent.getStringExtra(EXTRA_GAME_TITLE)?.takeIf { it.isNotBlank() }
        titleColumn.addView(TextView(this).apply {
            text = gameTitle?.let { "$it • player experimental" } ?: "Player interno • experimental"
            textSize = 10f
            setTextColor(MUTED)
            maxLines = 1
            ellipsize = android.text.TextUtils.TruncateAt.END
        })
        toolbar.addView(titleColumn, LinearLayout.LayoutParams(0, dp(48), 1f))
        toolbar.addView(actionButton("↻") { playerWebView?.reload() })
        if (activeProfile.providerId == ProviderCatalog.GEFORCE_NOW_ID) {
            toolbar.addView(actionButton("APP") { openOfficialProviderApp() })
        }
        toolbar.addView(actionButton("↗") { openExternal(currentStartUrl) })
        content.addView(toolbar, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            dp(52)
        ))

        progress = ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal).apply {
            max = 100
            progress = 0
            visibility = View.GONE
        }
        content.addView(progress, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, dp(3)
        ))
        root.addView(content, FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        ))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
            root.setOnApplyWindowInsetsListener { _, insets ->
                val bars = insets.getInsets(WindowInsets.Type.systemBars())
                content.setPadding(0, bars.top, 0, bars.bottom)
                insets
            }
        }
        setContentView(root)
    }

    private fun createPlayerWebView(activeProfile: ProviderWebProfile) {
        val webView = WebView(this)
        playerWebView = webView
        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            allowFileAccess = false
            allowContentAccess = false
            mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
            mediaPlaybackRequiresUserGesture = false
            javaScriptCanOpenWindowsAutomatically = false
            setSupportMultipleWindows(false)
            builtInZoomControls = false
            displayZoomControls = false
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) safeBrowsingEnabled = true
            if (activeProfile.normalizeAndroidWebViewUserAgent) {
                userAgentString = userAgentString.replace("; wv", "").replace("Version/4.0 ", "")
            }
        }
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        CookieManager.getInstance().setAcceptCookie(true)
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true)

        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                progress.progress = newProgress
                progress.visibility = if (newProgress in 1..99) View.VISIBLE else View.GONE
            }

            override fun onConsoleMessage(consoleMessage: ConsoleMessage): Boolean {
                Log.w(TAG, "${activeProfile.providerId}: ${consoleMessage.message()} (${consoleMessage.sourceId()}:${consoleMessage.lineNumber()})")
                return true
            }

            override fun onShowCustomView(view: View, callback: CustomViewCallback) {
                if (customView != null) {
                    callback.onCustomViewHidden()
                    return
                }
                customView = view
                customViewCallback = callback
                content.visibility = View.GONE
                root.addView(view, FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                ))
                applyFullscreenFlags()
            }

            override fun onHideCustomView() {
                hideCustomView()
            }
        }

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean =
                routeNavigation(request.url)

            @Suppress("DEPRECATION")
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean =
                routeNavigation(Uri.parse(url))

            override fun onPageStarted(view: WebView, url: String, favicon: android.graphics.Bitmap?) {
                currentStartUrl = url
                progress.visibility = View.VISIBLE
                progress.progress = 0
                Log.i(TAG, "${activeProfile.providerId}: loading $url")
            }

            override fun onPageFinished(view: WebView, url: String) {
                currentStartUrl = url
                progress.visibility = View.GONE
                Log.i(TAG, "${activeProfile.providerId}: page finished $url")
            }

            override fun onReceivedHttpError(
                view: WebView,
                request: WebResourceRequest,
                errorResponse: android.webkit.WebResourceResponse
            ) {
                super.onReceivedHttpError(view, request, errorResponse)
                if (request.isForMainFrame) {
                    Log.e(TAG, "${activeProfile.providerId}: HTTP ${errorResponse.statusCode} for ${request.url}")
                }
            }

            override fun onReceivedError(
                view: WebView,
                request: WebResourceRequest,
                error: android.webkit.WebResourceError
            ) {
                super.onReceivedError(view, request, error)
                if (request.isForMainFrame) {
                    Log.e(TAG, "${activeProfile.providerId}: ${error.description}")
                    Toast.makeText(
                        this@CloudPlayerActivity,
                        "Falha ao carregar o serviço. Verifique a conexão ou tente recarregar.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onRenderProcessGone(
                view: WebView,
                detail: android.webkit.RenderProcessGoneDetail
            ): Boolean {
                if (playerWebView === view) playerWebView = null
                (view.parent as? ViewGroup)?.removeView(view)
                view.destroy()
                showFallback("O Android encerrou o processo do player, possivelmente por limite de memória.")
                return true
            }
        }
        content.addView(webView, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            0,
            1f
        ))
    }

    private fun openOfficialProviderApp() {
        try {
            val launchIntent = packageManager.getLaunchIntentForPackage(GFN_PACKAGE)
            if (launchIntent != null) {
                startActivity(launchIntent)
            } else {
                openExternal("https://play.google.com/store/apps/details?id=$GFN_PACKAGE")
            }
        } catch (_: ActivityNotFoundException) {
            openExternal("https://play.google.com/store/apps/details?id=$GFN_PACKAGE")
        } catch (_: SecurityException) {
            openExternal("https://play.google.com/store/apps/details?id=$GFN_PACKAGE")
        }
    }

    private fun routeNavigation(uri: Uri): Boolean {
        val activeProfile = profile ?: return true
        val allowed = runCatching { activeProfile.allows(URI(uri.toString())) }.getOrDefault(false)
        if (allowed) return false
        if (uri.scheme.equals("https", ignoreCase = true) && !uri.host.isNullOrBlank()) {
            Toast.makeText(this, "Este destino será aberto fora do player.", Toast.LENGTH_SHORT).show()
            openExternal(uri.toString())
        } else {
            Toast.makeText(this, "Navegação bloqueada por segurança.", Toast.LENGTH_SHORT).show()
        }
        return true
    }

    private fun actionButton(label: String, action: () -> Unit): Button =
        Button(this).apply {
            text = label
            isAllCaps = false
            textSize = 15f
            minWidth = dp(40)
            minimumHeight = dp(44)
            setPadding(dp(3), 0, dp(3), 0)
            setTextColor(TEXT)
            backgroundTintList = android.content.res.ColorStateList.valueOf(ACCENT)
            setOnClickListener { action() }
        }

    private fun showFallback(message: String) {
        val page = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(dp(28), dp(28), dp(28), dp(28))
            setBackgroundColor(BACKGROUND)
        }
        page.addView(TextView(this).apply {
            text = "Cloud Hub Player"
            textSize = 22f
            setTextColor(TEXT)
        })
        page.addView(TextView(this).apply {
            text = message
            textSize = 14f
            setTextColor(MUTED)
            setPadding(0, dp(16), 0, dp(18))
        })
        page.addView(actionButton("Tentar novamente") {
            val view = playerWebView
            if (view != null) view.loadUrl(currentStartUrl) else recreate()
        })
        page.addView(actionButton("Abrir no navegador") {
            val target = currentStartUrl.ifBlank { profile?.entryUrl.orEmpty() }
            if (target.isNotBlank()) openExternal(target)
        })
        if (profile?.providerId == ProviderCatalog.GEFORCE_NOW_ID) {
            page.addView(actionButton("Abrir app GeForce NOW") { openOfficialProviderApp() })
        }
        setContentView(page)
    }

    private fun applyFullscreenFlags() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.let { controller ->
                controller.systemBarsBehavior =
                    android.view.WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                controller.hide(WindowInsets.Type.systemBars())
            }
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        }
    }

    private fun hideCustomView() {
        val view = customView ?: return
        customView = null
        (view.parent as? ViewGroup)?.removeView(view)
        content.visibility = View.VISIBLE
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        customViewCallback?.onCustomViewHidden()
        customViewCallback = null
    }

    private fun openExternal(url: String) {
        val uri = Uri.parse(url)
        if (uri.scheme != "https" || uri.host.isNullOrBlank()) return
        try {
            startActivity(Intent(Intent.ACTION_VIEW, uri))
        } catch (_: ActivityNotFoundException) {
            Toast.makeText(this, "Nenhum navegador compatível foi encontrado.", Toast.LENGTH_LONG).show()
        } catch (_: SecurityException) {
            Toast.makeText(this, "O Android bloqueou a abertura do link.", Toast.LENGTH_LONG).show()
        }
    }

    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()

    @Suppress("DEPRECATION")
    override fun onBackPressed() {
        if (customView != null) {
            hideCustomView()
            return
        }
        val view = playerWebView
        if (view?.canGoBack() == true) view.goBack() else super.onBackPressed()
    }

    override fun onPause() {
        playerWebView?.onPause()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        playerWebView?.onResume()
    }

    override fun onDestroy() {
        hideCustomView()
        playerWebView?.let { webView ->
            webView.stopLoading()
            webView.webChromeClient = null
            webView.webViewClient = WebViewClient()
            webView.loadUrl("about:blank")
            webView.clearHistory()
            webView.removeAllViews()
            webView.destroy()
        }
        playerWebView = null
        super.onDestroy()
    }
}

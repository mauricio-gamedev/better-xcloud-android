package io.github.mauriciogamedev.cloudhub.providers

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import io.github.mauriciogamedev.cloudhub.XboxCloudActivity

interface CloudProviderAdapter {
    fun launch(activity: Activity, destination: String)
}

class ExternalLinkProviderAdapter : CloudProviderAdapter {
    override fun launch(activity: Activity, destination: String) {
        val uri = Uri.parse(destination)
        if (uri.scheme != "https" || uri.host.isNullOrBlank()) {
            Toast.makeText(activity, "Destino do serviço inválido.", Toast.LENGTH_LONG).show()
            return
        }

        try {
            activity.startActivity(Intent(Intent.ACTION_VIEW, uri))
        } catch (_: ActivityNotFoundException) {
            Toast.makeText(activity, "Nenhum app ou navegador compatível foi encontrado.", Toast.LENGTH_LONG).show()
        } catch (_: SecurityException) {
            Toast.makeText(activity, "O Android bloqueou a abertura deste serviço.", Toast.LENGTH_LONG).show()
        }
    }
}

class XboxCloudProviderAdapter : CloudProviderAdapter {
    override fun launch(activity: Activity, destination: String) {
        val uri = Uri.parse(destination)
        val validHost = uri.scheme == "https" && uri.host == "www.xbox.com"
        val validPath = uri.path?.startsWith("/pt-BR/play") == true ||
            uri.path?.startsWith("/en-US/play") == true

        if (!validHost || !validPath) {
            Toast.makeText(activity, "Endereço do Xbox Cloud não permitido.", Toast.LENGTH_LONG).show()
            return
        }

        activity.startActivity(
            Intent(activity, XboxCloudActivity::class.java)
                .putExtra(XboxCloudActivity.EXTRA_START_URL, uri.toString())
        )
    }
}

object ProviderAdapterRegistry {
    private val external = ExternalLinkProviderAdapter()
    private val xbox = XboxCloudProviderAdapter()

    fun forProvider(provider: CloudProvider): CloudProviderAdapter =
        if (provider.id == ProviderCatalog.XBOX_CLOUD_ID) xbox else external
}

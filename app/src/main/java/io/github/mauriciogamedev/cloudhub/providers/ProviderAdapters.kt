package io.github.mauriciogamedev.cloudhub.providers

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import io.github.mauriciogamedev.cloudhub.CloudPlayerActivity
import io.github.mauriciogamedev.cloudhub.XboxCloudActivity
import java.net.URI

interface CloudProviderAdapter {
    fun launch(activity: Activity, destination: String, gameTitle: String? = null)
}

class ExternalLinkProviderAdapter : CloudProviderAdapter {
    override fun launch(activity: Activity, destination: String, gameTitle: String?) {
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
    override fun launch(activity: Activity, destination: String, gameTitle: String?) {
        val uri = Uri.parse(destination)
        val validHost = uri.scheme == "https" && uri.host.equals("www.xbox.com", ignoreCase = true)
        val path = uri.path?.lowercase().orEmpty()
        val validPath = path.startsWith("/pt-br/play") || path.startsWith("/en-us/play")
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

/** Shared player shell; each service supplies its own HTTPS allowlist. */
class EmbeddedWebProviderAdapter(
    private val providerId: String
) : CloudProviderAdapter {
    override fun launch(activity: Activity, destination: String, gameTitle: String?) {
        val profile = ProviderWebProfiles.forProvider(providerId)
        val uri = runCatching { URI(destination) }.getOrNull()
        if (profile == null || uri == null || !profile.allows(uri)) {
            Toast.makeText(activity, "Endereço do serviço não permitido.", Toast.LENGTH_LONG).show()
            return
        }
        activity.startActivity(
            Intent(activity, CloudPlayerActivity::class.java)
                .putExtra(CloudPlayerActivity.EXTRA_PROVIDER_ID, providerId)
                .putExtra(CloudPlayerActivity.EXTRA_START_URL, uri.toString())
                .putExtra(CloudPlayerActivity.EXTRA_GAME_TITLE, gameTitle)
        )
    }
}

object ProviderAdapterRegistry {
    private val external = ExternalLinkProviderAdapter()
    private val xbox = XboxCloudProviderAdapter()
    private val geforceNow = EmbeddedWebProviderAdapter(ProviderCatalog.GEFORCE_NOW_ID)
    private val boosteroid = EmbeddedWebProviderAdapter(ProviderCatalog.BOOSTEROID_ID)
    private val blacknut = EmbeddedWebProviderAdapter(ProviderCatalog.BLACKNUT_ID)

    fun forProvider(provider: CloudProvider): CloudProviderAdapter =
        when (provider.id) {
            ProviderCatalog.XBOX_CLOUD_ID -> xbox
            ProviderCatalog.GEFORCE_NOW_ID -> geforceNow
            ProviderCatalog.BOOSTEROID_ID -> boosteroid
            ProviderCatalog.BLACKNUT_ID -> blacknut
            else -> external
        }
}

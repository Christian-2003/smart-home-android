package de.christian2003.smarthome.data.view.room

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import coil.ImageLoader
import coil.disk.DiskCache
import coil.request.CachePolicy
import coil.util.CoilUtils
import de.christian2003.smarthome.data.model.SmartHomeRepository
import de.christian2003.smarthome.data.model.cert.CertHandler
import de.christian2003.smarthome.data.model.cert.ClientCert
import de.christian2003.smarthome.data.model.room.ShRoom
import de.christian2003.smarthome.data.model.userinformation.InformationTitle
import de.christian2003.smarthome.data.model.userinformation.InformationType
import de.christian2003.smarthome.data.model.userinformation.UserInformation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttp
import okhttp3.OkHttpClient
import java.net.Socket
import java.security.Principal
import java.security.PrivateKey
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.X509KeyManager
import javax.net.ssl.X509TrustManager


/**
 * Class implements the view model for the page to display the contents of a room.
 */
class RoomViewModel(application: Application): AndroidViewModel(application) {

    /**
     * Attribute stores the preferences in which to store the URL.
     */
    private val preferences = getApplication<Application>().getSharedPreferences("smart_home", Context.MODE_PRIVATE)

    private var imageErrorAdded: Boolean = false

    /**
     * Attribute stores the repository through which to access the data.
     */
    private lateinit var repository: SmartHomeRepository

    /**
     * Attribute stores whether to display warnings.
     */
    var showWarnings: Boolean by mutableStateOf(true)

    /**
     * Attribute stores whether to display errors.
     */
    var showErrors: Boolean by mutableStateOf(true)

    /**
     * Attribute stores the room to display.
     */
    lateinit var room: ShRoom

    /**
     * Attribute stores the items of the room that shall be displayed. This contains user information,
     * infos as well as devices.
     */
    val items: MutableList<Any> = mutableStateListOf()

    lateinit var serverUrl: String

    var imageLoader: ImageLoader? by mutableStateOf(null)


    /**
     * Method initializes the view model.
     *
     * @param repository    Repository from which to source the data.
     * @param position      Position of the room to display.
     */
    fun init(repository: SmartHomeRepository, position: Int) {
        createAuthenticatedImageLoader()
        this.repository = repository
        room = repository.rooms[position]

        showWarnings = preferences.getBoolean("show_warnings", true)
        showErrors = preferences.getBoolean("show_errors", true)
        serverUrl = preferences.getString("server_url", "")!!
        imageErrorAdded = false

        items.clear()
        items.addAll(room.userInformation)
        items.addAll(room.infos)
        items.addAll(room.devices)
    }


    fun onImageError(message: String) {
        if (!imageErrorAdded) {
            imageErrorAdded = true
            items.add(UserInformation(InformationType.ERROR, InformationTitle.Image, message))
            Log.d("Room", "Added user info: " + message)
        }
    }


    private fun createAuthenticatedImageLoader() = viewModelScope.launch(Dispatchers.IO) {
        val context: Context = getApplication<Application>().baseContext
        val certHandler = CertHandler(context)
        val cert = certHandler.getClientCert()
        if (cert == null) {
            Log.d("Cert", "Cert == null")
            return@launch
        }
        Log.d("Cert", "Cert != null")

        val sslContext = SSLContext.getInstance("TLS")
        val keyManager = createKeyManager(cert.key, cert.chain)
        val trustManager = createTrustManager()

        sslContext.init(arrayOf(keyManager), arrayOf(trustManager), null)

        val okHttpClient: OkHttpClient = OkHttpClient.Builder()
            .sslSocketFactory(sslContext.socketFactory, trustManager)
            .hostnameVerifier { _, _ -> true }
            .build()

        imageLoader = ImageLoader.Builder(context)
            .okHttpClient(okHttpClient)
            .crossfade(true)
            .respectCacheHeaders(true)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .diskCache(
                DiskCache.Builder()
                    .directory(context.cacheDir.resolve("image_cache"))
                    .maxSizeBytes(50L * 1024 * 1024) //Max size 50MB
                    .build()
            )
            .build()

        Log.d("Cert", "Created image loader")
    }


    private fun createTrustManager(): X509TrustManager {
        return object: X509TrustManager {
            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) { }

            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) { }

            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
        }
    }


    private fun createKeyManager(privateKey: PrivateKey, chain: Array<X509Certificate>): X509KeyManager {
        return object: X509KeyManager {
            override fun getClientAliases(keyType: String?, issuers: Array<out Principal>?): Array<String> = arrayOf("client")

            override fun chooseClientAlias(keyType: Array<out String>?, issuers: Array<out Principal>?, socket: Socket?): String = "client"

            override fun getServerAliases(keyType: String?, issuers: Array<out Principal>?): Array<String>? = null

            override fun chooseServerAlias(keyType: String?, issuers: Array<out Principal>?, socket: Socket?): String? = null

            override fun getCertificateChain(alias: String?): Array<X509Certificate> = chain

            override fun getPrivateKey(alias: String?): PrivateKey = privateKey
        }
    }

}

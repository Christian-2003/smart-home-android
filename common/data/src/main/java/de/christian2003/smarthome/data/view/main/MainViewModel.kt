package de.christian2003.smarthome.data.view.main

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import de.christian2003.smarthome.data.model.SmartHomeRepository
import de.christian2003.smarthome.data.model.cert.SslTrustResponse
import de.christian2003.smarthome.data.model.room.ShRoom
import de.christian2003.smarthome.data.model.userinformation.UserInformation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request


/**
 * Class implements the view model for the main view.
 */
class MainViewModel(application: Application): AndroidViewModel(application) {

    /**
     * Attribute stores the preferences in which to store the URL.
     */
    private val preferences = getApplication<Application>().getSharedPreferences("smart_home", Context.MODE_PRIVATE)


    /**
     * Attribute stores the repository through which to access the data.
     */
    private lateinit var repository: SmartHomeRepository

    /**
     * Attribute indicates whether the webpage content is currently loading.
     */
    var isLoading: Boolean by mutableStateOf(false)

    /**
     * Attribute stores whether to display warnings.
     */
    var showWarnings: Boolean by mutableStateOf(true)

    /**
     * Attribute stores whether to display errors.
     */
    var showErrors: Boolean by mutableStateOf(true)

    /**
     * Attribute stores the list of rooms.
     */
    var rooms: List<ShRoom> by mutableStateOf(emptyList())

    /**
     * Attribute stores errors that occurred while loading the webpage.
     */
    var infos: List<UserInformation> by mutableStateOf(emptyList())

    /**
     * Attribute stores the SSL trust response.
     */
    var sslTrustResponse: SslTrustResponse? by mutableStateOf(null)

    var isValid: Boolean by mutableStateOf(true)


    /**
     * Method initializes the view model.
     */
    fun init(repository: SmartHomeRepository) {
        showWarnings = preferences.getBoolean("show_warnings", true)
        showErrors = preferences.getBoolean("show_errors", true)
        this.repository = repository
        this.isLoading = repository.isLoading
        this.rooms = repository.rooms
        this.infos = repository.infos
        this.sslTrustResponse = repository.sslTrustResponse
    }


    /**
     * Method starts to reload the data from the server.
     */
    fun restartToFetchData() = viewModelScope.launch {
        if (!isLoading) {
            repository.restartFetchingData()
        }
    }

}

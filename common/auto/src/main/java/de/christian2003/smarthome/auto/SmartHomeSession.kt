package de.christian2003.smarthome.auto

import android.content.Intent
import androidx.annotation.OptIn
import androidx.car.app.Screen
import androidx.car.app.ScreenManager
import androidx.car.app.Session
import androidx.car.app.annotations.ExperimentalCarApi
import de.christian2003.smarthome.auto.screen.LoadingScreen


/**
 * Class implements a session for the car app.
 */
class SmartHomeSession: Session() {

    /**
     * Method creates the main screen for the session.
     *
     * @param intent    Intent through which the session is started.
     * @return          Main screen for the car app.
     */
    @OptIn(ExperimentalCarApi::class)
    override fun onCreateScreen(intent: Intent): Screen {
        val screenManager: ScreenManager = carContext.getCarService(ScreenManager::class.java)
        return LoadingScreen(carContext, screenManager)
    }

}

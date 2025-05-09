package de.christian2003.smarthome.data.model.devices;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Class models an unknown device.
 */
public class ShUnknownDevice extends ShGenericDevice{
    /**
     * Attribute stores the text for the button to turn on the unknown device.
     */
    @Nullable
    private final String onButtonText;

    /**
     * Attribute stores the text for the button to turn off the unknown device.
     */
    @Nullable
    private final String offButtonText;

    /**
     * The milli amp of the unknown device.
     */
    @Nullable
    private final String milliAmp;

    /**
     * The hours of the unknown device.
     */
    @Nullable
    private final String hours;

    /**
     * The wh of the unknown device.
     */
    @Nullable
    private final String wh;

    /**
     * Constructor instantiates a new unknown device.
     *
     * @param name          Name for the unknown device.
     * @param imageUri      URI for the image for the unknown device.
     * @param onButtonText  Text for the button to turn on the unknown device.
     * @param offButtonText Text for the button to turn off the unknown device.
     * @param milliAmp      The milli amp of the unknown device.
     * @param hours The hours of the unknown device.
     * @param wh    The wh of the unknown device.
     */
    public ShUnknownDevice (@NonNull String name, @Nullable String imageUri, @Nullable String onButtonText, @Nullable String offButtonText, @Nullable String milliAmp, @Nullable String hours, @Nullable String wh) {
        super(name, null, imageUri);
        this.onButtonText = onButtonText;
        this.offButtonText = offButtonText;
        this.milliAmp = milliAmp;
        this.hours = hours;
        this.wh = wh;
    }

    /**
     * Method returns the text for the button with which to turn on the unknown device.
     *
     * @return  Text for the button to turn on the unknown device.
     */
    @Nullable
    public String getOnButtonText() {
        return onButtonText;
    }

    /**
     * Method returns the text for the button with which to turn off the unknown device.
     *
     * @return  Text for the button to turn off the unknown device.
     */
    @Nullable
    public String getOffButtonText() {
        return offButtonText;
    }

    /**
     * Method returns the milli amp of the unknown device.
     *
     * @return The milli amp of the unknown device.
     */
    @Nullable
    public String getMilliAmp() {
        return milliAmp;
    }

    /**
     * Method returns the hours of the unknown device.
     *
     * @return  The hours of the unknown device.
     */
    @Nullable
    public String getHours() {
        return hours;
    }

    /**
     * Method returns the wh of the unknown device.
     *
     * @return  The wh of the unknown device.
     */
    @Nullable
    public String getWh() {
        return wh;
    }
}

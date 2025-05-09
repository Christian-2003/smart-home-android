package de.christian2003.smarthome.data.model.devices;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.io.Serializable;


/**
 * Class models a generic smart home device. All other smart home devices must extend this class.
 */
public class ShGenericDevice implements Serializable {

    /**
     * Attribute stores the name of the device. This name is shown to the user.
     */
    @NonNull
    private final String name;

    /**
     * The specifier of the device to distinguish different devices in a room that are the same type..
     */
    @Nullable
    private final String specifier;

    /**
     * Attribute stores the URI for the image to display to the user. This will be {@code null} if
     * the smart home device does not have any image.
     */
    @Nullable
    private final String imageUri;

    /**
     * Constructor instantiates a new generic smart home device.
     *
     * @param name  Name for the device.
     * @param specifier The specifier of the device to distinguish different devices in a room that are the same type.
     * @param imageUri  String that represents the URI for the image to display to the user.
     */
    public ShGenericDevice(@NonNull String name, @Nullable String specifier, @Nullable String imageUri) {
        this.name = name;
        this.specifier = specifier;
        this.imageUri = imageUri;
    }

    /**
     * Method returns the name of the smart home device.
     *
     * @return  Name of the smart home device.
     */
    @NonNull
    public String getName() {
        return name;
    }

    /**
     * Method returns a string that represents the URI for the image to display to the user. The method returns {@code null}
     * if the smart home device does not have any image to display.
     *
     * @return  URI for the image to display to the user.
     */
    @Nullable
    public String getImageUri() {
        return imageUri;
    }

    /**
     * Method returns the specifier of the device. If there is only one device of a type the specifier is null.
     *
     * @return  The specifier of the device.
     */
    @Nullable
    public String getSpecifier() {
        return specifier;
    }

}

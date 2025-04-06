package de.christian2003.smarthome.data.model.extraction.search.room;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.christian2003.smarthome.data.model.devices.ShGenericDevice;
import de.christian2003.smarthome.data.model.devices.ShLight;
import de.christian2003.smarthome.data.model.devices.ShOpening;
import de.christian2003.smarthome.data.model.devices.ShShutter;
import de.christian2003.smarthome.data.model.extraction.search.devices.ShOpeningSearch;
import de.christian2003.smarthome.data.model.extraction.search.devices.ShShutterSearch;
import de.christian2003.smarthome.data.model.extraction.search.devices.ShUnknownDeviceSearch;
import de.christian2003.smarthome.data.model.room.ShInfoText;
import de.christian2003.smarthome.data.model.room.ShRoom;
import de.christian2003.smarthome.data.model.userinformation.InformationTitle;
import de.christian2003.smarthome.data.model.userinformation.InformationType;
import de.christian2003.smarthome.data.model.userinformation.UserInformation;
import de.christian2003.smarthome.data.model.wrapper.RoomDeviceWrapper;
import de.christian2003.smarthome.data.model.wrapper.RoomInfoTextWrapper;

/**
 * Class models a search for a room for the smart home.
 */
public class ShRoomSearch implements Serializable {

    /**
     * Constructor instantiates a new room search object.
     */
    public ShRoomSearch() {

    }

    /**
     * Finds all the rooms of the smart home and returns a list containing all of them.
     *
     * @param document          The document with the source code of the webpage.
     * @return                  Returns a list with all the rooms of the smart home. If no rooms were found an empty list will be returned.
     */

    @NonNull
    public ArrayList<ShRoom> findAllRooms(@NonNull Document document) {
        boolean overallStatus = false;
        ArrayList<ShRoom> shRoomList = new ArrayList<>();

        for (Element script : document.select("script")) {
            String scriptContent = script.html(); // Inhalt des <script>-Tags
            // RegEx für DeviceList
            Pattern pattern = Pattern.compile("var DeviceList = (\\[.*?\\]);", Pattern.DOTALL);
            Matcher matcher = pattern.matcher(scriptContent);

            if (matcher.find()) {
                String deviceListString = matcher.group(1);
                // 1. Entferne die äußeren Klammern und unnötige Leerzeichen
                deviceListString = deviceListString.trim().replaceAll("^\\[|\\]$", "");

                // 2. Zerlege den String in Zeilen
                String[] rows = Arrays.stream(deviceListString.split("],\\s*\\["))
                        .filter(line -> !line.trim().startsWith("//")) // Filtert Kommentarzeilen heraus
                        .toArray(String[]::new);

                // 3. Hauptliste erstellen
                ArrayList<ArrayList<String>> deviceList = new ArrayList<>();

                // 4. Jede Zeile verarbeiten
                for (String row : rows) {
                    // Entferne Anführungszeichen und trenne die Werte per Komma
                    String[] values = row.replace("\"", "").split(",\\s*");

                    // Füge die Werte als Liste zur Hauptliste hinzu
                    deviceList.add(new ArrayList<>(Arrays.asList(values)));

                }

                // Map für die Gruppierung nach Raum
                Map<String, List<ElementAndId>> rooms = new HashMap<>();

                for (ArrayList<String> device : deviceList) {
                    String raum = device.get(1); // Raum (Bad, Wohnzimmer, etc.)
                    ElementAndId elementAndId = new ElementAndId(device.get(2), device.get(5), device.get(6)); // Name & ID

                    if (!device.get(2).isEmpty()) {
                        // Falls der Raum noch nicht existiert, erstelle eine neue Liste

                    }
                    rooms.computeIfAbsent(raum, k -> new ArrayList<>()).add(elementAndId);
                }
                return createRooms(rooms, document);

            }
        }
        /*
        // Find all rooms of the smart home.
        Elements rooms = document.select("div.room");

        // Iterates through all rooms and get their properties and devices.
        for (Element room: rooms) {
            Element roomNameEl = findRoomName(room);

            // Check if a name was found for the room.
            if (roomNameEl != null) {
                String roomName = roomNameEl.text();

                // Check if it was the "room" that displays the "gesamtstatus".
                // There can only be one "gesamtstatus" element.
                if (!overallStatus && roomName.toLowerCase().contains("gesamtstatus")) {
                    overallStatus = true;
                    shRoomList.add(0, parseContentTable(room, roomName, true));
                }
                else {
                    shRoomList.add(parseContentTable(room, roomName, false));
                }
            }
            else {
                // Div container with the class "room" was found but not title of the room could be found.
                String warningDescription = "A div container with the class \"room\" was found but not title of the room could be found.";
                shRoomList.add(new ShRoom("Unknown Room", null, null, new ArrayList<>(Collections.singletonList(new UserInformation(InformationType.WARNING, InformationTitle.UnknownRoom, warningDescription))), false));
            }
        }
        return shRoomList;
        */
        return new ArrayList<ShRoom>();
    }


    public ArrayList<ShRoom> createRooms(Map<String, List<ElementAndId>> rooms, Document document) {
        ArrayList<ShRoom> shRooms = new ArrayList<>();
        for (Object raum : rooms.keySet()) {
            String roomName = raum.toString();

            ArrayList<ShInfoText> infoTexts = new ArrayList<>();
            ArrayList<ShGenericDevice> shGenericDevices = new ArrayList<>();
            // Durch die Liste von ElementAndId iterieren
            for (ElementAndId element : rooms.get(raum)) {
                Element element1 = document.getElementById(element.getElementId());

                String text = null;
                if (element1 != null) {
                    if (element.getElementId().contains("R")) {
                        Element element2 = document.getElementById(element.getElementId() + "_NEXT");

                        if (element2 != null) {
                            text = element2.ownText();
                        }
                        else {
                            text = element1.ownText();
                        }
                    }
                    else {
                        text = element1.ownText();
                    }

                    String src = null;
                    if (!element.getMeasurementType().isEmpty()) {
                        Element element2 = document.getElementById(element.getElementId());

                        if (element2 != null) {
                            Element img = element2.selectFirst("img");

                            if (img != null) {
                                src = img.attr("src");
                            }
                        }
                    }

                    if (src != null) {
                        shGenericDevices.add(new ShGenericDevice(element.getElementName(), text, src));
                    }
                    else {
                        infoTexts.add(new ShInfoText(element.getElementName(), null, text));
                    }
                }
                else {
                    infoTexts.add(new ShInfoText(element.getElementName(), null, "ElementID in DeviceList aber nicht im Quellcode"));
                }
            }
            shRooms.add(new ShRoom(roomName, infoTexts, shGenericDevices, null, false));
        }
        // find gesamtstatus
        Element gesamtstatus = document.getElementById("gesamtstatus");
        if (gesamtstatus != null) {
            Elements rows = gesamtstatus.select("tr");
            ArrayList<ShInfoText> texts = new ArrayList<>();
            for (Element row : rows) {
                Elements columns = row.select("td");

                // Sicherstellen, dass mindestens zwei <td> vorhanden sind
                if (columns.size() >= 2) {
                    String name = columns.get(0).text();
                    String value = columns.get(1).text();
                    texts.add(new ShInfoText(name, null, value));
                }
            }
            shRooms.add(0, new ShRoom("Gesamtstatus", texts, null, null, true));
        }
        return shRooms;
    }


    /**
     * Finds the node in the html code which contains the name of the room.
     *
     * @param room          The room element.
     * @return              Returns the elements which contains the name of the room.
     */
    @Nullable
    public Element findRoomName(@NonNull Element room) {
        return room.select("div span.roomName").first();
    }

    /**
     * Parses the elements that are in the content table of a room.
     *
     * @param room          The element node which contains the name of the room.
     * @param roomName      The name of the room.
     * @param gesamtstatusElement   States if the room display the "gesamtstatus".
     *
     * @return              A list of all the info texts of the room and a list with all warnings/ errors that occurred while gathering the information.
     */
    @NonNull
    public ShRoom parseContentTable(@NonNull Element room, @NonNull String roomName, boolean gesamtstatusElement) {

        // Get the content table and its elements.
        Element contentTable = room.selectFirst("table");
        if (contentTable != null) {
            Elements tableRows = contentTable.select("> tbody > tr");

            if (!tableRows.isEmpty()) {
                ArrayList<ShInfoText> shInfoTexts = new ArrayList<>();
                ArrayList<UserInformation> userInformation = new ArrayList<>();
                ArrayList<ShGenericDevice> shGenericDevices = new ArrayList<>();

                // Find the different info texts and devices of the room.
                for (Element tableRow: tableRows) {
                    Set<String> classNames = tableRow.classNames();
                    if (classNames.contains("infoText")) {
                        RoomInfoTextWrapper roomInformationWrapper = ShInfoTextSearch.createInfoText(tableRow);
                        shInfoTexts.addAll(roomInformationWrapper.getInfoTexts());
                        userInformation.addAll(roomInformationWrapper.getUserInformation());
                    }
                    else if (classNames.contains("shutter")) {
                        RoomDeviceWrapper roomDeviceWrapper = ShShutterSearch.createShutterDevice(tableRow, roomName);
                        shGenericDevices.addAll(roomDeviceWrapper.getDevices());
                        userInformation.addAll(roomDeviceWrapper.getUserInformation());
                    }
                    else if (classNames.contains("opening")) {
                        RoomDeviceWrapper roomDeviceWrapper = ShOpeningSearch.createOpeningDevice(tableRow, roomName);
                        shGenericDevices.addAll(roomDeviceWrapper.getDevices());
                        userInformation.addAll(roomDeviceWrapper.getUserInformation());
                    }
                    else if (classNames.contains("status")) {
                        RoomDeviceWrapper roomDeviceWrapper = ShStatusSearch.gatherStatusContent(tableRow, roomName);
                        shGenericDevices.addAll(roomDeviceWrapper.getDevices());
                        userInformation.addAll(roomDeviceWrapper.getUserInformation());
                    }
                    else {
                        RoomDeviceWrapper roomDeviceWrapper = ShUnknownDeviceSearch.findUnknownDevice(tableRow, roomName);
                        shGenericDevices.addAll(roomDeviceWrapper.getDevices());
                        userInformation.addAll(roomDeviceWrapper.getUserInformation());
                    }
                }
                return new ShRoom(roomName, shInfoTexts, shGenericDevices, userInformation, gesamtstatusElement);
            }
            // Content table was found but it doesn´t contain any rows with information.
            else {
                String warningDescription = "A room was found but no table containing further information to the room could be found. Please check the code of the website and the documentation.";
                return new ShRoom(roomName, null, null, new ArrayList<>(Collections.singletonList(new UserInformation(InformationType.WARNING, InformationTitle.HtmlElementNotLocated, warningDescription))), gesamtstatusElement);
            }
        }
        // Room doesn´t contain a content table.
        else {
            String warningDescription = "A room was found but no table containing further information to the room could be found. Please check the code of the website and the documentation.";
            return new ShRoom(roomName, null, null, new ArrayList<>(Collections.singletonList(new UserInformation(InformationType.WARNING, InformationTitle.HtmlElementNotLocated, warningDescription))), gesamtstatusElement);
        }
    }

    /**
     * Method prints the properties of a ShRoom object.
     *
     * @param room      The room object which properties should be printed.
     */
    public static void printOutRoom(@NonNull ShRoom room) {
        System.out.println("Room name: " + room.getName() + "Länge Infos: " + room.getInfos().size());
        for (ShInfoText shInfoText: room.getInfos()) {
            System.out.println("\tLabel: " + shInfoText.getLabel() + ", Specifier " + shInfoText.getSpecifier() + ", Text: " + shInfoText.getText());
        }
        for (ShGenericDevice shGenericDevice: room.getDevices()) {
            if (shGenericDevice instanceof ShShutter) {
                System.out.println("\tShutter Name: " + shGenericDevice.getName() + ", Specifier: " + ((ShShutter) shGenericDevice).getSpecifier() + ", ButtonText: " + ((ShShutter) shGenericDevice).getSetButtonText() + ", Percentage: " + ((ShShutter) shGenericDevice).getPercentage() + ", Time: " + ((ShShutter) shGenericDevice).getTime());
            }
            if (shGenericDevice instanceof ShOpening) {
                System.out.println("\tOpening Name: " + shGenericDevice.getName() + ", Specifier: " + ((ShOpening) shGenericDevice).getSpecifier() + ", ImageUri: " + (shGenericDevice).getImageUri() + ", Type: " + ((ShOpening) shGenericDevice).getOpeningType() + ", Time: ");
            }
            if (shGenericDevice instanceof ShLight) {
                System.out.println("\tLight Name: " + shGenericDevice.getName() + ", Specifier: " + ((ShLight) shGenericDevice).getSpecifier() + ", ImageUri: " + (shGenericDevice).getImageUri() + ", OnButton: " + ((ShLight) shGenericDevice).getOnButtonText() + ", OffButton: " + ((ShLight) shGenericDevice).getOffButtonText() + ", MilliAmp: " + ((ShLight) shGenericDevice).getMilliAmp());
            }
        }
    }
}

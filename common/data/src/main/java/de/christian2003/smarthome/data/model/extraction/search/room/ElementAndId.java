package de.christian2003.smarthome.data.model.extraction.search.room;

public class ElementAndId {
    private String ElementName;
    private String ElementId;
    private String MeasurementType;

    public ElementAndId(String elementName, String elementId, String measurementType) {
        ElementName = elementName;
        ElementId = elementId;
        MeasurementType =  measurementType;
    }

    public String getElementName() {
        return ElementName;
    }

    public void setElementName(String elementName) {
        ElementName = elementName;
    }

    public String getElementId() {
        return ElementId;
    }

    public void setElementId(String elementId) {
        ElementId = elementId;
    }

    public String getMeasurementType() {
        return MeasurementType;
    }

    public void setMeasurementType(String measurementType) {
        MeasurementType = measurementType;
    }
}

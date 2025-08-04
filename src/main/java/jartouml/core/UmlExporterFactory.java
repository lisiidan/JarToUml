package jartouml.core;

public class UmlExporterFactory {
    public static UmlExporter getExporter(UmlFormat format) {
        return switch (format) {
            case PLANTUML -> new PlantUmlExporter();
            case YUML -> new YumlExporter();
        };
    }
}

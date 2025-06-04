import java.util.List;

public interface UmlExporter {
    String export(List<ClassInformation> classes, List<RelationshipInformation> relationships, UmlExportConfiguration config);
}

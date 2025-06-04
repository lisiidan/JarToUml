import java.util.HashSet;
import java.util.List;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        String jarFileName = args[0];
        String outputFilePath = "output.txt";
        List<Class<?>> classList = ClassExtractor.extract(jarFileName);
        InformationGatherer gatherer = new InformationGatherer(classList);

        gatherer.gatherInformation();

        List<ClassInformation> classInfos = gatherer.getClassListInformation();
        List<RelationshipInformation> relationships = gatherer.getRelationshipListInformation();
        Set<String> ignoredClass = new HashSet<String>();
        // ignoredClass.add("TemperatureSensor");
        ignoredClass.add("java.util");
        ignoredClass.add("java.lang");
        UmlExportConfiguration config = new UmlExportConfiguration(UmlFormat.YUML,false, true,true,true, ignoredClass);

        UmlExporter exporter = UmlExporterFactory.getExporter(config.getFormat());

        String umlOutput = exporter.export(classInfos, relationships, config);

        try (FileWriter writer = new FileWriter(outputFilePath)) {
            writer.write(umlOutput);
            System.out.println("File written successfully!");
        } catch (IOException e) {
            System.err.println("An error occurred while writing to the file:");
            e.printStackTrace();
        }
    }
}
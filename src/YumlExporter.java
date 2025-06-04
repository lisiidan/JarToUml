import java.util.ArrayList;
import java.util.List;

public class YumlExporter implements UmlExporter {
    @Override
    public String export(List<ClassInformation> classes, List<RelationshipInformation> relationships, UmlExportConfiguration config) {
        String umlOutput = "";
        for (ClassInformation ci : classes) {
            if(config.shouldIgnore(ci.className)) // config: shouldIgnore
                continue;

            String nameToUse;                     // config: useFullyQualifiedNames
            if(config.useFullyQualifiedNames)
                nameToUse = ci.className;
            else
                nameToUse = ci.classSimpleName;

            umlOutput += "[" + nameToUse;
            if(config.showAttributes){            // config: showAttributes
                umlOutput += "|";
                List<String> fields = new ArrayList<>();
                for (FieldInformation fi : ci.fieldListInformation) {
                    fields.add(fi.toString());
                }
                umlOutput += String.join(";", fields);
            }
            if(config.showMethodNames){           //config: showMethodNames
                umlOutput += "|";
                List<String> methods = new ArrayList<>();
                for (MethodInformation mi : ci.methodListInformation) {
                    methods.add(mi.toString());
                }
                umlOutput += String.join(";", methods);
            }

            umlOutput += "]\n";
        }

        for(RelationshipInformation ri : relationships) {
            if(config.shouldIgnore(ri.relationshipLvalue) || config.shouldIgnore(ri.relationshipRvalue))
                continue;
            if(!config.drawDependencyRelations && ri.relationshipType.equals("dependency"))
                continue;
            if(!config.useFullyQualifiedNames){
                ri.relationshipLvalue = getSimpleNameFrom(ri.relationshipLvalue);
                ri.relationshipRvalue = getSimpleNameFrom(ri.relationshipRvalue);
            }
            umlOutput = umlOutput + drawRelationshipBasedOnType(ri) + "\n";
        }

        return umlOutput;
    }
    private String getSimpleNameFrom(String fullName) {
        int lastDot = fullName.lastIndexOf('.');
        return lastDot >= 0 ? fullName.substring(lastDot + 1) : fullName;
    }

    public String drawRelationshipBasedOnType(RelationshipInformation ri) {
        return switch (ri.relationshipType) {
            case "extension" -> "[" + ri.relationshipLvalue + "]^[" + ri.relationshipRvalue + "]";
            case "implementation" -> "[" + ri.relationshipLvalue + "]^-.-[" + ri.relationshipRvalue + "]";
            case "association" -> "[" + ri.relationshipLvalue + "]->[" + ri.relationshipRvalue + "]";
            case "dependency" -> "[" + ri.relationshipLvalue + "]-.->[" + ri.relationshipRvalue + "]";
            default -> "Unknown Relationship Type";
        };
    }
}
package jartouml.core;

import jartouml.core.ClassInformation;

import java.util.List;

public class PlantUmlExporter implements UmlExporter {
    @Override
    public String export(List<ClassInformation> classes, List<RelationshipInformation> relationships, UmlExportConfiguration config) {
        String umlOutput = "@startuml\n";
        for(ClassInformation ci : classes) {
            if(config.shouldIgnore(ci.className))
                continue;

            String nameToUse;                           //config: useFullyQualifiedNames
            if(config.useFullyQualifiedNames)
                nameToUse = ci.className;
            else
                nameToUse = ci.classSimpleName;

            umlOutput = umlOutput + "\n" + ci.classType + " " + nameToUse + "{";

            if(config.showAttributes)
                for(FieldInformation fi : ci.fieldListInformation){
                    umlOutput = umlOutput + "\n" + fi.toString();
                }

            if(config.showMethodNames)
                for(MethodInformation mi : ci.methodListInformation){
                    umlOutput = umlOutput + "\n" + mi.toString();
                }

            umlOutput = umlOutput + "\n}\n";
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
        return umlOutput + "\n@enduml";
    }

    private String getSimpleNameFrom(String fullName) {
        int lastDot = fullName.lastIndexOf('.');
        return lastDot >= 0 ? fullName.substring(lastDot + 1) : fullName;
    }

    public String drawRelationshipBasedOnType(RelationshipInformation ri) {
        return switch (ri.relationshipType) {
            case "extension" -> ri.relationshipLvalue + "<|.." + ri.relationshipRvalue;
            case "implementation" -> ri.relationshipLvalue + "<|---" + ri.relationshipRvalue;
            case "association" -> ri.relationshipLvalue + "--->" + ri.relationshipRvalue;
            case "dependency" -> ri.relationshipLvalue + "..>" + ri.relationshipRvalue;
            default -> "Unknown Relationship Type";
        };
    }
}
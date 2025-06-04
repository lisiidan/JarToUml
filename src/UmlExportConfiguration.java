import java.util.Set;

public class UmlExportConfiguration {
    public final UmlFormat format;
    public boolean showMethodNames;
    public boolean showAttributes;
    public boolean useFullyQualifiedNames;
    public boolean drawDependencyRelations;
    public Set<String> ignoredClass;

    public boolean shouldIgnore(String className) {
        return ignoredClass.stream().anyMatch(className::startsWith);
    }
    public UmlExportConfiguration(UmlFormat format, boolean drawDependencyRelations, boolean showMethodNames, boolean showAttributes, boolean useFullyQualifiedNames, Set<String> ignoredClass) {
        this.showMethodNames = showMethodNames;
        this.showAttributes = showAttributes;
        this.useFullyQualifiedNames = useFullyQualifiedNames;
        this.ignoredClass = ignoredClass;
        this.format = format;
    }
    public UmlFormat getFormat() {
        return format;
    }
}

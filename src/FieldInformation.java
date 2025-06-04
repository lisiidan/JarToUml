import java.util.ArrayList;
import java.util.List;

public class FieldInformation {
    public String fieldName;
    public String fieldType;
    public String fieldVisibility;
    public List<String> fieldParametrizedTypes = new ArrayList<>();
    boolean isGenericType = true;

    public FieldInformation(String fieldName) {
        this.fieldName = fieldName;
    }

    public String toString() {
        return fieldVisibility + fieldName + ":" + fieldType;
    } // space?
}

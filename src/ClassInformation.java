import java.util.ArrayList;
import java.util.List;

public class ClassInformation {
    String classSimpleName;
    String className;
    String classType; // Interface, Class, AbstractClass
    public List<MethodInformation> methodListInformation = new ArrayList<>();
    public List<FieldInformation> fieldListInformation = new ArrayList<>();

    public ClassInformation(String className, String classSimpleName) {
        this.className = className;
        this.classSimpleName = classSimpleName;
    }
    public void setClassType(String classType) {
        this.classType = classType;
    }
    public void setMethodListInformation(List<MethodInformation> methodListInformation) {
        this.methodListInformation = methodListInformation;
    }
    public void setFieldListInformation(List<FieldInformation> fieldListInformation) {
        this.fieldListInformation = fieldListInformation;
    }
}

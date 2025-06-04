import java.util.ArrayList;
import java.util.List;

public class MethodInformation {
    public String methodName;
    public String returnType;
    public List<String> parameterTypes = new ArrayList<>();

    public MethodInformation(String methodName) {
        this.methodName = methodName;
    }

    public String toString() {
        return methodName + "()";
    }
}

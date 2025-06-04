import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/*
Information I can gather:
    classes, methods, fields, relationships between classes/interfaces
    4 type of relationships:
        extends
        implements
        association (aggregation and composition will be considered association)
        dependency
    When parametrized types are found, include also its actual type arguments in the diagram.
 */
public class InformationGatherer {
    private List<ClassInformation> classListInformation= new ArrayList<>();
    private List<RelationshipInformation> relationshipListInformation= new ArrayList<>();

    private List<Class<?>> classList= new ArrayList<>();

    public InformationGatherer(List<Class<?>> classList) {
        this.classList = classList;
    }

    public void gatherInformation() {
        for (Class<?> clazz : classList) {
            ClassInformation newClassInformation = new ClassInformation(clazz.getName(), clazz.getSimpleName());


            // Type
            if (clazz.isInterface()) newClassInformation.setClassType("interface");
            else if (clazz.isAnnotation()) newClassInformation.setClassType("annotation");
            else if (clazz.isEnum()) newClassInformation.setClassType("enum");
            else newClassInformation.setClassType("class");

            // Superclass
            Class<?> superclass = clazz.getSuperclass();
            if (superclass != null && !superclass.getName().equals("java.lang.Object")) {
                relationshipListInformation.add(new RelationshipInformation("extension", clazz.getSuperclass().getName(), clazz.getName()));
            }

            // Interfaces
            for (Class<?> iface : clazz.getInterfaces()) {
                relationshipListInformation.add(new RelationshipInformation("implementation", iface.getName(), clazz.getName()));
            }

            for(Field field : clazz.getDeclaredFields()) {
                int modifiers = field.getModifiers();
                FieldInformation fi = new FieldInformation(field.getName());
                if (Modifier.isPublic(modifiers)) fi.fieldVisibility = "+";
                else if (Modifier.isPrivate(modifiers)) fi.fieldVisibility = "-";
                else if (Modifier.isProtected(modifiers)) fi.fieldVisibility = "#";
                else if (Modifier.isStatic(modifiers)) fi.fieldVisibility = "unknownVisibility";

                fi.fieldType = formatType(field.getGenericType(), clazz); // Here association Relationship is created if it is the case
                newClassInformation.fieldListInformation.add(fi);
            }

            // Convert classList to a set of class names for fast lookup
            Set<String> classListNames = classList.stream()
                    .map(Class::getName)
                    .collect(Collectors.toSet());

            for (Method method : clazz.getDeclaredMethods()) {
                MethodInformation mi = new MethodInformation(method.getName());

                // --- Handle return type ---
                mi.returnType = method.getReturnType().getName();
                Type returnType = method.getGenericReturnType();

                if(!clazz.isInterface()) // Dont add dependency if interface??
                    for (String typeName : extractReferencedClassNames(returnType)) {
                        RelationshipInformation ri = new RelationshipInformation("dependency", clazz.getName(), typeName);
                        if (!hasAssociation(clazz.getName(), typeName, relationshipListInformation)
                                && classListNames.contains(typeName)
                                && !relationshipListInformation.contains(ri)) {
                            relationshipListInformation.add(ri);
                        }
                    }

                // --- Handle parameters ---
                if(!clazz.isInterface()) // Dont add dependency if interface??
                    for (Parameter p : method.getParameters()) {
                        mi.parameterTypes.add(p.getType().getName());

                        Type paramType = p.getParameterizedType();
                        for (String typeName : extractReferencedClassNames(paramType)) {
                            RelationshipInformation ri = new RelationshipInformation("dependency", clazz.getName(), typeName);
                            if (!hasAssociation(clazz.getName(), typeName, relationshipListInformation)
                                    && classListNames.contains(typeName)
                                    && !relationshipListInformation.contains(ri)) {
                                relationshipListInformation.add(ri);
                            }
                        }
                    }
                newClassInformation.methodListInformation.add(mi);
            }

            classListInformation.add(newClassInformation);
        }
    }
    public List<ClassInformation> getClassListInformation() {
        return this.classListInformation;
    }
    public List<RelationshipInformation> getRelationshipListInformation() {
        return this.relationshipListInformation;
    }

    private boolean hasAssociation(String source, String target, List<RelationshipInformation> relationships) {
        for (RelationshipInformation rel : relationships) {
            if (rel.relationshipLvalue.equals(source)
                    && rel.relationshipRvalue.equals(target)
                    && rel.relationshipType.equals("association")) {
                return true;
            }
        }
        return false;
    }

    // Recursive function to consider multiple parametrized types
    private String formatType(Type type, Class<?> currentClass) {
        if (type instanceof Class<?> clazz) {
            if (classList.contains(type)) {
                String source = currentClass.getName();
                String target = clazz.getName();
                boolean hasAssociation = false;
                RelationshipInformation dependencyToRemove = null;

                for (RelationshipInformation rel : relationshipListInformation) {
                    if (rel.relationshipLvalue.equals(source) && rel.relationshipRvalue.equals(target)) {
                        if (rel.relationshipType.equals("association")) {
                            hasAssociation = true;
                            break;
                        } else if (rel.relationshipType.equals("dependency")) {
                            dependencyToRemove = rel;
                        }
                    }
                }

                if (!hasAssociation) {
                    if (dependencyToRemove != null) {
                        relationshipListInformation.remove(dependencyToRemove);
                    }
                    relationshipListInformation.add(new RelationshipInformation("association", source, target));
                }
            }
            return clazz.getSimpleName();
        } else if (type instanceof ParameterizedType pt) {
            Type raw = pt.getRawType();
            Type[] args = pt.getActualTypeArguments();

            StringBuilder sb = new StringBuilder();
            sb.append(formatType(raw,currentClass)).append(" of ");

            for (int i = 0; i < args.length; i++) {
                sb.append(formatType(args[i],currentClass));
                if (i < args.length - 1) sb.append(", ");
            }

            return sb.toString();
        } else {
            return type.getTypeName();
        }
    }

    private static Set<String> extractReferencedClassNames(Type type) {
        Set<String> result = new HashSet<>();

        if (type instanceof Class<?> clazz) {
            result.add(clazz.getName());
        } else if (type instanceof ParameterizedType pt) {
            result.addAll(extractReferencedClassNames(pt.getRawType()));
            for (Type arg : pt.getActualTypeArguments()) {
                result.addAll(extractReferencedClassNames(arg));
            }
        }

        return result;
    }
}
//association - field te tip sau parametru de tip la field
//dependancy - la metoda parametru, return type

//ambele = association
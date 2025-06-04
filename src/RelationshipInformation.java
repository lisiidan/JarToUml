import java.util.Objects;

public class RelationshipInformation {
    public String relationshipType; // extension, implementation, association, dependency
    public String relationshipLvalue, relationshipRvalue;
    public RelationshipInformation(String relationshipType, String relationshipLvalue, String relationshipRvalue) {
        this.relationshipType = relationshipType;
        this.relationshipLvalue = relationshipLvalue;
        this.relationshipRvalue = relationshipRvalue;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        RelationshipInformation other = (RelationshipInformation) obj;
        return Objects.equals(relationshipType, other.relationshipType)
                && Objects.equals(relationshipLvalue, other.relationshipLvalue)
                && Objects.equals(relationshipRvalue, other.relationshipRvalue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(relationshipType, relationshipLvalue, relationshipRvalue);
    }
}

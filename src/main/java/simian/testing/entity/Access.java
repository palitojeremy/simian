package simian.testing.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * Access Entity - Merepresentasikan permission/hak akses menu dalam sistem
 * Setiap role memiliki akses berbeda ke menu tertentu
 */
@Entity
@Table(name = "access", uniqueConstraints = {
    @UniqueConstraint(columnNames = "access_name")
})
public class Access implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "access_id")
    private Integer accessId;

    @Column(name = "access_name", nullable = false, length = 100)
    private String accessName;

    @Column(name = "access_description", length = 255)
    private String accessDescription;

    @Column(name = "module_name", length = 50)
    private String moduleName;

    @Column(name = "action_type", length = 50)
    private String actionType;

    @CreationTimestamp
    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @UpdateTimestamp
    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    /**
     * Many-to-Many relationship: Banyak role dapat memiliki akses ini
     */
    @ManyToMany(mappedBy = "accesses", fetch = FetchType.LAZY)
    private Set<Role> roles = new HashSet<>();

    // Constructors
    public Access() {
    }

    public Access(String accessName, String accessDescription, String moduleName, String actionType) {
        this.accessName = accessName;
        this.accessDescription = accessDescription;
        this.moduleName = moduleName;
        this.actionType = actionType;
    }

    // Getters and Setters
    public Integer getAccessId() {
        return accessId;
    }

    public void setAccessId(Integer accessId) {
        this.accessId = accessId;
    }

    public String getAccessName() {
        return accessName;
    }

    public void setAccessName(String accessName) {
        this.accessName = accessName;
    }

    public String getAccessDescription() {
        return accessDescription;
    }

    public void setAccessDescription(String accessDescription) {
        this.accessDescription = accessDescription;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(LocalDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    @Override
    public String toString() {
        return "Access{" +
                "accessId=" + accessId +
                ", accessName='" + accessName + '\'' +
                ", accessDescription='" + accessDescription + '\'' +
                ", moduleName='" + moduleName + '\'' +
                ", actionType='" + actionType + '\'' +
                ", createdDate=" + createdDate +
                ", updatedDate=" + updatedDate +
                '}';
    }
}

package ag.egroup.issuetracker.entities;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "issue_type")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Table(indexes = {
        @Index(name = "ix_issue_type", columnList = "issue_type"),
        @Index(name = "ix_status", columnList = "status")
})
public class Issue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "Title cannot be empty")
    private String title;

    private String description;

    @Column(columnDefinition = "DATE DEFAULT CURRENT_DATE", updatable = false, insertable = false)
/*
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
*/
    private LocalDate createdOn;

    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "developer_id", foreignKey = @ForeignKey(name = "fk_developer_id"))
    private Developer developer;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Issue)) return false;
        Issue issue = (Issue) o;
        return getId() == issue.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}

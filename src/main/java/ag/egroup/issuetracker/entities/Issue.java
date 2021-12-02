package ag.egroup.issuetracker.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "issue_type")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Issue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "Title cannot be empty")
    private String title;

    private String description;

    @Column(columnDefinition = "DATE DEFAULT CURRENT_DATE", updatable = false, insertable = false)
    private LocalDate createdOn;

    @ManyToOne
    private Developer developer;

}

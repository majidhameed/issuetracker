package ag.egroup.issuetracker.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.xml.bind.annotation.XmlTransient;
import java.util.List;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Developer {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private int id;

    @NotBlank(message = "Name cannot be empty")
    private String name;

    @OneToMany(mappedBy = "developer")
    @JsonIgnore
    private List<Issue> issues;

    @Transient
    @Value("${app.developer.avg.capacity:10}")
    private int capacity;

    @PreRemove
    private void preRemove() {
        issues.forEach( issue -> issue.setDeveloper(null));
    }

}

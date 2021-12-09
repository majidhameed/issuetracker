package ag.egroup.issuetracker.entities;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Entity
@DiscriminatorValue(value = "bug")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Bug extends Issue {

    public enum STATUS {
        NEW, VERIFIED, RESOLVED
    }

    public enum PRIORITY {
        MINOR, MAJOR, CRITICAL
    }

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Status cannot be empty")
    private STATUS status;

    @Enumerated(EnumType.ORDINAL)
    @NotNull(message = "Priority cannot be empty")
    private PRIORITY priority;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Bug)) return false;
        if (!super.equals(o)) return false;
        Bug bug = (Bug) o;
        return getId() == bug.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getStatus(), getPriority());
    }
}

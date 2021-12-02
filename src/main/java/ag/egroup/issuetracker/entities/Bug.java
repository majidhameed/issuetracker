package ag.egroup.issuetracker.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@DiscriminatorValue(value = "bug")
@NoArgsConstructor
@AllArgsConstructor
@Data
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

}

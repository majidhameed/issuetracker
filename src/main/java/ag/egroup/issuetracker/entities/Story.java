package ag.egroup.issuetracker.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Entity
@DiscriminatorValue(value = "story")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Story extends Issue {

    public enum STATUS {
        NEW, ESTIMATED, COMPLETED
    }

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Status cannot be empty")
    private STATUS status;

    @Min(value = 0, message = "Estimated Point Value must be greater than or equal to 0")
    @Max(value = 10, message = "Estimated Point Value must be less than or equal to 10")
    private int estimatedPointValue;
}

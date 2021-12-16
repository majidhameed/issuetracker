package ag.egroup.issuetracker.exception;

import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import javax.validation.ConstraintViolation;
import java.util.Set;
import java.util.stream.Collectors;

public class BadRequestException extends RuntimeException {

    public BadRequestException(BindingResult bindingResult) {
        super(String.format("Request has invalid data = [%s]", bindingResult.getAllErrors()
                .stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.joining(", ")))
        );
    }

    public <T> BadRequestException(Set<ConstraintViolation<T>> violations) {
        super(String.format("Request has invalid data = [%s]", violations
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "))));
    }

    public BadRequestException(Exception e) {
        super(e);
    }
}

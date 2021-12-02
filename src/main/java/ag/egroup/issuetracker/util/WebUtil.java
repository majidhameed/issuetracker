package ag.egroup.issuetracker.util;

import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.util.function.Function;
import java.util.stream.Collectors;

public interface WebUtil {
    Function<BindingResult, String> formatError = e -> String.format("Request has invalid data = [%s]", e.getAllErrors()
            .stream()
            .map(ObjectError::getDefaultMessage)
            .collect(Collectors.joining(", ")));

}

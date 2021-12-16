package ag.egroup.issuetracker.advice;

import ag.egroup.issuetracker.exception.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ControllerAdvice
@Slf4j
public class ApplicationAdvice {

    @ExceptionHandler({BadRequestException.class, HttpMessageNotReadableException.class, MethodArgumentNotValidException.class})
    @ResponseBody
    @ResponseStatus(code = BAD_REQUEST)
    String exceptionHandler(Exception ex) {
        log.error(ex.getMessage());
        return ex.getMessage();
    }

}

package ag.egroup.issuetracker.rest.component;

import ag.egroup.issuetracker.exception.BadRequestException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

@Component
public class Mapper<T> {

    private final ObjectMapper objectMapper;
    private final Validator validator;

    @Autowired
    public Mapper(Validator validator, Jackson2ObjectMapperBuilder builder) {
        this.validator = validator;
        this.objectMapper = builder.build();
    }

    public T patch(JsonPatch patch, T object) {
        try {
            JsonNode typeNode = patch.apply(objectMapper.convertValue(object, JsonNode.class));
            T patchedObject = (T) objectMapper.treeToValue(typeNode, object.getClass());
            Set<ConstraintViolation<T>> violations = validator.validate(patchedObject);
            if (!violations.isEmpty()) {
                throw new BadRequestException(violations);
            }
            return patchedObject;
        } catch (JsonProcessingException e) {
            throw new BadRequestException(e);
        } catch (JsonPatchException e) {
            throw new BadRequestException(e);
        }

    }

}

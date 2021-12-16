package ag.egroup.issuetracker.rest;

import ag.egroup.issuetracker.dao.StoryDao;
import ag.egroup.issuetracker.entities.Story;
import ag.egroup.issuetracker.exception.BadRequestException;
import ag.egroup.issuetracker.rest.component.Mapper;

import com.github.fge.jsonpatch.JsonPatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

import static ag.egroup.issuetracker.rest.MediaType.APPLICATION_JSON_PATCH;
import static org.springframework.http.HttpStatus.NOT_FOUND;


@RestController
@RequestMapping(value = "#{'${app.rest.api}'.concat('/')}" + StoryController.ctxController)
public class StoryController {

    @Autowired
    private StoryDao storyDao;

    @Autowired
    private Mapper<Story> mapper;

    @Value("#{'${app.rest.api}'.concat('/')}" + StoryController.ctxController)
    private String path;

    protected static final String ctxController = "stories";

    @PostMapping
    public ResponseEntity<Story> create(@RequestBody @Valid Story story, BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
            Story savedStory = storyDao.save(story);
            return ResponseEntity
                    .created(URI.create(path + "/" + savedStory.getId()))
                    .body(savedStory);
        }
        throw new BadRequestException(bindingResult);
    }

    @GetMapping("/{id}")
    public Story read(@PathVariable int id) {
        return storyDao.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND));
    }

    @GetMapping
    public Iterable<Story> readAll() {
        return storyDao.findAll();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Story> update(@RequestBody @Valid Story story, @PathVariable int id, BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
            if (storyDao.existsById(id)) {
                story.setId(id);
                Story savedStory = storyDao.save(story);
                return ResponseEntity.ok(savedStory);
            }
            return ResponseEntity.notFound().build();
        }
        throw new BadRequestException(bindingResult);
    }

    @PatchMapping(value = "/{id}", consumes = APPLICATION_JSON_PATCH)
    public ResponseEntity<Story> patch(@RequestBody JsonPatch patch, @PathVariable int id) {
            Optional<Story> optionalStory = storyDao.findById(id);
            if (optionalStory.isPresent()) {
                return ResponseEntity.ok(storyDao.save(mapper.patch(patch, optionalStory.get())));
            }
            return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable int id) {
        if (storyDao.existsById(id)) {
            storyDao.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }


}

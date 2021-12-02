package ag.egroup.issuetracker.rest;

import ag.egroup.issuetracker.dao.StoryDao;
import ag.egroup.issuetracker.entities.Story;
import ag.egroup.issuetracker.util.WebUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.net.URI;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;


@RestController
@RequestMapping(value = "#{'${app.rest.api}'.concat('/')}" + StoryController.ctxController)
public class StoryController {

    @Autowired
    private StoryDao storyDao;

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
        throw new ResponseStatusException(BAD_REQUEST,
                String.format("Request has invalid data = [%s]", WebUtil.formatError.apply(bindingResult)));
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
        throw new ResponseStatusException(BAD_REQUEST, String.format("Request has invalid data = [%s]", WebUtil.formatError.apply(bindingResult)));
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

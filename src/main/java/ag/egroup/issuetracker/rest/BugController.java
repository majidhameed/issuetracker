package ag.egroup.issuetracker.rest;

import ag.egroup.issuetracker.dao.BugDao;
import ag.egroup.issuetracker.entities.Bug;
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
@RequestMapping(value = "#{'${app.rest.api}'.concat('/')}" + BugController.ctxController)
public class BugController {

    @Autowired
    private BugDao bugDao;

    @Value("#{'${app.rest.api}'.concat('/')}" + BugController.ctxController)
    private String path;

    protected static final String ctxController = "bugs";

    @Autowired
    private Mapper<Bug> mapper;

    @PostMapping
    public ResponseEntity<Bug> create(@RequestBody @Valid Bug bug, BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
            Bug savedBug = bugDao.save(bug);
            return ResponseEntity
                    .created(URI.create(path + "/" + savedBug.getId()))
                    .body(savedBug);
        }
        throw new BadRequestException(bindingResult);
    }

    @GetMapping("/{id}")
    public Bug read(@PathVariable int id) {
        return bugDao.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND));
    }

    @GetMapping
    public Iterable<Bug> readAll() {
        return bugDao.findAll();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Bug> update(@RequestBody @Valid Bug bug, @PathVariable int id, BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
            if (bugDao.existsById(id)) {
                bug.setId(id);
                return ResponseEntity.ok(bugDao.save(bug));
            }
            return ResponseEntity.notFound().build();
        }
        throw new BadRequestException(bindingResult);
    }

    @PatchMapping(value = "/{id}", consumes = APPLICATION_JSON_PATCH)
    public ResponseEntity<Bug> patch(@RequestBody JsonPatch patch, @PathVariable int id) {
        Optional<Bug> optionalBug = bugDao.findById(id);
        if (optionalBug.isPresent()) {
            return ResponseEntity.ok(bugDao.save(mapper.patch(patch, optionalBug.get())));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable int id) {
        if (bugDao.existsById(id)) {
            bugDao.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }


}

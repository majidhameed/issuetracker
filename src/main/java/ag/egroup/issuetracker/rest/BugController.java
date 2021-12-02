package ag.egroup.issuetracker.rest;

import ag.egroup.issuetracker.dao.BugDao;
import ag.egroup.issuetracker.entities.Bug;
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
@RequestMapping(value = "#{'${app.rest.api}'.concat('/')}" + BugController.ctxController)
public class BugController {

    @Autowired
    private BugDao bugDao;

    @Value("#{'${app.rest.api}'.concat('/')}" + BugController.ctxController)
    private String path;

    protected static final String ctxController = "bugs";


    @PostMapping
    public ResponseEntity<Bug> create(@RequestBody @Valid Bug bug, BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
            Bug savedBug = bugDao.save(bug);
            return ResponseEntity
                    .created(URI.create(path + "/" + savedBug.getId()))
                    .body(savedBug);
        }
        throw new ResponseStatusException(BAD_REQUEST,
                String.format("Request has invalid data = [%s]", WebUtil.formatError.apply(bindingResult)));
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
                Bug savedBug = bugDao.save(bug);
                return ResponseEntity.ok(savedBug);
            }
            return ResponseEntity.notFound().build();
        }
        throw new ResponseStatusException(BAD_REQUEST, WebUtil.formatError.apply(bindingResult));
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

package ag.egroup.issuetracker.rest;

import ag.egroup.issuetracker.dao.DeveloperDao;
import ag.egroup.issuetracker.entities.Developer;
import ag.egroup.issuetracker.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.net.URI;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping(value = "#{'${app.rest.api}'.concat('/')}" + DeveloperController.ctxController)
public class DeveloperController {

    @Autowired
    private DeveloperDao developerDao;

    @Value("#{'${app.rest.api}'.concat('/')}" + DeveloperController.ctxController)
    private String path;

    protected static final String ctxController = "developers";

    @PostMapping
    public ResponseEntity<Developer> create(@RequestBody @Valid Developer developer, BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
            Developer savedDeveloper = developerDao.save(developer);

            return ResponseEntity
                    .created(URI.create(path + "/" + savedDeveloper.getId()))
                    .body(savedDeveloper);
        }
        throw new BadRequestException(bindingResult);
    }

    @GetMapping("/{id}")
    public Developer read(@PathVariable int id) {
        return developerDao.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND));
    }

    @GetMapping
    public Iterable<Developer> readAll() {
        return developerDao.findAll();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Developer> update(@RequestBody @Valid Developer developer, @PathVariable int id, BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
            if (developerDao.existsById(id)) {
                developer.setId(id);
                return ResponseEntity.ok(developerDao.save(developer));
            }
            return ResponseEntity.notFound().build();
        }
        throw new BadRequestException(bindingResult);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable int id) {
        if (developerDao.existsById(id)) {
            developerDao.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

}

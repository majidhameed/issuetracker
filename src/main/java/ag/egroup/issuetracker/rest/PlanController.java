package ag.egroup.issuetracker.rest;

import ag.egroup.issuetracker.ds.Plan;
import ag.egroup.issuetracker.services.PlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@RequestMapping(value = "#{'${app.rest.api}'.concat('/plans')}")
public class PlanController {

    @Autowired
    private PlanService planService;

    @GetMapping
    public Plan plan() {
        return planService.plan(false)
                .orElseThrow(() -> new ResponseStatusException(NO_CONTENT));
    }

    @PutMapping(value={"", "/{assign}"})
    public ResponseEntity<Plan> plan(@PathVariable Optional<Boolean> assign) {
        Optional<Plan> plan = planService.plan(assign.orElse(false));
        if (plan.isPresent()) {
            return ResponseEntity.ok(plan.get());
        }
        return ResponseEntity.noContent().build();
    }
}

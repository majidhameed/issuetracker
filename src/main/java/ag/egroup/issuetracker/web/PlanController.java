package ag.egroup.issuetracker.web;

import ag.egroup.issuetracker.ds.Plan;
import ag.egroup.issuetracker.services.PlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller(value = PlanController.ctxController)
public class PlanController {

    @Autowired
    private PlanService planService;

    protected static final String ctxController = "plan";

    @GetMapping
    public String plan(Model model) {
        Optional<Plan> plan = planService.plan(false);
        model.addAttribute(ctxController, plan);
        return ctxController;
    }

    @PostMapping
    public String plan(@RequestParam Boolean assign, Model model) {
        Optional<Plan> plan = planService.plan(assign);
        model.addAttribute(ctxController, plan);
        return ctxController;
    }
}

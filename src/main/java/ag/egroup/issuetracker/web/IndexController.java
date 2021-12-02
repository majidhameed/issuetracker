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

@Controller
public class IndexController {

    @Autowired
    private PlanService planService;

    @GetMapping
    public String plan(Model model) {
        Optional<Plan> plan = planService.plan(false);
        model.addAttribute("plan", plan);
        return "index";
    }

    @PostMapping
    public String plan(@RequestParam Boolean assign, Model model) {
        Optional<Plan> plan = planService.plan(assign);
        model.addAttribute("plan", plan);
        return "index";
    }
}

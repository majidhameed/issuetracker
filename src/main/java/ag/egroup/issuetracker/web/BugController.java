package ag.egroup.issuetracker.web;

import ag.egroup.issuetracker.dao.DeveloperDao;
import ag.egroup.issuetracker.dao.BugDao;
import ag.egroup.issuetracker.entities.Bug;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller(value = BugController.ctxController)
@RequestMapping(value="/" + BugController.ctxController)
public class BugController {

    private final BugDao bugDao;

    private final DeveloperDao developerDao;

    protected static final String ctxController = "bugs";

    public BugController(BugDao bugDao, DeveloperDao developerDao) {
        this.bugDao = bugDao;
        this.developerDao = developerDao;
    }

    @PostMapping
    public String create(@ModelAttribute @Valid Bug bug, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return readAll(bug, model);
        }
        bugDao.save(bug);
        return "redirect:/" + ctxController;
    }

    @GetMapping
    public String readAll(Bug bug, Model model) {
        model.addAttribute(ctxController, bugDao.findAllByOrderByIdDesc());
        model.addAttribute("statuses", Bug.STATUS.values());
        model.addAttribute("priorities", Bug.PRIORITY.values());
        model.addAttribute("developers", developerDao.findAllByOrderByIdDesc());
        model.addAttribute(bug);
        return ctxController;
    }

    @GetMapping("/{id}")
    public String read(@PathVariable int id, Model model) {
        Bug bug = bugDao.findById(id).orElse(new Bug());
        return readAll(bug, model);
    }

    @PostMapping("/update/{id}")
    public String update(@ModelAttribute @Valid Bug bug, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return readAll(bug, model);
        }
        bugDao.save(bug);
        return "redirect:/" + ctxController;
    }

    @PostMapping(("/delete/{id}"))
    public String delete(@PathVariable int id) {
        bugDao.deleteById(id);
        return "redirect:/" + ctxController;
    }


}

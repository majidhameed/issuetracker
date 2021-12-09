package ag.egroup.issuetracker.web;

import ag.egroup.issuetracker.dao.DeveloperDao;
import ag.egroup.issuetracker.entities.Developer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller(value = DeveloperController.ctxController)
@RequestMapping(value="/" + DeveloperController.ctxController)
public class DeveloperController {

    @Autowired
    private DeveloperDao developerDao;

    protected static final String ctxController = "developers";

    @PostMapping
    public String create(@ModelAttribute @Valid Developer developer, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return readAll(developer, model);
        }
        developerDao.save(developer);
        return "redirect:/" + ctxController;
    }

    @GetMapping
    public String readAll(Developer developer, Model model) {
        model.addAttribute(ctxController, developerDao.findAllByOrderByIdDesc());
        model.addAttribute(developer);
        return ctxController;
    }

    @GetMapping("/{id}")
    public String read(@PathVariable int id, Model model) {
        Developer developer = developerDao.findById(id).orElse(new Developer());
        return readAll(developer, model);
    }

    @PostMapping("/update/{id}")
    public String update(@ModelAttribute @Valid Developer developer, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return readAll(developer, model);
        }
        developerDao.save(developer);
        return "redirect:/" + ctxController;
    }

    @PostMapping(("/delete/{id}"))
    public String delete(@PathVariable int id) {
        developerDao.deleteById(id);
        return "redirect:/" + ctxController;
    }


}

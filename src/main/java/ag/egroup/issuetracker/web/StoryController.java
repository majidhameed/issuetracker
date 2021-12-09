package ag.egroup.issuetracker.web;

import ag.egroup.issuetracker.dao.DeveloperDao;
import ag.egroup.issuetracker.dao.StoryDao;
import ag.egroup.issuetracker.entities.Story;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller(value = StoryController.ctxController)
@RequestMapping(value="/" + StoryController.ctxController)
public class StoryController {

    @Autowired
    private StoryDao storyDao;

    @Autowired
    private DeveloperDao developerDao;

    protected static final String ctxController = "stories";

    @PostMapping
    public String create(@ModelAttribute @Valid Story story, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return readAll(story, model);
        }
        storyDao.save(story);
        return "redirect:/" + ctxController;
    }

    @GetMapping
    public String readAll(Story story, Model model) {
        model.addAttribute(ctxController, storyDao.findAllByOrderByIdDesc());
        model.addAttribute("statuses", Story.STATUS.values());
        model.addAttribute("developers", developerDao.findAllByOrderByIdDesc());
        model.addAttribute(story);
        return ctxController;
    }

    @GetMapping("/{id}")
    public String read(@PathVariable int id, Model model) {
        Story story = storyDao.findById(id).orElse(new Story());
        return readAll(story, model);
    }

    @PostMapping("/update/{id}")
    public String update(@ModelAttribute @Valid Story story, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return readAll(story, model);
        }
        storyDao.save(story);
        return "redirect:/" + ctxController;
    }

    @PostMapping(("/delete/{id}"))
    public String delete(@PathVariable int id) {
        storyDao.deleteById(id);
        return "redirect:/" + ctxController;
    }


}

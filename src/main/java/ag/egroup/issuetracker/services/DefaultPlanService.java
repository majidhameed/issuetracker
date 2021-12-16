package ag.egroup.issuetracker.services;

import ag.egroup.issuetracker.dao.DeveloperDao;
import ag.egroup.issuetracker.dao.StoryDao;
import ag.egroup.issuetracker.ds.Plan;
import ag.egroup.issuetracker.ds.Week;
import ag.egroup.issuetracker.entities.Developer;
import ag.egroup.issuetracker.entities.Story;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service("DefaultPlanService")
@Deprecated(forRemoval = true)
public class DefaultPlanService implements PlanService {

    @Value("${app.developer.avg.capacity:10}")
    private int developerAvgCapacity;

    private final StoryDao storyDao;

    private final DeveloperDao developerDao;

    @Autowired
    public DefaultPlanService(StoryDao storyDao, DeveloperDao developerDao) {
        this.storyDao = storyDao;
        this.developerDao = developerDao;
    }

    /**
     * Creates a plan based on stories that are of STATUS.ESTIMATED
     * @param assign if true it will set developer to the unassigned stories.
     * @return Optional of Plan; if no stories or developers exists then Optional will be empty
     */
    @Override
    public Optional<Plan> plan(boolean assign){
        Iterable<Developer> developers = developerDao.findAll();
        LinkedList<Developer> developerPool = new LinkedList<>();
        developers.forEach(developerPool::add);

        LinkedList<Story> unAssignedStories = storyDao
                .findAllByDeveloperIsNullAndStatusOrderByEstimatedPointValueDesc(Story.STATUS.ESTIMATED);
        LinkedList<Story> assignedStories = storyDao
                .findAllByDeveloperIsNotNullAndStatusOrderByEstimatedPointValueDesc(Story.STATUS.ESTIMATED);
        Set<Developer> developersInPlan = new HashSet<>();

        int totalDevelopers = developerPool.size();
        int totalUnAssignedStories = unAssignedStories.size();
        int totalAssignedStories = assignedStories.size();
        int totalEstimatedPointValue = 0;
        int maxWeeklyCapacity = totalDevelopers * developerAvgCapacity;

        log.debug("Developers count: {}", totalDevelopers);
        log.debug("Estimated unassigned stories found: {}", totalUnAssignedStories);
        log.debug("Estimated assigned stories found: {}", totalAssignedStories);
        log.debug("developerAvgCapacity : {}", developerAvgCapacity);
        log.debug("maxWeeklyCapacity : {}", maxWeeklyCapacity);

        if (totalDevelopers == 0 || (totalUnAssignedStories == 0 && totalAssignedStories==0)) {
            return Optional.empty();
        }

        Plan plan = new Plan();
        Week week = new Week();

        int weeklyCapacity = maxWeeklyCapacity;
        while (!assignedStories.isEmpty()) {
            if (isInCapacity(assignedStories.peekFirst(), weeklyCapacity)) {
                totalEstimatedPointValue += assignedStories.peekFirst().getEstimatedPointValue();
                weeklyCapacity -= assignedStories.peekFirst().getEstimatedPointValue();
                developersInPlan.add(assignedStories.peekFirst().getDeveloper());
                week.addStory(assignedStories.pollFirst());
            } else if (isInCapacity(assignedStories.peekLast(), weeklyCapacity)) {
                totalEstimatedPointValue += assignedStories.peekLast().getEstimatedPointValue();
                weeklyCapacity -= assignedStories.peekLast().getEstimatedPointValue();
                developersInPlan.add(assignedStories.peekLast().getDeveloper());
                week.addStory(assignedStories.pollLast());
            } else {
                plan.addWeek(week);
                week = new Week();
                weeklyCapacity = maxWeeklyCapacity;
            }
        }
        if (weeklyCapacity == maxWeeklyCapacity && !week.getStories().isEmpty()) { // in case else of above isn't executed
            plan.addWeek(week);
            week = new Week();
            weeklyCapacity = maxWeeklyCapacity;
        }

        Developer developer;
        while (!unAssignedStories.isEmpty()) {
            if (developerPool.isEmpty()) {
                developers.forEach(developerPool::add);
            }
            developer = developerPool.pop();
            if (isInCapacity(unAssignedStories.peekFirst(), weeklyCapacity)) {
                totalEstimatedPointValue += unAssignedStories.peekFirst().getEstimatedPointValue();
                weeklyCapacity -= unAssignedStories.peekFirst().getEstimatedPointValue();
                developersInPlan.add(developer);
                assignDeveloperToStory(storyDao, unAssignedStories.peekFirst(), developer, assign);
                week.addStory(unAssignedStories.pollFirst());
            } else if (isInCapacity(unAssignedStories.peekLast(), weeklyCapacity)) {
                totalEstimatedPointValue += unAssignedStories.peekLast().getEstimatedPointValue();
                weeklyCapacity -= unAssignedStories.peekLast().getEstimatedPointValue();
                developersInPlan.add(developer);
                assignDeveloperToStory(storyDao, unAssignedStories.peekLast(), developer, assign);
                week.addStory(unAssignedStories.pollLast());
            } else {
                plan.addWeek(week);
                week = new Week();
                weeklyCapacity = maxWeeklyCapacity;
            }
        }
        if (!week.getStories().isEmpty()) {
            plan.addWeek(week);
        }

        plan.setSummary(String
                .format("Plan of %d week%s with %d developers working on %d stories of %d estimated point value.",
                        plan.getWeeks().size(), plan.getWeeks().size()>1 ? "s" : "",
                        developersInPlan.size(), totalUnAssignedStories+totalAssignedStories, totalEstimatedPointValue));

        return Optional.of(plan);

    }

    private boolean isInCapacity(Story story, int capacity) {
        return capacity >= story.getEstimatedPointValue();
    }


}

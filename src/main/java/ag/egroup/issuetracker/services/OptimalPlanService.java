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

import java.util.*;

@Slf4j
@Service("OptimalPlanService")
public class OptimalPlanService implements PlanService {

    @Value("${app.developer.avg.capacity:10}")
    private int developerAvgCapacity;

    @Autowired
    StoryDao storyDao;

    @Autowired
    DeveloperDao developerDao;

    /**
     * Creates a plan based on stories that are of STATUS.ESTIMATED
     *
     * @param assign if true it will set developer to the unassigned stories.
     * @return Optional of Plan; if no stories or developers exists then Optional will be empty
     */
    @Override
    public Optional<Plan> plan(boolean assign) {

        LinkedList<Story> stories = storyDao.findAllByStatusOrderByEstimatedPointValueDesc(Story.STATUS.ESTIMATED);
        Iterable<Developer> developers = developerDao.findAll();

        Map<Integer, Developer> developerMap = initDeveloperMap(developers);

        if (stories.isEmpty() || developerMap.isEmpty()) { // base case
            log.info("No developers or stories exist in the system");
            return Optional.empty();
        }

        log.debug("Developers count: {}", developerMap.size());
        log.debug("Estimated stories found: {}", stories.size());

        Plan plan = new Plan();
        Week week = new Week();
        Developer developer;
        while (!stories.isEmpty()) {
            if (stories.peekFirst().getDeveloper() != null &&
                    canDeveloperHandleStory(stories.peekFirst(), stories.peekFirst().getDeveloper(), developerMap)) {
                developer = stories.peekFirst().getDeveloper();
                processStory(week, stories.pollFirst(), developer, developerMap, false);
            } else if (stories.peekLast().getDeveloper() != null &&
                    canDeveloperHandleStory(stories.peekLast(), stories.peekLast().getDeveloper(), developerMap)) {
                developer = stories.peekLast().getDeveloper();
                processStory(week, stories.pollLast(), developer, developerMap, false);
            } else if (anyDeveloperHandleStory(stories.peekFirst(), developerMap).isPresent()) {
                developer = anyDeveloperHandleStory(stories.peekFirst(), developerMap).get();
                processStory(week, stories.pollFirst(), developer, developerMap, assign);
            } else if (anyDeveloperHandleStory(stories.peekLast(), developerMap).isPresent()) {
                developer = anyDeveloperHandleStory(stories.peekLast(), developerMap).get();
                processStory(week, stories.pollLast(), developer, developerMap, assign);
            } else { // No other option but to start a new week
                plan.addWeek(week);
                week = new Week();
                developerMap = initDeveloperMap(developers);
            }
        }

        if (!week.getStories().isEmpty()) {
            plan.addWeek(week);
        }

        plan.setSummary(generateSummary(plan));
        return Optional.of(plan);

    }

    /**
     * If any developer has the capacity to handle the story return Optional that Developer otherwise Empty optional.
     *
     * @param story
     * @param developerMap
     * @return
     */
    private Optional<Developer> anyDeveloperHandleStory(Story story, Map<Integer, Developer> developerMap) {
        if (developerMap.isEmpty()) {
            return Optional.empty();
        }

        return developerMap.keySet()
                .stream()
                .filter(id -> isInCapacity(story, developerMap.get(id).getCapacity()))
                .findFirst().map(id -> developerMap.get(id));
    }

    /**
     * Reduces the developer capacity, also removes it from the map if capacity is zero
     *
     * @param story        - whose estimated point value needs to be deducted from developers capacity
     * @param developer    - Developer is looked up from developerMap using id of this object
     * @param developerMap - if the developer's capacity reaches ero it is removed from the map.
     */
    private void reduceDeveloperCapacity(Story story, Developer developer, Map<Integer, Developer> developerMap) {
        Developer actualDeveloper = developerMap.get(developer.getId());
        actualDeveloper.setCapacity(actualDeveloper.getCapacity() - story.getEstimatedPointValue());
        if (actualDeveloper.getCapacity() > 0) {
            developerMap.put(actualDeveloper.getId(), actualDeveloper);
        } else {
            developerMap.remove(actualDeveloper.getId()); // developer's capacity reached.
        }
    }

    /**
     * Checks whether story can be assigned to the given developer also ensures if the developer is in the developer map
     *
     * @param story
     * @param developer
     * @param developerMap
     * @return
     */
    private boolean canDeveloperHandleStory(Story story, Developer developer, Map<Integer, Developer> developerMap) {
        if (!developerMap.containsKey(developer.getId())) {
            return false; // developer's capacity already reached and is removed from Map earlier.
        }
        return isInCapacity(story, developerMap.get(developer.getId()).getCapacity());
    }

    /**
     * Utility method calls reduce developer capacity, call assign developer for assignment, adds story to the week
     * @param week
     * @param story
     * @param developer
     * @param developerMap
     * @param assign
     */
    private void processStory(Week week, Story story, Developer developer, Map<Integer, Developer> developerMap, boolean assign) {
        reduceDeveloperCapacity(story, developer, developerMap);
        assignDeveloperToStory(storyDao, story, developer, assign);
        week.addStory(story);
    }

    /**
     * Utility method to generate the plan summary
     *
     * @param plan
     * @return
     */
    private String generateSummary(Plan plan) {
        Set<Integer> developers = new HashSet<>();
        int stories = 0;
        int pointValue = 0;
        for (Week week : plan.getWeeks()) {
            stories += week.getStories().size();
            for (Story story : week.getStories()) {
                developers.add(story.getDeveloper() == null ? -1 : story.getDeveloper().getId());
                pointValue += story.getEstimatedPointValue();
            }
        }
        developers.remove(-1); // in case of unassigned developer, need not to count

        return String
                .format("Plan of %d week%s with %d developers working on %d stories of %d estimated point value.",
                        plan.getWeeks().size(), plan.getWeeks().size() > 1 ? "s" : "",
                        developers.size(), stories, pointValue);
    }

    /**
     * Intilializes the develoeprMap also sets the avgCapacity transient attribute to default Avg. developer capacity.
     *
     * @param developers
     * @return
     */
    private Map<Integer, Developer> initDeveloperMap(Iterable<Developer> developers) {
        Map<Integer, Developer> map = new LinkedHashMap<>();
        developers.forEach(developer -> {
            developer.setCapacity(developerAvgCapacity);
            map.put(developer.getId(), developer);
        });
        return map;
    }

}

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
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service("OptimalPlanService")
@Primary
public class OptimalPlanService implements PlanService {

    @Value("${app.developer.avg.capacity:10}")
    private int developerAvgCapacity;

    private final StoryDao storyDao;

    private final DeveloperDao developerDao;

    @Autowired
    public OptimalPlanService(StoryDao storyDao, DeveloperDao developerDao) {
        this.storyDao = storyDao;
        this.developerDao = developerDao;
    }

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
            log.info("No developers or estimated stories available for planning.");
            return Optional.empty();
        }

        log.info("Developers count: {}", developerMap.size());
        log.info("Estimated stories found: {}", stories.size());

        Plan plan = new Plan();
        Week week = new Week();

        while (!stories.isEmpty()) { // until no estimated stories left
            if (!isAssignedStoryHandled(stories, developerMap, week) && // Picking assigned not possible
                    !isUnAssignedStoryHandled(stories, developerMap, week, assign) &&
                        !isStoryWithinCapacityAssigned(stories, developerMap, week)) {
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

    private boolean isStoryWithinCapacityAssigned(LinkedList<Story> stories, Map<Integer, Developer> developerMap, Week week) {
        Optional<Story> story  = pickFirstAssignedStoryWithinCapacity(stories, developerMap);
        if (story.isEmpty()) {
            return false;
        }
        processStory(stories, week, story.get(), story.get().getDeveloper(), developerMap, false);
        return true;
    }

    private boolean isAssignedStoryHandled(LinkedList<Story> stories, Map<Integer, Developer> developerMap, Week week) {
        boolean status = false;
        Story story;
        if (stories.peekFirst().getDeveloper() != null &&
                cabAssignedDeveloperHandleStory(stories.peekFirst(), stories.peekFirst().getDeveloper(), developerMap)) {
            story = stories.peekFirst();
            processStory(stories, week, story, story.getDeveloper(), developerMap, false);
            status = true;
        } else if (stories.peekLast().getDeveloper() != null &&
                cabAssignedDeveloperHandleStory(stories.peekLast(), stories.peekLast().getDeveloper(), developerMap)) {
            story = stories.peekLast();
            processStory(stories, week, story, story.getDeveloper(), developerMap, false);
            status = true;
        }
        return status;
    }

    private boolean isUnAssignedStoryHandled(LinkedList<Story> stories, Map<Integer, Developer> developerMap, Week week, boolean assign) {
        Story story = stories.peekFirst();
        Optional<Developer> developer = pickFirstDeveloperWhoCanHandleStory(story, developerMap);
        if (developer.isEmpty()) {
            story = stories.peekLast();
            developer = pickFirstDeveloperWhoCanHandleStory(story, developerMap);
        }
        if (developer.isEmpty()) {
            return false;
        }
        processStory(stories, week, story, developer.get(), developerMap, assign);
        return true;
    }

    /**
     * Picks first assigned story that can be handled this week;
     * if assigned developer has the capacity to handle the story return Optional of Story otherwise of Empty.
     *
     * @param stories
     * @param developerMap
     * @return
     */
    private Optional<Story> pickFirstAssignedStoryWithinCapacity(List<Story> stories, Map<Integer, Developer> developerMap) {
        if (developerMap.isEmpty()) {
            return Optional.empty();
        }
        return stories
                .stream()
                .filter(story -> story.getDeveloper() != null)
                .filter(story -> developerMap.containsKey(story.getDeveloper().getId()))
                .filter(story -> isInCapacity(story, developerMap.get(story.getDeveloper().getId())))
                .findFirst();
    }

    /**
     * Picks first unassigned story that can be handled this week;
     * if any developer (in the pool) has the capacity to handle the story return Optional of Developer otherwise of Empty.
     *
     * @param story
     * @param developerMap
     * @return
     */
    private Optional<Developer> pickFirstDeveloperWhoCanHandleStory(Story story, Map<Integer, Developer> developerMap) {
        if (developerMap.isEmpty() || story.getDeveloper() != null) {
            return Optional.empty();
        }

        return developerMap.values()
                .stream()
                .filter(developer -> isInCapacity(story, developer))
                .findFirst();
    }

    /**
     * Checks whether story can be assigned to the given developer also ensures if the developer is in the developer map
     *
     * @param story
     * @param developer
     * @param developerMap
     * @return
     */
    private boolean cabAssignedDeveloperHandleStory(Story story, Developer developer, Map<Integer, Developer> developerMap) {
        if (developerMap.isEmpty() || !developerMap.containsKey(developer.getId())) {
            return false; // developer's capacity already reached and is removed from Map earlier.
        }
        return isInCapacity(story, developerMap.get(developer.getId()));
    }

    /**
     * Reduces the developer capacity, also removes it from the map if capacity is zero
     *
     * @param story        - whose estimated point value needs to be deducted from developers capacity
     * @param developer    - Developer is looked up from developerMap using id of this object
     * @param developerMap - if the developer's capacity reaches ero it is removed from the map.
     */
    private void reduceDeveloperCapacityMaybeRemoveDeveloper(Story story, Developer developer, Map<Integer, Developer> developerMap) {
        Developer actualDeveloper = developerMap.get(developer.getId());
        actualDeveloper.setCapacity(actualDeveloper.getCapacity() - story.getEstimatedPointValue());
        if (actualDeveloper.getCapacity() > 0) {
            developerMap.put(actualDeveloper.getId(), actualDeveloper);
        } else {
            developerMap.remove(actualDeveloper.getId()); // developer's capacity reached.
        }
    }

    /**
     * Removes story from list, calls reduce developer capacity, call assign developer for assignment, adds story to the week
     *
     * @param stories
     * @param week
     * @param story
     * @param developer
     * @param developerMap
     * @param assign
     */
    private void processStory(List<Story> stories, Week week, Story story, Developer developer, Map<Integer, Developer> developerMap, boolean assign) {
        stories.remove(story);
        reduceDeveloperCapacityMaybeRemoveDeveloper(story, developer, developerMap);
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
        Set<Integer> developerSet = new HashSet<>();
        int stories = 0;
        int pointValue = 0;
        for (Week week : plan.getWeeks()) {
            stories += week.getStories().size();
            pointValue += week.getWorkLoad();
            for (Story story : week.getStories()) {
                developerSet.add(story.getDeveloper() == null ? -1 : story.getDeveloper().getId());
            }
        }
        developerSet.remove(-1); // in case of unassigned developer, need not to count

        return String
                .format("Plan of %d week%s with %d developers working on %d stories of total %d estimate point value.",
                        plan.getWeeks().size(), plan.getWeeks().size() > 1 ? "s" : "",
                        developerSet.size(), stories, pointValue);
    }

    /**
     * Initializes the developerMap also sets the avgCapacity transient attribute to default Avg. developer capacity.
     *
     * @param developers
     * @return
     */
    private Map<Integer, Developer> initDeveloperMap(Iterable<Developer> developers) {
        Map<Integer, Developer> developerMap = new LinkedHashMap<>();
        developers.forEach(developer -> {
            developer.setCapacity(developerAvgCapacity);
            developerMap.put(developer.getId(), developer);
        });
        return developerMap;
    }
}

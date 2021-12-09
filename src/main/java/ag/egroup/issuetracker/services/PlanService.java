package ag.egroup.issuetracker.services;

import ag.egroup.issuetracker.dao.StoryDao;
import ag.egroup.issuetracker.ds.Plan;
import ag.egroup.issuetracker.entities.Developer;
import ag.egroup.issuetracker.entities.Story;

import java.util.Optional;

public interface PlanService {

    Optional<Plan> plan(boolean assign);

    /**
     * Returns true developer has the capacity equal to or greater than the required story estimated point value.
     * @param story
     * @param developer
     * @return
     */
    default boolean isInCapacity(Story story, Developer developer) {
        return developer.getCapacity() >= story.getEstimatedPointValue();
    }

    /**
     * Assigns the developer to the story iff it's not already assigned and given assign flag is true
     * @param storyDao
     * @param story
     * @param developer
     * @param assign
     */
    default void assignDeveloperToStory(StoryDao storyDao, Story story, Developer developer, boolean assign) {
        if (assign && story.getDeveloper()==null) {
            story.setDeveloper(developer);
            storyDao.save(story);
        }
    }

}

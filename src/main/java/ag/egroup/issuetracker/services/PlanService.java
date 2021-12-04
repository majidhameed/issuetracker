package ag.egroup.issuetracker.services;

import ag.egroup.issuetracker.dao.StoryDao;
import ag.egroup.issuetracker.ds.Plan;
import ag.egroup.issuetracker.entities.Developer;
import ag.egroup.issuetracker.entities.Story;

import java.util.Optional;

public interface PlanService {

    Optional<Plan> plan(boolean assign);

    default boolean isInCapacity(Story story, int capacity) {
        return capacity >= story.getEstimatedPointValue();
    }

    default void assignDeveloperToStory(StoryDao storyDao, Story story, Developer developer, boolean assign) {
        if (assign && story.getDeveloper()==null) {
            story.setDeveloper(developer);
            storyDao.save(story);
        }
    }

}

package ag.egroup.issuetracker.ds;

import ag.egroup.issuetracker.entities.Story;

import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;


public class Week {

    private Set<Story> stories;
    private int workLoad;

    public Week() {
        this.stories = new TreeSet<>(Comparator.comparingInt(Story::getId));
    }

    public void addStory(Story story) {
        this.stories.add(story);
        this.workLoad += story.getEstimatedPointValue();
    }

    public Set<Story> getStories() {
        return Collections.unmodifiableSet(this.stories);
    }

    public int getWorkLoad() {
        return workLoad;
    }
}

package ag.egroup.issuetracker.ds;

import ag.egroup.issuetracker.entities.Story;

import java.util.*;


public class Week {

    private Set<Story> stories;

    public Week() {
        this.stories = new TreeSet<>(Comparator.comparingInt(Story::getId));
    }

    public void addStory(Story story) {
        this.stories.add(story);
    }

    public Set<Story> getStories() {
        return Collections.unmodifiableSet(this.stories);
    }

}

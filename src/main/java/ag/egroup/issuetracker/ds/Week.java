package ag.egroup.issuetracker.ds;

import ag.egroup.issuetracker.entities.Story;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


public class Week {

    private List<Story> stories;

    public Week() {
        this.stories = new LinkedList<>();
    }

    public void addStory(Story story) {
        this.stories.add(story);
    }

    public List<Story> getStories() {
        return Collections.unmodifiableList(this.stories);
    }

}

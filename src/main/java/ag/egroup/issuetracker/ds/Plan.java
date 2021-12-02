package ag.egroup.issuetracker.ds;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


public class Plan {

    private String summary;

    private List<Week> weeks;

    public Plan() {
        this.weeks = new LinkedList<>();
    }

    public String getSummary() {
        return summary;
    }
    public void setSummary(String summary) {
        this.summary = summary;
    }

    public void addWeek(Week week) {
        this.weeks.add(week);
    }
    public List<Week> getWeeks() {
        return Collections.unmodifiableList(this.weeks);
    }

}

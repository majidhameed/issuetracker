package ag.egroup.issuetracker.dao;

import ag.egroup.issuetracker.entities.Bug;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.LinkedList;

@Repository
public interface BugDao extends CrudRepository<Bug, Integer> {
    Iterable<Bug> findAllByOrderByIdDesc();

    LinkedList<Bug> findAllByStatusOrderByPriorityDesc(Bug.STATUS status);

}

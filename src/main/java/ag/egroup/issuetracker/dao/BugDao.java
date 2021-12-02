package ag.egroup.issuetracker.dao;

import ag.egroup.issuetracker.entities.Bug;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BugDao extends CrudRepository<Bug, Integer> {
    Iterable<Bug> findAllByOrderByIdDesc();

}

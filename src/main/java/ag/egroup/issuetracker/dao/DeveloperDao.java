package ag.egroup.issuetracker.dao;

import ag.egroup.issuetracker.entities.Developer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeveloperDao extends CrudRepository<Developer, Integer> {
    Iterable<Developer> findAllByOrderByIdDesc();
}

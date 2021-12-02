package ag.egroup.issuetracker.dao;

import ag.egroup.issuetracker.entities.Story;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.LinkedList;

@Repository
public interface StoryDao extends CrudRepository<Story, Integer> {

    LinkedList<Story> findAllByDeveloperIsNotNullAndStatusOrderByEstimatedPointValueDesc(Story.STATUS status);
    LinkedList<Story> findAllByDeveloperIsNullAndStatusOrderByEstimatedPointValueDesc(Story.STATUS status);

    Iterable<Story> findAllByOrderByIdDesc();


}

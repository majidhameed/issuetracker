package ag.egroup.issuetracker.dao;

import ag.egroup.issuetracker.entities.Story;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.LinkedList;

@Repository
public interface StoryDao extends CrudRepository<Story, Integer> {

    LinkedList<Story> findAllByDeveloperIsNotNullAndStatusOrderByEstimatedPointValueDesc(Story.STATUS status);

    LinkedList<Story> findAllByDeveloperIsNullAndStatusOrderByEstimatedPointValueDesc(Story.STATUS status);

    LinkedList<Story> findAllByStatusOrderByEstimatedPointValueDesc(Story.STATUS status);

    @Query("SELECT min(estimatedPointValue) FROM Story WHERE status=:status")
    int minEstimatePointValueStory(@Param("status") Story.STATUS status);

    @Query("SELECT max(estimatedPointValue) FROM Story WHERE status=:status")
    int maxEstimatePointValueStory(@Param("status") Story.STATUS status);

    Iterable<Story> findAllByOrderByIdDesc();

}

/**
 *
 */
package one.tracking.framework.repo;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import one.tracking.framework.entity.meta.IntervalType;
import one.tracking.framework.entity.meta.ReleaseStatusType;
import one.tracking.framework.entity.meta.ReminderType;
import one.tracking.framework.entity.meta.Survey;

/**
 * @author Marko Voß
 *
 */
public interface SurveyRepository extends CrudRepository<Survey, Long> {

  boolean existsByNameId(String nameId);

  List<Survey> findByNameId(String nameId);

  Optional<Survey> findTopByNameIdOrderByVersionDesc(String nameId);

  Optional<Survey> findTopByNameIdAndReleaseStatusOrderByVersionDesc(String nameId, ReleaseStatusType status);

  boolean existsTopByNameIdAndReleaseStatusOrderByVersionDesc(String nameId, ReleaseStatusType status);

  List<Survey> findByReleaseStatusOrderByNameIdAscVersionDesc(ReleaseStatusType status);

  List<Survey> findByNameIdOrderByVersionDesc(String nameId);

  List<Survey> findByNameIdAndReleaseStatusNotOrderByVersionDesc(String nameId, ReleaseStatusType status);

  List<Survey> findByReleaseStatusAndReminderTypeNotAndIntervalTypeNotOrderByNameIdAscVersionDesc(
      ReleaseStatusType status,
      ReminderType reminderType,
      IntervalType intervalType);

  List<Survey> findByNameIdAndReleaseStatusAndReminderTypeNotAndIntervalTypeNotOrderByNameIdAscVersionDesc(
      String nameId,
      ReleaseStatusType status,
      ReminderType reminderType,
      IntervalType intervalType);

  @Query(value = "SELECT s FROM Survey s " +
      "WHERE s.releaseStatus = 'EDIT'" +
      "OR s.version = ( " +
      "  SELECT MAX(x.version) " +
      "  FROM Survey x " +
      "  WHERE x.nameId = s.nameId AND x.releaseStatus = 'RELEASED'" +
      "  AND (x.intervalStart < NOW() OR x.intervalStart IS NULL)" +
      "  )" +
      "OR s.version = ( " +
      "  SELECT MAX(x.version) " +
      "  FROM Survey x " +
      "  WHERE x.nameId = s.nameId AND x.releaseStatus = 'RELEASED'" +
      "  AND x.intervalStart >= NOW()" +
      "  )" +
      "ORDER BY s.nameId ASC, s.version ASC")
  List<Survey> findCurrentVersions();

  @Query(value = "SELECT DISTINCT s.nameId FROM Survey s ORDER BY s.nameId ASC")
  List<String> findAllNameIds();

  @Query(value = "SELECT DISTINCT s.nameId FROM Survey s WHERE s.dependsOn = ?1 ORDER BY s.nameId ASC")
  List<String> findAllNameIdsByDependsOn(String nameId);

  @Query(value = "SELECT CASE WHEN COUNT(s) > 0 THEN TRUE ELSE FALSE END FROM Survey s WHERE s.dependsOn = ?1")
  boolean existsAnyNameIdByDependsOn(String nameId);
}

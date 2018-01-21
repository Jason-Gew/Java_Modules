package gew.dataStorage.repository;

import gew.dataStorage.entity.SensorRecord;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Definition of common - unique service methods for JPA.
 * @author Jason/GeW
 */
public interface SensorRecordRepository extends CrudRepository<SensorRecord, Integer>
{
    List<SensorRecord> findRecordsByIdBetween(final Integer begin, final Integer end);
    List<SensorRecord> findRecordsByNote(final String note);
}

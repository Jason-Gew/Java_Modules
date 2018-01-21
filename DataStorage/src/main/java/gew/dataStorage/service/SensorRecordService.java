package gew.dataStorage.service;

import gew.dataStorage.entity.SensorRecord;

import java.util.List;

/**
 * @author Jason/GeW
 */
public interface SensorRecordService
{
    List<SensorRecord> getAllRecords();
    List<SensorRecord> getRecordsByIdBetween(Integer begin, Integer end);
    List<SensorRecord> getRecordsByNote(String note);
    String addRecord(SensorRecord record);
    String updateRecord(Integer id, SensorRecord record);
    String deleteRecordById(Integer id);
}

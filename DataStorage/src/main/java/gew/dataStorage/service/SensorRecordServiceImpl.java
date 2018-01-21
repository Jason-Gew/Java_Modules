package gew.dataStorage.service;

import gew.dataStorage.entity.SensorRecord;
import gew.dataStorage.repository.SensorRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Jason/GeW
 */
@Service
public class SensorRecordServiceImpl implements SensorRecordService
{
    @Autowired
    private SensorRecordRepository repository;

    public final static String SUCCESS = "SUCCESS";
    public final static String FAILURE = "FAIL";
    public final static String UNKNOWN = "UNKNOWN";

    @Override
    public List<SensorRecord> getAllRecords()
    {
        List<SensorRecord> records = new ArrayList<>();
        try
        {
            repository.findAll().forEach(records::add);
            return records;
        }
        catch (Exception err)
        {
            System.err.println(err.toString());
            return records;
        }
    }

    @Override
    public List<SensorRecord> getRecordsByIdBetween(Integer begin, Integer end)
    {
        List<SensorRecord> records = new ArrayList<>();
        try
        {
            Iterable<SensorRecord> results = repository.findRecordsByIdBetween(begin, end);
            results.forEach(records::add);
            return records;
        }
        catch (Exception err)
        {
            System.err.println("Get Records By Id Between Failed: " + err.toString());
            return records;
        }
    }

    @Override
    public List<SensorRecord> getRecordsByNote(String note)
    {
        List<SensorRecord> records = new ArrayList<>();
        try
        {
            Iterable<SensorRecord> results = repository.findRecordsByNote(note);
            results.forEach(records::add);
            return records;
        }
        catch (Exception err)
        {
            System.err.println("Get Records By Note Failed: " + err.toString());
            return records;
        }
    }

    @Override
    @Transactional
    public String addRecord(SensorRecord record)
    {
        try
        {
            repository.save(record);
            return SUCCESS;
        }
        catch(Exception err)
        {
            System.err.println("Add Record Failed: " + err.toString());
            return err.getMessage();
        }
    }

    @Override
    @Transactional
    public String updateRecord(Integer id, SensorRecord record)
    {
        try
        {
            if(repository.exists(id))
            {
                repository.save(record);
                return SUCCESS;
            }
            else
            {
                return FAILURE + ": Record Does Not Exist";
            }
        }
        catch(Exception err)
        {
            System.err.println("Update Record Failed: " + err.toString());
            return err.getMessage();
        }
    }

    @Override
    public String deleteRecordById(Integer id)
    {
        try
        {
            repository.delete(id);
            return SUCCESS;
        }
        catch(Exception err)
        {
            System.err.println("Delete Record Failed: " + err.toString());
            return err.getMessage();
        }
    }
}

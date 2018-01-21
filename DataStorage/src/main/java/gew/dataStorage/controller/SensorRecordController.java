package gew.dataStorage.controller;

import gew.dataStorage.entity.RESTResponse;
import gew.dataStorage.entity.SensorRecord;
import gew.dataStorage.service.SensorRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Jason/GeW
 */
@RestController
@RequestMapping("/dataStorage/sensorRecords")
public class SensorRecordController
{
    @Autowired
    SensorRecordService service;

    @GetMapping(value = "all", produces = "application/json")
    public ResponseEntity<RESTResponse> getAllRecords()
    {
        HttpStatus status;
        RESTResponse response;
        try
        {
            List<SensorRecord> records = service.getAllRecords();
            response = new RESTResponse(RESTResponse.SUCCESS, "Successfully Get All Records",
                    records.size(), records);
            status = HttpStatus.OK;
        }
        catch(Exception err)
        {
            response = new RESTResponse(RESTResponse.FAILURE, "Get All Records Failed: " + err.getMessage(), null);
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(response, status);
    }

    @PostMapping(value = "add", produces = "application/json")
    public ResponseEntity<RESTResponse> addRecord(@RequestBody final SensorRecord record)
    {
        HttpStatus status;
        RESTResponse response;
        try
        {
            String result = service.addRecord(record);
            if(result.equalsIgnoreCase(RESTResponse.SUCCESS))
            {
                response = new RESTResponse(RESTResponse.SUCCESS, "Successfully Add Records",
                        null);
                status = HttpStatus.OK;
            }
            else
            {
                response = new RESTResponse(RESTResponse.FAILURE, "Add Record Failed: " + result,
                        null);
                status = HttpStatus.OK;
            }
        }
        catch(Exception err)
        {
            response = new RESTResponse(RESTResponse.FAILURE, "Add Record Failed: " + err.getMessage(), null);
            status = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(response, status);
    }

    @PutMapping(value = "update", produces = "application/json")
    public ResponseEntity<RESTResponse> updateRecord(@RequestBody final SensorRecord record)
    {
        HttpStatus status;
        RESTResponse response;
        try
        {
            if(record.getId() == null)
            {
                response = new RESTResponse(RESTResponse.FAILURE, "Update Record Failed: Invalid ID", null);
                status = HttpStatus.BAD_REQUEST;
            }
            else
            {
                String result = service.updateRecord(record.getId(), record);
                if(result.equalsIgnoreCase(RESTResponse.SUCCESS))
                {
                    response = new RESTResponse(RESTResponse.SUCCESS, "Successfully Update Records: "+record.getId().toString(),
                            null);
                    status = HttpStatus.OK;
                }
                else
                {
                    response = new RESTResponse(RESTResponse.FAILURE, "Add Record Failed: " + result, null);
                    status = HttpStatus.BAD_REQUEST;
                }
            }
        }
        catch(Exception err)
        {
            response = new RESTResponse(RESTResponse.FAILURE, "Add Record Failed: " + err.getMessage(), null);
            status = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(response, status);
    }

    @DeleteMapping(value = "remove", produces = "application/json")
    public ResponseEntity<RESTResponse> removeRecord(@RequestParam(value = "id") final Integer id)
    {
        HttpStatus status;
        RESTResponse response;
        try
        {
            String result = service.deleteRecordById(id);
            if(result.equalsIgnoreCase(RESTResponse.SUCCESS))
            {
                response = new RESTResponse(RESTResponse.SUCCESS, "Successfully Remove Records: "+id.toString(),
                        null);
                status = HttpStatus.OK;
            }
            else
            {
                response = new RESTResponse(RESTResponse.FAILURE, "Remove Record Failed: " + result,
                        null);
                status = HttpStatus.BAD_REQUEST;
            }
        }
        catch(Exception err)
        {
            response = new RESTResponse(RESTResponse.UNKNOWN, "Remove Record Failed: " + err.getMessage(), null);
            status = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(response, status);
    }
}

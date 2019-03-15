package gew.data.warehouse.gps.controller;


import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.Expressions;
import gew.data.warehouse.gps.config.DataWarehouseConfig;
import gew.data.warehouse.gps.model.DeviceConfig;
import gew.data.warehouse.gps.model.DeviceUser;
import gew.data.warehouse.gps.model.GPSData;
import gew.data.warehouse.gps.model.GPSDataDto;
import gew.data.warehouse.gps.model.RestResponse;
import gew.data.warehouse.gps.model.Status;
import gew.data.warehouse.gps.service.DeviceConfigService;
import gew.data.warehouse.gps.service.DeviceUserService;
import gew.data.warehouse.gps.service.GPSDataService;
import gew.data.warehouse.gps.util.RemoteClientUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.QPageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;


/**
 * GPS Data REST Controller for query, insertion, batch deletion.
 * @author Jason/GeW
 * @since 2019-03-12
 */
@Log4j2
@RestController
@Api(value="GPSData", tags="GPS Data Operations")
public class GPSDataController {

    @Autowired
    private GPSDataService dataService;

    @Autowired
    private DeviceUserService userService;

    @Autowired
    private DeviceConfigService configService;

    @Autowired
    private DataWarehouseConfig dataWarehouseConfig;

    private static final Long TIMESTAMP_MIN = 946684800L;       // 2001-01-01


    private int fixPageNum(int page) {
        if (page < 0) {
            page = 0;
        } else if (page > 0) {
            page--;                 // Default page starts from 0
        }
        return page;
    }

    private int fixPageSize(int size) {
        if (size <= 0) {
            size = dataWarehouseConfig.getDefaultPageSize();
        } else if (size > dataWarehouseConfig.getMaxPageSize()) {
            size = dataWarehouseConfig.getDefaultPageSize();
        }
        return size;
    }

    private Pageable preparePageRequest(int page, int size, String order) {
        // Prepare for page request
        Pageable pageRequest;
        if (dataWarehouseConfig.getSortOrderByTime().equalsIgnoreCase("DESC")
                && order.equalsIgnoreCase("DESC")) {
            pageRequest = new QPageRequest(page, size, new OrderSpecifier<>(Order.DESC,
                    Expressions.asString("unixTimestamp")));
        } else {
            pageRequest = new QPageRequest(page, size);        // New Implementation of Pageable Interface
        }
        return pageRequest;
    }


    @ApiOperation(value = "Add GPS Data // Token May Need")
    @PostMapping(value = "/api/data/gps/add", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestResponse> addGpsData(@RequestBody GPSDataDto tempData,
                                                   @RequestHeader(value = "username", required = false) String username,
                                                   @RequestHeader(value = "deviceID", required = false) String deviceId,
                                                   @RequestHeader(value = "token", required = false) String token,
                                                   HttpServletRequest httpServletRequest) {
        RestResponse response;
        HttpStatus httpStatus;
        String clientIp = RemoteClientUtil.getClientIpAddress(httpServletRequest);
        log.info("Received Insert GPS Data [{}] Request From: {}", tempData, clientIp);
        if ((tempData.getUsername() == null && username == null)
                || (tempData.getDeviceId() == null && deviceId == null)) {
            response = new RestResponse(400, Status.FAIL, "Invalid username or deviceID");
            httpStatus = HttpStatus.BAD_REQUEST;
            return new ResponseEntity<>(response, httpStatus);
        }
        if (tempData.getUsername() == null) {
            tempData.setUsername(username);
        }
        if (tempData.getDeviceId() == null) {
            tempData.setDeviceId(deviceId);
        }
        try {
            DeviceUser user = userService.getDeviceUser(tempData.getUsername());
            if (user == null) {
                log.warn("Insert GPS Data Failed: No User Has Been Found: [{}]", tempData.getUsername());
                response = new RestResponse(400, Status.FAIL, "No User Has Been Found");
                httpStatus = HttpStatus.NOT_FOUND;
                return new ResponseEntity<>(response, httpStatus);
            } else if (!user.isActive()) {
                log.error("Insert GPS Data Failed: User is not Active", tempData.getUsername());
                response = new RestResponse(404, Status.FAIL, "User is Not Active");
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }
            DeviceConfig config = configService.getDeviceConfig(user, tempData.getDeviceId());
            if (config == null) {
                log.warn("No Config Found for User [{}] and Device [{}]",
                        tempData.getUsername(), tempData.getDeviceId());
                response = new RestResponse(401, Status.FAIL, "No Config Found for User with DeviceID: " +
                        tempData.getDeviceId());
                httpStatus = HttpStatus.NOT_FOUND;
                return new ResponseEntity<>(response, httpStatus);
            }
            if (!config.isEnable()) {
                log.error("Add GPS Data Failed for User [{}] and Device [{}]: Device is not enabled",
                        tempData.getUsername(), tempData.getDeviceId());
                response = new RestResponse(454, Status.FAIL, "Device is Not Enabled");
                httpStatus = HttpStatus.FORBIDDEN;
                return new ResponseEntity<>(response, httpStatus);
            }
            GPSData data = new GPSData(user, config);
            data.autoFulfil(tempData);
            if (dataWarehouseConfig.getAutoConvertDatetime() && data.getUnixTimestamp() == null) {
                data.autoDateTimeConvert();
            }
            GPSData saved = dataService.addGpsData(data);
            if (saved == null) {
                response = new RestResponse(451, Status.FAIL, "Add GPS Data Failed");
                httpStatus = HttpStatus.OK;
            } else {
                GPSDataDto shortData = new GPSDataDto(saved);
                log.info("Successfully Add GPS Data: {}", shortData);
                response = new RestResponse(200, Status.SUCCESS, "Add GPS Data Success", shortData);
                httpStatus = HttpStatus.CREATED;
            }

        } catch (Exception err) {
            log.error("Add GPS Data [{}] Failed: {}", tempData, err.getMessage());
            response = new RestResponse(450, Status.FAIL, "Add GPS Data Failed: " + err.getMessage());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(response, httpStatus);
    }


    @ApiOperation(value = "Get Total GPS Data Number By Username and DeviceId // Token May Need")
    @GetMapping(value = "/api/data/gps/total", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestResponse> getTotalNumber(@RequestParam("username") @NotNull final String name,
                                                       @RequestParam("deviceID") @NotNull final String device,
                                                       @RequestHeader(value = "token", required = false) String token,
                                                       HttpServletRequest httpServletRequest) {
        RestResponse response;
        HttpStatus httpStatus;
        String clientIp = RemoteClientUtil.getClientIpAddress(httpServletRequest);
        log.info("Received Get Total Data Record Number By [user={}, deviceId={}] Request From: {}",
                name, device, clientIp);

        try {
            DeviceUser user = userService.getDeviceUser(name);
            if (user == null) {
                log.warn("Get Total Data Record Failed: No User Has Been Found: [{}]", name);
                response = new RestResponse(400, Status.FAIL, "No User Has Been Found");
                httpStatus = HttpStatus.NOT_FOUND;
                return new ResponseEntity<>(response, httpStatus);
            }
            DeviceConfig config = configService.getDeviceConfig(user, device);
            if (config == null) {
                log.warn("No Config Found for User [{}] and Device [{}]", name, device);
                response = new RestResponse(401, Status.FAIL, "No Config Found for User with DeviceID: " +
                        device);
                httpStatus = HttpStatus.NOT_FOUND;
                return new ResponseEntity<>(response, httpStatus);
            }
            Long number = dataService.countGpsData(user, config);
            response = new RestResponse(200, Status.SUCCESS, "Get Total Record Number Success", number);
            httpStatus = HttpStatus.OK;

        } catch (Exception err) {
            log.error("Get Total Data Record Number for [user={}, deviceId={}] Failed: {}",
                    name, device, err.getMessage());
            response = new RestResponse(455, Status.FAIL, "Get Total Number Failed: " + err.getMessage());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(response, httpStatus);
    }


    @ApiOperation(value = "Get GPS Data By Username, DeviceId and Page // Token May Need")
    @GetMapping(value = "/api/data/gps/get", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestResponse> getGpsData(@RequestParam("username") @NotNull final String name,
                                                   @RequestParam("deviceID") @NotNull final String device,
                                                   @RequestParam("page") @NotNull Integer page,
                                                   @RequestParam("size") @NotNull Integer size,
                                                   @RequestHeader(value = "token", required = false) String token,
                                                   @RequestParam(value = "order",  defaultValue = "desc",
                                                                 required = false) final String order,
                                                   HttpServletRequest httpServletRequest) {
        RestResponse response;
        HttpStatus httpStatus;
        String clientIp = RemoteClientUtil.getClientIpAddress(httpServletRequest);
        log.info("Received Read GPS Data By [user={}, deviceId={}] Request From: {}", name, device, clientIp);

        page = fixPageNum(page);
        size = fixPageSize(size);

        try {
            DeviceUser user = userService.getDeviceUser(name);
            if (user == null) {
                log.info("Read GPS Data Failed: No User Has Been Found: [{}]", name);
                response = new RestResponse(400, Status.FAIL, "No User Has Been Found");
                httpStatus = HttpStatus.NOT_FOUND;
                return new ResponseEntity<>(response, httpStatus);
            }
            DeviceConfig config = configService.getDeviceConfig(user, device);
            if (config == null) {
                log.info("Read GPS Data Failed: No Config Found for User [{}] and Device [{}]", name, device);
                response = new RestResponse(401, Status.FAIL, "No Config Found for User with DeviceID: " +
                        device);
                httpStatus = HttpStatus.NOT_FOUND;
                return new ResponseEntity<>(response, httpStatus);
            }

            Pageable pageRequest = preparePageRequest(page, size, order);
            List<GPSData> dataList = dataService.getGpsData(user, config, pageRequest);

            if (dataList.isEmpty()) {
                log.info("No GPS Data Found for User [{}] and Device [{}]", name, device);
                response = new RestResponse(200, Status.SUCCESS, "No GPS Data Found for User" +
                        " with DeviceID: " + device, dataList);
            } else {
                log.info("Read GPS Data Success");
                List<GPSDataDto> data = dataList.stream()
                        .map(GPSDataDto::new)
                        .collect(Collectors.toList());
                response = new RestResponse(200, Status.SUCCESS, "Get GPS Data Success", data);
                response.setCount(data.size());
            }
            httpStatus = HttpStatus.OK;

        } catch (Exception err) {
            log.error("Read GPS Data [user={}, deviceId={}] Failed: {}", name, device, err.getMessage());
            response = new RestResponse(455, Status.FAIL, "Read GPS Data Failed: " + err.getMessage());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(response, httpStatus);
    }


    @ApiOperation(value = "Search GPS Data By Username, DeviceId, Start/End Timestamp and Page // Token May Need")
    @GetMapping(value = "/api/data/gps/timestamp_search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestResponse> getGpsData(@RequestParam("username") @NotNull final String name,
                                                   @RequestParam("deviceID") @NotNull final String device,
                                                   @RequestParam("start") @NotNull Long start,
                                                   @RequestParam("end") @NotNull Long end,
                                                   @RequestParam("page") @NotNull Integer page,
                                                   @RequestParam("size") @NotNull Integer size,
                                                   @RequestHeader(value = "token", required = false) String token,
                                                   @RequestParam(value = "order",  defaultValue = "desc",
                                                           required = false) final String order,
                                                   HttpServletRequest httpServletRequest) {
        RestResponse response;
        HttpStatus httpStatus;
        String clientIp = RemoteClientUtil.getClientIpAddress(httpServletRequest);
        log.info("Received Read GPS Data By [user={}, deviceId={}, start={}, end={}] Request From: {}"
                , name, device, start, end, clientIp);

        page = fixPageNum(page);
        size = fixPageSize(size);

        try {
            DeviceUser user = userService.getDeviceUser(name);
            if (user == null) {
                log.info("Read GPS Data By Timestamp Failed: No User Has Been Found: [{}]", name);
                response = new RestResponse(400, Status.FAIL, "No User Has Been Found");
                httpStatus = HttpStatus.NOT_FOUND;
                return new ResponseEntity<>(response, httpStatus);
            }
            DeviceConfig config = configService.getDeviceConfig(user, device);
            if (config == null) {
                log.info("Read GPS Data By Timestamp Failed: No Config Found for User [{}] and Device [{}]",
                        name, device);
                response = new RestResponse(401, Status.FAIL, "No Config Found for User with DeviceID: " +
                        device);
                httpStatus = HttpStatus.NOT_FOUND;
                return new ResponseEntity<>(response, httpStatus);
            }

            Pageable pageRequest = preparePageRequest(page, size, order);
            if (start <= TIMESTAMP_MIN || end < TIMESTAMP_MIN ) {
                log.error("Read GPS Data [user={}, deviceId={}, start={}, end={} Failed: " +
                                "Invalid Start/End Timestamp", name, device, start, end);
                response = new RestResponse(456, Status.FAIL,
                        "Read GPS Data Failed: Invalid Start/End Timestamp");
                httpStatus = HttpStatus.BAD_REQUEST;
                return new ResponseEntity<>(response, httpStatus);
            }
            List<GPSData> dataList = dataService.getGpsData(user, config, pageRequest, start, end);
            if (dataList.isEmpty()) {
                log.info("No GPS Data Found for User [{}] and Device [{}] Between Timestamp [{} - {}]",
                        name, device, start, end);
                response = new RestResponse(200, Status.SUCCESS, "No GPS Data Found for User" +
                        " with DeviceID: " + device + " In Given Timestamp Range", dataList);
            } else {
                log.info("Read GPS Data Success");
                List<GPSDataDto> data = dataList.stream()
                        .map(GPSDataDto::new)
                        .collect(Collectors.toList());
                response = new RestResponse(200, Status.SUCCESS,
                        "Get GPS Data By Timestamp Range Success", data);
                response.setCount(data.size());
            }
            httpStatus = HttpStatus.OK;

        } catch (Exception err) {
            log.error("Read GPS Data [user={}, deviceId={}, start={}, end={} Failed: {}",
                    name, device, start, end, err.getMessage());
            response = new RestResponse(456, Status.FAIL, "Read GPS Data Failed: " + err.getMessage());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(response, httpStatus);
    }
}

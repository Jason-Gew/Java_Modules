package gew.data.warehouse.gps.controller;

import gew.data.warehouse.gps.config.DataWarehouseConfig;
import gew.data.warehouse.gps.model.DeviceConfig;
import gew.data.warehouse.gps.model.DeviceConfigDto;
import gew.data.warehouse.gps.model.DeviceUser;
import gew.data.warehouse.gps.model.RestResponse;
import gew.data.warehouse.gps.model.Status;
import gew.data.warehouse.gps.service.DeviceConfigService;
import gew.data.warehouse.gps.service.DeviceUserService;
import gew.data.warehouse.gps.util.RemoteClientUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * DeviceConfig CRUD REST Controller
 * @author Jason/GeW
 * @since 2019-03-10
 */
@Log4j2
@RestController
@Api(value="DeviceConfig", tags="Device Config CRUD")
public class DeviceConfigController {

    @Autowired
    private DeviceConfigService configService;

    @Autowired
    private DeviceUserService userService;

    @Autowired
    private DataWarehouseConfig dataWarehouseConfig;



    @ApiOperation(value = "Get Device Config By User // ApiKey May Need")
    @GetMapping(value = "/api/config/get", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestResponse> getDeviceConfig(@RequestParam("username") @NotNull final String name,
                                                        @RequestHeader(value = "apikey", required = false) String apiKey,
                                                        HttpServletRequest httpServletRequest) {
        RestResponse response;
        HttpStatus httpStatus;
        String clientIp = RemoteClientUtil.getClientIpAddress(httpServletRequest);  //TODO need further operations.
        log.info("Received Get Device Config By User [{}] Request From: {}", name, clientIp);

        List<DeviceConfig> configs;
        try {
            DeviceUser user = userService.getDeviceUser(name);
            if (user == null) {
                log.info("No User Has Been Found: [{}]", name);
                response = new RestResponse(400, Status.FAIL, "No User Has Been Found");
                httpStatus = HttpStatus.BAD_REQUEST;
                return new ResponseEntity<>(response, httpStatus);
            }
            configs = configService.getDeviceConfig(user);
            if (configs == null) {
                log.info("No Config Found for User [{}]", name);
                configs = new ArrayList<>(0);
                response = new RestResponse(200, Status.SUCCESS, "No Config Found for User", configs);
            } else {
                List<DeviceConfigDto> shorterConfigs = configs.stream()
                        .map(DeviceConfigDto::new)
                        .collect(Collectors.toList());
                response = new RestResponse(200, Status.SUCCESS, "Found for User Config Success",
                        shorterConfigs);
                response.setCount(configs.size());
            }
            httpStatus = HttpStatus.OK;

        } catch (Exception err) {
            log.error("Found Device Config By User [{}] Failed: {}", name, err.getMessage());
            response = new RestResponse(430, Status.FAIL, "Found User Config By User Failed: " +
                    err.getMessage());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(response, httpStatus);
    }


    @ApiOperation(value = "Get Device Config By User And Config // ApiKey May Need")
    @GetMapping(value = "/api/config/get_one", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestResponse> getDeviceConfig(@RequestParam("username") @NotNull final String name,
                                                        @RequestParam("deviceID") @NotNull final String device,
                                                        @RequestHeader(value = "apikey", required = false) String apiKey,
                                                        HttpServletRequest httpServletRequest) {
        RestResponse response;
        HttpStatus httpStatus;
        String clientIp = RemoteClientUtil.getClientIpAddress(httpServletRequest);  //TODO need further operations.
        log.info("Received Get Device Config By User [{}] And Config [{}] Request From: {}",
                name, device, clientIp);

        try {
            DeviceUser user = userService.getDeviceUser(name);
            if (user == null) {
                log.info("Get Device Config Failed: No User Has Been Found: [{}]", name);
                response = new RestResponse(400, Status.FAIL, "No User Has Been Found");
                httpStatus = HttpStatus.BAD_REQUEST;
                return new ResponseEntity<>(response, httpStatus);
            }
            DeviceConfig config = configService.getDeviceConfig(user, device);
            if (config == null) {
                log.info("No Config Found for User [{}] and Device [{}]", name, device);
                response = new RestResponse(200, Status.SUCCESS,
                        "No Config Found for User with DeviceID: " + device);
            } else {
                DeviceConfigDto shorterConfig = new DeviceConfigDto(config);
                response = new RestResponse(200, Status.SUCCESS, "Found for User Config Success",
                        shorterConfig);
            }
            httpStatus = HttpStatus.OK;

        } catch (Exception err) {
            log.error("Found Device Config By User [{}] And DeviceID [{}] Failed: {}", name, device, err.getMessage());
            response = new RestResponse(430, Status.FAIL, "Found User Config By User And DeviceID Failed: " +
                    err.getMessage());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(response, httpStatus);
    }


    @ApiOperation(value = "Add Device Config // Token May Need")
    @PostMapping(value = "/api/config/add", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestResponse> addDeviceConfig(@RequestBody DeviceConfigDto tempConfig,
                                                        @RequestHeader(value = "token", required = false) String token,
                                                        HttpServletRequest httpServletRequest) {
        RestResponse response;
        HttpStatus httpStatus;
        String clientIp = RemoteClientUtil.getClientIpAddress(httpServletRequest);
        log.info("Received Add Device Config [{}] Request From: {}", tempConfig, clientIp);

        try {
            DeviceUser user = userService.getDeviceUser(tempConfig.getUsername());
            if (user == null) {
                log.info("Add Device Config Failed: No User Has Been Found: [{}]", tempConfig.getUsername());
                response = new RestResponse(400, Status.FAIL, "No User Has Been Found");
                httpStatus = HttpStatus.NOT_FOUND;
                return new ResponseEntity<>(response, httpStatus);
            } else if (!user.getApiKey().equalsIgnoreCase(tempConfig.getApiKey())) {
                log.error("Add Device Config [{}] Failed: ApiKey Does Not Match", tempConfig);
                response = new RestResponse(422, Status.FAIL, "Add Device Config Failed: ApiKey Does Not Match");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            } else if (!user.isActive()) {
                log.error("Add Device Config [{}] Failed: User is not Active", tempConfig);
                response = new RestResponse(404, Status.FAIL, "Add Device Config Failed: User is Not Active");
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }
            DeviceConfig config = new DeviceConfig();
            config.setUser(user);
            config.setDeviceId(tempConfig.getDeviceId());
            if (dataWarehouseConfig.getDefaultEnableDevice()) {
                config.setEnable(true);
            }
            config.setFunction(tempConfig.getFunction());
            config.setNote(tempConfig.getNote());
            DeviceConfig saved = configService.addDeviceConfig(config);
            if (saved == null) {
                response = new RestResponse(431, Status.FAIL, "Add Device Config Failed");
                httpStatus = HttpStatus.OK;
            } else {
                DeviceConfigDto shorterConfig = new DeviceConfigDto(saved);
                response = new RestResponse(200, Status.SUCCESS, "Add Device Config Success", shorterConfig);
                httpStatus = HttpStatus.CREATED;
            }
        } catch (Exception err) {
            log.error("Add Device Config [{}] for User [{}] Failed: {}",
                    tempConfig.getDeviceId(), tempConfig.getUsername(), err.getMessage());
            response = new RestResponse(432, Status.FAIL, "Add Device Config Failed: " + err.getMessage());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(response, httpStatus);
    }


    @ApiOperation(value = "Update Device Config // Token May Need")
    @PutMapping(value = "/api/config/update", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestResponse> updateDeviceConfig(@RequestBody DeviceConfigDto tempConfig,
                                                           @RequestHeader(value = "token", required = false) String token,
                                                           HttpServletRequest httpServletRequest) {
        RestResponse response;
        HttpStatus httpStatus;
        String clientIp = RemoteClientUtil.getClientIpAddress(httpServletRequest);
        log.info("Received Update Device Config [{}] Request From: {}", tempConfig, clientIp);

        try {
            DeviceUser user = userService.getDeviceUser(tempConfig.getUsername());
            if (user == null) {
                log.info("Update Device Config Failed: No User Has Been Found: [{}]", tempConfig.getUsername());
                response = new RestResponse(400, Status.FAIL, "No User Has Been Found");
                httpStatus = HttpStatus.NOT_FOUND;
                return new ResponseEntity<>(response, httpStatus);
            } else if (!user.getApiKey().equalsIgnoreCase(tempConfig.getApiKey())) {
                log.error("Update Device Config [{}] Failed: ApiKey Does Not Match", tempConfig);
                response = new RestResponse(422, Status.FAIL, "Update Device Config Failed: ApiKey Does Not Match");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            DeviceConfig old = configService.getDeviceConfig(tempConfig.getId());
            if (old == null) {
                log.error("Update Device Config [{}] Failed: Record ID Does Not Match", tempConfig);
                response = new RestResponse(402, Status.FAIL, "Record ID Does Not Match");
                httpStatus = HttpStatus.BAD_REQUEST;
                return new ResponseEntity<>(response, httpStatus);
            }
            if (!old.getDeviceId().equalsIgnoreCase(tempConfig.getDeviceId())
                    && configService.existDeviceConfig(user, tempConfig.getDeviceId())) {
                log.error("Update Device Config [{}] Failed: Device Unique ID For User Already Exist", tempConfig);
                response = new RestResponse(442, Status.FAIL, "Device Name For User Already Exist");
                httpStatus = HttpStatus.BAD_REQUEST;
                return new ResponseEntity<>(response, httpStatus);
            }
            old.setDeviceId(tempConfig.getDeviceId());
            old.setFunction(tempConfig.getFunction());
            old.setNote(tempConfig.getNote());
            old.setEnable(tempConfig.getEnable());
            DeviceConfig updated = configService.updateDeviceConfig(old);
            DeviceConfigDto shorterConfig = new DeviceConfigDto(updated);
            log.info("Update Device Config [user={} | deviceId={}] Has Been Processed...",
                    updated.getUser().getUsername(), updated.getDeviceId());
            response = new RestResponse(200, Status.SUCCESS, "Update Device Config Success", shorterConfig);
            httpStatus = HttpStatus.OK;

        } catch (Exception err) {
            log.error("Update DeviceConfig [{}] Failed: {}", tempConfig, err.getMessage());
            response = new RestResponse(440, Status.FAIL, "Update Device Config Failed: " + err.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(response, httpStatus);
    }


    @ApiOperation(value = "Delete Device Config By Username, Device Unique ID and ApiKey // Token May Need")
    @DeleteMapping(value = "/api/config/delete", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestResponse> deleteDeviceConfig(@RequestParam("username") @NotNull final String name,
                                                           @RequestParam("deviceID") @NotNull final String device,
                                                           @RequestHeader("apikey") final String apiKey,
                                                           @RequestHeader(value = "token", required = false) String token,
                                                           HttpServletRequest httpServletRequest) {
        RestResponse response;
        HttpStatus httpStatus;
        String clientIp = RemoteClientUtil.getClientIpAddress(httpServletRequest);
        log.info("Received Delete Device Config [{}] for User [{}] Request From: {}", device, name, clientIp);

        try {
            DeviceUser user = userService.getDeviceUser(name);
            if (user == null) {
                log.info("elete Device Config Failed: No User Has Been Found: [{}]", name);
                response = new RestResponse(400, Status.FAIL, "No User Has Been Found");
                httpStatus = HttpStatus.NOT_FOUND;
                return new ResponseEntity<>(response, httpStatus);
            } else if (!user.getApiKey().equalsIgnoreCase(apiKey)) {
                log.error("Delete Device Config [{}] Failed: ApiKey Does Not Match", apiKey);
                response = new RestResponse(422, Status.FAIL, "Delete Device Config Failed: ApiKey Does Not Match");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            configService.removeDeviceConfig(user, device);
            log.info("Delete Device Config [{}] For User [{}] Has Been Processed...", device, name);
            response = new RestResponse(200, Status.SUCCESS, "Delete Device Config Has Been Processed");
            httpStatus = HttpStatus.OK;
        } catch (Exception err) {
            log.error("Delete Device Config [{}] For User [{}] Failed: {}", device, name, err.getMessage());
            response = new RestResponse(450, Status.FAIL, "Delete Device Config Failed: " + err.getMessage());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(response, httpStatus);
    }
}

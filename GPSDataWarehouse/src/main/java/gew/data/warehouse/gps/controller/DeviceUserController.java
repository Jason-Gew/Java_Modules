package gew.data.warehouse.gps.controller;


import gew.data.warehouse.gps.config.DataWarehouseConfig;
import gew.data.warehouse.gps.model.DeviceUser;
import gew.data.warehouse.gps.model.RestResponse;
import gew.data.warehouse.gps.model.Status;
import gew.data.warehouse.gps.model.UserProfileDto;
import gew.data.warehouse.gps.service.DeviceUserService;
import gew.data.warehouse.gps.util.HashUtil;
import gew.data.warehouse.gps.util.RemoteClientUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;


/**
 * DeviceUser CRUD REST Controller
 * @author Jason/GeW
 * @since 2019-03-10
 */
@Log4j2
@RestController
@Api(value="DeviceUser", tags="User CRUD")
public class DeviceUserController {

    @Autowired
    private DeviceUserService userService;

    @Autowired
    private DataWarehouseConfig dataWarehouseConfig;


    private String generateApiKey(final String username, final String email) {
        return HashUtil.secureHash(dataWarehouseConfig.getHashAlgorithm(), username, email);
    }

    @ApiOperation(value = "Get User By Username // ApiKey May Need")
    @GetMapping(value = "/api/user/get", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestResponse> getDeviceUser(@RequestParam("username") @NotNull final String name,
                                                      @RequestHeader(value = "apikey", required = false) String apiKey,
                                                      HttpServletRequest httpServletRequest) {
        RestResponse response;
        HttpStatus httpStatus;
        String clientIp = RemoteClientUtil.getClientIpAddress(httpServletRequest);  //TODO need further operations.
        log.info("Received Get Device User Request From: {}", clientIp);

        try {
            DeviceUser user = userService.getDeviceUser(name);
            if (user == null) {
                response = new RestResponse(400, Status.FAIL, "No User Has Been Found");
            } else {
                response = new RestResponse(200, Status.SUCCESS, "User Has Been Found", user);
            }
            httpStatus = HttpStatus.OK;

        } catch (Exception err) {
            log.error("Found User By Name [{}] Failed: {}", name, err.getMessage());
            response = new RestResponse(400, Status.UNKNOWN, "Found User Failed: " + err.getMessage());
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(response, httpStatus);
    }


    @ApiOperation(value = "Add User // Token May Need")
    @PostMapping(value = "/api/user/create", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestResponse> addDeviceUser(@RequestBody UserProfileDto user,
                                                      @RequestHeader(value = "token", required = false) String token,
                                                      HttpServletRequest httpServletRequest) {
        RestResponse response;
        HttpStatus httpStatus;
        String clientIp = RemoteClientUtil.getClientIpAddress(httpServletRequest);  //TODO need further operations.
        log.info("Received Add Device User [{}] Request From: {}", user.toString(), clientIp);

        //TODO Verify Token
        try {
            DeviceUser deviceUser = new DeviceUser();
            deviceUser.setUsername(user.getUsername());
            deviceUser.setEmail(user.getEmail().toLowerCase());
            deviceUser.setActive(false);
            deviceUser.setRole(0);
            deviceUser.setApiKey(generateApiKey(user.getUsername(), user.getEmail()));
            deviceUser.setTimezone(user.getTimezone());
            DeviceUser saved = userService.addDeviceUser(deviceUser);
            if (saved == null) {
                response = new RestResponse(401, Status.FAIL, "Add Device User Failed");
                httpStatus = HttpStatus.OK;
            } else {
                response = new RestResponse(200, Status.SUCCESS, "Add Device User Success", saved);
                httpStatus = HttpStatus.CREATED;
            }

        } catch (Exception err) {
            log.error("Add Device User [{} | {}] Failed: {}", user.getUsername(), user.getEmail(), err.getMessage());
            response = new RestResponse(401, Status.FAIL, "Add Device User Failed: " + err.getMessage());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(response, httpStatus);
    }


    @ApiOperation(value = "Update User // Token May Need")
    @PutMapping(value = "/api/user/update", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestResponse> updateDeviceUser(@RequestHeader(value = "token", required = false) String token,
                                                         @RequestBody UserProfileDto user,
                                                         HttpServletRequest httpServletRequest) {
        RestResponse response;
        HttpStatus httpStatus;
        String clientIp = RemoteClientUtil.getClientIpAddress(httpServletRequest);  //TODO need further operations.
        log.info("Received Update Device User [{}] Request From: {}", user, clientIp);

        //TODO Verify Token
        try {
            DeviceUser old = userService.getDeviceUser(user.getUsername());
            if (old == null) {
                log.error("Update Device User [{}] Failed: No User Has Been Found", user.getUsername());
                response = new RestResponse(400, Status.FAIL, "No User Has Been Found");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            if (!old.getId().equals(user.getId())) {
                log.error("Update Device User [{}] Failed: User ID Does Not Match", user.getUsername());
                response = new RestResponse(402, Status.FAIL, "User ID Does Not Match");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            if (!old.getEmail().equalsIgnoreCase(user.getEmail())) {
                old.setEmail(user.getEmail().toLowerCase());
                if (userService.existUserEmail(user.getEmail().toLowerCase())) {
                    log.error("Update Device User [{} | {}] Failed: User Email Already Exist",
                            user.getUsername(), user.getEmail());
                    response = new RestResponse(403, Status.FAIL, "User Email Already Match");
                    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                }
                if (dataWarehouseConfig.getRegenApiKey()) {
                    old.setApiKey(generateApiKey(old.getUsername(), user.getEmail()));
                }
            }
            old.setTimezone(user.getTimezone());
            DeviceUser updated = userService.updateDeviceUser(old);
            log.info("Update Device User [{} | {}]...", updated.getUsername(), updated.getEmail());
            response = new RestResponse(200, Status.SUCCESS, "Update Device User Success", updated);
            httpStatus = HttpStatus.OK;

        } catch (Exception err) {
            log.error("Update Device User [{}] Failed: {}", user.getUsername(), err.getMessage());
            response = new RestResponse(406, Status.FAIL, "Update Device User Failed: " + err.getMessage());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(response, httpStatus);
    }


    @ApiOperation(value = "Delete User By Username & ApiKey // Token May Need")
    @DeleteMapping(value = "/api/user/delete", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestResponse> deleteDeviceUser(@RequestParam("username") @NotNull final String name,
                                                         @RequestHeader("apikey") final String apiKey,
                                                         @RequestHeader(value = "token", required = false) String token,
                                                         HttpServletRequest httpServletRequest)
    {
        RestResponse response;
        HttpStatus httpStatus;
        String clientIp = RemoteClientUtil.getClientIpAddress(httpServletRequest);  //TODO need further operations.
        log.info("Received Delete Device User [{}] Request From: {}", name, clientIp);

        //TODO Verify Token
        try {
            DeviceUser user = userService.getDeviceUser(name);
            if (user == null) {
                log.error("Delete User [{}] Failed: User Does Not Exist", name);
                response = new RestResponse(420, Status.FAIL, "Delete User Failed: User Does Not Exist");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            if (!user.getApiKey().equalsIgnoreCase(apiKey)) {
                log.error("Delete User [{}] Failed: ApiKey Does Not Match", name);
                response = new RestResponse(422, Status.FAIL, "Delete User Failed: ApiKey Does Not Match");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            userService.removeDeviceUser(name);
            response = new RestResponse(200, Status.SUCCESS, "Delete User Has Been Processed", name);
            httpStatus = HttpStatus.OK;

        } catch (Exception err) {
            log.error("Delete User [{}] Failed: {}", name, err.getMessage());
            response = new RestResponse(424, Status.FAIL, "Delete User Failed: " + err.getMessage());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(response, httpStatus);
    }


}

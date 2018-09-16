package gew.caching.controller;


import gew.caching.entity.RestResponse;
import gew.caching.entity.Status;
import gew.caching.entity.UserInfo;
import gew.caching.service.UserInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

/**
 * @author Jason/GeW
 */
@Log4j2
@RestController
@RequestMapping("/user")
@Api(value="user", tags="User Information Management")
public class UserInfoController {

    @Autowired
    private UserInfoService userInfoService;

    private static String timestamp() {
        return Instant.now().toString();
    }

    @ApiOperation(value = "Get User By Username")
    @GetMapping(value = "/name/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestResponse> getUserByName(@PathVariable("username") final String name) {

        RestResponse response;
        log.info("-> Received Get UserInfo By Name Request: " + name);
        UserInfo user = userInfoService.getUserInfoByUsername(name);
        if (user == null) {
            response = new RestResponse(4010, Status.FAIL, "Unable to Find User Based on Name: " + name,
                    timestamp(), null);
        } else {
            response = new RestResponse(200, Status.SUCCESS, "Get User Info Success", timestamp(), user);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @ApiOperation(value = "Add A New User")
    @PostMapping(value = "/add", consumes = "application/json", produces = "application/json")
    public ResponseEntity<RestResponse> addUser(@RequestBody @Validated final UserInfo user) {
        RestResponse response;
        HttpStatus httpStatus;
        log.info("-> Received Add User Request: " + user.toString());
        if (user.getUsername() == null || user.getUsername().isEmpty()) {
            httpStatus = HttpStatus.BAD_REQUEST;
            response = new RestResponse(4002, Status.FAIL, "Invalid Username", timestamp(), null);
        } else if (userInfoService.existCheckByUsername(user.getUsername())) {
            httpStatus = HttpStatus.BAD_REQUEST;
            response = new RestResponse(4003, Status.FAIL, "Username Already Exist", timestamp(), null);
        } else if (userInfoService.existCheckByEmail(user.getEmail())) {
            httpStatus = HttpStatus.BAD_REQUEST;
            response = new RestResponse(4005, Status.FAIL, "Email Already Exist", timestamp(), null);
        } else {
            try {
                UserInfo result = userInfoService.createUserInfo(user);
                httpStatus = HttpStatus.CREATED;
                response = new RestResponse(201, Status.SUCCESS,
                        "Create User [" + result.getUsername() + "] Success", timestamp(), result);
            } catch (Exception err) {
                httpStatus = HttpStatus.BAD_REQUEST;
                response = new RestResponse(4010, Status.FAIL,
                        "Create User [" + user.getUsername()+ "] Failed: " + err.getMessage(), timestamp(), null);
            }
        }
        return new ResponseEntity<>(response, httpStatus);
    }

    @ApiOperation(value = "Update User Information Based On ID")
    @PutMapping(value = "/update", consumes = "application/json", produces = "application/json")
    public ResponseEntity<RestResponse> updateUser(@RequestBody @Validated UserInfo user,
                                                   @RequestParam(name = "id") final Long id) {
        RestResponse response;
        HttpStatus httpStatus;
        log.info("-> Received Update User [{}] Request: {}", user.getUsername(), user.toString());
        if (user.getUsername().isEmpty()) {
            httpStatus = HttpStatus.BAD_REQUEST;
            response = new RestResponse(4002, Status.FAIL, "Invalid Username", timestamp(), null);
        } else if (!userInfoService.existCheckByUsername(user.getUsername())) {
            httpStatus = HttpStatus.BAD_REQUEST;
            response = new RestResponse(4001, Status.FAIL, "User Does Not Exist", timestamp(), null);
        } else {
            UserInfo oldInfo = userInfoService.getUserInfoById(id);
            if (oldInfo == null) {
                httpStatus = HttpStatus.BAD_REQUEST;
                response = new RestResponse(4001, Status.FAIL, "User Does Not Exist", timestamp(), null);
            } else if (!oldInfo.getUsername().equals(user.getUsername())) {
                httpStatus = HttpStatus.BAD_REQUEST;
                response = new RestResponse(4006, Status.FAIL, "User Name Does Not Match", timestamp(), null);
            } else if (!user.getEmail().equals(oldInfo.getEmail()) && userInfoService.existCheckByEmail(user.getEmail())) {
                httpStatus = HttpStatus.BAD_REQUEST;
                response = new RestResponse(4005, Status.FAIL, "Email Already Exist", timestamp(), null);
            } else {
                if (user.getId() == null) {
                    user.setId(id);
                }
                try {
                    UserInfo result = userInfoService.updateUserInfo(user);
                    if (result != null) {
                        httpStatus = HttpStatus.OK;
                        response = new RestResponse(200, Status.SUCCESS,
                                "Update User [" + user.getUsername() + "] Success", timestamp(), result);
                    } else {
                        response = new RestResponse(4012, Status.UNKNOWN, "Update User [" + user.getUsername() + "] Failed",
                            timestamp(), null);
                        httpStatus = HttpStatus.OK;
                    }
                } catch (Exception err) {
                    httpStatus = HttpStatus.BAD_REQUEST;
                    response = new RestResponse(4012, Status.FAIL, "Update User Info Failed: " + err.getMessage(), timestamp(), null);
                }
            }
        }
        return new ResponseEntity<>(response, httpStatus);
    }


    @ApiOperation(value = "Delete A User")
    @DeleteMapping(value = "/delete", consumes = "application/json", produces = "application/json")
    public ResponseEntity<RestResponse> deleteUser(@RequestBody final UserInfo user) {
        RestResponse response;
        HttpStatus httpStatus;
        log.info("-> Received Delete User Request: " + user.toString());
        if (user.getUsername() == null || user.getUsername().isEmpty()) {
            httpStatus = HttpStatus.BAD_REQUEST;
            response = new RestResponse(4002, Status.FAIL, "Invalid Username", timestamp(), null);
        } else if (user.getEmail() == null || user.getEmail().isEmpty()) {
            httpStatus = HttpStatus.BAD_REQUEST;
            response = new RestResponse(4004, Status.FAIL, "Invalid Email", timestamp(), null);
        } else {
            String result = userInfoService.deleteUserInfo(user);
            if (result.equalsIgnoreCase(Status.SUCCESS.toString())) {
                httpStatus = HttpStatus.OK;
                response = new RestResponse(200, Status.SUCCESS, "Request Has Been Processed", timestamp(), result);
            } else {
                httpStatus = HttpStatus.BAD_REQUEST;
                response = new RestResponse(4014, Status.FAIL, "Delete User Info " + result, timestamp(), null);
            }
        }
        return new ResponseEntity<>(response, httpStatus);
    }

}

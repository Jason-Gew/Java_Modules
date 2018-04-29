package gew.management.controller;

import gew.management.model.RestResponse;
import gew.management.model.Status;
import gew.management.model.UserInfo;
import gew.management.service.UserInfoServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * @author Jason/GeW
 */
@RestController
@RequestMapping("/user")
public class UserManageController {

    @Autowired
    private UserInfoServiceImpl userInfoService;

    private static final Logger logger = LoggerFactory.getLogger(UserManageController.class);

    private static String timestamp() {
        return Instant.now().toString();
    }


    @Deprecated
    @GetMapping(value = "/all", produces = "application/json")
    public ResponseEntity<RestResponse> getAllUsers() {
        RestResponse response;
        List<UserInfo> users = userInfoService.getAllUsers();
        if (users != null) {
            response = new RestResponse(200, Status.SUCCESS, "Get All User Record Success",
                    timestamp(), users);
            response.setCount(users.size());
        } else {
            response = new RestResponse(4000, Status.FAIL, "Get All User Record Fail", timestamp(), null);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/get", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestResponse> getUsers(@NotNull final Pageable pageable) {
        RestResponse response;

        logger.info("Received Get All Users Pageable Request: {}", pageable.toString());
        PageRequest request = new PageRequest(pageable.getPageNumber(), pageable.getPageSize());

        List<UserInfo> users = userInfoService.getAllUsers(request);
        if (users != null) {
            response = new RestResponse(200, Status.SUCCESS, "Get User Record Success",
                    timestamp(), users);
            response.setCount(users.size());
        } else {
            response = new RestResponse(4001, Status.FAIL, "Get Users Record Fail", timestamp(), null);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/id/{userId}", produces = "application/json")
    public ResponseEntity<RestResponse> getUserById(@PathVariable("userId") final String userId) {
        RestResponse response;
        logger.info("-> Received Get User By ID Request: " + userId);
        UserInfo user = userInfoService.getUserById(userId);
        if (user == null) {
            response = new RestResponse(4010, Status.FAIL, "Unable to Find User Based on ID: " + userId,
                    timestamp(), null);
        } else {
            response = new RestResponse(200, Status.SUCCESS, "Get User Record Success", timestamp(), user);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/{username}", produces = "application/json")
    public ResponseEntity<RestResponse> getUserByName(@PathVariable("username") final String username) {
        RestResponse response;
        logger.info("-> Received Get User By Username Request: " + username);
        UserInfo user = userInfoService.getUserByName(username);
        if (user == null) {
            response = new RestResponse(4011, Status.FAIL, "Unable to Find User Based on Username: " + username,
                    timestamp(), null);
        } else {
            response = new RestResponse(200, Status.SUCCESS, "Get User Record Success", timestamp(), user);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @PostMapping(value = "/add", consumes = "application/json", produces = "application/json")
    public ResponseEntity<RestResponse> addUser(@RequestBody final UserInfo user) {
        RestResponse response;
        HttpStatus httpStatus;
        logger.info("-> Received Add User Request: " + user.toString());
        if (user.getUsername() == null || user.getUsername().isEmpty()) {
            httpStatus = HttpStatus.BAD_REQUEST;
            response = new RestResponse(4002, Status.FAIL, "Invalid Username", timestamp(), null);
        } else if (userInfoService.isExist(user)) {
            httpStatus = HttpStatus.NOT_ACCEPTABLE;
            response = new RestResponse(4003, Status.FAIL, "Username or Email Already Exist", timestamp(), null);
        } else if (user.getEmail() == null || user.getEmail().isEmpty()) {
            httpStatus = HttpStatus.BAD_REQUEST;
            response = new RestResponse(4004, Status.FAIL, "Invalid Email", timestamp(), null);
        } else if (user.getPassword() == null || user.getPassword().length() < 8) {
            httpStatus = HttpStatus.BAD_REQUEST;
            response = new RestResponse(4006, Status.FAIL, "Invalid Password", timestamp(), null);
        } else {
            String result = userInfoService.addUser(user);
            httpStatus = HttpStatus.OK;
            response = new RestResponse(200, Status.SUCCESS, "Request Has Been Processed", timestamp(), result);
        }
        return new ResponseEntity<>(response, httpStatus);
    }


    @PutMapping(value = "/update", consumes = "application/json", produces = "application/json")
    public ResponseEntity<RestResponse> updateUser(@RequestParam final String username,
                                                   @RequestBody final Map<String, Object> content) {
        RestResponse response;
        HttpStatus httpStatus;
        logger.info("-> Received Update User [{}] Request: {}", username, content);
        if (username.isEmpty()) {
            httpStatus = HttpStatus.BAD_REQUEST;
            response = new RestResponse(4002, Status.FAIL, "Invalid Username", timestamp(), null);
        } else {
            String result = userInfoService.updateUser(username, content);
            httpStatus = HttpStatus.OK;
            if (result.equalsIgnoreCase(Status.SUCCESS.value())) {
                response = new RestResponse(200, Status.SUCCESS, "Update User [" + username + "] Success",
                        timestamp(), result);
            } else {
                response = new RestResponse(200, Status.UNKNOWN, "Update User [" + username + "] Failed",
                        timestamp(), result);
            }
        }
        return new ResponseEntity<>(response, httpStatus);
    }


    @DeleteMapping(value = "/delete", consumes = "application/json", produces = "application/json")
    public ResponseEntity<RestResponse> deleteUser(@RequestBody final UserInfo user) {
        RestResponse response;
        HttpStatus httpStatus;
        logger.info("-> Received Delete User Request: " + user.toString());
        if (user.getUsername() == null || user.getUsername().isEmpty()) {
            httpStatus = HttpStatus.BAD_REQUEST;
            response = new RestResponse(4002, Status.FAIL, "Invalid Username", timestamp(), null);
        } else if (user.getEmail() == null || user.getEmail().isEmpty()) {
            httpStatus = HttpStatus.BAD_REQUEST;
            response = new RestResponse(4004, Status.FAIL, "Invalid Email", timestamp(), null);
        } else {
            UserInfo record = userInfoService.getUserByName(user.getUsername());
            if (record == null) {
                httpStatus = HttpStatus.NOT_ACCEPTABLE;
                response = new RestResponse(4011, Status.FAIL, "Unable to Find User: " + user.getUsername(),
                        timestamp(), null);
            } else if (!user.getPassword().equals(record.getPassword())) {
                httpStatus = HttpStatus.NOT_ACCEPTABLE;
                response = new RestResponse(4007, Status.FAIL, "Incorrect Password",
                        timestamp(), null);
            } else {
                httpStatus = HttpStatus.OK;
                String result = userInfoService.deleteUser(record);
                if (result.equalsIgnoreCase(Status.SUCCESS.value())) {
                    response = new RestResponse(200, Status.SUCCESS, "User [" + user.getUsername() + "] Has Been Deleted",
                            timestamp(), null);
                } else {
                    response = new RestResponse(200, Status.UNKNOWN, "Delete User [" + user.getUsername() + "] Failed",
                            timestamp(), result);
                }
            }
        }
        return new ResponseEntity<>(response, httpStatus);
    }

}

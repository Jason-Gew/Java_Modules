package gew.caching.controller;

import gew.caching.entity.CacheMessage;
import gew.caching.entity.RestResponse;
import gew.caching.entity.Status;
import gew.caching.util.TimeIntervalHelper;
import io.swagger.annotations.Api;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
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

import javax.validation.constraints.NotEmpty;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author Jason/GeW
 * @since 2018-09-10
 */
@Log4j2
@RestController
@RequestMapping("/message")
@Api(value="message", tags="Cache Message CRUD")
public class CacheCrudController {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;


    @GetMapping(value = "/get/{key}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestResponse> getMessageByKey(@PathVariable("key") @NotEmpty final String key,
                                                        @RequestParam(value = "type", required = false) Optional<String> type,
                                                        @RequestParam(value = "start", required = false) Optional<Long> start,
                                                        @RequestParam(value = "end", required = false) Optional<Long> end) {
        RestResponse restResponse;
        HttpStatus httpStatus;
        try {
            Object value = null;
            if (type.isPresent()) {
                switch (type.get().toUpperCase()) {
                    case "STRING":
                        value = redisTemplate.opsForValue().get(key);
                        break;
                    case "SET":
                        value = redisTemplate.opsForSet().members(key);
                        break;
                    case "ZSET":
                        value= redisTemplate.opsForZSet().range(key, start.get(), end.get());
                        break;
                    case "LIST":
                        value = redisTemplate.opsForList().range(key, start.get(), end.get());
                        break;
                    default:
                        log.error("Unidentified Redis Data Type: " + type.get());
                }
            } else {
                value = redisTemplate.opsForValue().get(key);
            }
            if (value != null) {
                log.info("Get Value By Key [{}] Success", key);
                restResponse = new RestResponse(200, Status.SUCCESS, "Get Message Success", value);
                httpStatus = HttpStatus.OK;
            } else {
                log.info("Value for Key [[]] Does Not Exist", key);
                restResponse = new RestResponse(400, Status.FAIL, "Message Does Not Exist", null);
                httpStatus = HttpStatus.NOT_FOUND;
            }
        } catch (Exception err) {
            log.error("Get Value By Key [{}] Failed: {}", key, err.getMessage());
            restResponse = new RestResponse(4000, Status.FAIL, "Get Message Failed: " + err.getMessage(), null);
            httpStatus = HttpStatus.EXPECTATION_FAILED;
        }

        return new ResponseEntity<>(restResponse, httpStatus);
    }


    @PostMapping(value = "/addOrUpdateMessage", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestResponse> addOrUpdateMessage(@RequestBody @Validated final CacheMessage message,
                                                           @RequestParam(name = "update", required = false) Optional<Boolean> update) {
        RestResponse restResponse;
        HttpStatus httpStatus;
        try {
            Boolean existence = redisTemplate.hasKey(message.getKey());
            log.info("Exist Key [{}] Check: {}", message.getKey(), existence);
            if (update.isPresent() && update.get()) {
                log.info("Update Key With New Value...");
                existence = false;
            }
            if (existence != null && existence) {
                log.error("Add Value for Key [{}] Failed: Key Already Exist", message.getKey());
                restResponse = new RestResponse(401, Status.FAIL, "Add Message Failed: Key Already Exist", null);
                httpStatus = HttpStatus.BAD_REQUEST;
            } else {
                if (message.getTtl() == null || message.getTimeUnit() == null || message.getTtl() < 1) {
                    redisTemplate.opsForValue().set(message.getKey(), message.getMessage());
                    log.info("Add Value for Key [{}] Has Been Processed");
                    restResponse = new RestResponse(201, Status.SUCCESS, "Request Has Been Processed", null);
                } else {
                    Long ttl = message.getTtl();
                    TimeUnit unit = TimeIntervalHelper.convertTimeUnitFromString(message.getTimeUnit());
                    redisTemplate.opsForValue().set(message.getKey(), message.getMessage(), ttl, unit);
                    log.info("Add Value for Key [{}] with TTL [{} {}] Has Been Processed", message.getKey(), ttl, message.getTimeUnit());
                    restResponse = new RestResponse(201, Status.SUCCESS, "Request Has Been Processed", null);
                }
                httpStatus = HttpStatus.CREATED;
            }
        } catch (Exception err) {
            log.error("Add or Update Value for Key [{}] Failed: {}", message.getKey(), err.getMessage());
            restResponse = new RestResponse(4000, Status.FAIL, "Add or Update Message Failed: " + err.getMessage(), null);
            httpStatus = HttpStatus.EXPECTATION_FAILED;
        }
        return new ResponseEntity<>(restResponse, httpStatus);
    }

    @PostMapping(value = "/addOrUpdateSet", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestResponse> addOrUpdateSetMessage(@RequestBody final CacheMessage message,
                                                              @RequestParam(name = "update", required = false) Optional<Boolean> update) {
        RestResponse restResponse;
        HttpStatus httpStatus;

        try {
            Boolean existence = redisTemplate.hasKey(message.getKey());
            log.info("Exist Key [{}] Check: {}", message.getKey(), existence);
            if (update.isPresent() && update.get()) {
                redisTemplate.delete(message.getKey());
            }

            if (message.getTtl() == null || message.getTimeUnit() == null || message.getTtl() < 1) {
                Long result = redisTemplate.opsForSet().add(message.getKey(), message.getMessage());
                log.info("Add Set Value for Key [{}] Has Been Processed");
                restResponse = new RestResponse(201, Status.SUCCESS, "Request Has Been Processed", result);
            } else {
                Long ttl = message.getTtl();
                TimeUnit unit = TimeIntervalHelper.convertTimeUnitFromString(message.getTimeUnit());
                Long result = redisTemplate.opsForSet().add(message.getKey(), message.getMessage());
                redisTemplate.expire(message.getKey(), ttl, unit);
                log.info("Add Set Value for Key [{}] with TTL [{} {}] Has Been Processed", message.getKey(), ttl, message.getTimeUnit());
                restResponse = new RestResponse(201, Status.SUCCESS, "Request Has Been Processed", result);
            }
            httpStatus = HttpStatus.CREATED;

        } catch (Exception err) {
            log.error("Add or Update Value for Key [{}] Failed: {}", message.getKey(), err.getMessage());
            restResponse = new RestResponse(4000, Status.FAIL, "Add or Update Set Message Failed: " + err.getMessage(), null);
            httpStatus = HttpStatus.EXPECTATION_FAILED;
        }
        return new ResponseEntity<>(restResponse, httpStatus);
    }


    @DeleteMapping(value = "/delete", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestResponse> deleteMessageByKey(@RequestParam("key") @NotEmpty final String key) {
        RestResponse restResponse;
        HttpStatus httpStatus;
        try {
            Boolean existence = redisTemplate.hasKey(key);
            if (existence == null || existence) {
                log.error("Delete Value for Key [{}] Failed: Key Does Not Already Exist", key);
                restResponse = new RestResponse(402, Status.FAIL, "Delete Message Failed: Key Does Not Exist", null);
                httpStatus = HttpStatus.BAD_REQUEST;
            } else {
                Boolean result = redisTemplate.delete(key);
                restResponse = new RestResponse(200, Status.SUCCESS, "Delete Message Has Been Processed", result);
                httpStatus = HttpStatus.OK;
            }

        } catch (Exception err) {
            log.error("Delete Value for Key [{}] Failed: {}", key, err.getMessage());
            restResponse = new RestResponse(4000, Status.FAIL, "Delete Message Failed: " + err.getMessage(), null);
            httpStatus = HttpStatus.EXPECTATION_FAILED;
        }
        return new ResponseEntity<>(restResponse, httpStatus);
    }

    @PutMapping(value = "/refresh_ttl", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestResponse> refreshTtlByKey(@RequestParam("key") final String key,
                                                        @RequestParam("ttl") final Long ttl,
                                                        @RequestParam("timeUnit") final String timeUnit) {
        RestResponse restResponse;
        HttpStatus httpStatus;
        log.info("Get Refresh TTL Request for Key [{}]: {} {}", key, ttl, timeUnit);
        if (key.isEmpty() || timeUnit.isEmpty()) {
            restResponse = new RestResponse(4100, Status.FAIL, "Invalid Request", null);
            httpStatus = HttpStatus.BAD_REQUEST;
        } else {
            try {
                Boolean existence = redisTemplate.hasKey(key);
                if (existence == null || !existence) {
                    log.error("Refresh TTL for Key [{}] Failed: Key Does Not Exist", key);
                    restResponse = new RestResponse(402, Status.FAIL, "Refresh TTL Failed: Key Does Not Exist", null);
                    httpStatus = HttpStatus.BAD_REQUEST;
                } else {
                    Boolean result = redisTemplate.expire(key, ttl, TimeIntervalHelper.convertTimeUnitFromString(timeUnit));
                    restResponse = new RestResponse(200, Status.SUCCESS, "Refresh TTL Has Been Processed", result);
                    httpStatus = HttpStatus.OK;
                }
            } catch (Exception err) {
                log.error("Refresh TTL for Key [{}] Failed: {}", key, err.getMessage());
                restResponse = new RestResponse(4106, Status.FAIL, "Refresh TTL Failed: " + err.getMessage(), null);
                httpStatus = HttpStatus.EXPECTATION_FAILED;
            }
        }
        return new ResponseEntity<>(restResponse, httpStatus);
    }

}

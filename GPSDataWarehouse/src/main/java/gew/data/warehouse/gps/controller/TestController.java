package gew.data.warehouse.gps.controller;

import gew.data.warehouse.gps.model.DeviceConfig;
import gew.data.warehouse.gps.model.DeviceUser;
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
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;

/**
 * @author Jason/GeW
 * @since 2019-03-10
 */
@Log4j2
@Profile({"dev", "test"})
@RestController
@Api(value="Test", tags="test")
@RequestMapping("/test")
public class TestController {

    @Autowired
    private DeviceUserService userService;

    @Autowired
    private DeviceConfigService configService;

    @Autowired
    private GPSDataService dataService;


}

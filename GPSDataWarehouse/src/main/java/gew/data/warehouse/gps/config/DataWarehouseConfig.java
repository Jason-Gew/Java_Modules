package gew.data.warehouse.gps.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Validated
@Component
@ConfigurationProperties(prefix = "data.warehouse")
public class DataWarehouseConfig {

    @NotNull
    private Integer defaultPageSize;
    @NotNull
    private Integer maxPageSize;
    private Integer maxDeviceNumPerUser;
    @NotNull
    @NotEmpty
    private String hashAlgorithm;
    private Boolean regenApiKey;
    private Boolean defaultEnableDevice;
    private Boolean autoConvertDatetime;
    private String sortOrderByTime;
}

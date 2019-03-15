package gew.data.warehouse.gps.service;


import gew.data.warehouse.gps.model.DeviceConfig;
import gew.data.warehouse.gps.model.DeviceUser;
import gew.data.warehouse.gps.model.GPSData;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Jason/GeW
 * @since 2019-03-04
 */
public interface GPSDataService {

    List<GPSData> getGpsData(final DeviceUser user, final DeviceConfig config);

    List<GPSData> getGpsData(final DeviceUser user, final DeviceConfig config, Pageable pageRequest);

    List<GPSData> getGpsData(final DeviceUser user, final DeviceConfig config, Pageable pageRequest,
                             final LocalDateTime start, final LocalDateTime end);

    List<GPSData> getGpsData(final DeviceUser user, final DeviceConfig config, Pageable pageRequest,
                             final Long start, final Long end);

    Long countGpsData(final DeviceUser user, final DeviceConfig config);

    GPSData addGpsData(GPSData data);

    List<GPSData> addAllGpsData(final List<GPSData> data);

    void removeGpsDataByUser(final DeviceUser user);

    void removeGpsDataByUserAndConfig(final DeviceUser user, final DeviceConfig config);
}

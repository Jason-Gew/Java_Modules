package gew.data.warehouse.gps.service;


import gew.data.warehouse.gps.model.DataWarehouseException;
import gew.data.warehouse.gps.model.DeviceConfig;
import gew.data.warehouse.gps.model.DeviceUser;

import java.util.List;

/**
 * @author Jason/GeW
 * @since 2019-03-04
 */
public interface DeviceConfigService {

    int countDeviceConfig(final DeviceUser user);

//    DeviceConfig getDeviceConfig(final String username, final String deviceId);

    DeviceConfig getDeviceConfig(final Long id);

    DeviceConfig getDeviceConfig(final DeviceUser user, final String deviceId);

    List<DeviceConfig> getDeviceConfig(final DeviceUser user);

//    List<DeviceConfig> getDeviceConfig(final String username);

    DeviceConfig addDeviceConfig(DeviceConfig config) throws DataWarehouseException;

    DeviceConfig updateDeviceConfig(DeviceConfig config) throws DataWarehouseException;

    void removeDeviceConfig(final DeviceUser user, final String deviceId) throws DataWarehouseException;

    boolean existDeviceConfig(final DeviceUser user, final String deviceId) throws DataWarehouseException;
}

package gew.data.warehouse.gps.service;

import gew.data.warehouse.gps.model.DataWarehouseException;
import gew.data.warehouse.gps.model.DeviceUser;

/**
 * @author Jason/GeW
 * @since 2019-03-04
 */
public interface DeviceUserService {

    DeviceUser getDeviceUser(final String username);

    DeviceUser getDeviceUserByEmail(final String email);

    DeviceUser addDeviceUser(DeviceUser deviceUser) throws DataWarehouseException;

    DeviceUser updateDeviceUser(DeviceUser deviceUser) throws DataWarehouseException;

    boolean existDeviceUser(final String username, final String email) throws DataWarehouseException;

    boolean existUserEmail(final String email);

    void removeDeviceUser(final String username) throws DataWarehouseException;
}

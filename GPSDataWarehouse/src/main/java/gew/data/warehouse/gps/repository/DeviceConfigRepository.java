package gew.data.warehouse.gps.repository;

import gew.data.warehouse.gps.model.DeviceConfig;
import gew.data.warehouse.gps.model.DeviceUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Jason/GeW
 */
@Repository
public interface DeviceConfigRepository extends JpaRepository<DeviceConfig, Long> {

    List<DeviceConfig> findByUser(final DeviceUser user);

    boolean existsByUserAndDeviceId(final DeviceUser user, final String deviceId);

    DeviceConfig findByUserAndDeviceId(final DeviceUser user, final String deviceId);

    @Query("SELECT COUNT(config) FROM DeviceConfig config WHERE config.user = :user")
    int countByUser(@Param("user") final DeviceUser user);
}

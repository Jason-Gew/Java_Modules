package gew.data.warehouse.gps.repository;

import gew.data.warehouse.gps.model.DeviceConfig;
import gew.data.warehouse.gps.model.DeviceUser;
import gew.data.warehouse.gps.model.GPSData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Jason/GeW
 * @since 2019-03-04
 */
@Repository
public interface GPSDataRepository extends JpaRepository<GPSData, Long> {

    @Query("SELECT data FROM GPSData data WHERE data.user = :user AND data.config = :config ORDER BY data.id DESC")
    List<GPSData> findByUserAndConfig(@Param("user") final DeviceUser user,
                                      @Param("config") final DeviceConfig config);

    Page<GPSData> findAllByUserAndConfig(final DeviceUser user, final DeviceConfig config, Pageable pageable);


    Page<GPSData> findByUserAndConfigAndUnixTimestampBetween(final DeviceUser user, final DeviceConfig config,
                                                             final Long start, final Long end, Pageable pageable);

    Long countByUserAndConfig(final DeviceUser user, final DeviceConfig config);

    void deleteByUser(final DeviceUser user);

    void deleteByUserAndConfig(final DeviceUser user, final DeviceConfig config);
}

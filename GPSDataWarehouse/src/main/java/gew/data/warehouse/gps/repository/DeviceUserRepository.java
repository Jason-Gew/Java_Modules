package gew.data.warehouse.gps.repository;

import gew.data.warehouse.gps.model.DeviceUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Jason/GeW
 * @since 2019-03-04
 */
@Repository
public interface DeviceUserRepository extends JpaRepository<DeviceUser, Long> {

    boolean existsByUsername(final String username);

    boolean existsByEmail(final String email);

    DeviceUser findByUsername(final String username);

    DeviceUser findByEmail(final String email);

}

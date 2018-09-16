package gew.caching.repository;

import gew.caching.entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


import java.util.Optional;

/**
 * @author Jason/GeW
 */
public interface UserInfoRepository extends CrudRepository<UserInfo, Long> {

    Optional<UserInfo> findByUsername(final String username);

    Optional<UserInfo> findByEmail(final String email);

    boolean existsByUsername(final String username);

    boolean existsByEmail(final String email);
}

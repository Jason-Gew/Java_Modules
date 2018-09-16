package gew.caching.service;


import gew.caching.entity.UserInfo;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

/**
 * @author Jason/GeW
 */
public interface UserInfoService {

    UserInfo getUserInfoById(final Long id);

    UserInfo getUserInfoByUsername(final String username);

    UserInfo getUserInfoByEmail(final String email);

    UserInfo createUserInfo(final UserInfo userInfo);

    UserInfo updateUserInfo(final UserInfo userInfo);

    String deleteUserInfo(final UserInfo userInfo);

    String deleteUserInfo(final Long id);

    String deleteUserInfo(final String username);

    Boolean existCheckByUsername(final String username);

    Boolean existCheckByEmail(final String email);

    Long count();

}

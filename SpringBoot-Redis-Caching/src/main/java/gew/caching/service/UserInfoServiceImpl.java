package gew.caching.service;

import gew.caching.entity.Status;
import gew.caching.entity.UserInfo;
import gew.caching.repository.UserInfoRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author Jason/GeW
 */
@Log4j2
@Service
public class UserInfoServiceImpl implements UserInfoService {

    @Autowired
    private UserInfoRepository userInfoRepository;


    @Override
    public UserInfo getUserInfoById(Long id) {
        UserInfo user = null;
        Optional<UserInfo> userInfo = userInfoRepository.findById(id);
        if (userInfo.isPresent()) {
            user = userInfo.get();
            log.info("Find User [{}, {}] By ID [{}]", user.getUsername(), user.getEmail(), user.getId());
        } else {
            log.info("User ID [{}] Does Not Exist...", id);
        }
        return user;
    }

    @Override
    @Cacheable(key = "#username", value = "UserInfo", unless = "#result == null")
    public UserInfo getUserInfoByUsername(String username) {
        UserInfo userInfo = null;
        Optional<UserInfo> result = userInfoRepository.findByUsername(username);
        if (result.isPresent()) {
            userInfo = result.get();
            log.info("Find User [{}, {}] By Name", userInfo.getUsername(), userInfo.getEmail());
        } else {
            log.info("User Name [{}] Does Not Exist...", username);
        }
        return userInfo;
    }

    @Override
    public UserInfo getUserInfoByEmail(String email) {
        UserInfo user = null;
        Optional<UserInfo> userInfo = userInfoRepository.findByEmail(email);
        if (userInfo.isPresent()) {
            user = userInfo.get();
            log.info("Find User [{}, {}] By Email", user.getUsername(), user.getEmail());
        } else {
            log.info("User Email [{}] Does Not Exist...", email);
        }
        return user;
    }

    @Override
    @Caching(put = {@CachePut(value = "UserInfo", key = "#userInfo.username")})
    public UserInfo createUserInfo(UserInfo userInfo) {
        UserInfo result;
        try {
            result = userInfoRepository.save(userInfo);
            log.info("User Info [{}, {}] Has Been Created", userInfo.getUsername(), userInfo.getEmail());
        } catch (Exception err) {
            log.error("Create User Info [{}] Failed: {}", userInfo, err.getMessage());
            throw err;
        }
        return result;
    }

    @Override
    @Caching(put = {@CachePut(value = "UserInfo", key = "#userInfo.username")})
    public UserInfo updateUserInfo(UserInfo userInfo) {
        UserInfo result;
        try {
            result = userInfoRepository.save(userInfo);
            log.info("User Info [{}, {}] Has Been Updated", userInfo.getUsername(), userInfo.getEmail());
        } catch (Exception err) {
            log.error("Update User Info [{}] Failed: {}", userInfo, err.getMessage());
            throw err;
        }
        return result;
    }

    @Override
    @Caching(evict = {@CacheEvict(key = "#userInfo.username", value = "UserInfo", beforeInvocation = true)})
    public String deleteUserInfo(UserInfo userInfo) {
        String result;
        if (userInfoRepository.existsById(userInfo.getId())) {
            try {
                userInfoRepository.delete(userInfo);
                result = Status.SUCCESS.toString();
                log.info("User Info [{}, {}] Has Been Deleted", userInfo.getUsername(), userInfo.getEmail());

            }  catch (Exception err) {
                log.error("Delete User Info [{}] Failed: {}", userInfo, err.getMessage());
                result = Status.FAIL.toString() + ": " + err.getMessage();
            }
        } else {
            log.error("Delete User Info [{}] Failed: User Does Not Exist", userInfo);
            result =Status.FAIL.toString() + ": User Does Not Exist";
        }
        return result;
    }

    @Override
    public String deleteUserInfo(Long id) {
        return null;
    }

    @Override
    @Caching(evict = {@CacheEvict(key = "#username", value = "UserInfo", beforeInvocation = true)})
    public String deleteUserInfo(String username) {
        return null;
    }

    @Override
    public Boolean existCheckByUsername(String username) {
        return userInfoRepository.existsByUsername(username);
    }

    @Override
    public Boolean existCheckByEmail(String email) {
        return userInfoRepository.existsByEmail(email);
    }

    @Override
    public Long count() {
        return userInfoRepository.count();
    }
}

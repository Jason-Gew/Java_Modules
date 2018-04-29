package gew.management.service;

import gew.management.model.UserInfo;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Map;

/**
 * @author Jason/GeW
 */
public interface UserInfoService {

    String addUser(final UserInfo user);

    List<UserInfo> getAllUsers();

    List<UserInfo> getAllUsers(PageRequest pageRequest);

    UserInfo getUserById(final String id);

    UserInfo getUserByName(final String username);

    boolean isExist(final UserInfo user);

    String updateUser(final UserInfo user);

    String updateUser(final String username, final Map<String, Object> content);

    String deleteUser(final UserInfo user);
}

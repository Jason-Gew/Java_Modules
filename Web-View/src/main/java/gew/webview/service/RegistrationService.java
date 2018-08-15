package gew.webview.service;

import gew.webview.model.Status;
import gew.webview.model.User;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Log4j2
@Component
@Qualifier("RegistrationService")
public class RegistrationService implements Registration {

    private static ConcurrentMap<String, User> USER_CONTAINER;

    public RegistrationService() {

    }

    @PostConstruct
    private void init() {
        if (USER_CONTAINER == null) {
            USER_CONTAINER = new ConcurrentHashMap<>();
        }
        User systemAdmin = new User("Admin", "System", "System-Admin",
                "System-Admin@user-web-view.com");
        systemAdmin.setId("999999-2018-Aug-08-08-16-48-567");
        USER_CONTAINER.put("System-Admin", systemAdmin);
    }

    @Override
    public int getCurrentUserNumber() {
        return USER_CONTAINER.size();
    }

    @Override
    public User getUser(final String username) {
        return USER_CONTAINER.get(username);
    }

    @Override
    public List<User> getAllUsers() {
        return new CopyOnWriteArrayList<>(USER_CONTAINER.values());
    }

    @Override
    public String addUser(final User user) {
        String result;
        if (USER_CONTAINER.containsKey(user.getUserName())) {
            result = Status.FAIL.value() + ": User Already Exists!";
        } else {
            boolean duplicateEmail = USER_CONTAINER.values().parallelStream()
                    .anyMatch(user1 -> user1.getEmail().equalsIgnoreCase(user.getEmail()));
            if (duplicateEmail) {
                log.error("Add User {} Failed: Email Already Exists", user);
                result = Status.FAIL.value() + ": Email Already Exists!";
            } else {
                USER_CONTAINER.put(user.getUserName(), user);
                log.info("Add User {} Success!", user);
                result = Status.SUCCESS.value();
            }
        }
        return result;
    }

    /**
     * Hard Check, consider similar names (only cases are different).
     * @param user user
     * @return boolean exist or not
     */
    @Override
    public boolean exist(final User user) {
        return USER_CONTAINER.keySet().parallelStream().anyMatch(name -> name.equalsIgnoreCase(user.getUserName()));
    }

    @Override
    public boolean exist(final String name) {
        return USER_CONTAINER.containsKey(name);
    }

    @Override
    public boolean delete(final String username) {
        boolean status = false;
        User user = USER_CONTAINER.remove(username);
        if (user != null) {
            log.info("Remove User {} Success", user);
            status = true;
        } else {
            log.error("Remove User By Username [{}] Failed: User Does Not Exist", username);
        }
        return status;
    }

    @Override
    public String update(final User user) {
        if (exist(user.getUserName())) {
            USER_CONTAINER.put(user.getUserName(), user);
            log.info("Update User [{}] Success", user);
            return Status.SUCCESS.value();
        } else {
            log.error("User [{}] Does Not Exist", user.getUserName());
            return Status.FAIL.value() + "User Does Not Exist";
        }
    }

}

package gew.management.service;

import com.mongodb.WriteResult;
import gew.management.model.Status;
import gew.management.model.UserInfo;
import gew.management.repository.UserInfoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Jason/GeW
 * @since 2018-04-20
 */
@Service
public class UserInfoServiceImpl implements UserInfoService {


    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private UserInfoRepository repository;

    private final static Logger logger = LoggerFactory.getLogger(UserInfoServiceImpl.class);

    @Override
    public String addUser(final UserInfo user) {
        String result;
        if (user.getUsername() != null && user.getEmail() != null) {
            try {
                repository.save(user);
                result = Status.SUCCESS.value();
                logger.info("User [{}] Has Been Added!", user.getUsername());
            } catch (Exception err) {
                result = Status.FAIL.value() + ": " + err.getMessage();
                logger.error("Add User [{}] Failed: {}", user.getUsername(), err.getMessage());
            }
        } else {
            result = Status.FAIL.value() + ": Invalid Username or Email";
            logger.error("Add User [{}] Failed: Invalid Username or Email...", user.getUsername());
        }
        return result;
    }

    @Override
    public List<UserInfo> getAllUsers() {
        List<UserInfo> users = new ArrayList<>();
        try {
            users.addAll(repository.findAll());
            logger.info("Get All Users Success");
        } catch (Exception err) {
            logger.error("Get All Users Failed: {}", err.getMessage());
        }
        return users;
    }

    @Override
    public List<UserInfo> getAllUsers(PageRequest pageRequest) {
        List<UserInfo> users = null;
        try {
            Page<UserInfo> pages = repository.findAll(pageRequest);
            users = pages.getContent();
            logger.info("Find Users Based on PageRequest: " + pageRequest.toString());
        } catch (Exception err) {
            logger.error("Get Users Based on PageRequest Failed: " + err.getMessage());
        }
        return users;
    }

    @Override
    public UserInfo getUserById(final String id) {
        UserInfo user = null;
        try {
            user = repository.findById(id);
            if (user == null) {
                logger.info("User [{}] Does Not Exist", id);
            } else {
                logger.info("Find User [{}] Information Success", id);
            }
        } catch (Exception err) {
            logger.error("Find User By ID [{}] Failed: {}", id, err.getMessage());
        }
        return user;
    }

    @Override
    public UserInfo getUserByName(final String username) {
        UserInfo user = null;
        try {
            user = repository.findByUsername(username);

        } catch (Exception err) {
            logger.error("Find User By Username [{}] Failed: {}", username, err.getMessage());
        }
        return user;
    }

    @Override
    public boolean isExist(final UserInfo user) {
        boolean status = false;
        try {
            Query query = new Query(Criteria.where("username").exists(true).andOperator(
                    Criteria.where("username").is(user.getUsername()))
            );
            query.addCriteria(Criteria.where("email").exists(true).andOperator(
                    Criteria.where("email").is(user.getEmail())));
            status = mongoTemplate.exists(query, UserInfo.class);

        } catch (Exception err) {
            logger.error("Check User Existence By Username [{}] & Email [{}] Failed: {}",
                    user.getUsername(), user.getEmail(), err.getMessage());
        }
        return status;
    }

    @Override
    public String updateUser(final UserInfo user) {
        String result;
        try {
            if (user.getId() == null || user.getId().isEmpty()) {
                return Status.FAIL.value() + ": Invalid User Record ID";
            }
            Query query = new Query(Criteria.where("_id").is(user.getId()));
            Update update = new Update();
            if (user.getUsername() != null || !user.getUsername().isEmpty()) {
                update.set("username", user.getUsername());
            }
            if (user.getEmail() != null || !user.getEmail().isEmpty()) {
                update.set("email", user.getEmail());
            }
            if (user.getPassword() != null || !user.getPassword().isEmpty()) {
                update.set("password", user.getPassword());
            }
            if (user.getPhone() != null) {
                update.set("password", user.getPhone());
            }
            if (user.getGroup() != null) {
                update.set("group", user.getGroup());
            }
            if (user.getAddress() != null) {
                update.set("address", user.getAddress());
            }
            if (user.getNote() != null) {
                update.set("note", user.getNote());
            }

            WriteResult writeResult = mongoTemplate.updateFirst(query, update, UserInfo.class);
            boolean check = writeResult.isUpdateOfExisting();
            if (check) {
                result = Status.SUCCESS.value();
            } else {
                result = Status.FAIL.value() + ": " + writeResult.toString();
            }
            logger.info("Update User [{}] Result: {}", user.getId(), writeResult);

        } catch (Exception err) {
            logger.error("Update User Failed: " + err.getMessage());
            result = Status.UNKNOWN.value() + ": " + err.getMessage();
        }
        return result;
    }

    @Override
    public String updateUser(String username, Map<String, Object> content) {
        String result;
        try {
            if (username == null || username.isEmpty()) {
                return Status.FAIL.value() + ": Invalid Username";
            } else if (content == null || content.isEmpty()) {
                return Status.FAIL.value() + ": Invalid User Info";
            } else {
                Query query = new Query(Criteria.where("username").is(username));
                Update update = new Update();
                if (content.containsKey("password") && content.get("password") != null) {
                    update.set("password", content.get("password"));
                }
                if (content.containsKey("email") && content.get("email") != null) {
                    update.set("email", content.get("email"));
                }
                if (content.containsKey("phone") && content.get("phone") != null) {
                    update.set("phone", content.get("phone"));
                }
                if (content.containsKey("group") && content.get("group") != null) {
                    update.set("group", content.get("group"));
                }
                if (content.containsKey("address") && content.get("address") != null) {
                    update.set("address", content.get("address"));
                }
                if (content.containsKey("note") && content.get("note") != null) {
                    update.set("note", content.get("note"));
                }
                WriteResult writeResult = mongoTemplate.updateFirst(query, update, UserInfo.class);
                boolean check = writeResult.isUpdateOfExisting();
                if (check) {
                    result = Status.SUCCESS.value();
                } else {
                    result = Status.FAIL.value() + ": " + writeResult.toString();
                }
                logger.info("Update User [{}] Result: {}", username, writeResult);
            }
        } catch (Exception err) {
            logger.error("Update User [{}] Failed: {}", username, err.getMessage());
            result = Status.UNKNOWN.value() + ": " + err.getMessage();
        }
        return result;
    }

    @Override
    public String deleteUser(final UserInfo user) {
        String result;
        try {
            repository.delete(user);
            result = Status.SUCCESS.value();
        } catch (Exception err) {
            logger.error("Delete User [{}] Failed: {}", user.getUsername(), err.getMessage());
            result = Status.FAIL.value() + ": " + err.getMessage();
        }
        return result;
    }
}

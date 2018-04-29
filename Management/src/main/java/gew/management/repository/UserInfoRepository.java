package gew.management.repository;

import gew.management.model.UserInfo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;


/**
 * @author Jason/GeW
 */
public interface UserInfoRepository extends MongoRepository<UserInfo, String> {

    @Query("{_id:'?0'}")
    UserInfo findById(final String id);

    @Query("{username:'?0'}")
    UserInfo findByUsername(final String username);

}

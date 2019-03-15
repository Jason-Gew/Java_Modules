package gew.data.warehouse.gps.service;

import gew.data.warehouse.gps.model.DataWarehouseException;
import gew.data.warehouse.gps.model.DeviceUser;
import gew.data.warehouse.gps.repository.DeviceUserRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

/**
 * @author Jason/GeW
 * @since 2019-03-06
 */
@Log4j2
@Service
public class DeviceUserServiceImpl implements DeviceUserService {

    @Autowired
    private DeviceUserRepository repository;


    @Override
    public DeviceUser getDeviceUser(String username) {
        return repository.findByUsername(username);
    }

    @Override
    public DeviceUser getDeviceUserByEmail(String email) {
        return repository.findByEmail(email);
    }

    @Override
    @Transactional
    public DeviceUser addDeviceUser(DeviceUser deviceUser) throws DataWarehouseException {
        if (existDeviceUser(deviceUser.getUsername(), deviceUser.getEmail())) {
            log.error("Add Device User [{}] Failed: Username or Email Already Exist", deviceUser.getUsername());
            throw new DataWarehouseException("Username or Email Already Exist");
        } else {
            DeviceUser saved = repository.save(deviceUser);
            if (saved != null) {
                log.info("Add Device User [{}] Success", deviceUser.getUsername());
            }
            return saved;
        }
    }

    @Override
    @Transactional
    public DeviceUser updateDeviceUser(DeviceUser deviceUser) throws DataWarehouseException {
        try {
            DeviceUser updated = repository.save(deviceUser);
            if (updated != null) {
                log.info("Update Device User [{}] Success", deviceUser.getUsername());
            }
            return updated;
        } catch (Exception err) {
            log.error(err.getCause().getMessage());
            throw new DataWarehouseException("Update Device User Failed: " + err.getMessage(), err.getCause());
        }
    }

    @Override
    public boolean existDeviceUser(String username, String email) {
        if (username == null || username.isEmpty() || email == null || email.isEmpty()) {
            log.error("Invalid Username({}) or Email({}) Format", username, email);
            throw new IllegalArgumentException("Invalid Username or Email");
        } else if (repository.existsByUsername(username)) {
            log.debug("Device User [username = {}] Already Exist!", username);
            return true;
        } else if (repository.existsByEmail(email)) {
            log.debug("Device User [email = {}] Already Exist!", email);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean existUserEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    @Transactional
    public void removeDeviceUser(String username) throws DataWarehouseException {
        if (repository.existsByUsername(username)) {
            repository.delete(repository.findByUsername(username));
            log.info("Remove Device User [{}] Success", username);
        } else {
            log.error("Remove Device User [{}] Failed: User Does Not Exist", username);
            throw new DataWarehouseException("Device User Does Not Exist");
        }
    }
}

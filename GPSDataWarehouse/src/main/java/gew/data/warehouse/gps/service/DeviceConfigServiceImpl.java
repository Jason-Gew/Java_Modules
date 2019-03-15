package gew.data.warehouse.gps.service;

import gew.data.warehouse.gps.model.DataWarehouseException;
import gew.data.warehouse.gps.model.DeviceConfig;
import gew.data.warehouse.gps.model.DeviceUser;
import gew.data.warehouse.gps.repository.DeviceConfigRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

/**
 * @author Jason/GeW
 * @since 2019-03-06
 */
@Log4j2
@Service
public class DeviceConfigServiceImpl implements DeviceConfigService {

    @Autowired
    private DeviceConfigRepository repository;


    @Override
    public int countDeviceConfig(DeviceUser user) {
        return repository.countByUser(user);
    }

    @Override
    public DeviceConfig getDeviceConfig(DeviceUser user, String deviceName) {
        return repository.findByUserAndDeviceId(user, deviceName);
    }

    @Override
    public List<DeviceConfig> getDeviceConfig(DeviceUser user) {
        return repository.findByUser(user);
    }

    @Override
    public DeviceConfig getDeviceConfig(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public DeviceConfig addDeviceConfig(DeviceConfig config) throws DataWarehouseException {
        if (existDeviceConfig(config.getUser(), config.getDeviceId())) {
            log.error("Add Device Config [{}] for User [{}] Failed: Config Already Exist", config.getDeviceId(),
                    config.getUser().getUsername());
            throw new DataWarehouseException("Device Config [" + config.getDeviceId() + "] " +
                    "For User [" + config.getUser().getUsername() + "] Already Exist");
        } else {
            DeviceConfig saved = repository.save(config);
            if (saved != null) {
                log.info("Add Device Config [{}] for User [{}] Success",
                        config.getDeviceId(), config.getUser().getUsername());
            }
            return saved;
        }
    }

    @Override
    @Transactional
    public DeviceConfig updateDeviceConfig(DeviceConfig config) throws DataWarehouseException {
        try {
            DeviceConfig updated = repository.save(config);
            if (updated != null) {
                log.info("Update Device Config [{}] for User [{}] Success",
                        config.getDeviceId(), config.getUser().getUsername());
            }
            return updated;
        } catch (Exception err) {
            log.error("Update Device Config [{}] for User [{}] Failed: {}",
                    config.getDeviceId(), config.getUser().getUsername(), err.getMessage());
            throw new DataWarehouseException("Update Device Config Failed", err.getCause());
        }
    }

    @Override
    @Transactional
    public void removeDeviceConfig(DeviceUser user, String deviceId) throws DataWarehouseException {
        if (repository.existsByUserAndDeviceId(user, deviceId)) {
            repository.delete(repository.findByUserAndDeviceId(user, deviceId));
            log.info("Remove Device Config [{}] for User [{}] Success", deviceId, user.getUsername());
        } else {
            log.error("Remove Device Config [{}] for User [{}] Failed: Config Does Not Exist",
                    deviceId, user.getUsername());
            throw new DataWarehouseException("Device Config [" + deviceId + "] " +
                    "For User [" + user.getUsername() + "] Does Not Exist");
        }
    }

    @Override
    public boolean existDeviceConfig(DeviceUser user, String deviceId) throws DataWarehouseException {
        return repository.existsByUserAndDeviceId(user, deviceId);
    }
}

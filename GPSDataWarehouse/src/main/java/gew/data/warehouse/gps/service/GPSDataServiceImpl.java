package gew.data.warehouse.gps.service;


import gew.data.warehouse.gps.config.DataWarehouseConfig;
import gew.data.warehouse.gps.model.DataWarehouseException;
import gew.data.warehouse.gps.model.DeviceConfig;
import gew.data.warehouse.gps.model.DeviceUser;
import gew.data.warehouse.gps.model.GPSData;
import gew.data.warehouse.gps.repository.GPSDataRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Jason/GeW
 * @since 2019-03-06
 */
@Log4j2
@Service
public class GPSDataServiceImpl implements GPSDataService {

    @Autowired
    private GPSDataRepository repository;

    @Autowired
    private DataWarehouseConfig dataWarehouseConfig;


    @Override
    public List<GPSData> getGpsData(DeviceUser user, DeviceConfig config) {
        List<GPSData> data;
        try {
            data = repository.findByUserAndConfig(user, config);
            if (data == null) {
                log.info("No Record for User [{}] with Device [{}]", user.getUsername(), config.getDeviceId());
                return new ArrayList<>(0);
            } else {
                return data;
            }
        } catch (Exception err) {
            log.error("Find Record for User [{}] with Device [{}] Failed: {}",
                    user.getUsername(), config.getDeviceId(), err.getMessage());
            throw new DataWarehouseException("Find GPS Data Failed: " + err.getMessage(), err.getCause());
        }
    }

    @Override
    public List<GPSData> getGpsData(DeviceUser user, DeviceConfig config, Pageable pageRequest) {
        try {
            Page<GPSData> gpsDataPage = repository.findAllByUserAndConfig(user, config, pageRequest);
            if (gpsDataPage == null) {
                log.info("No Record for User [{}] with Device [{}]", user.getUsername(), config.getDeviceId());
                return new ArrayList<>(0);
            } else {
                return gpsDataPage.get().collect(Collectors.toList());
            }
        } catch (Exception err) {
            log.error("Find Record for User [{}] with Device [{}] Failed: {}",
                    user.getUsername(), config.getDeviceId(), err.getMessage());
            throw new DataWarehouseException("Find GPS Data Failed: " + err.getMessage() , err.getCause());
        }
    }

    @Override
    public List<GPSData> getGpsData(DeviceUser user, DeviceConfig config, Pageable pageRequest,
                                    LocalDateTime start, LocalDateTime end) {
        //TODO
        return null;
    }

    @Override
    public List<GPSData> getGpsData(DeviceUser user, DeviceConfig config, Pageable pageRequest, Long start, Long end) {
        try {
            Page<GPSData> gpsDataPage = repository
                    .findByUserAndConfigAndUnixTimestampBetween(user, config,  start, end, pageRequest);
            if (gpsDataPage == null) {
                return new ArrayList<>(0);
            } else {
                return gpsDataPage.get().collect(Collectors.toList());
            }
        } catch (Exception err) {
            log.error("Find Record for User [{}] with Device [{}], Timestamp [{} ~ {}] Failed: {}",
                    user.getUsername(), config.getDeviceId(), start, end, err.getMessage());
            throw new DataWarehouseException("Find GPS Data Failed: " + err.getMessage() , err.getCause());
        }
    }

    @Override
    public Long countGpsData(DeviceUser user, DeviceConfig config) {
        return repository.countByUserAndConfig(user, config);
    }

    @Override
    public GPSData addGpsData(GPSData data) {
        return repository.save(data);
    }

    @Override
    @Transactional
    public List<GPSData> addAllGpsData(List<GPSData> data) {
        return repository.saveAll(data);
    }


    @Override
    @Transactional
    public void removeGpsDataByUser(DeviceUser user) {
        try {
            repository.deleteByUser(user);
        } catch (Exception err) {
            log.error("Remove GPS Data for User [{}] Failed: {}", user.getUsername(), err.getMessage());
            throw new DataWarehouseException("Remove GPS Data By User Failed: " + err.getMessage() , err.getCause());
        }
    }

    @Override
    @Transactional
    public void removeGpsDataByUserAndConfig(DeviceUser user, DeviceConfig config) {
        try {
            repository.deleteByUserAndConfig(user, config);
        } catch (Exception err) {
            log.error("Remove GPS Data for User [{}], Device [{}] Failed: {}",
                    user.getUsername(), config.getDeviceId(), err.getMessage());
            throw new DataWarehouseException("Remove GPS Data By User and Config Failed: " +
                    err.getMessage(), err.getCause());
        }
    }
}

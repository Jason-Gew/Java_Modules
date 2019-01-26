package gew.zookeeper.util;

import gew.zookeeper.config.NodeManagerConfig;
import gew.zookeeper.config.ZooKeeperConfig;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Static Class for loading config
 * @author Jason/GeW
 */
public class LoadConfig {


    private static final String DEFAULT_CONFIG_PATH = "config/config.properties";

    private LoadConfig() {
        // Private Constructor, prevent instantiation
    }


    public static ZooKeeperConfig loadZooKeeperConfig() throws Exception {
        return loadZooKeeperConfig(DEFAULT_CONFIG_PATH);
    }


    public static ZooKeeperConfig loadZooKeeperConfig(final String configPath) throws Exception {
        ZooKeeperConfig zooKeeperConfig;
        try (InputStream inputStream = new FileInputStream(configPath)) {
            Properties config = new Properties();
            config.load(inputStream);
            zooKeeperConfig = loadZooKeeperConfig(config);
        }
        return zooKeeperConfig;
    }

    public static ZooKeeperConfig loadZooKeeperConfig(final Properties property) {
        if (property == null) {
            throw new IllegalArgumentException("Invalid ZooKeeper Properties");
        }

        ZooKeeperConfig zooKeeperConfig = ZooKeeperConfig.builder().build();
        if (property.getProperty("zookeeper.host") == null || property.getProperty("zookeeper.host").isEmpty()) {
            throw new IllegalArgumentException("Missing ZooKeeper Host Config");
        } else {
            zooKeeperConfig.setHost(property.getProperty("zookeeper.host"));
        }

        if (property.getProperty("zookeeper.root") == null || property.getProperty("zookeeper.root").isEmpty()
            || property.getProperty("zookeeper.root").equalsIgnoreCase("zookeeper")) {
            throw new IllegalArgumentException("Invalid ZooKeeper Root Path Config");
        } else {
            zooKeeperConfig.setRoot(property.getProperty("zookeeper.root"));
        }

        if (property.getProperty("zookeeper.timeout") == null) {
            throw new IllegalArgumentException("Missing ZooKeeper Session Timeout Config");
        } else {
            int timeout;
            try {
                timeout = Integer.parseInt(property.getProperty("zookeeper.timeout"));
            } catch (ArithmeticException | NumberFormatException e) {
                throw new IllegalArgumentException("Invalid ZooKeeper Session Timeout Config");
            }
            if (timeout < 2000) {
                System.err.println("ZooKeeper Session Timeout is too low. System set to default");
                zooKeeperConfig.setTimeout(5000);
            } else {
                zooKeeperConfig.setTimeout(timeout);
            }
        }

        if (property.getProperty("zookeeper.readOnly") == null) {
            throw new IllegalArgumentException("Missing ZooKeeper Read Only Config");
        } else {
            boolean readOnly;
            try {
                readOnly = Boolean.parseBoolean(property.getProperty("zookeeper.readOnly"));
            } catch (Exception err) {
                System.err.println("Invalid ZooKeeper Read Only setting: " + err.getMessage());
                throw new IllegalArgumentException("Invalid ZooKeeper Read Only Setting");
            }
            zooKeeperConfig.setReadOnly(readOnly);
        }

        if (property.getProperty("zookeeper.sessionId") != null
                && !property.getProperty("zookeeper.sessionId").isEmpty()) {
            long sessionId;
            try {
                sessionId = Long.parseLong(property.getProperty("zookeeper.sessionId"));
                zooKeeperConfig.setSessionId(sessionId);
            } catch (ArithmeticException e) {
                throw new IllegalArgumentException("Invalid ZooKeeper Session ID Config");
            }
        }
        if (property.getProperty("zookeeper.sessionPassword") != null
                && !property.getProperty("zookeeper.sessionPassword").isEmpty()) {
            zooKeeperConfig.setSessionPassword(property.getProperty("zookeeper.sessionPassword"));
        }

        if (property.getProperty("zookeeper.authType") != null
                && !property.getProperty("zookeeper.authType").isEmpty()) {
            zooKeeperConfig.setAuthType(property.getProperty("zookeeper.authType"));
        }
        if (property.getProperty("zookeeper.authInfo") != null
                && !property.getProperty("zookeeper.authInfo").isEmpty()) {
            zooKeeperConfig.setAuthInfo(property.getProperty("zookeeper.authInfo"));
        }

        if (property.getProperty("zookeeper.nodeMode") == null || property.getProperty("zookeeper.nodeMode").isEmpty()) {
            throw new IllegalArgumentException("Missing ZooKeeper Node Mode Setting");
        } else {
            switch (property.getProperty("zookeeper.nodeMode").toLowerCase()) {
                case "server":
                    zooKeeperConfig.setNodeMode(ZooKeeperConfig.NodeMode.SERVER);
                    break;
                case "client":
                    zooKeeperConfig.setNodeMode(ZooKeeperConfig.NodeMode.CLIENT);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid ZooKeeper Node Mode Setting");
            }
        }

        if (property.getProperty("zookeeper.sequentialMode") == null) {
            throw new IllegalArgumentException("Missing ZooKeeper Sequential Mode Config");
        } else {
            boolean sequentialMode;
            try {
                sequentialMode = Boolean.parseBoolean(property.getProperty("zookeeper.sequentialMode"));
            } catch (Exception err) {
                System.err.println("Invalid ZooKeeper Sequential Mode setting: " + err.getMessage());
                throw new IllegalArgumentException("Invalid ZooKeeper Sequential Mode Setting");
            }
            zooKeeperConfig.setSequentialMode(sequentialMode);
        }

        if (property.getProperty("zookeeper.enableWatching") == null) {
            throw new IllegalArgumentException("Missing ZooKeeper Enable Watching Config");
        } else {
            boolean enableWatching;
            try {
                enableWatching = Boolean.parseBoolean(property.getProperty("zookeeper.enableWatching"));
            } catch (Exception err) {
                System.err.println("Invalid ZooKeeper Enable Watching setting: " + err.getMessage());
                throw new IllegalArgumentException("Invalid ZooKeeper Enable Watching Setting");
            }
            zooKeeperConfig.setEnableWatching(enableWatching);
        }

        boolean advancedWatcher;
        try {
            advancedWatcher = Boolean
                    .parseBoolean(property.getProperty("zookeeper.enableAdvancedWatcher", "false"));
        } catch (Exception err) {
            System.err.println("Invalid ZooKeeper Enable Advanced Watcher setting: " + err.getMessage());
            throw new IllegalArgumentException("Invalid ZooKeeper Enable Advanced Watcher Setting");
        }
        zooKeeperConfig.setEnableAdvancedWatcher(advancedWatcher);

        return zooKeeperConfig;
    }



    public static NodeManagerConfig loadNodeManagerConfig() throws Exception {
        return loadNodeManagerConfig(DEFAULT_CONFIG_PATH);
    }


    public static NodeManagerConfig loadNodeManagerConfig(final String configPath) throws Exception {
        NodeManagerConfig nodeManagerConfig;
        try (InputStream inputStream = new FileInputStream(configPath)) {
            Properties config = new Properties();
            config.load(inputStream);
            nodeManagerConfig = loadNodeManagerConfig(config);
        }
        return nodeManagerConfig;
    }


    public static NodeManagerConfig loadNodeManagerConfig(final Properties property) throws Exception {
        if (property == null) {
            throw new IllegalArgumentException("Invalid Node Manager Config Properties");
        }

        NodeManagerConfig config = new NodeManagerConfig();
        if (property.getProperty("manager.id") == null) {
            throw new IllegalArgumentException("Missing Node Manager ID Config");
        } else {
            int id;
            try {
                id = Integer.parseInt(property.getProperty("manager.id"));
            } catch (ArithmeticException | NumberFormatException e) {
                throw new IllegalArgumentException("Invalid Node ID Config: " + e.getMessage());
            }
            config.setId(id);
        }

        if (property.getProperty("manager.name") == null
                || property.getProperty("manager.name").isEmpty()) {
            throw new IllegalArgumentException("Missing Node Name Config");
        } else {
            config.setName(property.getProperty("manager.name"));
        }

        if (property.getProperty("manager.timezoneOffset") != null
                && !property.getProperty("manager.timezoneOffset").isEmpty()) {
            config.setTimezoneOffset(property.getProperty("manager.timezoneOffset"));
        }

        if (property.getProperty("manager.ports") != null && !property.getProperty("manager.ports").isEmpty()) {
            List<Integer> portList = new ArrayList<>();
            if (property.getProperty("manager.ports").contains(",")) {
                String[] ports = property.getProperty("manager.ports").split(",");
                for (String port : ports) {
                    try {
                        int portNum = Integer.parseInt(port.trim());
                        portList.add(portNum);
                    } catch (ArithmeticException | NumberFormatException e) {
                        System.err.println("Invalid Node Ports Config");
                        throw new IllegalArgumentException("Invalid Node Ports Config");
                    }
                }
            } else {
                int port;
                try {
                    port = Integer.parseInt(property.getProperty("manager.ports"));
                    portList.add(port);
                } catch (ArithmeticException | NumberFormatException e) {
                    System.err.println("Invalid Node Ports Config");
                    throw new IllegalArgumentException("Invalid Node Ports Config");
                }
            }
            config.setPorts(portList);
        }

        if (property.getProperty("manager.notes") != null
                && !property.getProperty("manager.notes").isEmpty()) {
            config.setNotes(property.getProperty("manager.notes"));
        }

        return config;
    }


}

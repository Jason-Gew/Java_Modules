package gew.zookeeper;

import gew.zookeeper.client.NodeClient;
import gew.zookeeper.client.NodeClientImpl;
import gew.zookeeper.client.NodeManager;
import gew.zookeeper.config.ZooKeeperConfig;
import gew.zookeeper.model.NodeInfo;
import gew.zookeeper.model.ZKData;
import lombok.extern.log4j.Log4j2;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;


/**
 * Distributed System Dynamic Management Bases On Apache Zookeeper
 * @author Jason/GeW
 * @since 2018-12-20
 */
@Log4j2
public class Main {


    public static void main(String[] args) throws InterruptedException {
        ZooKeeperConfig config = ZooKeeperConfig.builder()
                .host("192.168.50.117:2181")
                .root("/servers")
                .timeout(10000)
                .readOnly(false)
                .enableWatching(true)
                .sequentialMode(false)
                .build();

//        nodeManagementExample(config, args[0]);
//        Thread.sleep(Long.MAX_VALUE);
        config.setReadOnly(true);
        nodeClientExample(config);
    }


    private static void nodeManagementExample(final ZooKeeperConfig config, final String name) {
        Thread manager = new Thread(() -> {
            NodeManager.setZkConfig(config);
            NodeManager manager1 = NodeManager.getInstance();
            try {
                boolean stat = manager1.connect();
                log.info("Connection Result: {}", stat);
                Random random = new Random();
                NodeInfo info = new NodeInfo(random.nextInt(128), name, "1.0.0");
                Map<String, String> infoMap = new HashMap<>();
                infoMap.put("ip", "192.168.0.123");
                infoMap.put("port", "8080");
                info.setInfo(infoMap);
                manager1.register(info);
                manager1.listNodes().forEach(System.out::println);


            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                Thread.sleep(Long.MAX_VALUE);
            } catch (InterruptedException e) {
                manager1.deregister();
                manager1.disconnect();
                log.info("User End The Process!");
            }
        });
        manager.setDaemon(true);
        manager.run();
    }


    private static void nodeClientExample(final ZooKeeperConfig config) throws InterruptedException {
        Callable<ZKData> client = new NodeClientImpl(config);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        FutureTask<ZKData> futureTask = new FutureTask<>(client);
        executor.submit(futureTask);
        Thread.sleep(5000);
        if (futureTask.isDone()) {
            try {
                ZKData data = futureTask.get();
                log.info("Client -> Root Path: {}", data.getRoot());
                data.getNodes().entrySet().forEach(System.out::println);
            } catch (ExecutionException err) {
                log.error(err.getMessage());
            }
            executor.shutdown();
        }
    }
}

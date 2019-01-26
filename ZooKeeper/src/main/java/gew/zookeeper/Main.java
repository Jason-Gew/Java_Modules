package gew.zookeeper;

import gew.zookeeper.client.AdvancedWatcher;
import gew.zookeeper.client.NodeClient;
import gew.zookeeper.client.NodeClientImpl;
import gew.zookeeper.client.NodeManager;
import gew.zookeeper.config.NodeManagerConfig;
import gew.zookeeper.config.ZooKeeperConfig;
import gew.zookeeper.model.NodeInfo;
import gew.zookeeper.model.ZKData;
import gew.zookeeper.util.LoadConfig;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;


/**
 * Distributed System Dynamic Management Bases On Apache Zookeeper
 * @author Jason/GeW
 * @since 2018-12-20
 */
@Log4j2
public class Main {

    public static void main(String[] args) throws Exception {

        ZooKeeperConfig config = LoadConfig.loadZooKeeperConfig();
        CommandLineParser parser = new DefaultParser();
        CommandLine line = parser.parse(setArgParser(), args);
        if (line.hasOption('a')) {
            config.setHost(line.getOptionValue('a'));
        } else if (line.hasOption("address")) {
            config.setHost(line.getOptionValue("address"));
        }
        if (line.hasOption("root")) {
            config.setRoot(line.getOptionValue("root"));
        }
        if (line.hasOption('s') || line.hasOption("server")) {
            nodeManagementExample(config, line.getOptionValue("name"));
        } else if (line.hasOption('c') || line.hasOption("client")) {
            if (line.hasOption("watching")) {
                nodeClientExample2(config);
            } else {
                nodeClientExample(config);
            }
        } else {
            System.err.println("\nFor Example Usage, Must Choose Server Mode (Register + Listen) or Client Mode (Listen)\n");
        }

    }

    private static Options setArgParser() {
        Options options = new Options();
        options.addOption("s", "server", false,"Application Node Server Mode");
        options.addOption("c", "client",false,"Application Node Client Mode");
        options.addOption("a", "address",true,"ZooKeeper Host Address (10.0.0.1:2181)");
        options.addOption("name", true,"Node Name");
        options.addOption("root", true,"Node Name");
        options.addOption("watching", false,"Enable Continuous Watching Node Changes");
        return options;
    }

    private static void nodeManagementExample(final ZooKeeperConfig config, final String name) throws Exception {
        NodeManagerConfig nodeConfig = LoadConfig.loadNodeManagerConfig();
        Thread managerT = new Thread(() -> {
            NodeManager.setZkConfig(config);
            NodeManager manager = NodeManager.getInstance();
            try {
                boolean stat = manager.connect();
                log.info("Connection Result: {}", stat);
                Random random = new Random();
                NodeInfo info = new NodeInfo(random.nextInt(128),
                        name == null ? nodeConfig.getName() : name, "1.0.0");
                Map<String, String> infoMap = new HashMap<>();
                infoMap.put("ip", "192.168.0." + random.nextInt(250));
                infoMap.put("port", nodeConfig.getPorts().toString());
                info.setInfo(infoMap);
                info.setTimezone(nodeConfig.getTimezoneOffset());
                manager.register(info);
                manager.listNodes().forEach(System.out::println);

            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                TimeUnit.DAYS.sleep(1);
            } catch (InterruptedException e) {
                manager.deregister();
                manager.disconnect();
                log.info("User End The Process!");
            }
        });
        managerT.setDaemon(true);
        managerT.run();
    }


    private static void nodeClientExample(final ZooKeeperConfig config) throws InterruptedException {
        Callable<ZKData> client = new NodeClientImpl(config);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        FutureTask<ZKData> futureTask = new FutureTask<>(client);
        executor.submit(futureTask);
        while (!Thread.interrupted()) {
            if (futureTask.isDone()) {
                try {
                    ZKData data = futureTask.get();
                    log.info("Client -> Root Path: {}\n", data.getRoot());
                    data.getNodes().entrySet().forEach(System.out::println);
                } catch (ExecutionException err) {
                    log.error(err.getMessage());
                }
                executor.shutdown();
                break;
            }
        }
    }

    private static void nodeClientExample2(final ZooKeeperConfig config) throws InterruptedException {
        NodeClient client = new NodeClientImpl(config);
        Queue<ZKData> queue = new LinkedBlockingQueue<>();
        AdvancedWatcher adWatcher = new AdvancedWatcher(config.getRoot(), null, queue);
        adWatcher.enableWatching(true);
        Thread t = new Thread(() -> {
            try {
                boolean result = client.connect();
                log.info("Connection Result: " + result);
                List<String> nodes = client.listNodes(config.getRoot(), adWatcher);
                System.out.println("\nCurrent Node(s): ");
                nodes.forEach(System.out::println);
                TimeUnit.DAYS.sleep(1);
            } catch (Exception err) {
                log.error(err.getMessage());
            }
        });
        t.setDaemon(true);
        t.start();
        while (!Thread.interrupted()) {
            if (!queue.isEmpty()) {
                ZKData data = queue.poll();
                System.out.println("\n" + data.getTimestamp() + " Current State: " + data.getType());
                data.getNodes().entrySet().forEach(System.out::println);
            }
        }
        t.join();
    }
}

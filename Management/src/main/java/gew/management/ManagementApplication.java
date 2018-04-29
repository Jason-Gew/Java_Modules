package gew.management;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Jason/GeW
 */
@SpringBootApplication
public class ManagementApplication {

	private static final String BANNER = "   __  ___                                       __     ____         __          \n" +
            "  /  |/  /__ ____  ___ ____ ____ __ _  ___ ___  / /_   / __/_ _____ / /____ __ _ \n" +
            " / /|_/ / _ `/ _ \\/ _ `/ _ `/ -_)  ' \\/ -_) _ \\/ __/  _\\ \\/ // (_-</ __/ -_)  ' \\\n" +
            "/_/  /_/\\_,_/_//_/\\_,_/\\_, /\\__/_/_/_/\\__/_//_/\\__/  /___/\\_, /___/\\__/\\__/_/_/_/\n" +
            "                      /___/                              /___/                   ";

	private static final Logger LOGGER = LoggerFactory.getLogger(ManagementApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(ManagementApplication.class, args);
		LOGGER.info("\n" + BANNER + "  Started!\n");

	}
}

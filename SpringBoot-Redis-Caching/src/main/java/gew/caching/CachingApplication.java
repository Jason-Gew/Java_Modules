package gew.caching;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author Jason/GeW
 * @since 2018-09-01
 */
@Log4j2
@EnableCaching
@ComponentScan
@SpringBootApplication
public class CachingApplication {

    private static final String BANNER = "   _____            _                ____           ___         ______           __    _            \n" +
			"  / ___/____  _____(_)___  ____ _   / __ \\___  ____/ (_)____   / ____/___ ______/ /_  (_)___  ____ _\n" +
			"  \\__ \\/ __ \\/ ___/ / __ \\/ __ `/  / /_/ / _ \\/ __  / / ___/  / /   / __ `/ ___/ __ \\/ / __ \\/ __ `/\n" +
			" ___/ / /_/ / /  / / / / / /_/ /  / _, _/  __/ /_/ / (__  )  / /___/ /_/ / /__/ / / / / / / / /_/ / \n" +
			"/____/ .___/_/  /_/_/ /_/\\__, /  /_/ |_|\\___/\\__,_/_/____/   \\____/\\__,_/\\___/_/ /_/_/_/ /_/\\__, /  \n" +
			"    /_/                 /____/                                                             /____/   ";

	public static void main(String[] args) {
		SpringApplication.run(CachingApplication.class, args);
		log.info("\n" + BANNER + " Started!\n");
	}
}

package gew.data.warehouse.gps;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * <h1>GPS Data Warehouse Service</h1>
 * @author Jason/GeW
 * @since 2019-03-04
 */
@Log4j2
@SpringBootApplication
public class GpsDataWarehouseApplication {

	private static final String BANNER = "   __________  _____    ____        __           _       __                __ \n" +
			"  / ____/ __ \\/ ___/   / __ \\____ _/ /_____ _   | |     / /___ _________  / /_  ____  __  __________ \n" +
			" / / __/ /_/ /\\__ \\   / / / / __ `/ __/ __ `/   | | /| / / __ `/ ___/ _ \\/ __ \\/ __ \\/ / / / ___/ _ \\\n" +
			"/ /_/ / ____/___/ /  / /_/ / /_/ / /_/ /_/ /    | |/ |/ / /_/ / /  /  __/ / / / /_/ / /_/ (__  )  __/\n" +
			"\\____/_/    /____/  /_____/\\__,_/\\__/\\__,_/     |__/|__/\\__,_/_/   \\___/_/ /_/\\____/\\__,_/____/\\___/ ";


	public static void main(String[] args) {
		SpringApplication.run(GpsDataWarehouseApplication.class, args);
		log.info("\n{} By Jason/GeW -> Started! \n", BANNER);
	}

}

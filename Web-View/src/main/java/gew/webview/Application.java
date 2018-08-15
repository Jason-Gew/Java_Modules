package gew.webview;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Log4j2
@SpringBootApplication
public class Application {

    private static final String BANNER = " ____ ___                      __      __      ___.     ____   ____.__               \n" +
            "|    |   \\______ ___________  /  \\    /  \\ ____\\_ |__   \\   \\ /   /|__| ______  _  __\n" +
            "|    |   /  ___// __ \\_  __ \\ \\   \\/\\/   // __ \\| __ \\   \\   Y   / |  |/ __ \\ \\/ \\/ /\n" +
            "|    |  /\\___ \\\\  ___/|  | \\/  \\        /\\  ___/| \\_\\ \\   \\     /  |  \\  ___/\\     / \n" +
            "|______//____  >\\___  >__|      \\__/\\  /  \\___  >___  /    \\___/   |__|\\___  >\\/\\_/  \n" +
            "             \\/     \\/               \\/       \\/    \\/                     \\/        ";

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
		log.info("\n" + BANNER + " Started!\n");
	}
}

package pitt.infsci2140.finalprj;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

/**
 * Don't change anything in this class UNLESS necessary
 */
@SpringBootApplication
public class FinalApplication implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(FinalApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(FinalApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        TimeZone.setDefault(TimeZone.getTimeZone("America/New_York"));
        if (args.getSourceArgs().length > 0)
            logger.warn("Command-line arguments: {}", String.join(",", args.getSourceArgs()));
        for (String name : args.getOptionNames()) {
            logger.warn("Non-option->{}={}", name, args.getOptionValues(name));
        }
    }
}

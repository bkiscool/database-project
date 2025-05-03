package me.brandonkey.project;

import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@RequestMapping("/api")
public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args)
    {
        ConfigurableApplicationContext context = SpringApplication.run(Main.class, args); // Create the bootstrap
        
        // Disconnect from the SQL database and exit the SpringApplication on exit
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run()
            {
                try {
                    DB.getInstance().disconnect();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                SpringApplication.exit(context);
            }
        });
        
    }

    @GetMapping("/reset")
    public String reset()
    {
        try {
            Process p = new ProcessBuilder("bash", "reset_database.sh").start();
            p.waitFor();
        } catch (Exception e)
        {
            e.printStackTrace();
            return "Error: Could not reset SQL database;";
        }

        String response = "Reset SQL databse";
        logger.info(response);

        return response;
    }

}

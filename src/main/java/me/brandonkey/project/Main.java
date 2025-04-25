package me.brandonkey.project;

import java.sql.SQLException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@RequestMapping("/api")
public class Main {
    public static final DB DB = new DB();

    public static void main(String[] args)
    {
        ConfigurableApplicationContext context = SpringApplication.run(Main.class, args); // Create the bootstrap
        
        // Disconnect from the SQL database and exit the SpringApplication on exit
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run()
            {
                try {
                    DB.disconnect();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                SpringApplication.exit(context);
            }
        });
        
    }

}

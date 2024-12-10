package microtruco.lobbies;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@SpringBootApplication
public class LobbiesApplication {
    public static void main(String... args) {
        SpringApplication.run(LobbiesApplication.class, args);
    }

    @Autowired
    Environment env;

    @Bean
    DataSource dataSource() {
        final DriverManagerDataSource dataSource = new DriverManagerDataSource(
                "jdbc:sqlite:lobbies.db",
                "lobbies",
                "lobbies");
        return dataSource;
    }
}

package microtruco.games;

import javax.sql.DataSource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@SpringBootApplication
public class GamesApplication {
    public static void main(String... args) {
        SpringApplication.run(GamesApplication.class, args);
    }

    @Bean
    DataSource dataSource() {
        final DriverManagerDataSource dataSource = new DriverManagerDataSource(
                "jdbc:postgresql://localhost:5432/games",
                "postgres",
                "123");
        return dataSource;
    }
}

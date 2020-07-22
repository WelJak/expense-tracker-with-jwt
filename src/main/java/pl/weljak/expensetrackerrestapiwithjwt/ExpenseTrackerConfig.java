package pl.weljak.expensetrackerrestapiwithjwt;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class ExpenseTrackerConfig {
    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}

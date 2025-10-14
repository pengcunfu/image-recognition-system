package com.pcf.recognition.config;

import com.pcf.recognition.service.DbConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Database Configuration Loader
 * Loads sensitive configurations from database and overrides application properties
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class DatabaseConfigLoader implements ApplicationListener<ApplicationReadyEvent> {

    private final DbConfigService dbConfigService;
    private final ConfigurableEnvironment environment;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        log.info("Loading sensitive configurations from database...");
        
        try {
            // Initialize default configurations if needed
            dbConfigService.initializeDefaultConfigs();
            
            // Load all active configurations from database
            Map<String, String> dbConfigs = dbConfigService.getAllActiveConfigs();
            
            if (!dbConfigs.isEmpty()) {
                // Add database configurations as a high-priority property source
                Map<String, Object> configMap = new HashMap<>(dbConfigs);
                MapPropertySource dbPropertySource = new MapPropertySource("databaseConfig", configMap);
                environment.getPropertySources().addFirst(dbPropertySource);
                
                log.info("Loaded {} sensitive configurations from database", dbConfigs.size());
                
                // Log loaded configuration keys (not values for security)
                dbConfigs.keySet().forEach(key -> log.debug("Loaded config key: {}", key));
            } else {
                log.warn("No configurations found in database");
            }
            
        } catch (Exception e) {
            log.error("Failed to load configurations from database", e);
        }
    }

    /**
     * Refresh configurations from database
     * This method can be called to reload configurations without restarting the application
     */
    public void refreshConfigurations() {
        log.info("Refreshing configurations from database...");
        
        try {
            // Remove existing database property source
            if (environment.getPropertySources().contains("databaseConfig")) {
                environment.getPropertySources().remove("databaseConfig");
            }
            
            // Reload configurations
            Map<String, String> dbConfigs = dbConfigService.getAllActiveConfigs();
            
            if (!dbConfigs.isEmpty()) {
                Map<String, Object> configMap = new HashMap<>(dbConfigs);
                MapPropertySource dbPropertySource = new MapPropertySource("databaseConfig", configMap);
                environment.getPropertySources().addFirst(dbPropertySource);
                
                log.info("Refreshed {} configurations from database", dbConfigs.size());
            }
            
        } catch (Exception e) {
            log.error("Failed to refresh configurations from database", e);
        }
    }
}

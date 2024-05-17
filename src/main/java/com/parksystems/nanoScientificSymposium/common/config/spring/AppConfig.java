package com.parksystems.nanoScientificSymposium.common.config.spring;

import com.parksystems.nanoScientificSymposium.common.ssrpage.logMessage.LogMessage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class AppConfig {
    @Bean
    public LogMessage LogMessage() {
        return new LogMessage();
    }
    @Bean
    public List<LogMessage> logMessageList() {
        return new ArrayList<>();
    }

}

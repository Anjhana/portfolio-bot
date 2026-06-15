package com.anjhana.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

	
	@Bean
    public ChatClient chatClient(ChatClient.Builder builder) {
        
        return builder
        		.defaultSystem("\"You are a helpful recruiter assistant for summarizing and helping with consolidating the skill profile, projects that a canditate have worked and checking if a candidate is suitable for a given job description. \"")
        		.build();
	}
}

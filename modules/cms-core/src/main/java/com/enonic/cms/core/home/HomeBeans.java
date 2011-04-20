package com.enonic.cms.core.home;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HomeBeans
{
    @Bean
    public HomeDir homeDir()
    {
        return new HomeResolver().resolve();
    }
}

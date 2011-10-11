package com.enonic.cms.core.config;

import org.junit.Test;
import org.springframework.core.env.StandardEnvironment;
import static org.junit.Assert.*;

public class ConfigBeansTest
{
    @Test
    public void testConfig()
    {
        final StandardEnvironment env = new StandardEnvironment();
        final ConfigBeans beans = new ConfigBeans();
        final GlobalConfig config = beans.config(env);

        assertNotNull(config);
    }
}

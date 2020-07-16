package com.yss.configuration;

import com.yss.client.ZookeeperPropertiesService;
import com.yss.service.ConfigurationPersistenceServiceImpl;
import com.yss.service.commons.ConfigurationPersistenceService;
import com.yss.service.properties.UnifiedConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName UnifiedConfigurationAutoConfiguration
 * @Description 自动装配
 **/
@Configuration
@EnableConfigurationProperties(UnifiedConfigurationProperties.class)
public class UnifiedConfigurationAutoConfiguration {


  @Bean(value = "configurationPersistenceService")
  public ConfigurationPersistenceService getConfigurationPersistenceService(
      UnifiedConfigurationProperties unifiedConfigurationProperties) {
    ConfigurationPersistenceService configurationPersistenceService = new ConfigurationPersistenceServiceImpl(
        unifiedConfigurationProperties);
    return configurationPersistenceService;
  }

  @Bean(value = "zookeeperPropertiesService")
  public ZookeeperPropertiesService getZookeeperOperatingService() {
    ZookeeperPropertiesService zookeeperPropertiesService = new ZookeeperPropertiesService();
    return zookeeperPropertiesService;
  }

}

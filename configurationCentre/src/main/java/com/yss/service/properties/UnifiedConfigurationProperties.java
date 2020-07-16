package com.yss.service.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @ClassName UnifiedConfigurationProperties
 * @Description 配置参数自动注入
 **/
@ConfigurationProperties(prefix = "com.yss.unified.config")
@Data
public class UnifiedConfigurationProperties {

  private String basePath;
  private String zookeeperHost;

}

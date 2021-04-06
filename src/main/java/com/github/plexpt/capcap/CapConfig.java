package com.github.plexpt.capcap;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 * @author pengtao
 * @email plexpt@gmail.com
 * @date 2021-03-31 10:25
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "app.chrome")
public class CapConfig {

    String path;

    String driver;
}

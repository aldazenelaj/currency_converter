package com.currency.converter.app.properties;

import com.currency.converter.app.utilities.JsonPropertySourceFactory;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.List;

@Configuration
@PropertySource(value = "classpath:application.json", factory = JsonPropertySourceFactory.class)
@ConfigurationProperties
@Data
public class CurrencyConverterProperties {
    private String url;
    private String key;
    private List<String> currency;

}

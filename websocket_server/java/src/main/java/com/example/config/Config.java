package com.example.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Config {

    @Value("${api_path}")
    private String apiPath;

    @Value("${ml_server_url}")
    private String mlServerUrl;

    @Value("${db_host}")
    private String dbHost;

    public String getApiPath() {
        return apiPath;
    }

    public String getMlServerUrl() {
        return mlServerUrl;
    }

    public String getDbHost() {
        return dbHost;
    }
}

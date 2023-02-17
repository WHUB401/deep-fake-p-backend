package com.pharaoh.deepfake.common.util;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CrmProperties {

    @Value("${variable.datasource.url}")
    private String mysql_url;
    @Value("${spring.datasource.username}")
    private String user_name;
    @Value("${spring.datasource.password}")
    private String password;

    public String getMysqlUrl() {
        return mysql_url;
    }
    public String getUserName() {
        return user_name;
    }
    public String getPassword() {
        return password;
    }
}


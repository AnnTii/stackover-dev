package com.javamentor.qa.platform.initdb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnMissingClass({"org.springframework.boot.test.context.SpringBootTest"})
public class TestEntityInit implements CommandLineRunner {

    private final DataBaseInitialize databaseInitialize;

    @Value("${spring.jpa.hibernate.ddl-auto}")
    private String ddlAuto;

    @Autowired
    public TestEntityInit(DataBaseInitialize databaseInitialize) {
        this.databaseInitialize = databaseInitialize;
    }

    @Override
    public void run(String... args) {
        if (ddlAuto.contains("create")) {
            databaseInitialize.dataBaseInit();
        }
    }
}

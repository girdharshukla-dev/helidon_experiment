package com.girdharshukla.example;

import io.helidon.logging.common.LogConfig;
import io.helidon.config.Config;
import io.helidon.dbclient.DbClient;
import io.helidon.service.registry.Services;
import io.helidon.webserver.WebServer;
import io.helidon.webserver.http.HttpRouting;


public class Main {

    static DbClient dbClient;
    public static void main(String[] args) {
        
        LogConfig.configureRuntime();

        Config config = Config.create();
        Services.set(Config.class, config);
        dbClient = DbClient.create(Config.global().get("db"));
        
        
        WebServer server = WebServer.builder()
        .config(config.get("server"))
        .routing(Main::routing)
        .build()
                .start();


        System.out.println("WEB server is up! http://localhost:" + server.port());

    }



    static void routing(HttpRouting.Builder routing) {
        routing.register("/health", new HealthService())
                .register("/users", new UserService(dbClient));
    }
}
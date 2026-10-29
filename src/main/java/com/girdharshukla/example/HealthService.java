package com.girdharshukla.example;

import io.helidon.webserver.http.HttpRules;
import io.helidon.webserver.http.HttpService;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;

public class HealthService implements HttpService {

    public record Status(String status){}

    private void health_check(ServerRequest request, ServerResponse resp) {
        resp.send(new Status("health ok"));
    }

    @Override
    public void routing(HttpRules rules) {
       rules.get("/check", this::health_check);
    }
    
}

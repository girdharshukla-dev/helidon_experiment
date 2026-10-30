package com.girdharshukla.example;

import java.sql.SQLException;
import java.util.Map;

import at.favre.lib.crypto.bcrypt.BCrypt;
import io.helidon.dbclient.DbClient;
import io.helidon.webserver.http.HttpRules;
import io.helidon.webserver.http.HttpService;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;

public class UserService implements HttpService {
    public record SignupRequest(String email, String password) {
    }

    DbClient dbClient;

    public UserService(DbClient dbClient) {
        this.dbClient = dbClient;
    }

    @Override
    public void routing(HttpRules rules) {
        rules.post("/signup", this::signup);
    }

    private void signup(ServerRequest req, ServerResponse resp) {
        SignupRequest body = req.content().as(SignupRequest.class);

        if (body.email() == null || body.password() == null) {
            resp.status(400).send("Invalid email");
        }

        String email = body.email().trim().toLowerCase();
        String password_hash = BCrypt.withDefaults().hashToString(12, body.password().toCharArray());

        try {
            dbClient.execute().insert(
                    "INSERT INTO users (email, password_hash) VALUES (?, ?)",
                    email, password_hash);
        } catch (RuntimeException e) {
            if (isUniqueViolation(e)) {
                System.out.println("Email conflict");
                resp.status(409).send("Email already exists");
            }
        }

        resp.status(201).send(Map.of("email", email));
    }

    private static boolean isUniqueViolation(Throwable t) {
        while (t != null) {
            if (t instanceof SQLException sql && "23505".equals(sql.getSQLState())) {
                return true;
            }
            t = t.getCause();
        }
        return false;
    }

}

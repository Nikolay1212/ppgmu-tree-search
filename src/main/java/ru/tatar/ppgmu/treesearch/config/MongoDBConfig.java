package ru.tatar.ppgmu.treesearch.config;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class MongoDBConfig {

    @Autowired
    private Environment environment;

    @RefreshScope
    @Bean(destroyMethod = "close")
    MongoClient mongoClient() {
        String user = environment.getProperty("app.mongodb.username");
        String pass = environment.getProperty("app.mongodb.password");
        String database = environment.getRequiredProperty("app.mongodb.database");

        MongoCredential credential = MongoCredential.createCredential(user, database, pass.toCharArray());

        String replicset = environment.getProperty("app.mongodb.replicset");

        MongoClient mongo;
        if (replicset != null && !replicset.isEmpty()) {
            List<ServerAddress> serverAddresses = new ArrayList<>();
            String[] hosts = replicset.trim().split(",");
            for (String hostPortStr:
                 hosts) {
                String[] hostPort = hostPortStr.split(":");
                String host = hostPort[0];
                int port;
                if (hostPort.length == 2) {
                    port = Integer.parseInt(hostPort[1]);
                } else {
                    port = 27017;
                }
                ServerAddress serverAddress = new ServerAddress(host, port);
                serverAddresses.add(serverAddress);
            }
            mongo = new MongoClient(serverAddresses, credential, MongoClientOptions.builder().build());
        } else {
            String host = environment.getRequiredProperty("app.mongodb.host");
            int port = environment.getRequiredProperty("app.mongodb.port", Integer.TYPE);
            ServerAddress serverAddress = new ServerAddress(host, port);
            mongo = new com.mongodb.MongoClient(serverAddress, credential, MongoClientOptions.builder().build());
        }
        return mongo;
    }
}

package ru.tatar.ppgmu.treesearch.config;

import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.tatar.ppgmu.treesearch.constants.Constants;
import ru.tatar.ppgmu.treesearch.model.TreeSearchManager;
import ru.evolenta.server.service.DatabaseService;

@Slf4j
@Configuration
public class TreeSearchManagerConfig {

    private final DatabaseService databaseService;

    @Autowired
    private TreeSearchManager treeSearchManager;

    public TreeSearchManagerConfig(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    @Bean
    public String getCronValue() {

        Document treeSearchManagerSettings = databaseService.getFirstDocument(Constants.SETTINGS.getName(), new Document(Constants.NAME.getName(), Constants.TREE_SEARCH_MANAGER.getName()));
        if (treeSearchManagerSettings != null && treeSearchManagerSettings.getString("cron") != null) {
            return treeSearchManagerSettings.getString("cron");
        } else {
            log.warn("Incorrect TreeSearchManager");
            throw new RuntimeException("Incorrect TreeSearchManager");
        }
    }
}
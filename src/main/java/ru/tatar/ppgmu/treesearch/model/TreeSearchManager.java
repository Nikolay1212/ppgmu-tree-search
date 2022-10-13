package ru.tatar.ppgmu.treesearch.model;

import org.bson.Document;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Менеджер древовидного поиска содержит список коллекций и полей, по которым нужно держать списки уникальных значений
 */
@Component
public class TreeSearchManager {
    private String name;
    private boolean isAutoUpdate;
    private List<Document> collectionSearchList;
    private List<Document> logs;

    public TreeSearchManager() {
    }

    public TreeSearchManager(String name, boolean isAutoUpdate, List<Document> collectionSearchList, List<Document> logs) {
        this.name = name;
        this.isAutoUpdate = isAutoUpdate;
        this.collectionSearchList = collectionSearchList;
        this.logs = logs;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isAutoUpdate() {
        return isAutoUpdate;
    }

    public void setAutoUpdate(boolean autoUpdate) {
        this.isAutoUpdate = autoUpdate;
    }

    public List<Document> getCollectionSearchList() {
        return collectionSearchList;
    }

    public void setCollectionSearchList(List<Document> collectionSearchList) {
        this.collectionSearchList = collectionSearchList;
    }

    public List<Document> getLogs() {
        return logs;
    }

    public void setLogs(List<Document> logs) {
        this.logs = logs;
    }

}

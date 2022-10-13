package ru.tatar.ppgmu.treesearch.dto;

import java.util.List;

/**
 * Класс-маппер поискового запроса
 */
public class RequestDto {
    private String collectionName;
    private List<Levels> levels;

    public RequestDto(String collectionName, List<Levels> levels) {
        this.collectionName = collectionName;
        this.levels = levels;
    }

    public RequestDto(String collectionName) {
        this.collectionName = collectionName;
    }

    public RequestDto() {
    }

    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    public List<Levels> getLevels() {
        return levels;
    }

    public void setLevels(List<Levels> levels) {
        this.levels = levels;
    }
}
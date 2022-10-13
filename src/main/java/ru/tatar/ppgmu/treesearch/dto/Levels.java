package ru.tatar.ppgmu.treesearch.dto;

import org.bson.Document;

import java.util.List;

/**
 * Поля, по которым нужно вытащить данные в соответствии с параметрами
 */
public class Levels {
    private String field;
    private List<Document> params;

    public Levels() {
    }

    public Levels(String field, List<Document> params) {
        this.field = field;
        this.params = params;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public List<Document> getParams() {
        return params;
    }

    public void setParams(List<Document> params) {
        this.params = params;
    }
}

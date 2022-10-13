package ru.tatar.ppgmu.treesearch.cache.type;

import org.bson.Document;

import java.util.List;
import java.util.Set;

/**
 * Задает поведение классов, которые отдают данные с параметрами
 */
public interface DataConstructor {
    Set<Object> constructData(List<Document> params);
}

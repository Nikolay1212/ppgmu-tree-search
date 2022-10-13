package ru.tatar.ppgmu.treesearch.service;

import org.bson.Document;
import ru.tatar.ppgmu.treesearch.dto.Levels;

import java.util.List;
import java.util.Set;

public interface TreeSearchService {

    List<Set<Object>> getLevelsContent(String collection, List<Levels> fields);
    boolean updateLevelsContent(String collection);
}

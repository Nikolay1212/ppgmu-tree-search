package ru.tatar.ppgmu.treesearch.cache.type;

import lombok.extern.slf4j.Slf4j;
import org.bson.Document;

import java.util.List;
import java.util.Set;

/**
 * Класс для работы с числовыми данными
 */
@Slf4j
public class NumberDetailed extends AbstractData implements DataConstructor {

    public NumberDetailed(String fieldName, String dataType, List<String> objectSet) {
        super(fieldName, dataType, objectSet);
    }

    @Override
    public Set<Object> constructData() {
        // Not implemented
        return null;
    }

    @Override
    public Set<Object> constructData(List<Document> params) {
        // Not implemented
        return null;
    }
}

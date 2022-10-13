package ru.tatar.ppgmu.treesearch.cache;

import org.bson.Document;
import ru.tatar.ppgmu.treesearch.cache.type.AbstractData;
import ru.tatar.ppgmu.treesearch.cache.type.DateDetailed;
import ru.tatar.ppgmu.treesearch.constants.Constants;
import ru.tatar.ppgmu.treesearch.dto.Levels;

import java.util.*;

/**
 * Класс для кэширования списков полей и их уникальных значений
 */
public class CachedLevels {

    // Данные кэшируются на 30 секунд
    private final long cacheLifeTime = 30 * 1000;
    private String collection;
    private final long createTimeStamp;
    private final List<Document> documents;

    public CachedLevels() {
        this.createTimeStamp = System.currentTimeMillis();
        this.documents = null;
    }

    public CachedLevels(List<Document> documents, String collection) {
        this.collection = collection;
        this.createTimeStamp = System.currentTimeMillis();
        this.documents = documents;
    }

    public List<Document> getDocuments() {
        return documents;
    }

    public String getCollection() {
        return collection;
    }

    public boolean isExpired() {
        return (System.currentTimeMillis() - createTimeStamp) > cacheLifeTime;
    }

    /**
     * Метод возвращает закешированные списки с уникальными значениями в зависимости от типа поля и переданных параметров
     * @return возвращает список полей, каждый из который в свою очередь содержит список уникальных значений
     */
    public List<Set<Object>> getFieldsList(List<Levels> requiredFields) {
        List<Set<Object>> result = new ArrayList<>();
        for (Levels level :
                requiredFields) {
            String requiredField = level.getField();
            List<Document> params = level.getParams();
            Optional<Document> optionalDocument = documents.stream().filter(d -> requiredField.equals(d.get(Constants.NAME.getName()))).findFirst();
            AbstractData data;
            if (optionalDocument.isPresent()) {
                Document currentValues = optionalDocument.get();
                switch (currentValues.getString("type")) {
                    case "Date": {
                        List<String> values = (List<String>) currentValues.get(Constants.FIELDS.getName());
                        data = new DateDetailed(requiredField, "Date", values);
                        result.add(data.constructData(params));
                        break;
                    }
                    default: {
                        result.add(new HashSet<>((List<Object>) currentValues.get(Constants.FIELDS.getName())));
                        break;
                    }
                }
            } else {
                //  случае, если списка значений по такому полю нет, отдаем клиенту пустой массив
                result.add(new HashSet<>());
            }
        }
        return result;
    }
}

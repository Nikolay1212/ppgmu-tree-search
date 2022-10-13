package ru.tatar.ppgmu.treesearch.worker;

import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import ru.evolenta.server.service.DatabaseService;
import ru.evolenta.server.service.MongoDBService;
import ru.evolenta.server.utils.EntryMetaInfo;
import ru.tatar.ppgmu.treesearch.constants.Constants;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Callable;

import static ru.tatar.ppgmu.treesearch.constants.Constants.TREE_LIST_META;

/**
 * Класс-поток, который обновляет информацию об уникальных значениях в определенных полях
 */
@Slf4j
public class CollectionResearcher implements Callable<Document> {

    private String collectionName;
    private List<Object> values;
    private DatabaseService databaseService;
    private MongoDBService mongoDBService;

    public CollectionResearcher() {
    }

    public CollectionResearcher(String collectionName, List<Object> values, DatabaseService databaseService, MongoDBService mongoDBService) {
        this.collectionName = collectionName;
        this.values = values;
        this.databaseService = databaseService;
        this.mongoDBService = mongoDBService;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    public List<Object> getValues() {
        return values;
    }

    public void setValues(List<Object> values) {
        this.values = values;
    }

    @Override
    public Document call() {
        log.info("Start updating fields by collection '{}'", collectionName);
        // Получаем список обязательных для обновления полей
        List<Document> requiredFields = new ArrayList<>();
        for (Object o :
                values) {
            requiredFields.add((Document) o);
        }
        List<Document> actualFields = requiredFields;
        // Если апдейт производится в первый раз
        if (actualFields == null) {
            actualFields = new ArrayList<>();
        }
        // По каждому полю создаем список уникальных значений
        List<Document> newList = new ArrayList<>(actualFields);
        for (Document document :
                requiredFields) {
            for (Document d :
                    actualFields) {
                if (d.getString("name").equals(document.getString("name"))) {
                    String field = document.getString("name");
                    String type = document.getString("type");
                    // Удаляем старые значения
                    newList.remove(d);
                    Set<Object> values = getUniqueValues(collectionName, field, type);
                    // Добавляем актуальные
                    newList.add(new Document("name", field).append("type", type).append("fields", values));
                    break;
                }
            }
        }
        // Производим апдейт сущности treeListMeta - по переданным полям обновляем уникальные значения
        databaseService.updateDocument(TREE_LIST_META.getName(), new Document(Constants.COLLECTION_NAME.getName(), collectionName), new Document("$set", new Document(Constants.ACTUAL_FIELDS_VALUES.getName(), newList)));
        log.info("Collection '{}' is updated, fields '{}'", collectionName, values);
        return new Document("collName", collectionName).append("fields", values);
    }

    /**
     * Метод получает список уникальных значений по полю и его типу
     *
     * @param collection коллекция, в которой необходимо произвести обновление
     * @param field      поле, по которому ищутся все уникальные значения
     * @param type       тип поля, в зависимости от которого применяется тот или иной метод выборки
     */
    private Set<Object> getUniqueValues(String collection, String field, String type) {
        Set<Object> values = new HashSet<>();
        try {
            switch (type) {
                case "Date": {
                    List<Date> dateList = mongoDBService
                            .getDb(EntryMetaInfo.getGlobalEntryMetaInfo(collection))
                            .getCollection(collection)
                            .distinct(field, Date.class)
                            .filter(new Document(field, new Document("$ne", null)))
                            .into(new ArrayList<>());
                    for (Date currentDate :
                            dateList) {
                        String formattedDate = new SimpleDateFormat("yyyy-MM-dd").format(currentDate);
                        values.add(formattedDate);
                    }
                }
                case "String": {
                    values = mongoDBService
                            .getDb(EntryMetaInfo.getGlobalEntryMetaInfo(collection))
                            .getCollection(collection)
                            .distinct(field, String.class)
                            .filter(new Document(field, new Document("$ne", null)))
                            .into(new HashSet<>());
                }
                case "Integer": {
                    values = mongoDBService
                            .getDb(EntryMetaInfo.getGlobalEntryMetaInfo(collection))
                            .getCollection(collection)
                            .distinct(field, Integer.class)
                            .filter(new Document(field, new Document("$ne", null)))
                            .into(new HashSet<>());
                }
                default: {
                    log.warn("Method not implemented for type={} field={} in collection={}", type, field, collection);
                }
            }
        } catch (RuntimeException e) {
            log.warn("Check type='{}' or field='{}' in collection={}", type, field, collection);
        }
        return values;
    }
}

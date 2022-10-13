package ru.tatar.ppgmu.treesearch.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.evolenta.server.service.DatabaseService;
import ru.evolenta.server.service.MongoDBService;
import ru.evolenta.server.utils.EntryMetaInfo;
import ru.tatar.ppgmu.treesearch.cache.CachedLevels;
import ru.tatar.ppgmu.treesearch.constants.Constants;
import ru.tatar.ppgmu.treesearch.dto.Levels;
import ru.tatar.ppgmu.treesearch.model.TreeSearchManager;
import ru.tatar.ppgmu.treesearch.service.TreeSearchService;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Сервис отдает актуальные списки уникальных значений в необходимых полях
 */
@Slf4j
@Service
public class TreeSearchServiceImpl implements TreeSearchService {

    private final Map<String, CachedLevels> levelsCash = new ConcurrentHashMap();
    @Autowired
    private TreeSearchManager treeSearchManager;
    private final MongoDBService mongoDBService;
    private final DatabaseService databaseService;

    public TreeSearchServiceImpl(MongoDBService mongoDBService, DatabaseService databaseService) {
        this.mongoDBService = mongoDBService;
        this.databaseService = databaseService;
    }

    /**
     * Метод возвращает список уникальных значений для полей из определенной коллекции, значения периодически обнавляются
     *
     * @param collection выбранная коллекция
     * @param levels     поля, по которым нужно получить список уникальных значений
     * @return возвращает документ, который содержит список полей и их уникальных значений
     */
    @Override
    public List<Set<Object>> getLevelsContent(String collection, List<Levels> levels) {
        // Сначала ищем списки в кэше
        CachedLevels cachedLevels = levelsCash.get(collection);
        if (cachedLevels != null && !cachedLevels.isExpired() && cachedLevels.getDocuments() != null) {
            log.info("===============> return cashed");
        } else {
            try {
                createCashedLevelsByCollection(collection);
                log.info("===============> return new");
            } catch (RuntimeException e) {
                log.warn("No data for collection '{}', exception={}", collection, e.getMessage());
                return new ArrayList<>();
            }
        }
        return levelsCash.get(collection).getFieldsList(levels);
    }

    // Метод создает новый объект CashedLevels при первом обращении, либо после истечения времени жизни объекта
    private void createCashedLevelsByCollection(String collection) {
        Document actualFields = databaseService.getFirstDocument(Constants.TREE_LIST_META.getName(), new Document(Constants.COLLECTION_NAME.getName(), collection), Constants.ACTUAL_FIELDS_VALUES.getName());
        if (actualFields.get(Constants.ACTUAL_FIELDS_VALUES.getName()) != null) {
            levelsCash.put(collection, new CachedLevels((List<Document>) actualFields.get(Constants.ACTUAL_FIELDS_VALUES.getName()), collection));
        } else {
            throw new RuntimeException();
        }
    }

    /**
     * Метод производит ручной апдейт списков уникальных значений в требуемых полях
     *
     * @param collection
     * @return
     */
    @Override
    public boolean updateLevelsContent(String collection) {
        // Получаем список обязательных для обновления полей
        Document treeMeta;
        List<Document> requiredFields;
        try {
            treeMeta = databaseService.getFirstDocument(Constants.TREE_LIST_META.getName(), new Document(Constants.COLLECTION_NAME.getName(), collection));
            requiredFields = (ArrayList<Document>) treeMeta.get("requiredFields");
        } catch (IllegalArgumentException e) {
            log.warn("Required fields are empty, try to \"/update/levels\" for treeMeta collection={}", collection);
            return false;
        }
        List<Document> currentValues = (ArrayList<Document>) treeMeta.get("currentValues");
        // Если апдейт производится в первый раз
        if (currentValues == null) {
            currentValues = new ArrayList<>();
        }
        // По каждому полю создаем список уникальных значений при этом сохраняя предыдущие апдейты
        List<Document> actualValues = new ArrayList<>(currentValues);
        for (Document document :
                requiredFields) {
            Set<Object> values;
            String field = document.getString("name");
            String type = document.getString("type");
            // Если в списке уникальных значений есть предыдущий набор по данному полю, то его нужно удалить и добавить актуальный
            if (currentValues.size() > 0) {
                for (Document actual :
                        currentValues) {
                    if (actual.getString("name").equals(document.getString("name"))) {
                        actualValues.remove(actual);
                        break;
                    }
                }
            }
            values = getUniqueValues(collection, field, type);
            actualValues.add(new Document(Constants.NAME.getName(), field).append(Constants.FIELDS.getName(), values));
        }
        // Производим апдейт сущности treeListMeta - по переданным полям обновляем уникальные значения
        databaseService.updateDocument(
                "treeListMeta",
                new Document(Constants.COLLECTION_NAME.getName(), collection),
                new Document("$set", new Document(Constants.ACTUAL_FIELDS_VALUES.getName(), actualValues)));
        return false;
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
            if ("Date".equals(type)) {
                Calendar calendar = Calendar.getInstance();
                List<Date> dateList = mongoDBService
                        .getDb(EntryMetaInfo.getGlobalEntryMetaInfo(collection))
                        .getCollection(collection)
                        .distinct(field, Date.class)
                        .filter(new Document(field, new Document("$ne", null)))
                        .into(new ArrayList<>());
                for (Date currentDate :
                        dateList) {
                    calendar.setTime(currentDate);
                    String formattedDate = calendar.get(Calendar.YEAR) + "-" + calendar.get(Calendar.MONTH) + "-" + calendar.get(Calendar.DATE);
                    values.add(formattedDate);
                }
            } else if ("String".equals(type)) {
                values = mongoDBService
                        .getDb(EntryMetaInfo.getGlobalEntryMetaInfo(collection))
                        .getCollection(collection)
                        .distinct(field, String.class)
                        .filter(new Document(field, new Document("$ne", null)))
                        .into(new HashSet<>());
            } else if ("Integer".equals(type)) {
                values = mongoDBService
                        .getDb(EntryMetaInfo.getGlobalEntryMetaInfo(collection))
                        .getCollection(collection)
                        .distinct(field, Integer.class)
                        .filter(new Document(field, new Document("$ne", null)))
                        .into(new HashSet<>());
            } else {
                log.warn("Method not implemented for type={}", type);
            }
        } catch (RuntimeException e) {
            log.warn("Check type='{}' or field='{}'", type, field);
        }
        return values;
    }
}

package ru.tatar.ppgmu.treesearch.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.tatar.ppgmu.treesearch.constants.Constants;
import ru.tatar.ppgmu.treesearch.worker.CollectionResearcher;
import ru.tatar.ppgmu.treesearch.model.TreeSearchManager;
import ru.tatar.ppgmu.treesearch.service.UpdateService;
import ru.evolenta.server.service.DatabaseService;
import ru.evolenta.server.service.MongoDBService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Slf4j
@Service
public class UpdateServiceImpl implements UpdateService {

    @Autowired
    private DatabaseService databaseService;
    @Autowired
    private MongoDBService mongoDBService;

    @Autowired
    private String cron;

    @Autowired
    private TreeSearchManager treeSearchManager;

    /**
     * Метод регулярно производит апдейт списков полей уникальными значениями
     * Частота опроса берется из коллекции SETTINGS
     */
    @Override
    @Scheduled(fixedRateString = "#{@getCronValue}")
    public void regularUpdateLevels() {

        log.info("Start update");

        // Достаем менеджера древовидного поиска
        Document treeSearchManagerSettings = databaseService.getFirstDocument(Constants.TREE_LIST_META.getName(), new Document(Constants.NAME.getName(), Constants.TREE_SEARCH_MANAGER.getName()));

        // Если поискового менеджера нет, ничего не делаем
        if (treeSearchManagerSettings == null) {
            log.warn("TreeSearchManager is not set");
            return;
        }
        // Если в БД есть настройки, то формируем TreeSearchManager, иначе выходим с ошибкой
        try {
            treeSearchManager.setName(treeSearchManagerSettings.getString("name"));
            treeSearchManager.setAutoUpdate(treeSearchManagerSettings.getBoolean("isAutoUpdate"));
            treeSearchManager.setCollectionSearchList((List<Document>) treeSearchManagerSettings.get("collectionSearchList"));
            treeSearchManager.setLogs((List<Document>) treeSearchManagerSettings.get("logs"));
        } catch (RuntimeException e) {
            log.warn(
                    "Bad data for TreeSearchManager id={}. Next try will be in {} seconds. Exception={}",
                    treeSearchManagerSettings.get("_id"),
                    Integer.parseInt(cron) / 1000,
                    e.getMessage());
            return;
        }

        // Если менеджер отключен - ничего не делаем
        if (!treeSearchManager.isAutoUpdate()) {
            log.info("Tree search manager is off");
            return;
        }

        // Получаем список коллекций с обязательными для обновления полями
        List<Document> collectionSearchList = treeSearchManager.getCollectionSearchList();

        // Если список пуст, ничего не делаем
        if (collectionSearchList.size() == 0) {
            log.info("Collection search list is empty");
            return;
        }
        // Создаем пул потоков, каждый из которых будет осуществлять апдейт по своей коллекции
        ExecutorService executorService = Executors.newFixedThreadPool(collectionSearchList.size());

        // Создаем список потоков обновления - для каждой коллекции свой
        List<CollectionResearcher> collectionResearchers = new ArrayList<>();
        for (Document document :
                collectionSearchList) {
            String collection = document.getString(Constants.COLLECTION_NAME.getName());
            List<Object> fields = (ArrayList<Object>) document.get(Constants.FIELDS.getName());
            CollectionResearcher researcher = new CollectionResearcher(collection, fields, databaseService, mongoDBService);
            collectionResearchers.add(researcher);
        }
        List<Future<Document>> taskFutures = new ArrayList<>();
        try {
            taskFutures = executorService.invokeAll(collectionResearchers);
        } catch (InterruptedException e) {
            log.warn("===========> update failed");
        }
        for (Future<Document> documentFuture :
                taskFutures) {
            try {
                System.out.println(documentFuture.get());
            } catch (InterruptedException | ExecutionException e) {
                log.error("The fields values have not updated. Exception={}", e.getMessage());
            }
        }
    }

    /**
     * Метод по команде клиента производит апдейт списков полей уникальными значениями, списки полей берутся у поискового менеджера
     */
    @Override
    public void oneTimeUpdate(String collection) {

        // Создаем поток, который будет производить апдейт уникальных значений определенных полях
        CollectionResearcher researcher= new CollectionResearcher();
        // Находим у поискового менеджера нужную коллекцию
        List<Document> collectionSearchList = treeSearchManager.getCollectionSearchList();
        for (Document document :
                collectionSearchList) {
            String currentCollection = document.getString(Constants.COLLECTION_NAME.getName());
            if (collection.equals(currentCollection)) {
                List<Object> fields = (ArrayList<Object>) document.get(Constants.FIELDS.getName());
                researcher = new CollectionResearcher(collection, fields, databaseService, mongoDBService);
                break;
            }
        }
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<Document> taskFuture = executorService.submit(researcher);
        try {
            System.out.println(taskFuture.get().toJson());
        } catch (InterruptedException | ExecutionException e) {
            log.error("Crash Update");
        }
    }

    @Override
    public void customUpdate(String collection, List<String> fields) {
        //not implemented
    }
}
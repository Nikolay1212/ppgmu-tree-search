package ru.tatar.ppgmu.treesearch.service.impl;

import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.tatar.ppgmu.treesearch.constants.Constants;
import ru.tatar.ppgmu.treesearch.service.FieldService;
import ru.evolenta.server.service.DatabaseService;

import java.util.List;

@Slf4j
@Service
public class FieldServiceImpl implements FieldService {

    private final String TREE_LIST_META = "treeListMeta";

    private final DatabaseService databaseService;

    public FieldServiceImpl(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }


    /**
     * Метод по команде клиента обновляет информацию о полях, по которым возможен древовидный поиск
     *
     * @param document содержит уровни, которые нужно обновить
     * @return возвращает документ с результатом апдейта
     */
    @Override
    public ResponseEntity<Document> updateRequiredFields(Document document) {
        ResponseEntity<Document> responseEntity;
        String collection = document.getString(Constants.COLLECTION_NAME.getName());
        List<Document> fields = (List<Document>) document.get("requiredFields");
        Document updateCommand = new Document("$set", new Document("requiredFields", fields));

        UpdateResult updateDocument = databaseService.updateDocument(TREE_LIST_META, new Document(Constants.COLLECTION_NAME.getName(), collection), updateCommand);
        if (updateDocument.getMatchedCount() == 1 && updateDocument.getModifiedCount() == 1) {
            responseEntity = new ResponseEntity<>(new Document("Update status", "SUCCESSFUL"), HttpStatus.OK);
        }
        else if (updateDocument.getMatchedCount() == 1) {
            responseEntity = new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
        } else {
            responseEntity = new ResponseEntity<>(new Document("Update status", "Incorrect update document"), HttpStatus.NOT_FOUND);
        }
        return responseEntity;
    }
}
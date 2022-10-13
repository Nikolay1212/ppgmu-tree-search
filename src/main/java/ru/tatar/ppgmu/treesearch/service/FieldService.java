package ru.tatar.ppgmu.treesearch.service;

import org.bson.Document;
import org.springframework.http.ResponseEntity;

/**
 * Сервис работает с полями древовидного поиска
 */
public interface FieldService {

    /**
     * Метод обновляет информацию о полях, по которым возможен древовидный поиск
     *
     * @param levels уровни, которые нужно обновить
     * @return возвращает документ с результатом апдейта
     */
    ResponseEntity<Document> updateRequiredFields(Document levels);
}

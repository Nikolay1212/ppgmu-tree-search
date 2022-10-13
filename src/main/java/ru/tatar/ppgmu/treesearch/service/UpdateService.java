package ru.tatar.ppgmu.treesearch.service;

import java.util.List;

public interface UpdateService {

    /**
     * Метод регулярно обновляет информацию об уникальных значениях в полях, по которым необходим древовидный поиск
     */
    void regularUpdateLevels();

    /**
     * Метод по команде клиента производит апдейт списков полей уникальными значениями
     * @param collection коллекция, в которой будет производится апдейт
     */
    void oneTimeUpdate(String collection);

    /**
     * Метод по команде клиента производит кастомный апдейт списков полей уникальными значениями в конкретной коллекции
     * @param collection коллекция, в которой будет производится апдейт
     * @param fields поля, по которым нужно произвести апдейт
     */
    void customUpdate(String collection, List<String> fields);
}

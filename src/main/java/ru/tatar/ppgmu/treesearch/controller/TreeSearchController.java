package ru.tatar.ppgmu.treesearch.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import ru.evolenta.server.service.FulltextEngineService;
import ru.tatar.ppgmu.treesearch.dto.RequestDto;
import ru.tatar.ppgmu.treesearch.service.FieldService;
import ru.tatar.ppgmu.treesearch.service.TreeSearchService;
import ru.tatar.ppgmu.treesearch.service.UpdateService;

import java.util.List;
import java.util.Set;

/**
 * Контроллер для работы с коллекцией treeListMeta
 */
@RestController
@RequestMapping("/api/tatar/v1/tree-filter")
public class TreeSearchController {

    @Autowired
    private TreeSearchService treeSearchService;
    @Autowired
    private FieldService fieldService;
    @Autowired
    private UpdateService updateService;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private FulltextEngineService fulltextEngineService;

    /**
     * Метод возвращает списки с уникальными значениями по запрашиваемым полям
     * @param request запрос, содержащий имя коллекции и список полей
     * @return списки, содеражащие уникальные значения по запрашиваемым полям
     */
    @PostMapping(value = "/get/levels", produces = {"application/json"})
    public ResponseEntity<List> getLevelsContentByParams(@RequestBody RequestDto request) {
        List<Set<Object>> result;
        try {
            result = treeSearchService.getLevelsContent(request.getCollectionName(), request.getLevels());
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * Метод по команде клиента обновляет списки уникальных значений по заданным полям
     * @param collection коллекция, из полей которой необходимо обновить списки уникальных значений
     * @return
     */
    @PostMapping(value = "/update/{collection}", produces = {"application/json", "*/*"})
    public ResponseEntity updateLevelsContent(@PathVariable String collection) {
        try {
            updateService.oneTimeUpdate(collection);
        } catch (RuntimeException e) {
            System.out.println("error");
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

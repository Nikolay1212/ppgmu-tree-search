package ru.tatar.ppgmu.treesearch.cache.type;

import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import ru.tatar.ppgmu.treesearch.cache.CachedLevels;
import ru.tatar.ppgmu.treesearch.constants.Constants;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Класс содержит актуальные значения дат, объект всегда создается с not-null CachedLevels
 */
@Slf4j
public class DetailedDates {

    private final static String DATE_CREATION = "dateCreation";
    private CachedLevels cachedLevels;

    public DetailedDates(CachedLevels cachedLevels) {
        this.cachedLevels = cachedLevels;
    }

    public Set<Object> getYears() {
        List<String> dates = getAllDates();
        return dates.stream().map(s -> s.substring(0, 4)).collect(Collectors.toSet());
    }

    public Set<Object> getMonths(String year) {
        List<String> dates = getAllDates();
        return dates.stream().filter(s -> s.startsWith(year)).map(s -> s.substring(5, 7)).collect(Collectors.toSet());
    }

    public Set<Object> getDays(String year, String month) {
        List<String> dates = getAllDates();
        return dates.stream().filter(s -> s.startsWith(year + "-" + month)).map(s -> s.substring(8)).collect(Collectors.toSet());
    }

    private List<String> getAllDates() {
        List<Document> documents = cachedLevels.getDocuments();
        Optional<Document> optionalDates = documents.stream().filter(d -> DATE_CREATION.equals(d.getString(Constants.NAME.getName()))).findFirst();
        if (optionalDates.isPresent() && optionalDates.get().get(Constants.FIELDS.getName()) != null) {
            Document document = optionalDates.get();
            return (ArrayList<String>) document.get(Constants.FIELDS.getName());
        } else {
            log.info("No data for field '{}' in collection '{}'", DATE_CREATION, cachedLevels.getCollection());
        }
        return new ArrayList<>();
    }
}

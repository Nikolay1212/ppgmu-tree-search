package ru.tatar.ppgmu.treesearch.cache.type;

import lombok.extern.slf4j.Slf4j;
import org.bson.Document;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Класс для работы с типом данных Date
 */
@Slf4j
public class DateDetailed extends AbstractData implements DataConstructor {
    public DateDetailed(String fieldName, String dataType, List<String> objectSet) {
        super(fieldName, dataType, objectSet);
    }
    public Set<Object> getYears() {
        return objectList.stream().map(s -> s.substring(0, 4)).map(s -> Integer.parseInt(s)).sorted(Comparator.naturalOrder()).map(o -> (Object) o).collect(Collectors.toSet());
    }

    public Set<Object> getMonths(String year) {
        return objectList.stream().filter(s -> s.startsWith(year)).map(s -> s.substring(5, 7)).map(s -> Integer.parseInt(s)).sorted(Comparator.naturalOrder()).map(o -> (Object) o).collect(Collectors.toSet());
    }

    public Set<Object> getDays(String year, String month) {
        return objectList.stream().filter(s -> s.startsWith(year + "-" + month)).map(s -> s.substring(8)).map(s -> Integer.parseInt(s)).sorted(Comparator.naturalOrder()).map(o -> (Object) o).collect(Collectors.toSet());
    }

    // По умолчанию возвращаются все года, в которых есть дела
    @Override
    public Set<Object> constructData() {
        return getYears();
    }

    public Set<Object> constructData(List<Document> params) {
        if (params == null || params.size() == 0) {
            return getYears();
        }
        Map<String, String> yearMonth = new HashMap<>();
        for (Document param :
                params) {
            if (param != null) {
                String name = param.getString("name");
                String value = param.getString("value");
                yearMonth.put(name, value);
            }
        }
        if (yearMonth.get("year") != null && yearMonth.get("month") !=null) {
            return getDays(yearMonth.get("year"), yearMonth.get("month"));
        }
        if (yearMonth.get("year") != null) {
            return getMonths(yearMonth.get("year"));
        }
        return new HashSet<>();
    }
}

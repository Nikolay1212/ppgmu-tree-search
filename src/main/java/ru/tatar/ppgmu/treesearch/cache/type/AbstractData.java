package ru.tatar.ppgmu.treesearch.cache.type;

import java.util.List;
import java.util.Set;

/**
 * Предок для всех типов данных, которые будут получены из ДБ
 */
public abstract class AbstractData implements DataConstructor {
    private String fieldName;
    private String dataType;
    List<String> objectList;

    public AbstractData(String fieldName, String dataType, List<String> objectSet) {
        this.fieldName = fieldName;
        this.dataType = dataType;
        this.objectList = objectSet;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public List<String> getObjectList() {
        return objectList;
    }

    public void setObjectSet(List<String> objectSet) {
        this.objectList = objectSet;
    }

    /**
     * Метод возвращает данные по дефолту
     * @return
     */
    abstract Set<Object> constructData();
}

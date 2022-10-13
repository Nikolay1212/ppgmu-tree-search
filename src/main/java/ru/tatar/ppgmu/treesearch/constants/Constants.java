package ru.tatar.ppgmu.treesearch.constants;

public enum Constants {

    TREE_SEARCH_MANAGER("TreeSearchManager"),
    TREE_LIST_META("treeListMeta"),
    NAME("name"),
    SETTINGS("settings"),
    COLLECTION_NAME("collectionName"),
    ACTUAL_FIELDS_VALUES("actualFieldsValues"),
    DATE("date"),
    STRING("string"),
    INTEGER("integer"),
    FIELDS("fields");
    private final String name;

    Constants(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

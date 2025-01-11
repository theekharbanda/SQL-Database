package com.tabledb.server;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public interface DatabaseOperations {
    CompletableFuture<String> createTable(String tableName,List<String> ColumnNames, List<String> DataTypes);
    CompletableFuture<String> insert(String tableName, List<Object> paredValues);
}

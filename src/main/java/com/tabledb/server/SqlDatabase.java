package com.tabledb.server;

import com.tabledb.modals.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReadWriteLock;

public class SqlDatabase implements DatabaseOperations {

    private final ReadWriteLock rwLock;

    private final Map<String,Table> tables;
    private boolean isRunning;

    private static final int THREAD_POOL_SIZE = Runtime.getRuntime().availableProcessors();
    private final ExecutorService executorService;



    public SqlDatabase() {
        tables = new java.util.concurrent.ConcurrentHashMap<>();
        isRunning = true;
        executorService=  Executors.newFixedThreadPool(THREAD_POOL_SIZE, r -> {
            Thread thread = new Thread(r);
            thread.setDaemon(true);
            return thread;
        });
        rwLock = new java.util.concurrent.locks.ReentrantReadWriteLock();
    }

    @Override
    public CompletableFuture<String> createTable(String tableName,List<String> ColumnNames, List<String> DataTypes) {
        return CompletableFuture.supplyAsync(() -> {

            rwLock.writeLock().lock();
            if(tables.containsKey(tableName)){
                return "TABLE_EXISTS";
            }
            Table table = new Table(ColumnNames,DataTypes);
            tables.put(tableName,table);
            rwLock.writeLock().unlock();
            return "SUCCESS";
        },executorService);
    }

    @Override
    public CompletableFuture<String> insert(String tableName, List<Object> paredValues) {
        return CompletableFuture.supplyAsync(() -> {
            rwLock.writeLock().lock();
            if(!tables.containsKey(tableName)){
                return "TABLE_NOT_FOUND";
            }
            Table table = tables.get(tableName);
            List<String> columnNames = table.getColumnNames();
            List<String> columnTypes = table.getDataTypes();
            if(columnNames.size() != paredValues.size()){
                return "INVALID_COMMAND";
            }
            //check for valid data types
            for(int i=0;i<columnNames.size();i++){
                if(!validateDataType(paredValues.get(i),columnTypes.get(i))){
                    return "INVALID_COMMAND";
                }
            }
            for(int i=0;i<columnNames.size();i++) {
                table.get(columnNames.get(i)).add(paredValues.get(i));
            }
            rwLock.writeLock().unlock();
            return "SUCCESS";
        },executorService);
    }

    private boolean validateDataType(Object value, String columnType) {
        if(columnType.equals("INT")){
            return value instanceof Integer;
        }
        if(columnType.equals("STRING")){
            return value instanceof String;
        }
        return false;
    }


}

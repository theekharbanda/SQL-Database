package com.tabledb.modals;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Table {
    Map<String, List<Object>> table;
    List<String> columnNames;
    List<String> DataTypes;


    public Table(List<String> columnNames, List<String> DataTypes){
        table = new LinkedHashMap<>();
        for(String columnName: columnNames){
            table.put(columnName,new ArrayList<>());
        }
        this.columnNames = columnNames;
        this.DataTypes = DataTypes;
    }

    public void clear(){
        table.clear();
    }
    public void put(String key, List<Object> value){
        table.put(key,value);

    }
    public List<Object> get(String key){
        return table.get(key);
    }
    public List<String> getColumnNames(){
        return columnNames;
    }
    public List<String> getDataTypes(){
        return DataTypes;
    }


}

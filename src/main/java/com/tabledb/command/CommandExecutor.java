package com.tabledb.command;

import com.tabledb.server.DatabaseOperations;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandExecutor {
    private final DatabaseOperations database;
    private static final Pattern DOCUMENT_PATTERN =
            Pattern.compile("\\{([^}]+)}");

    public CommandExecutor(DatabaseOperations database) {
        this.database = database;
    }
    public CompletableFuture<String> executeCommand(String command) {
        String[] parts = command.trim().split("\\s+", 3);
        String operation = parts[0].toUpperCase();

        return switch (operation) {
            case "CREATE_TABLE" -> executeCreateTable(parts);
            case "INSERT" -> executeInsertInto(command);
//            case "DELETE" -> executeDeleteFrom(parts[1]);
//            case "SELECT" -> executeSelect(parts[1]);
//            case "UPDATE" -> executeUpdate(parts[1]);
//            case "SHOW" -> executeShowTables(parts[1]);


//            case "STOP" -> {
//                database.stop();
//                yield CompletableFuture.completedFuture("ADIOS!");
//            }
            default -> CompletableFuture.completedFuture("INVALID_COMMAND");
        };
    }

        private CompletableFuture<String> executeCreateTable(String[] parts) {

            if (parts.length < 3) {
                throw new IllegalArgumentException("INVALID_COMMAND");
            }
            String tableName = parts[1].trim();
            String columnDefinitions = parts[2].trim();

            // Remove parentheses from the column definition part
            if (!columnDefinitions.startsWith("(") || !columnDefinitions.endsWith(")")) {
                throw new IllegalArgumentException("INVALID_COMMAND");
            }
            columnDefinitions = columnDefinitions.substring(1, columnDefinitions.length() - 1);

            // Split the column definitions by spaces (name type pairs)
            String[] columnParts = columnDefinitions.split("\\s+");


            // Create a list of Column objects
            List<String> columnNames = new ArrayList<>();
            List<String> columnTypes = new ArrayList<>();
            for (int i = 0; i < columnParts.length; i += 2) {
                String columnName = columnParts[i];
                String columnType = columnParts[i + 1].replace(",","");
                columnNames.add(columnName);
                columnTypes.add(columnType);
            }

            return database.createTable(tableName, columnNames,columnTypes);
    }

//    private CompletableFuture<String> executeShowTables(String part) {
//        return parseDocument(part)
//                .map(database::findMinHops)
//                .orElse(CompletableFuture.completedFuture("INVALID_COMMAND"));
//    }

//    private CompletableFuture<String> executeUpdate(String part) {
//        return parseDocument(part)
//                .map(database::findRelatedNodes)
//                .orElse(CompletableFuture.completedFuture("INVALID_COMMAND"));
//    }

//    private  CompletableFuture<String> executeSelect(String part){
//        return parseDocument(part)
//                .map(database::deleteRelationship)
//                .orElse(CompletableFuture.completedFuture("INVALID_COMMAND"));
//    }
//    private CompletableFuture<List<String>> executeDeleteFrom(String part) {
//        return parseDocument(part)
//                .map(database::findNodes)
//                .orElse(CompletableFuture.completedFuture(Collections.singletonList("INVALID_COMMAND")));
//    }

    private CompletableFuture<String> executeInsertInto(String command) {
        command = command.trim();

        if (!command.startsWith("INSERT INTO")) {
            throw new IllegalArgumentException("INVALID_COMMAND");
        }

        String[] parts = command.substring(11).trim().split("\\s+", 2);

        if (parts.length < 2) {
            throw new IllegalArgumentException("INVALID_COMMAND");
        }

        String tableName = parts[0];

        String[] valuesPart = parts[1].split(" ",2);
//        if (!valuesPart.startsWith("(") || !valuesPart.endsWith(")")) {
//            throw new IllegalArgumentException("INVALID_COMMAND3");
//        }

        String stringValuePart = valuesPart[1].substring(1, valuesPart[1].length() - 1).trim();
        String[] values = stringValuePart.split(",");

        List<Object> parsedValues = new ArrayList<>();
        for (String value : values) {
            value = value.trim();
            if (value.startsWith("\"") && value.endsWith("\"")) {
                // It's a string, remove quotes
                parsedValues.add(value.substring(1, value.length() - 1));
            } else {
                // It's an integer or other type, parse it
                try {
                    parsedValues.add(Integer.parseInt(value));
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid value: Could not parse value '" + value + "'.");
                }
            }
        }

        return database.insert(tableName, parsedValues);
    }


//    private Optional<List<Object>> parseDocument(String documentStr) {
//        Matcher matcher = DOCUMENT_PATTERN.matcher(documentStr);
//        if (!matcher.find()) return Optional.empty();
//
//        Map<String, String> document = new HashMap<>();
//        String[] pairs = matcher.group(1).split(",");
//
//        // Check for invalid key-value pairs
//        for (String pair : pairs) {
//            String[] parts = pair.trim().split(":", 2);
//
//            // If we don't have exactly two parts (key:value), return empty
//            if (parts.length != 2 || parts[1].trim().isEmpty()) {
//                return Optional.empty();
//            }
//            document.put(parts[0].trim().substring(1,parts[0].length()-1), parts[1].trim());
//        }
//        return Optional.of(document);
//    }
}

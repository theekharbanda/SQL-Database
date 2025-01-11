package com.tabledb.core;

import com.tabledb.command.CommandExecutor;
import com.tabledb.server.DatabaseOperations;
import com.tabledb.server.SqlDatabase;

import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

public class App {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        DatabaseOperations database = new SqlDatabase();
        CommandExecutor executor = new CommandExecutor(database);

        while(true){
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("exit")) {
                System.out.println("Goodbye!");
                break;
            }

            try {
                CompletableFuture<String> future = executor.executeCommand(input);
                String result = future.join();
                System.out.println(result);

                if (result.contains("ADIOS")) {
                    break;
                }
            } catch (Exception e) {
                System.out.println("INVALID_COMMAND");
            }
        }
        scanner.close();
    }
}

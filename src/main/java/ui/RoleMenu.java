package com.scc.ui;

import com.scc.dao.UserDao;
import com.scc.model.User;

import java.util.Scanner;

public class RoleMenu {

    private final String title;
    private final Scanner scanner;
    private final UserDao userDao;
    private final User user;

    public RoleMenu(String title, Scanner scanner, UserDao userDao, User user) {
        this.title = title;
        this.scanner = scanner;
        this.userDao = userDao;
        this.user = user;
    }

    public void show() {
        System.out.println("\nWelcome, " + user.username() + "! (Status: " + user.status() + ")");

        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println("\n===== " + title + " =====");
            System.out.println("[1] View Profile");
            System.out.println("[2] Logout");
            System.out.print("Enter choice: ");

            switch (scanner.nextLine().trim()) {
                case "1" -> {
                    System.out.println("ID: " + user.id());
                    System.out.println("Username: " + user.username());
                    System.out.println("Role: " + user.role());
                    System.out.println("Status: " + user.status());
                }
                case "2" -> {
                    System.out.println("Logged out successfully.");
                    loggedIn = false;
                }
                default -> System.out.println("Invalid choice. Please enter 1 or 2.");
            }
        }
    }
}
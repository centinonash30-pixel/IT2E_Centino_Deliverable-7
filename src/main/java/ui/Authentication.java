package com.scc.ui;

import com.scc.dao.UserDao;
import com.scc.model.User;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Authentication {

    // Replace with the roles from Section 2 of your approved proposal.
    private static final List<String> ROLES = List.of("ADMIN", "ASSET_MANAGER", "DISTRIBUTION_OFFICER");

    private final Scanner scanner;
    private final UserDao userDao;

    public Authentication(Scanner scanner, UserDao userDao) {
        this.scanner = scanner;
        this.userDao = userDao;
    }

    public void start() {
        boolean running = true;
        while (running) {
            System.out.println("\n===== EXCHANGE ASSET & DISTRIBUTION MANAGEMENT SYSTEM =====");
            System.out.println("[1] Register");
            System.out.println("[2] Login");
            System.out.println("[3] Exit");
            System.out.print("Enter choice: ");

            switch (scanner.nextLine().trim()) {
                case "1" -> register();
                case "2" -> login();
                case "3" -> {
                    System.out.println("Exiting system... Thank you!");
                    running = false;
                }
                default -> System.out.println("Invalid choice. Please enter 1, 2, or 3.");
            }
        }
    }

    private void register() {
        System.out.println("\n--- REGISTER ---");
        System.out.print("Enter Username: ");
        String username = scanner.nextLine().trim();
        if (username.isEmpty()) {
            System.out.println("Username cannot be empty.");
            return;
        }
        if (userDao.usernameExists(username)) {
            System.out.println("Username already taken.");
            return;
        }

        System.out.print("Enter Password: ");
        String password = scanner.nextLine();
        if (password.isEmpty()) {
            System.out.println("Password cannot be empty.");
            return;
        }

        String role = selectRole();

        if (userDao.register(username, password, role)) {
            System.out.println("SUCCESS: Account registered as " + role + " (Status: PENDING).");
        } else {
            System.out.println("ERROR: Registration failed.");
        }
    }

    private String selectRole() {
        while (true) {
            System.out.println("Select role:");
            for (int i = 0; i < ROLES.size(); i++) {
                System.out.println("[" + (i + 1) + "] " + ROLES.get(i));
            }
            System.out.print("Enter choice: ");
            String input = scanner.nextLine().trim();
            try {
                int index = Integer.parseInt(input) - 1;
                if (index >= 0 && index < ROLES.size()) {
                    return ROLES.get(index);
                }
            } catch (NumberFormatException ignored) {
                // fall through to the error message
            }
            System.out.println("Invalid role choice.");
        }
    }

    private void login() {
        System.out.println("\n--- LOGIN ---");
        System.out.print("Enter Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Enter Password: ");
        String password = scanner.nextLine();

        Optional<User> user = userDao.login(username, password);
        if (user.isEmpty()) {
            System.out.println("Invalid username or password.");
            return;
        }
        if (!"APPROVED".equals(user.get().status())) {
            System.out.println("Your account is " + user.get().status() + ". Please wait for admin approval.");
            return;
        }
        routeByRole(user.get());
    }

    private void routeByRole(User user) {
        switch (user.role()) {
            case "ADMIN" -> new AdminMenu(scanner, userDao, user).show();
            case "ASSET_MANAGER" -> new RoleMenu("ASSET MANAGER MENU", scanner, userDao, user).show();
            case "DISTRIBUTION_OFFICER" -> new RoleMenu("DISTRIBUTION OFFICER MENU", scanner, userDao, user).show();
            default -> System.out.println("No menu available for role: " + user.role());
        }
    }
}
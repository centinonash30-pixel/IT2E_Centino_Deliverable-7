package com.scc.ui;

import com.scc.dao.UserDao;
import com.scc.model.User;

import java.util.List;
import java.util.Scanner;

public class AdminMenu {

    // Keep in sync with the roles in Authentication.java
    private static final List<String> ROLES = List.of("ADMIN", "ASSET_MANAGER", "DISTRIBUTION_OFFICER");
    private static final List<String> STATUSES = List.of("APPROVED", "PENDING");

    // Column widths: ID, Username, Role, Status
    private static final String BORDER = "+" + "-".repeat(7) + "+" + "-".repeat(17)
            + "+" + "-".repeat(24) + "+" + "-".repeat(12) + "+";
    private static final String ROW_FORMAT = "| %-5s | %-15s | %-22s | %-10s |%n";

    private final Scanner scanner;
    private final UserDao userDao;
    private final User admin;

    public AdminMenu(Scanner scanner, UserDao userDao, User admin) {
        this.scanner = scanner;
        this.userDao = userDao;
        this.admin = admin;
    }

    public void show() {
        System.out.println("\nWelcome, " + admin.username() + "!");

        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println("\n" + "=".repeat(50));
            System.out.println("                ADMIN MENU");
            System.out.println("=".repeat(50));
            System.out.println("[1] View All System Users");
            System.out.println("[2] View Pending Registrations");
            System.out.println("[3] Approve User Account");
            System.out.println("[4] Update User Role & Status");
            System.out.println("[5] Logout");
            System.out.println("=".repeat(50));

            int choice = readIntInput("Enter choice: ");
            switch (choice) {
                case 1 -> viewAllUsers();
                case 2 -> viewPendingUsers();
                case 3 -> approveUser();
                case 4 -> updateUser();
                case 5 -> {
                    System.out.println("Logged out successfully.");
                    loggedIn = false;
                }
                default -> System.out.println("Invalid choice. Please enter 1 to 5.");
            }
        }
    }

    private void viewAllUsers() {
        System.out.println("\n--- ALL SYSTEM USERS ---");
        printUserTable(userDao.getAllUsers());
    }

    private void viewPendingUsers() {
        System.out.println("\n--- PENDING REGISTRATIONS ---");
        printUserTable(userDao.getPendingUsers());
    }

    private void approveUser() {
        System.out.println("\n--- APPROVE USER ACCOUNT ---");
        List<User> pending = userDao.getPendingUsers();
        if (pending.isEmpty()) {
            System.out.println("No pending accounts to approve.");
            return;
        }
        printUserTable(pending);

        while (true) {
            int targetId = readIntInput("Enter Target User ID to approve (0 to cancel): ");
            if (targetId == 0) {
                System.out.println("Approval cancelled.");
                return;
            }
            boolean isPending = pending.stream().anyMatch(u -> u.id() == targetId);
            if (!isPending) {
                System.out.println("ID " + targetId + " is not in the pending list. Try again.");
                continue;
            }
            if (userDao.approveUser(targetId)) {
                System.out.println("SUCCESS: User ID " + targetId + " has been approved!");
            } else {
                System.out.println("ERROR: Could not approve user ID " + targetId + ".");
            }
            return;
        }
    }

    private void updateUser() {
        System.out.println("\n--- UPDATE USER ROLE & STATUS ---");
        List<User> users = userDao.getAllUsers();
        printUserTable(users);

        int targetId;
        while (true) {
            targetId = readIntInput("Enter Target User ID (0 to cancel): ");
            if (targetId == 0) {
                System.out.println("Update cancelled.");
                return;
            }
            final int id = targetId;
            if (users.stream().noneMatch(u -> u.id() == id)) {
                System.out.println("ID " + targetId + " does not exist. Try again.");
                continue;
            }
            break;
        }

        if (targetId == admin.id()) {
            System.out.println("WARNING: Modifying the currently active admin account is restricted.");
            return;
        }

        String newRole = readValidRole();
        if (newRole == null) {
            System.out.println("Update cancelled.");
            return;
        }

        String newStatus = readValidStatus();
        if (newStatus == null) {
            System.out.println("Update cancelled.");
            return;
        }

        if (userDao.updateUserRoleAndStatus(targetId, newRole, newStatus)) {
            System.out.println("SUCCESS: User ID " + targetId + " updated to role "
                    + newRole + " with status " + newStatus + ".");
        } else {
            System.out.println("ERROR: Update failed for user ID " + targetId + ".");
        }
    }

    // ---------- Input validation wrappers ----------

    private int readIntInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    private String readValidRole() {
        while (true) {
            System.out.print("Enter new role " + ROLES + " (0 to cancel): ");
            String input = scanner.nextLine().trim().toUpperCase();
            if (input.equals("0")) {
                return null;
            }
            if (ROLES.contains(input)) {
                return input;
            }
            System.out.println("Invalid role. Choose one of: " + ROLES);
        }
    }

    private String readValidStatus() {
        while (true) {
            System.out.print("Enter new status " + STATUSES + " (0 to cancel): ");
            String input = scanner.nextLine().trim().toUpperCase();
            if (input.equals("0")) {
                return null;
            }
            if (STATUSES.contains(input)) {
                return input;
            }
            System.out.println("Invalid status. Choose one of: " + STATUSES);
        }
    }

    // ---------- Table helper ----------

    private void printUserTable(List<User> users) {
        System.out.println(BORDER);
        System.out.printf(ROW_FORMAT, "ID", "USERNAME", "ROLE", "STATUS");
        System.out.println(BORDER);
        for (User u : users) {
            System.out.printf(ROW_FORMAT, u.id(), u.username(), u.role(), u.status());
        }
        System.out.println(BORDER);
        System.out.println("Total Records Displayed: " + users.size());
    }
}
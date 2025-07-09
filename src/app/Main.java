
package app;

import database.DBConnection;
import java.sql.Connection;
import java.util.List;
import java.util.Scanner;
import model.User;
import service.UserService;

public class Main {
    public static void main(String[] args) {
        Connection conn = DBConnection.getConnection();

        if (conn != null) {
            System.out.println(" Connection successful from Main class!");

            try {
                // Take user input for registration
                Scanner scanner = new Scanner(System.in);

                System.out.print("Enter your name: ");
                String name = scanner.nextLine();

                System.out.print("Enter your email: ");
                String email = scanner.nextLine();

                System.out.print("Enter your password: ");
                String password = scanner.nextLine();

                System.out.print("Enter your skill: ");
                String skill = scanner.nextLine();

                System.out.print("Enter your desired skill: ");
                String desiredSkill = scanner.nextLine();

                // Create a User object based on the input
                User user = new User(0, name, email, password, skill, desiredSkill);
                UserService userService = new UserService();
                boolean success = userService.registerUser(user, conn);

                if (success) {
                    System.out.println(" User registered successfully!");

                    // Step 4: Show other users
                    List<User> otherUsers = userService.getAllOtherUsers(user.getId(), conn);
                    System.out.println("\n Available Users:");
                    for (User u : otherUsers) {
                        System.out.println("- Name: " + u.getName() + ", Email: " + u.getEmail()
                                + ", Skill: " + u.getSkills() + ", Desired Skill: " + u.getDesiredSkill());
                    }

                    // Step 5: Skill swap match
                    List<User> matches = userService.getMatchingUsers(user, conn);
                    System.out.println("\n Skill Swap Matches:");
                    if (matches.isEmpty()) {
                        System.out.println("No matches found.");
                    } else {
                        for (User match : matches) {
                            System.out.println("- Name: " + match.getName() + ", Has: " + match.getSkills()
                                    + ", Wants: " + match.getDesiredSkill());

                            // Ask if user wants to chat
                            System.out.print("Do you want to chat with " + match.getName() + "? (yes/no): ");
                            String response = scanner.nextLine();

                            if (response.equalsIgnoreCase("yes")) {
                                // Launch ChatServer in a new thread
                                new Thread(() -> {
                                    try {
                                        ChatServer.main(new String[0]); // Starts the server
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }).start();

                                // Wait a second to ensure server starts first
                                Thread.sleep(1000);

                                // Launch ChatClient in another thread
                                new Thread(() -> {
                                    try {
                                        ChatClient.main(new String[] { user.getName(), match.getName() }); // Starts the
                                                                                                           // client
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }).start();

                                // Launch ChatClient for the matched user in a new process using ProcessBuilder
                                String command = "java -cp .;lib/mysql-connector-j-9.2.0.jar;bin app.ChatClient "
                                        + match.getName() + " " + user.getName();
                                ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));
                                processBuilder.directory(new java.io.File("."));
                                Process process = processBuilder.start();
                                process.waitFor();

                                break; // Chat started, no need to ask for other matches
                            }
                        }
                    }

                    // Optional Step 6: Show mutual matches
                    System.out.println("\n Finding Matches Between All Users:");
                    List<String> allMatches = userService.findSkillSwapMatches(conn);
                    if (allMatches.isEmpty()) {
                        System.out.println("No mutual matches found.");
                    } else {
                        for (String match : allMatches) {
                            System.out.println("---> " + match);
                        }
                    }

                } else {
                    System.out.println("Failed to register user. Email might already exist.");
                }

            } catch (Exception e) {
                System.out.println(" Error: " + e.getMessage());
                e.printStackTrace();
            } finally {
                DBConnection.closeConnection();
            }

        } else {
            System.out.println(" Database connection failed.");
        }
    }
}

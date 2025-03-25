package ui;

import chess.ChessGame;
import client.ServerFacade;
import model.GameData;

import java.util.List;
import java.util.Scanner;

public class ChessClient {

    private final Scanner scanner = new Scanner(System.in);
    private final ServerFacade facade;
    private String authToken = null;
    private String username = null;

    public ChessClient(ServerFacade facade) {
        this.facade = facade;
    }

    public void run() {
        while (true) {
            System.out.print(prompt());
            String line = scanner.nextLine().trim();
            String[] parts = line.split(" ");
            String command = parts[0].toLowerCase();

            try {
                if (authToken == null) {
                    handlePreLogin(command, parts);
                } else {
                    handlePostLogin(command, parts);
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private String prompt() {
        return (authToken == null ? "[LOGGED_OUT]" : "[LOGGED_IN " + username + "]") + " >>> ";
    }

    private void handlePreLogin(String command, String[] parts) throws Exception {
        switch (command) {
            case "help" -> printPreLoginHelp();
            case "quit" -> System.exit(0);
            case "register" -> {
                System.out.print("Username: ");
                String user = scanner.nextLine();
                System.out.print("Password: ");
                String pass = scanner.nextLine();
                System.out.print("Email: ");
                String email = scanner.nextLine();
                var auth = facade.register(user, pass, email);
                this.authToken = auth.authToken();
                this.username = auth.username();
                System.out.println("Registered and logged in as " + username);
            }
            case "login" -> {
                System.out.print("Username: ");
                String user = scanner.nextLine();
                System.out.print("Password: ");
                String pass = scanner.nextLine();
                var auth = facade.login(user, pass);
                this.authToken = auth.authToken();
                this.username = auth.username();
                System.out.println("Logged in as " + username);
            }
            default -> System.out.println("Unknown command. Type 'help'");
        }
    }

    private void handlePostLogin(String command, String[] parts) throws Exception {
        switch (command) {
            case "help" -> printPostLoginHelp();
            case "quit" -> System.exit(0);
            case "logout" -> {
                facade.logout(authToken);
                this.authToken = null;
                this.username = null;
                System.out.println("Logged out.");
            }
            case "create" -> {
                System.out.print("Game name: ");
                String gameName = scanner.nextLine();
                var game = facade.createGame(authToken, gameName);
                System.out.println("Created game: " + game.gameName() + " (ID: " + game.gameID() + ")");
            }
            case "list" -> {
                var games = facade.listGames(authToken);
                System.out.println("Available Games:");
                printGameList(games);

            }
            case "play" -> {
                var games = facade.listGames(authToken);
                if (games.isEmpty()) {
                    System.out.println("No games available.");
                    return;
                }

                System.out.println("Choose a game to join:");
                printGameList(games);


                System.out.print("Enter game number: ");
                int choice = Integer.parseInt(scanner.nextLine()) - 1;

                if (choice < 0 || choice >= games.size()) {
                    System.out.println("Invalid game number.");
                    return;
                }

                var game = games.get(choice);

                System.out.print("Join as WHITE or BLACK? ");
                String colorInput = scanner.nextLine().trim().toUpperCase();

                ChessGame.TeamColor color;
                try {
                    color = ChessGame.TeamColor.valueOf(colorInput);
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid color. Choose WHITE or BLACK.");
                    return;
                }

                facade.joinGame(authToken, game.gameID(), color);

                System.out.println("Joined game: " + game.gameName() + " as " + color);
                BoardPrinter.drawBoard(new ChessGame(), color);
            }
            case "observe" -> {
                var games = facade.listGames(authToken);
                if (games.isEmpty()) {
                    System.out.println("No games available.");
                    return;
                }

                System.out.println("Choose a game to observe:");
                for (int i = 0; i < games.size(); i++) {
                    var g = games.get(i);
                    System.out.printf("%d. %s | White: %s | Black: %s%n", i + 1, g.gameName(),
                            g.whiteUsername() != null ? g.whiteUsername() : "?",
                            g.blackUsername() != null ? g.blackUsername() : "?");
                }

                System.out.print("Enter game number: ");
                int choice = Integer.parseInt(scanner.nextLine()) - 1;

                if (choice < 0 || choice >= games.size()) {
                    System.out.println("Invalid game number.");
                    return;
                }

                var game = games.get(choice);

                System.out.println("Observing game: " + game.gameName());
                BoardPrinter.drawBoard(new ChessGame(), ChessGame.TeamColor.WHITE);
            }

            default -> System.out.println("Unknown command. Type 'help'");
        }
    }

    private void printGameList(List<GameData> games) {
        for (int i = 0; i < games.size(); i++) {
            var g = games.get(i);
            System.out.printf("%d. %s | White: %s | Black: %s%n",
                    i + 1,
                    g.gameName(),
                    g.whiteUsername() != null ? g.whiteUsername() : "?",
                    g.blackUsername() != null ? g.blackUsername() : "?");
        }
    }


    private void printPreLoginHelp() {
        System.out.println("""
            Pre-login commands:
            - help       Show this help menu
            - register   Create a new account
            - login      Log into an existing account
            - quit       Exit the app
        """);
    }

    private void printPostLoginHelp() {
        System.out.println("""
            Post-login commands:
            - help       Show this help menu
            - create     Create a new chess game
            - list       List all available games
            - logout     Log out of your account
            - play       Join a game and play
            - observe    Observe a game
            - quit       Exit the app
        """);
    }
}

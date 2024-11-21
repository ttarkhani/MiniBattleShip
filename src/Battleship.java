import java.util.Scanner;
import java.util.Random;

public class Battleship {
    private static final int BOARD_SIZE = 5;
    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[41m";
    private static final String GREY = "\u001B[47m";
    private static final String BLUE = "\u001B[44m";

    private static final char WATER = '~';
    private static final char SHIP = 'S';
    private static final char HIT = 'X';
    private static final char MISS = 'O';

    private static char[][] playerBoard = new char[BOARD_SIZE][BOARD_SIZE];
    private static char[][] botBoard = new char[BOARD_SIZE][BOARD_SIZE];
    private static char[][] botVisibleBoard = new char[BOARD_SIZE][BOARD_SIZE];

    private static int playerShips = 6; // Total ship cells (1x1 + 1x2 + 1x3)
    private static int botShips = 6;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Random random = new Random();

        initializeBoards();

        System.out.println("Welcome to Battleship!");
        System.out.println("Place your ships on the 5x5 grid.");

        placePlayerShips(scanner);
        placeBotShips(random);

        System.out.println("\n--- Let the battle begin! ---");

        while (playerShips > 0 && botShips > 0) {
            // Player's turn
            System.out.println("\nYour board:");
            printBoardWithCoordinates(playerBoard, true);
            System.out.println("\nBot's board:");
            printBoardWithCoordinates(botVisibleBoard, false);

            System.out.println("\nYour turn! Enter target (e.g., A1):");
            String input = scanner.next().toUpperCase();

            if (!isValidInput(input)) {
                System.out.println("Invalid input. Please enter a valid coordinate (e.g., A1).");
                continue;
            }

            int row = Integer.parseInt(input.substring(1)) - 1; // Convert to 0-indexed
            int col = input.charAt(0) - 'A'; // Convert column letter to index

            if (botBoard[row][col] == SHIP) {
                System.out.println("You hit a ship!");
                botBoard[row][col] = HIT;
                botVisibleBoard[row][col] = HIT;
                botShips--;
            } else if (botBoard[row][col] == WATER) {
                System.out.println("You missed!");
                botBoard[row][col] = MISS;
                botVisibleBoard[row][col] = MISS;
            } else {
                System.out.println("You already targeted this spot. Try again.");
                continue;
            }

            if (botShips == 0) {
                System.out.println("Congratulations! You sank all the bot's ships!");
                break;
            }

            // Bot's turn
            System.out.println("\nBot's turn...");
            int botRow, botCol;
            do {
                botRow = random.nextInt(BOARD_SIZE);
                botCol = random.nextInt(BOARD_SIZE);
            } while (playerBoard[botRow][botCol] == HIT || playerBoard[botRow][botCol] == MISS);

            if (playerBoard[botRow][botCol] == SHIP) {
                System.out.println("The bot hit your ship!");
                playerBoard[botRow][botCol] = HIT;
                playerShips--;
            } else {
                System.out.println("The bot missed!");
                playerBoard[botRow][botCol] = MISS;
            }

            if (playerShips == 0) {
                System.out.println("Game over! The bot sank all your ships!");
            }
        }
    }

    private static void initializeBoards() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                playerBoard[i][j] = WATER;
                botBoard[i][j] = WATER;
                botVisibleBoard[i][j] = WATER;
            }
        }
    }

    private static void printBoardWithCoordinates(char[][] board, boolean showShips) {
        System.out.print("   "); // Space for column labels
        for (int col = 0; col < BOARD_SIZE; col++) {
            System.out.print(" " + (char) ('A' + col) + " "); // Print column labels (A, B, C, etc.)
        }
        System.out.println();

        for (int row = 0; row < BOARD_SIZE; row++) {
            System.out.print(row + 1 + "  "); // Print row numbers
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (board[row][col] == HIT) {
                    System.out.print(RED + " X " + RESET);
                } else if (board[row][col] == MISS) {
                    System.out.print(GREY + " O " + RESET);
                } else if (board[row][col] == SHIP && showShips) {
                    System.out.print(GREY + " S " + RESET);
                } else {
                    System.out.print(BLUE + " ~ " + RESET);
                }
            }
            System.out.println();
        }
    }

    private static void placePlayerShips(Scanner scanner) {
        placeShip(scanner, playerBoard, 1); // 1x1 ship
        placeShip(scanner, playerBoard, 2); // 1x2 ship
        placeShip(scanner, playerBoard, 3); // 1x3 ship
    }

    private static void placeBotShips(Random random) {
        for (int size = 1; size <= 3; size++) {
            while (true) {
                int row = random.nextInt(BOARD_SIZE);
                int col = random.nextInt(BOARD_SIZE);
                char orientation = random.nextBoolean() ? 'H' : 'V';

                if (canPlaceShip(botBoard, row, col, size, orientation)) {
                    for (int i = 0; i < size; i++) {
                        if (orientation == 'H') {
                            botBoard[row][col + i] = SHIP;
                        } else {
                            botBoard[row + i][col] = SHIP;
                        }
                    }
                    break;
                }
            }
        }
    }

    private static void placeShip(Scanner scanner, char[][] board, int size) {
        while (true) {
            System.out.println("\nPlace your " + size + "x1 ship. Enter starting position and orientation (e.g., A1H or A1V):");
            printBoardWithCoordinates(board, true);
            String input = scanner.next().toUpperCase();

            if (!isValidInputForPlacement(input)) {
                System.out.println("Invalid input. Please enter a valid position and orientation (e.g., A1H or A1V).");
                continue;
            }

            int row = Integer.parseInt(input.substring(1, input.length() - 1)) - 1; // Convert to 0-indexed
            int col = input.charAt(0) - 'A'; // Convert column letter to index
            char orientation = input.charAt(input.length() - 1);

            if (isValidCoordinate(row, col) && canPlaceShip(board, row, col, size, orientation)) {
                for (int i = 0; i < size; i++) {
                    if (orientation == 'H') {
                        board[row][col + i] = SHIP;
                    } else {
                        board[row + i][col] = SHIP;
                    }
                }
                break;
            } else {
                System.out.println("Invalid position. Try again.");
            }
        }
    }

    private static boolean isValidInput(String input) {
        return input.matches("[A-E][1-5]");
    }

    private static boolean isValidInputForPlacement(String input) {
        return input.matches("[A-E][1-5][HV]");
    }

    private static boolean isValidCoordinate(int row, int col) {
        return row >= 0 && row < BOARD_SIZE && col >= 0 && col < BOARD_SIZE;
    }

    private static boolean canPlaceShip(char[][] board, int row, int col, int size, char orientation) {
        if (orientation == 'H') {
            if (col + size > BOARD_SIZE) return false;
            for (int i = 0; i < size; i++) {
                if (board[row][col + i] != WATER) return false;
            }
        } else if (orientation == 'V') {
            if (row + size > BOARD_SIZE) return false;
            for (int i = 0; i < size; i++) {
                if (board[row + i][col] != WATER) return false;
            }
        }
        return true;
    }
}

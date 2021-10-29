package main.java.intro;

import static main.java.intro.Repository.WAREHOUSE1;
import static main.java.intro.Repository.WAREHOUSE2;

import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Provides necessary methods to deal through the Warehouse management actions
 *
 * @author riteshp
 */
public class TheWarehouseManager {
    // =====================================================================================
    // Member Variables
    // =====================================================================================

    // To read inputs from the console/CLI
    private final Scanner reader = new Scanner(System.in);
    private final String[] userOptions = {
            "1. List items by warehouse", "2. Search an item and place an order", "3. Quit"
    };
    // To refer the user provided name.
    private String userName;

    // =====================================================================================
    // Public Member Methods
    // =====================================================================================

    /** Welcome User */
    public void welcomeUser() {
        this.seekUserName();
        this.greetUser();
    }

    /** Ask for user's choice of action */
    public int getUsersChoice() {
        System.out.println("What would you like to do?");
        System.out.println("1. List items by warehouse");
        System.out.println("2. Search an item and place an order");
        System.out.println("3. Quit");
        System.out.println("Type the number of the operation:");
        int choice;
        try {
            choice = reader.nextInt();
        } catch (InputMismatchException e) {
            choice = -1;
        }
        reader.nextLine();
        return choice;
    }

    /** Initiate an action based on given option */
    public void performAction(int option) {
        switch (option) {
            case 1:
                this.listItemsByWarehouse();
                break;
            case 2:
                this.searchItemAndPlaceOrder();
                break;
            case 3:
                this.quit();
            default:
                System.out.println("The option you entered is not valid! Please try again.");
        }
    }

    /**
     * Confirm an action
     *
     * @return action
     */
   public boolean confirm(String message) {
       System.out.println(message);
       return (reader.nextLine().toLowerCase().charAt(0) == 'y');
    }

    /** End the application */
    public void quit() {
        System.out.printf("\nThank you for your visit, %s!\n", this.userName);
        System.exit(0);
    }

    // =====================================================================================
    // Private Methods
    // =====================================================================================

    /** Get user's name via CLI */
    private void seekUserName() {
        System.out.println("Please enter your user name:");
        this.userName = reader.nextLine();
    }

    /** Print a welcome message with the given user's name */
    private void greetUser() {
        System.out.println("Hello " + this.userName + "!");
    }

    private void listItemsByWarehouse() {
        System.out.println("Items in Warehouse 1: ");
        listItems(WAREHOUSE1);

        System.out.println("\n");

        System.out.println("Items in Warehouse 2: ");
        listItems(WAREHOUSE2);
    }

    private void listItems(String[] warehouse) {
        for (String item : warehouse) {
            System.out.println("- " + item);
        }
    }

    private void searchItemAndPlaceOrder() {
        System.out.println("What is the name of the item?");
        String itemName = reader.nextLine();

        int[] allAmounts = this.getAvailableAmounts(itemName);

        System.out.println("Amount available: " + allAmounts[0]);

        String location;
        if (allAmounts[0] == 0) {
            location = "Not in stock";
        } else if (allAmounts[1]== 0) {
            location = "Warehouse 2";
        } else if (allAmounts[2] == 0) {
            location = "Warehouse 1";
        } else {
            location = "Both warehouses";
        }

        System.out.println("Location: " + location);

        if (location.equals("Not in stock")) return;
        else if (location.equals("Both warehouses")) {
            if (allAmounts[1] >= allAmounts[2]) {
                System.out.println("Maximum availability: " + allAmounts[1] + " in Warehouse 1");
            } else {
                System.out.println("Maximum availability: " + allAmounts[2] + " in Warehouse 2");
            }
        }

        char choice = this.askItemToOrder();

        if (choice == 'y') this.askAmountAndConfirmOrder(allAmounts[0], itemName);

    }

    /**
     * Ask the user to specify an Item to Order
     *
     * @return String itemName
     */
    private char askItemToOrder() {
        System.out.println("Would you like to order this item? (y/n)");
        return reader.nextLine().toLowerCase().charAt(0);
    }

    /**
     * Calculate total availability of the given item
     *
     * @param itemName itemName
     * @return integer availableCount
     */
    private int[] getAvailableAmounts(String itemName) {
        int amountw1 = find(itemName, WAREHOUSE1);
        int amountw2 = find(itemName, WAREHOUSE2);
        int total = amountw1 + amountw2;

        return new int[] {total, amountw1, amountw2};
    }

    /**
     * Find the count of an item in a given warehouse
     *
     * @param item the item
     * @param warehouse the warehouse
     * @return count
     */
    private int find(String item, String[] warehouse) {
        int amount = 0;

        for (String product: warehouse) {
            if (product.equals(item)) amount++;
        }

        return amount;
    }

/** Ask order amount and confirm order */
    private void askAmountAndConfirmOrder(int availableAmount, String item) {
        System.out.println("How many would you like to order?");
        int order = getOrderAmount(availableAmount);
        if (order > 0) {
            System.out.println("Your order of " + order + " " + item + " is confirmed.");
        }
    }

/**
     * Get amount of order
     *
     * @param availableAmount
     * @return
     */
    private int getOrderAmount(int availableAmount) {
        int desiredAmount = reader.nextInt();
        reader.nextLine();

        if (desiredAmount > availableAmount) {
            System.out.println("There are not this many available. The maximum amount that can be ordered is " + availableAmount);
            System.out.println("Would you like to order this amount? (y/n)");
            char choice = reader.nextLine().toLowerCase().charAt(0);
            if (choice == 'y') {
                return availableAmount;
            } else return -1;
        }

        return desiredAmount;
    }
}

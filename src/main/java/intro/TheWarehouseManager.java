package main.java.intro;


import main.java.intro.data.Item;
import main.java.intro.data.Repository;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static main.java.intro.data.Repository.getItemsByWarehouse;

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
            "1. List items by warehouse", "2. Search an item and place an order", "3. Browse by category", "4. Quit"
    };
    // To refer the user provided name.
    private String userName;

    // To refer to warehouses and categories
    private Set<Integer> warehouses = Repository.getWarehouses();
    private Set<String> categories = Repository.getCategories();

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
        for (String option : this.userOptions) {
            System.out.println(option);
        }
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
                this.browseByCategory();
                break;
            case 4:
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
        Map<Integer, Integer> totalItems = new HashMap<>(this.warehouses.size());

        for (int warehouse : this.warehouses) {
            System.out.println("Items in Warehouse " + warehouse);
            List<Item> warehouseItems = new ArrayList<>(Repository.getItemsByWarehouse(warehouse));

            for (Item item : warehouseItems) {
                System.out.println("- " + item.getState()+ " " + item.getCategory());
            }

            System.out.println("\n");
            totalItems.put(warehouse, warehouseItems.size());
        }

        for (Map.Entry<Integer, Integer> entry : totalItems.entrySet()) {
            System.out.println("Total items in warehouse " + entry.getKey() + ": " + entry.getValue());
        }

    }

    /* // NOT USED ANYMORE
    private void listItems(String[] warehouse) {
        for (String item : warehouse) {
            System.out.println("- " + item);
        }
    }*/

    private void searchItemAndPlaceOrder() {
        String itemName = askItemToOrder();

        Map<Integer, List<Item>> allAmounts = this.getMatchingItemLists(itemName);
        int totalAmount = this.getAvailableAmount(allAmounts);

        System.out.println("Amount available: " + totalAmount);

        if (totalAmount == 0) {
            printLocation("Not in stock");
        } else {
            printLocation(allAmounts);
            if (allAmounts.size() > 1) {
                printMaximumAvailability(allAmounts);
            }

            System.out.println("Would you like to order this item? (y/n)");
            char choice = reader.nextLine().toLowerCase().charAt(0);

            if (choice == 'y') this.askAmountAndConfirmOrder(totalAmount, itemName);
        }
    }

    /**
     * Ask the user to specify an Item to Order
     *
     * @return String itemName
     */
    private String askItemToOrder() {
        System.out.println("What is the name of the item?");
        return reader.nextLine();
    }

    /**
     * Calculate availability of the given item
     *
     * @param matchingItems List of matchingItems per Warehouse
     * @return integer total amount
     */
    private int getAvailableAmount(Map<Integer, List<Item>> matchingItems) {
        int totalAmount = 0;

         for (List<Item> warehouseItems : matchingItems.values()) {
             totalAmount += warehouseItems.size();
         }

        return totalAmount;
    }

    /**
     * Create a map of all the occurrences of an item separated by warehouse
     * @param itemName, String, the name of the item
     * @return allAmounts, the matching items per warehouse
     */
    private Map<Integer, List<Item>> getMatchingItemLists(String itemName) {
        Map<Integer, List<Item>> allAmounts = new HashMap<>(this.warehouses.size());

        for (int warehouse : this.warehouses) {
            List<Item> matchingItems = find(itemName, warehouse);
            allAmounts.put(warehouse, matchingItems);
        }
        return allAmounts;
    }

    /**
     * Find the item in a given warehouse
     *
     * @param item the item
     * @param warehouse the warehouse
     * @return matchingItems, a List of the corresponding items found in the warehouse
     */
    private List<Item> find(String item, int warehouse) {
        List<Item> warehouseItems = new ArrayList<>(Repository.getItemsByWarehouse(warehouse));
        List<Item> matchingItems = new ArrayList<>();

        for (Item warehouseItem : warehouseItems) {
            String productName = warehouseItem.getState().toLowerCase() + " " + warehouseItem.getCategory().toLowerCase();
            if (item.toLowerCase().equals(productName)) matchingItems.add(warehouseItem);
        }

        return matchingItems;
    }

    /** Print the location of an item without listing the available items
     *
     * @param location the location of the items
     */
    private void printLocation(String location) {
        System.out.println("Location: " + location);
    }

    /** Print the location of an item and lists the corresponding items and their warehouse
     *
     * @param matchingItems a map of the matching items in the different locations
     */
    private void printLocation(Map<Integer, List<Item>> matchingItems) {
        Date today = new Date();
        System.out.println("Location: ");
        for (List<Item> warehouseItems : matchingItems.values()) {
            for (Item item : warehouseItems) {
                long days = TimeUnit.DAYS.convert(today.getTime() - item.getDateOfStock().getTime(), TimeUnit.MILLISECONDS);
                System.out.println("- Warehouse " + item.getWarehouse() + " (in stock for " + days + " days)");
            }
        }
    }

    /**
     * Print the location with the maximum availability of an item
     *
     * @param matchingItems a map of the matching items in the different locations
     */
    private void printMaximumAvailability(Map<Integer, List<Item>> matchingItems) {
        int max_size = 0;
        int warehouse = 0;

        for (Map.Entry<Integer, List<Item>> warehouseItems : matchingItems.entrySet()) {
            if (warehouseItems.getValue().size() > max_size) {
                max_size = warehouseItems.getValue().size();
                warehouse = warehouseItems.getKey();
            }
        }

        System.out.println("Maximum availability: " + max_size + " in Warehouse " + warehouse);
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
     * @param availableAmount the total available amount of the item in question
     * @return desiredAmount the amount to be ordered
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

    private void browseByCategory() {
        Map<Integer, String> categoryList = getCategoryMenu();
        this.showCategoryMenu(categoryList);

        int categoryNumber = this.getCategoryChoice();
        if (categoryNumber > 0 && categoryNumber <= categoryList.size()) {
            String category = categoryList.get(categoryNumber);
            printCategoryItems(category);
        } else {
            System.out.println("This is not a valid category.");
        }
    }

    /** Create a menu of categories with corresponding numbers
     *
     * @return categoryList, a map of categories and their numbers
     */
    private Map<Integer, String> getCategoryMenu() {
        Map<Integer, String> categoryList = new HashMap<>();
        int count = 1;

        for (String category : this.categories) {
            categoryList.put(count, category);
            count++;
        }

        return categoryList;
    }

    /**
     * Print the menu of categories and the number of items in each category
     * @param categoryList a map of the categories and their numbers
     */
    private void showCategoryMenu(Map<Integer, String> categoryList) {
        for (Map.Entry<Integer, String> entry : categoryList.entrySet()) {
            System.out.println(entry.getKey() + ". " + entry.getValue() + " (" + getAmountPerCategory(entry.getValue()) + ")");
        }
    }

    /**
     * Return the amount of items in a category
     * @param category, String, name of the category
     * @return int, amount of items
     */
    private int getAmountPerCategory(String category) {
        return Repository.getItemsByCategory(category).size();
    }

    /**
     * Ask for the user's choice of category
     * @return int, the chosen category
     */
    private int getCategoryChoice() {
        System.out.println("Type the number of the category to browse:");
        int choice;
        try {
            choice = reader.nextInt();
        } catch (InputMismatchException e) {
            choice = -1;
        }
        reader.nextLine();
        return choice;
    }

    /**
     * Print the available items in a chosen category and their location
     * @param category, String, the name of the category
     */
    private void printCategoryItems(String category) {
        System.out.println("List of " + category.toLowerCase() + "s available:");
        for (Item item : Repository.getItemsByCategory(category)) {
            System.out.println(item.getState() + " " + item.getCategory() + ", Warehouse " + item.getWarehouse());
        }
    }
}

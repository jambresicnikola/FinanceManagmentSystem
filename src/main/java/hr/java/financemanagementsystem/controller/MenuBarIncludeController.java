package hr.java.financemanagementsystem.controller;

import hr.java.financemanagementsystem.util.SceneManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller for the menu bar included on every screen.
 * Handles navigation between all screens in the app.
 */
public class MenuBarIncludeController {
    private static final Logger logger = LoggerFactory.getLogger(MenuBarIncludeController.class);

    /** Navigates to the Home screen. */
    public void home() {
        SceneManager.openHomeScreen();
    }

    /** Navigates to the Manage Categories screen. */
    public void manageCategories() {
        SceneManager.openManageCategoriesScreen();
    }

    /** Navigates to the Add Category screen. */
    public void addCategory() {
        SceneManager.openAddCategoryScreen();
    }

    /** Navigates to the Manage Transactions screen. */
    public void manageTransactions() {
        SceneManager.openManageTransactionsScreen();
    }

    /** Navigates to the Add Transaction screen. */
    public void addTransactions() {
        SceneManager.openAddTransactionScreen();
    }

    /** Navigates to the Manage Account screen. */
    public void manageAccount() {
        SceneManager.openManageAccountScreen();
    }

    /**
     * Logs the user out and navigates back to the Welcome screen.
     */
    public void logOut() {
        logger.info("User logged out.");
        SceneManager.openWelcomeScreen();
    }

    /** Navigates to the Charts screen. */
    public void charts() {
        SceneManager.openChartsScreen();
    }
}

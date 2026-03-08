package hr.java.financemanagementsystem.controller;

import hr.java.financemanagementsystem.util.ScreenManager;

public class MenuBarIncludeController {
    public void home() {
        ScreenManager.openHomeScreen();
    }

    public void manageCategories() {
        ScreenManager.openManageCategoriesScreen();
    }

    public void addCategory() {
        ScreenManager.openAddCategoryScreen();
    }

    public void manageTransactions() {
        ScreenManager.openManageTransactionsScreen();
    }

    public void addTransactions() {
        ScreenManager.openAddTransactionScreen();
    }

    public void manageAccount() {
        ScreenManager.openManageAccountScreen();
    }

    public void logOut() {
        ScreenManager.openWelcomeScreen();
    }
}

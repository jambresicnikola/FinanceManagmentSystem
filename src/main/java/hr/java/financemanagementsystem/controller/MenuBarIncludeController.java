package hr.java.financemanagementsystem.controller;

import hr.java.financemanagementsystem.util.SceneManager;

public class MenuBarIncludeController {
    public void home() {
        SceneManager.openHomeScreen();
    }

    public void manageCategories() {
        SceneManager.openManageCategoriesScreen();
    }

    public void addCategory() {
        SceneManager.openAddCategoryScreen();
    }

    public void manageTransactions() {
        SceneManager.openManageTransactionsScreen();
    }

    public void addTransactions() {
        SceneManager.openAddTransactionScreen();
    }

    public void manageAccount() {
        SceneManager.openManageAccountScreen();
    }

    public void logOut() {
        SceneManager.openWelcomeScreen();
    }

    public void summary() {
        //summary
    }

    public void charts() {
        SceneManager.openChartsScreen();
    }

    public void about() {
        //about
    }
}

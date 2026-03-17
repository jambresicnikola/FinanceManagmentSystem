package hr.java.financemanagementsystem.controller;

import hr.java.financemanagementsystem.exception.TransactionValidationException;
import hr.java.financemanagementsystem.service.DialogService;
import hr.java.financemanagementsystem.service.TransactionService;
import hr.java.financemanagementsystem.service.UserService;
import hr.java.financemanagementsystem.util.SceneManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller for the Add Transaction screen.
 * Extends {@link TransactionFormIncludeController} to reuse shared form fields and logic.
 */
public class AddTransactionScreenController extends TransactionFormIncludeController {
    private static final Logger logger = LoggerFactory.getLogger(AddTransactionScreenController.class);

    /**
     * Sets up the transaction form when the screen loads.
     */
    public void initialize() {
        initializeForm();
    }

    /**
     * Called when the user clicks the add transaction button.
     * Reads form data, validates it and saves the transaction.
     * Shows an error dialog if something is wrong with the input.
     */
    public void addNewTransaction() {
        logger.debug("Attempting to create new transaction for user with id: {}",
                UserService.getLoggedInUser().getId());

        try {
            TransactionService.createNewTransaction(buildTransactionFromForm());

            logger.info("Transaction successfully created for user with id: {}",
                    UserService.getLoggedInUser().getId());

            DialogService.information("Transaction added", "Your transaction has been successfully added.");

            SceneManager.openManageTransactionsScreen();
        } catch (TransactionValidationException e) {
            DialogService.error("Transaction not created", e.getMessage());
            logger.warn("Transaction creation failed: {}", e.getMessage());
        }
    }
}
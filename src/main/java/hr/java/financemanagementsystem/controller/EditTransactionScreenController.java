package hr.java.financemanagementsystem.controller;

import hr.java.financemanagementsystem.exception.TransactionValidationException;
import hr.java.financemanagementsystem.model.Transaction;
import hr.java.financemanagementsystem.service.DialogService;
import hr.java.financemanagementsystem.service.TransactionService;
import hr.java.financemanagementsystem.util.SceneManager;
import javafx.scene.control.SpinnerValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller for the Edit Transaction screen.
 * Extends {@link TransactionFormIncludeController} to reuse shared form fields and logic.
 */
public class EditTransactionScreenController extends TransactionFormIncludeController {
    private static final Logger logger = LoggerFactory.getLogger(EditTransactionScreenController.class);

    /**
     * Pre-fills the form with the current transaction data when the screen loads.
     */
    public void initialize() {
        initializeForm();

        Transaction transaction = TransactionService.getTransactionToManage();
        logger.debug("Edit transaction screen loaded for transaction with id: {}", transaction.getId());

        transactionDescriptionTextField.setText(transaction.getDescription());
        transactionAmountSpinner.setValueFactory(new SpinnerValueFactory
                .IntegerSpinnerValueFactory(0, 100, transaction.getAmount()));
        transactionPriceTextField.setText(transaction.getPrice().toString());
        transactionCategoryComboBox.setValue(transaction.getCategory().getName());
        transactionTypeComboBox.setValue(transaction.getTransactionType());
        transactionDatePicker.setValue(transaction.getDate());
    }

    /**
     * Called when the user clicks the confirm button.
     * Builds an updated transaction from the form and saves it.
     * Shows an error dialog if something is wrong with the input.
     */
    public void confirmChanges() {
        Transaction transactionToUpdate = buildTransactionFromForm();
        transactionToUpdate.setId(TransactionService.getTransactionToManage().getId());

        logger.debug("Attempting to update transaction with id: {}", transactionToUpdate.getId());

        try {
            TransactionService.updateTransaction(transactionToUpdate);

            logger.info("Transaction with id: {} successfully updated.", transactionToUpdate.getId());

            DialogService.information("Transaction updated",
                    "Your transaction has been successfully updated.");
            SceneManager.openManageTransactionsScreen();
        } catch (TransactionValidationException e) {
            logger.warn("Transaction update failed for id: {} - {}", transactionToUpdate.getId(), e.getMessage());
            DialogService.error("Transaction not updated", e.getMessage());
        }
    }
}

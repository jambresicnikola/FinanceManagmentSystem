package hr.java.financemanagementsystem.controller;

import hr.java.financemanagementsystem.exception.TransactionValidationException;
import hr.java.financemanagementsystem.model.Transaction;
import hr.java.financemanagementsystem.service.DialogService;
import hr.java.financemanagementsystem.service.TransactionService;
import javafx.scene.control.SpinnerValueFactory;

public class EditTransactionScreenController extends TransactionFormIncludeController {
    public void initialize() {
        initializeForm();

        transactionDescriptionTextField.setText(TransactionService.getTransactionToManage().getDescription());
        transactionAmountSpinner.setValueFactory(new SpinnerValueFactory
                .IntegerSpinnerValueFactory(0, 100, TransactionService.getTransactionToManage().getAmount()));
        transactionPriceTextField.setText(TransactionService.getTransactionToManage().getPrice().toString());
        transactionCategoryComboBox.setValue(TransactionService.getTransactionToManage().getCategory().getName());
        transactionTypeComboBox.setValue(TransactionService.getTransactionToManage().getTransactionType());
        transactionDatePicker.setValue(TransactionService.getTransactionToManage().getDate());
    }

    public void confirmChanges() {
        Transaction transactionToUpdate = buildTransactionFromForm();
        transactionToUpdate.setId(TransactionService.getTransactionToManage().getId());

        try {
            TransactionService.updateTransaction(transactionToUpdate);
        } catch (TransactionValidationException e) {
            DialogService.error("Transaction not updated", e.getMessage());
        }
    }
}

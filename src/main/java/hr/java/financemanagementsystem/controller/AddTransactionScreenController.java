package hr.java.financemanagementsystem.controller;

import hr.java.financemanagementsystem.exception.TransactionValidationException;
import hr.java.financemanagementsystem.service.DialogService;
import hr.java.financemanagementsystem.service.TransactionService;
import javafx.scene.control.*;

public class AddTransactionScreenController extends TransactionFormIncludeController {
    public void initialize() {
        initializeForm();
    }

    public void addNewTransaction() {
        try {
            TransactionService.createNewTransaction(buildTransactionFromForm());
        } catch (TransactionValidationException e) {
            DialogService.error("Transaction not created", e.getMessage());
        }
    }
}
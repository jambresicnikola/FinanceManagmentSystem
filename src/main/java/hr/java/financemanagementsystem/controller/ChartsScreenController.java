package hr.java.financemanagementsystem.controller;

import hr.java.financemanagementsystem.database.TransactionDatabaseRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;

public class ChartsScreenController {
    @FXML
    private PieChart expensesPerCategoryPieChart;
    @FXML
    private AreaChart<String, Number> incomePerMonthAreaChart;

    public void initialize(){
        ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
        TransactionDatabaseRepository.getInstance().getPieChartData().forEach((categoryName, expense) ->
            data.add(new PieChart.Data(categoryName, expense.doubleValue())));

        expensesPerCategoryPieChart.setData(data);

        XYChart.Series series = new XYChart.Series();
        TransactionDatabaseRepository.getInstance().getAreaChartData().forEach((monthName, income) ->
                series.getData().add(new XYChart.Data(monthName, income.doubleValue())));
        incomePerMonthAreaChart.getData().add(series);
    }
}

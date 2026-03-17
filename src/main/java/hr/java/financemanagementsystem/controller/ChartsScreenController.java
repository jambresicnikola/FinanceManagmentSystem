package hr.java.financemanagementsystem.controller;

import hr.java.financemanagementsystem.database.TransactionDatabaseRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller for the Charts screen.
 * Loads and displays transaction data as a pie chart and an area chart.
 */
public class ChartsScreenController {
    private static final Logger logger = LoggerFactory.getLogger(ChartsScreenController.class);

    @FXML
    private PieChart expensesPerCategoryPieChart;
    @FXML
    private AreaChart<String, Number> incomePerMonthAreaChart;

    /**
     * Loads chart data when the screen opens.
     * Pie chart shows expenses grouped by category.
     * Area chart shows income grouped by month.
     */
    public void initialize(){
        loadPieChartData();
        loadAreaChartData();
    }

    /**
     * Fetches expense data per category and populates the pie chart.
     */
    private void loadPieChartData() {
        logger.debug("Loading expenses per category pie chart data.");

        ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
        TransactionDatabaseRepository.getInstance().getPieChartData().forEach((categoryName, expense) ->
                data.add(new PieChart.Data(categoryName, expense.doubleValue())));

        expensesPerCategoryPieChart.setData(data);
        logger.info("Pie chart loaded with {} categories.", data.size());
    }

    /**
     * Fetches income data per month and populates the area chart.
     */
    private void loadAreaChartData() {
        logger.debug("Loading income per month area chart data.");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        TransactionDatabaseRepository.getInstance().getAreaChartData().forEach((monthName, income) ->
                series.getData().add(new XYChart.Data<>(monthName, income.doubleValue())));

        incomePerMonthAreaChart.getData().add(series);
        logger.info("Area chart loaded with {} months of data.", series.getData().size());
    }
}

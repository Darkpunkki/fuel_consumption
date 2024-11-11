package org.example.fuel_consumption_calc;

import Service.DatabaseUtils;
import Service.FuelUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public class FuelController {
    @FXML
    private TextField distanceField;

    @FXML
    private TextField fuelField;

    @FXML
    private Label distanceLabel;

    @FXML
    private Label fuelLabel;

    @FXML
    private ComboBox<String> languageSelector;

    @FXML
    private Label resultLabel;

    @FXML
    private TableView<FuelLog> dataTable;

    @FXML
    private TableColumn<FuelLog, Integer> idColumn;

    @FXML
    private TableColumn<FuelLog, Double> distanceColumn;

    @FXML
    private TableColumn<FuelLog, Double> fuelUsedColumn;

    @FXML
    private TableColumn<FuelLog, Double> consumptionColumn;

    @FXML
    private TableColumn<FuelLog, String> costColumn;

    @FXML
    private TableColumn<FuelLog, String> languageColumn;

    @FXML
    private Button calculateButton;


    private Locale currentLocale = Locale.ENGLISH;

    private final Map<String, Locale> languages = new HashMap<>();

    @FXML
    public void initialize() {
        // Populate language options
        languages.put("English", Locale.ENGLISH);
        languages.put("French", Locale.FRENCH);
        languages.put("Japanese", Locale.JAPAN);
        languages.put("Persian", new Locale("fa", "IR"));

        languageSelector.setItems(FXCollections.observableArrayList(languages.keySet()));
        languageSelector.setValue("English");

        // Set language change listener
        languageSelector.setOnAction(event -> onLanguageChange());
        // Set column resize policy to UNCONSTRAINED
        dataTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        // Bind each column's width proportionally to the table width
        idColumn.prefWidthProperty().bind(dataTable.widthProperty().multiply(0.1));       // 10%
        distanceColumn.prefWidthProperty().bind(dataTable.widthProperty().multiply(0.2)); // 20%
        fuelUsedColumn.prefWidthProperty().bind(dataTable.widthProperty().multiply(0.2)); // 20%
        consumptionColumn.prefWidthProperty().bind(dataTable.widthProperty().multiply(0.25)); // 25%
        costColumn.prefWidthProperty().bind(dataTable.widthProperty().multiply(0.15));    // 15%
        languageColumn.prefWidthProperty().bind(dataTable.widthProperty().multiply(0.1));


        languageSelector.setItems(FXCollections.observableArrayList(languages.keySet()));
        languageSelector.setValue("English");

        languageSelector.setOnAction(event -> onLanguageChange());

        // Initialize Table Columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        distanceColumn.setCellValueFactory(new PropertyValueFactory<>("distance"));
        fuelUsedColumn.setCellValueFactory(new PropertyValueFactory<>("fuelUsed"));
        consumptionColumn.setCellValueFactory(new PropertyValueFactory<>("consumptionPer100Km"));
        costColumn.setCellValueFactory(new PropertyValueFactory<>("cost"));
        languageColumn.setCellValueFactory(new PropertyValueFactory<>("language"));

        // Load Latest Data
        loadLatestData();
    }

    private void onLanguageChange() {
        String selectedLanguage = languageSelector.getValue();
        if (languages.containsKey(selectedLanguage)) {
            currentLocale = languages.get(selectedLanguage);
            updateLabels();
        }
    }

    private void updateLabels() {
        // Load resource bundle for the current locale
        ResourceBundle bundle = ResourceBundle.getBundle("messages", currentLocale);

        // Update labels with localized text
        distanceLabel.setText(bundle.getString("distance.label"));
        fuelLabel.setText(bundle.getString("fuel.label"));
        calculateButton.setText(bundle.getString("calculate.button"));
        resultLabel.setText(bundle.getString("result.label"));
    }


    @FXML
    protected void onCalculateButtonClick() {
        try {
            double distance = Double.parseDouble(distanceField.getText());
            double fuelUsed = Double.parseDouble(fuelField.getText());

            // Perform calculation with localization
            String localizedResult = FuelUtils.calculateFuelCost(distance, fuelUsed, currentLocale);

            // Fetch the localized "Result:" prefix
            ResourceBundle bundle = ResourceBundle.getBundle("messages", currentLocale);
            String resultText = bundle.getString("result.label");

            // Display the localized result
            resultLabel.setText(resultText + " " + localizedResult);

            // Reload latest data after adding a new entry
            loadLatestData();
        } catch (NumberFormatException e) {
            ResourceBundle bundle = ResourceBundle.getBundle("messages", currentLocale);
            resultLabel.setText(bundle.getString("invalid.input"));
        }
    }



    private void loadLatestData() {
        ObservableList<FuelLog> logs = FXCollections.observableArrayList();

        String query = "SELECT id, distance, fuel_used, consumption_per_100km, cost, language " +
                "FROM fuel_logs ORDER BY created_at DESC LIMIT 10";

        try (Connection connection = DatabaseUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                logs.add(new FuelLog(
                        resultSet.getInt("id"),
                        resultSet.getDouble("distance"),
                        resultSet.getDouble("fuel_used"),
                        resultSet.getDouble("consumption_per_100km"),
                        resultSet.getString("cost"),
                        resultSet.getString("language")
                ));
            }

            dataTable.setItems(logs);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

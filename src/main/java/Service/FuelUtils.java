package Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;
import java.util.ResourceBundle;

public class FuelUtils {
    private static final double PRICE_PER_LITER = 1.67;
    private static final double YEN_CONVERSION_RATE = 145.0;
    private static final double USD_CONVERSION_RATE = 1.1;
    private static final double EURO_CONVERSION_RATE = 1.0;
    private static final double IRR_CONVERSION_RATE = 45000.0;

    public static double calculateConsumptionPer100Km(double distance, double fuelUsed) {
        return (fuelUsed / distance) * 100;
    }

    public static String calculateFuelCost(double distance, double fuelUsed, Locale locale) {
        double consumptionPer100Km = (fuelUsed / distance) * 100;
        double totalCost = fuelUsed * PRICE_PER_LITER;

        // Adjust cost for the locale
        Currency currency;
        if (locale.getLanguage().equals(Locale.JAPANESE.getLanguage())) {
            totalCost *= YEN_CONVERSION_RATE;
            currency = Currency.getInstance("JPY");
        } else if (locale.getLanguage().equals(Locale.ENGLISH.getLanguage())) {
            totalCost *= USD_CONVERSION_RATE;
            currency = Currency.getInstance("USD");
        } else if (locale.getLanguage().equals(Locale.FRENCH.getLanguage())) {
            totalCost *= EURO_CONVERSION_RATE;
            currency = Currency.getInstance("EUR");
        } else if (locale.getLanguage().equals("fa")) {
            totalCost *= IRR_CONVERSION_RATE;
            currency = Currency.getInstance("IRR");
        } else {
            currency = Currency.getInstance("EUR");
        }

        // Format the cost based on locale
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);
        currencyFormatter.setCurrency(currency);
        String formattedCost = currencyFormatter.format(totalCost);

        // Load localized strings for "Consumption" and "Cost"
        ResourceBundle bundle = ResourceBundle.getBundle("messages", locale);
        String consumptionText = bundle.getString("consumption.text");
        String costText = bundle.getString("cost.text");

        // Return localized result
        return String.format("%s: %.2f L/100km, %s: %s",
                consumptionText, consumptionPer100Km, costText, formattedCost);
    }


    private static void saveLogToDatabase(double distance, double fuelUsed, double consumptionPer100Km, double cost, String currency, String language) {
        String query = "INSERT INTO fuel_logs (distance, fuel_used, consumption_per_100km, cost, currency, language) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setDouble(1, distance);
            statement.setDouble(2, fuelUsed);
            statement.setDouble(3, consumptionPer100Km);
            statement.setDouble(4, cost);
            statement.setString(5, currency);
            statement.setString(6, language);

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}

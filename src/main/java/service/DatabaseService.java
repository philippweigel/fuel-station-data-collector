package service;

import config.DatabaseConfig;

import java.sql.*;

public class DatabaseService {

    public float getSumOfKwhFromCustomer(String dbUrl, int customerId) {
        String query = "SELECT SUM(kwh) as sum_kwh FROM charge WHERE customer_id = ?";
        float sumKwh = 0;

        try (
                Connection connection = DatabaseConfig.getConnectionWithGivenUrlFromMessage(dbUrl);
                PreparedStatement ps = connection.prepareStatement(query);
        ) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    sumKwh = rs.getFloat("sum_kwh");
                }
            } catch (SQLException e) {
                System.err.println("Failed to fetch result set for sum of kwh for customer id: " + customerId);
                e.printStackTrace();
            }
        } catch (SQLException e) {
            System.err.println("Failed to prepare statement with url:" + dbUrl);
            e.printStackTrace();
        }
        return sumKwh;
    }
}

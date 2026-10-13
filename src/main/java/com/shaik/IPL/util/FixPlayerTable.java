
package com.shaik.IPL.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class FixPlayerTable {

    public static void main(String[] args) throws Exception {

        String url = "jdbc:mysql://localhost:3306/shaik";
        String username = "root";
        String password = "root";

        try (Connection con =
                DriverManager.getConnection(url, username, password);
             Statement stmt = con.createStatement()) {

            stmt.executeUpdate(
                "ALTER TABLE players MODIFY economy DOUBLE NULL"
            );

            System.out.println(
                "Economy column updated successfully!"
            );
        }
    }
}

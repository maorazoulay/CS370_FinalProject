package com.example.javatests;


import com.mysql.cj.jdbc.MysqlDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DBOperations {
    private final String HOST = "localhost";
    private final String USER = "maorazoulay";
    private final String PASSWORD = "Maor-290";
    private final int PORT = 3306;
    private final String SCHEMA = "college";
    private Connection connection;

    public DBOperations() {
        System.out.println("Initializing connection...");

        try {
            MysqlDataSource dataSource = new MysqlDataSource();
            dataSource.setUser(USER);
            dataSource.setPassword(PASSWORD);
            dataSource.setServerName(HOST);
            dataSource.setPortNumber(PORT);
            dataSource.setDatabaseName(SCHEMA);
            dataSource.setCharacterEncoding("latin1");
            dataSource.setServerTimezone("UTC");
            connection = dataSource.getConnection();
        } catch (SQLException e) {
            System.out.println("Failed to initialize connection!");
            e.printStackTrace();
        }
    }
    private static Date convertToHumanDate(String epochString) {
        long epoch = Long.parseLong(epochString);
        return new Date(epoch * 1000);
    }

    public String executeInsert(List<String> data) {
        PreparedStatement preparedStatement = null;
        ResultSet rs;
        StringBuilder sb = null;

        try {
            Date date = convertToHumanDate(data.get(0));
            Timestamp timestamp = new Timestamp(date.getTime());
            String query = String.format
                    ("INSERT INTO `local`.`statistics` (`date`, `tested`, `tested_positive`, `deaths`) VALUES ('%s', %s, %s, %s)",
                            timestamp.toString(), data.get(1), data.get(2), data.get(3));

            System.out.println(query);

            if (connection == null) {
                return "failed to create a connection to database";
            }
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.executeUpdate();

            int id;
            String dbFirstName;
            String dbLastName;

            PreparedStatement selectAllStatement = connection.prepareStatement("SELECT * FROM `local`.`statistics`");
            rs = selectAllStatement.executeQuery();

            System.out.println("Current table data: \n");
            sb = new StringBuilder();
            while (rs.next()) {
                id = rs.getInt(1);
                dbFirstName = rs.getString(2);
                dbLastName = rs.getString(3);
                sb.append("id: ").append(id).append("\tFirst Name: ").append(dbFirstName)
                        .append("\tLast Name: ").append(dbLastName).append("\n");
            }
            System.out.println(sb.toString());
            rs.close();
            preparedStatement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        if (sb == null) {
            return "";
        } else {
            return sb.toString();
        }
    }

    public static void main(String[] args) {
        List<String> data = new ArrayList<>();
        data.add("1585611141");
        data.add("72573");
        data.add("66497");
        data.add("914");
        DBOperations dbOperations = new DBOperations();
        String response = dbOperations.executeInsert(data);
        System.out.println(response);

    }
}


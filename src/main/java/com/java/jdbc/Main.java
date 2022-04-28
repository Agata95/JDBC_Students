package com.java.jdbc;

import com.mysql.cj.jdbc.MysqlDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final String CREATE_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS `students` (\n" +
            "`id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,\n" +
            "`name` VARCHAR(255) NOT NULL,\n" +
            "`age` INT NOT NULL,\n" +
            "`average` DOUBLE NOT NULL,\n" +
            "`alive` TINYINT NOT NULL\n" +
            ");";

    private static final String INSERT_QUERY = "INSERT INTO `students` \n" +
            "(`name`, `age`, `average`, `alive`) \n" +
            "VALUES (?, ?, ?, ?);";

    private static final String DELETE_QUERY = "DELETE FROM `students` WHERE `id` = ?;";

    private static final String SELECT_ALL_QUERY = "SELECT * FROM `students`;";

    private static final String SELECT_BY_ID_QUERY = "SELECT * FROM `students` WHERE `id` = ?;";

    private static final String DB_HOST = "localhost";
    private static final String DB_PORT = "3306";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "root";
    private static final String DB_NAME = "jdbc_students";

    public static void main(String[] args) {
        MysqlDataSource dataSource = new MysqlDataSource();

        dataSource.setServerName(DB_HOST);
        dataSource.setPort(Integer.parseInt(DB_PORT));
        dataSource.setUser(DB_USERNAME);
        dataSource.setPassword(DB_PASSWORD);
        dataSource.setDatabaseName(DB_NAME);

        try {
            dataSource.setServerTimezone("Europe/Warsaw");
            dataSource.setUseSSL(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            Connection connection = dataSource.getConnection();
            System.out.println("Connected");


            try (PreparedStatement statement = connection.prepareStatement(CREATE_TABLE_QUERY)) {
                statement.execute();
            }

            Scanner scanner = new Scanner(System.in);
            String commend;
            do {
                System.out.println("What do you want to do?");
                System.out.println("1. add");
                System.out.println("2. delete");
                System.out.println("3. list");
                System.out.println("4. get by id");
                System.out.println("quit");
                commend = scanner.nextLine();
                if ((commend.equalsIgnoreCase("add")) || (commend.equals("1"))) {
                    System.out.println("Name:");
                    String name = scanner.nextLine();
                    System.out.println("Age:");
                    int age = Integer.parseInt(scanner.nextLine());
                    System.out.println("Average:");
                    double average = Double.parseDouble(scanner.nextLine());
                    System.out.println("Is alive? False/True");
                    boolean alive = Boolean.parseBoolean(scanner.nextLine());

                    Student student = new Student(null, name, age, average, alive);
                    insertStudent(connection, student);
                } else if ((commend.equalsIgnoreCase("delete")) || (commend.equals("2"))) {
                    System.out.println("Write the number to delete:");
                    Long studentId = Long.parseLong(scanner.nextLine());

                    deleteStudent(connection, studentId);
                } else if ((commend.equalsIgnoreCase("list")) || (commend.equals("3"))) {

                    listAllStudents(connection);
                } else if ((commend.equalsIgnoreCase("get by id")) || (commend.equals("4"))) {
                    System.out.println("Write the student's id list:");
                    Long searchId = Long.parseLong(scanner.nextLine());

                    getByIdStudent(connection, searchId);

                }
            } while ((!commend.equalsIgnoreCase("quit")) && (!commend.equalsIgnoreCase("q")));

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /**
     * List student by student's id, if it is in the database
     *
     * @param connection
     * @param searchId
     * @throws SQLException
     */
    private static void getByIdStudent(Connection connection, Long searchId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SELECT_BY_ID_QUERY)) {
            statement.setLong(1, searchId);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                Student student = new Student();

                student.setId(resultSet.getLong(1));
                student.setName(resultSet.getString(2));
                student.setAge(resultSet.getInt(3));
                student.setAverage(resultSet.getDouble(4));
                student.setAlive(resultSet.getBoolean(5));

                System.out.println(student);
            } else {
                System.out.println("Not found student by id = " + searchId);
            }
        }
    }

    /**
     * List all students from database
     *
     * @param connection
     * @throws SQLException
     */
    private static void listAllStudents(Connection connection) throws SQLException {
        List<Student> studentList = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(SELECT_ALL_QUERY)) {
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Student student = new Student();

                student.setId(resultSet.getLong(1));
                student.setName(resultSet.getString(2));
                student.setAge(resultSet.getInt(3));
                student.setAverage(resultSet.getDouble(4));
                student.setAlive(resultSet.getBoolean(5));

                studentList.add(student);
            }
        }
        for (Student student : studentList) {
            System.out.println(student);
        }
    }

    /**
     * Delete Student by id number from database
     *
     * @param connection
     * @param studentId
     * @throws SQLException
     */
    private static void deleteStudent(Connection connection, Long studentId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(DELETE_QUERY)) {
            statement.setLong(1, studentId);
            statement.execute();
        }
    }

    /**
     * Insert new student to database
     *
     * @param connection
     * @param student
     * @throws SQLException
     */
    private static void insertStudent(Connection connection, Student student) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(INSERT_QUERY)) {
            statement.setString(1, student.getName());
            statement.setInt(2, student.getAge());
            statement.setDouble(3, student.getAverage());
            statement.setBoolean(4, student.isAlive());

            boolean success = statement.execute();

            if (success) {
                System.out.println("SUKCES!");
            }
        }
    }
}

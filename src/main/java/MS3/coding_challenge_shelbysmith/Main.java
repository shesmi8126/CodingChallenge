/*
 *  Shelby Smith
 *  05/24/2020
 *  Coding Challenge
 */
package MS3.coding_challenge_shelbysmith;

import static MS3.coding_challenge_shelbysmith.Main.RowLengthValidator;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriterBuilder;
import com.opencsv.ICSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import com.opencsv.validators.RowFunctionValidator;
import com.opencsv.validators.RowValidator;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Main {

    // method that accepts String[] row and returns Boolean. True = good
    public static boolean RowLengthValidator(String[] row) {
        return row.length == 10;
    }

    public static Connection connect(String filename) {

        // SQLite connection string  
        String url = "jdbc:sqlite:" + filename;
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    static void GetDataFromCSV(String filename, List<String[]> goodValues, List<String[]> badValues) {
        try {
            Function<String[], Boolean> RowLengthValidatorFunc = (row) -> RowLengthValidator(row);
            // Make validator, pass method made above
            RowValidator rowValidator = new RowFunctionValidator(RowLengthValidatorFunc, "Bad Information");
            //  Make Builder use validator
            CSVReaderBuilder builder = new CSVReaderBuilder(new FileReader(filename + ".csv"));
            CSVReader csvReader = builder.withRowValidator(rowValidator).build();

            csvReader.readNext();
            String[] row = new String[0];
            while (row != null) {
                try {
                    row = csvReader.readNext();
                    if (row == null) {
                        break;
                    }
                    // GOOD ROW
                    goodValues.add(row);
                } catch (CsvValidationException csvValidationException) {
                    // BAD ROW
                    row = csvReader.readNextSilently();
                    badValues.add(row);
                }
            }

        } catch (IOException | CsvValidationException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    static void MakeBadCSV(String filename, List<String[]> badValues) {
        try {
            // Output Bad CSV
            Writer writer = new FileWriter(filename + "-bad.csv");
            ICSVWriter csvWriter = new CSVWriterBuilder(writer).build();
            csvWriter.writeAll(badValues);
            csvWriter.close();
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    static void InsertGoodCSV(String filename, List<String[]> goodValues) {
        // Add Good to DB
        String sql = "INSERT INTO ValidData (A, B, C, D, E, F, G, H, I, J) VALUES(?,?,?,?,?,?,?,?,?,?)";
        try {
            Connection conn = connect(filename + ".db");
            PreparedStatement pstmt = conn.prepareStatement(sql);

            for (int i = 0; i < goodValues.size(); i++) {
                String[] row = goodValues.get(i);
                for (int j = 0; j < row.length; j++) {
                    String column = row[j];
                    pstmt.setString(j + 1, column);
                }
                pstmt.addBatch();
            }

            // execute the batch
            pstmt.executeBatch();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    static void CreateLog(String filename, List<String[]> goodValues, List<String[]> badValues) {
        // Output Stats to Log
        Writer writer;

        try {
            writer = new FileWriter(filename + ".log");
            int total = goodValues.size() + badValues.size();

            writer.write("# of records recieved: " + total + "\n");
            writer.write("# of records successful: " + goodValues.size() + "\n");
            writer.write("# of records failed: " + badValues.size() + "\n");

            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void main(String[] args) {
        String filename = args[0];
        List<String[]> goodValues = new ArrayList<>();
        List<String[]> badValues = new ArrayList<>();
       

        GetDataFromCSV(filename, goodValues, badValues);

        MakeBadCSV(filename, badValues);

        InsertGoodCSV(filename, goodValues);
        
        CreateLog(filename, goodValues, badValues);

    }
}

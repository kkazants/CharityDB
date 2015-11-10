package charitydb;

/**
 * Konstantin Kazantsev Updating Charity Database
 * Lucas Clarke Lock/Unlock Tables
 * Zack Wiseman
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;

public class CharityDB {

    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://montreat.cs.unca.edu:3306/charitydb";

    //  Database credentials
    static final String USER = "CSCI343";
    static final String PASS = "DBMS9154";

    // Set up Input Stream Reader and Burreded it to read
    static final InputStreamReader inputStreamReader = new InputStreamReader(System.in);
    static BufferedReader read = new BufferedReader(inputStreamReader);
    
    static Connection conn = null;
    static Statement stmt = null;
    static String sql;
    static ResultSet rs;
    static String lock;
    static String unlock = "UNLOCK TABLES;";
    

    /**
     *
     * @throws IOException
     * @throws SQLException
     */
    private static void whatToDo() throws IOException, SQLException {
        System.out.println("What would you like to do? \nEnter: 'add donor', 'add company', or 'add donation' without the quotes.");
        String doing = read.readLine();
        if ("add donor".equals(doing)) {
            donorInfo();
        } else if ("add company".equals(doing)) {
            companyInfo();
        } else if ("add donation".equals(doing)) {
            addDonation();
        } else if ("done".equals(doing)) {
            System.out.println("\nDone. Have a nice day!");
            System.exit(0);
        } else {
            // Unknown command or misspelled
            System.out.println("\nERROR: Unknown command or command misspelled. Try again.");
            System.out.println(doing);
            whatToDo();
        }
    } // whatToDo()

    /**
     *
     * @throws IOException
     * @throws SQLException
     */
    private static void donorInfo() throws IOException, SQLException {
        String[] donor = new String[6];
        System.out.print("Enter last name: ");
        donor[0] = read.readLine();
        System.out.print("Enter first name: ");
        donor[1] = read.readLine();
        System.out.print("Enter address: ");
        donor[2] = read.readLine();
        System.out.print("City: ");
        donor[3] = read.readLine();
        System.out.print("State: ");
        donor[4] = read.readLine();
        System.out.print("Zip: ");
        donor[5] = read.readLine();
        // try to add donor
        executeDonor(donor);
        // ask if want to add another donor
        System.out.println("\nDo you want to add another donor?"
                + "\nEnter: Y or N");
        String yn = read.readLine();
        if ("Y".equals(yn)) {
            donorInfo();
        } else if ("N".equals(yn)) {
            System.out.println("\nWhat to do now?: 'done' 'add company' 'add donation'");
            String whatNext = read.readLine();
            if ("done".equals(whatNext)) {
                System.out.println("\nDone. Have a nice day!");
                System.exit(0);
            } else if ("add company".equals(whatNext)) {
                companyInfo();
            } else if ("add donation".equals(whatNext)) {
                addDonation();
            } else {
                System.out.println("\nUnkown command. Try again\n");
                whatToDo();
            }
        } else {
            System.out.println("\nUnkown command. Try again\n");
            whatToDo();
        }
    } // donorInfo()

    /**
     *
     * @param donor
     * @throws SQLException
     */
    static private void executeDonor(String[] donor) throws SQLException {
        // get info from donor
        String lastName = donor[0];
        String firstName = donor[1];
        String address = donor[2];
        String city = donor[3];
        String state = donor[4];
        int zip = Integer.parseInt(donor[5]);
        try {
            //Register JDBC driver
            Class.forName("com.mysql.jdbc.Driver");
            //Open a connection
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            stmt = conn.createStatement();
            // lock table
            lock = "LOCK TABLE donors WRITE;";
            stmt.executeUpdate(lock);
            conn.setAutoCommit(false);
            //check if donor table has lastName and firstName of donor
            sql = "SELECT lastName, firstName FROM donors WHERE lastName = '" + lastName + "' AND firstName = '" + firstName + "';";
            rs = stmt.executeQuery(sql);
            // if donor is in the table
            if (rs.next()) {
                System.out.println("Donor already in the table.");
                //stmt.executeUpdate(unlock);
            } // if not, then add donor to table
            else {
                System.out.println("Adding donor to table");
                int donorID = 1; // set donorID to start from 1
                // check for free donorID
                sql = "SELECT donorID FROM donors WHERE donorID = " + donorID + ";";
                rs = stmt.executeQuery(sql);
                // while donorID exists
                while (rs.next() == true) {
                    // incriment donorID by 1 and try again
                    donorID += 1;
                    sql = "SELECT donorID FROM donors WHERE donorID = " + donorID + ";";
                    rs = stmt.executeQuery(sql);
                }
                // add donor to table
                sql = "INSERT INTO donors VALUES (" + donorID + ", '" + lastName + "', '" + firstName + "', '" + address + "', '" + city + "', '" + state + "', " + zip + ");"; 
                stmt.executeUpdate(sql);
                // unlock the tables
                stmt.executeUpdate(unlock);
                conn.commit();
            }
            //Clean-up environment
            rs.close();
            stmt.close();
            conn.close();
            
        } catch (SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
            // in case of exception, rollback the transaction
            conn.rollback();
            stmt.executeUpdate(unlock);
        } catch (Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
        } finally {
            //finally block used to close resources
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException se2) {
            }// nothing we can do
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }//end finally try
        }//end try
    }// executeDonor()

    /**
     *
     * @throws IOException
     * @throws SQLException
     */
    private static void companyInfo() throws IOException, SQLException {
        // ask for company information
        String[] company = new String[8];
        System.out.print("Name of company: ");
        company[0] = read.readLine();
        System.out.print("Address: ");
        company[1] = read.readLine();
        System.out.print("City: ");
        company[2] = read.readLine();
        System.out.print("State: ");
        company[3] = read.readLine();
        System.out.print("Zip: ");
        company[4] = read.readLine();
        System.out.print("Match Percent (0%-200%): ");
        company[5] = read.readLine();
        System.out.print("Min Match (minimum $0): $");
        company[6] = read.readLine();
        System.out.print("Max Match (minimum $1): $");
        company[7] = read.readLine();
        // try to add the company
        executeCompany(company);
        // ask if want to add another company
        System.out.println("\nDo you want to add another company?"
                + "\nEnter: Y or N");
        String yn = read.readLine();
        if ("Y".equals(yn)) {
            companyInfo();
        } else if ("N".equals(yn)) {
            System.out.println("\nWhat to do now?: 'done' 'add company' 'add donation'");
            String whatNext = read.readLine();
            if ("done".equals(whatNext)) {
                System.out.println("\nDone. Have a nice day!");
                System.exit(0);
            } else if ("add donor".equals(whatNext)) {
                donorInfo();
            } else if ("add donation".equals(whatNext)) {
                addDonation();
            } else {
                System.out.println("\nUnkown command. Try again\n");
                whatToDo();
            }
        } else {
            System.out.println("\nUnkown command. Try again\n");
            whatToDo();
        }
    }// companyInfo()

    /**
     *
     * @param company
     * @throws SQLException
     */
    private static void executeCompany(String[] company) throws SQLException {
        // extract the information
        String name = company[0];
        String address = company[1];
        String city = company[2];
        String state = company[3];
        int zip = Integer.parseInt(company[4]);
        double matchPercent = Double.parseDouble(company[5]);
        double minMatch = Double.parseDouble(company[6]);
        double maxMatch = Double.parseDouble(company[7]);
        try {
            //Register JDBC driver
            Class.forName("com.mysql.jdbc.Driver");
            //Open a connection
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            // Set auto-commit to false
            conn.setAutoCommit(false);
            stmt = conn.createStatement();
            // check if 'matchingCompanies' table has company 'name'
            sql = "SELECT name FROM matchingCompanies WHERE name = '" + name + "';";
            rs = stmt.executeQuery(sql);
            // if company is already in table, do nothing.
            if (rs.next()) {
                System.out.println("Company already in the Table");
            } else {
                System.out.println("Adding company to table...");
                // initilize companyID
                int companyID = 0;
                // check for free companyID
                sql = "SELECT companyID FROM matchingCompanies WHERE companyID = '" + companyID + "';";
                rs = stmt.executeQuery(sql);
                while (rs.next() == true) {
                    // incriment companyID by 1 and look again
                    companyID += 1;
                    sql = "SELECT companyID FROM matchingCompanies WHERE companyID = '" + companyID + "';";
                    rs = stmt.executeQuery(sql);
                }
                System.out.println(companyID);
                // add company to table
                lock = "LOCK TABLE mathingCompanies WRITE;";
                sql = "INSERT INTO matchingCompanies VALUES (" + companyID + ", '" + name + "', '" + address + "', '" + city + "', '" + state + "', " + zip + ", " + matchPercent + ", " + minMatch + ", " + maxMatch + ");";
                stmt.executeUpdate(lock);
                stmt.executeUpdate(sql);
                stmt.executeUpdate(unlock);
                conn.commit();
            }
            //Clean-up environment
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
            // in case of exception, rollback the transaction
            conn.rollback();
        } catch (Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
        } finally {
            //finally block used to close resources
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException se2) {
            }// nothing we can do
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }//end finally try
        }//end try
    }// executeCompany()

    /**
     *
     * @throws IOException
     * @throws SQLException
     */
    private static void addDonation() throws IOException, SQLException {
        System.out.print("Last name of donor: ");
        String lastName = read.readLine();
        System.out.print("First name of donor: ");
        String firstName = read.readLine();
        System.out.print("Name of matching company: ");
        String company = read.readLine();
        System.out.print("Amount donated (amount over $0): $");
        String donated = read.readLine();
        // get donorID
        try {
            //Register JDBC driver
            Class.forName("com.mysql.jdbc.Driver");
            //Open a connection
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            // Set auto-commit to false
            conn.setAutoCommit(false);
            stmt = conn.createStatement();
            // get donorID
            sql = "SELECT donorID FROM donors WHERE lastName = '" + lastName + "' AND firstName = '" + firstName + "';";
            rs = stmt.executeQuery(sql);
            String donorID = "";
            while (rs.next()) {
                donorID = rs.getString("donorID");
            }
            // make sure donorID is not empty string
            if ("".equals(donorID)) {
                System.out.println("Donor does not exist or "
                        + "misspelled donors name");
                System.exit(1);
            }
            // get companyID
            sql = "SELECT companyID FROM matchingCompanies WHERE name = '" + company + "';";
            rs = stmt.executeQuery(sql);
            String companyID = "";
            while (rs.next()) {
                companyID = rs.getString("companyID");
            }
            // make sure companyID is not empty string
            if ("".equals(companyID)) {
                System.out.println("Company does not exist or "
                        + "misspelled companies name");
                System.exit(1);
            }
        // block table

            // check for unused 'donationNumber'
            int donationNumber = 1;
            sql = "SELECT donationNumber FROM donations WHERE donationNumber = " + donationNumber + ";";
            rs = stmt.executeQuery(sql);
            while (rs.next() == true) {
                donationNumber += 1;
                sql = "SELECT donationNumber FROM donations WHERE donationNumber = " + donationNumber + ";";
                rs = stmt.executeQuery(sql);
            }
            // add recod to donation table
            lock = "LOCK TABLE donations WRITE;";
            System.out.println("Adding donation record...");
            sql = "INSERT INTO donations VALUES (" + donationNumber + ", " + donorID + ", " + companyID + ", " + donated + ");";
            stmt.executeUpdate(lock);
            stmt.executeUpdate(sql);
            stmt.executeUpdate(unlock);
            conn.commit();
            //Clean-up environment
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
            // in case of exception, rollback the transaction
            conn.rollback();
        } catch (Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
        } finally {
            //finally block used to close resources
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException se2) {
            }// nothing we can do
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }//end finally try
        }//end try
        whatToDo();// what to do next
    }// addDonation()

    /**
     *
     * @param args
     * @throws SQLException
     * @throws IOException
     */
    public static void main(String[] args) throws IOException, SQLException {
        // start cycle
        
        whatToDo();
        stmt.executeUpdate(unlock);
    }//end main 
}//end CharityDB

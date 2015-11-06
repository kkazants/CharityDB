package charitydb;

/**
 * Konstantin Kazantsev
 * Charity Reports assignment
 * Question #3
 * @author Marietta E. Cameron
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

    /**
     * 
     * @throws IOException
     * @throws SQLException 
     */
    private static void whatToDo() throws IOException, SQLException{
        System.out.println("What would you like to do? \nEnter: 'add donor', 'add company', or 'add donation' without the quotes.");
        String doing = read.readLine();
        if ("add donor".equals(doing)){
            //add donor
            donorInfo();
        }
        else if ("add company".equals(doing)){
            // add company
            System.out.println("Still working on " + doing);
        }
        else if ("add donation".equals("Still working on " + doing)){
            // add donation
            System.out.println(doing);
        }
        else if ("done".equals(doing)){
            System.out.println("\nDone. Have a nice day!");
            System.exit(1);
        }
        else {
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
        if ("Y".equals(yn)){
            donorInfo();
        }
        else if ("N".equals(yn)){
            System.out.println("\nWhat to do now?: 'done' 'add company' 'add donation'");
            String whatNext = read.readLine();
            if ("done".equals(whatNext)){
                System.out.println("\nDone. Have a nice day!");
                System.exit(1);
            }
            else if ("add company".equals(whatNext)){
                // call on "add company" method
            }
            else if ("add donation".equals(whatNext)){
                // call on "add donation" method
            }
            else {
                System.out.println("\nUnkown command. Try again\n");
                whatToDo();
            }
        }
        else {
            System.out.println("\nUnkown command. Try again\n");
            whatToDo();
        }
    } // donorInfo()
    
    /**
     * 
     * @param donor
     * @throws SQLException 
     */
    static private void executeDonor(String[] donor) throws SQLException{
        try {
            //STEP 2: Register JDBC driver
            Class.forName("com.mysql.jdbc.Driver");

            //STEP 3: Open a connection
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            // Set auto-commit to false
            conn.setAutoCommit(false);
            // get info from donor
            String lastName = donor[0];
            String firstName = donor[1];
            String address = donor[2];
            String city = donor[3];
            String state = donor[4];
            int zip = Integer.parseInt(donor[5]);
            
            // check if donor table has lastName and firstName of donor
            stmt = conn.createStatement();
            String sql;
            sql = "SELECT lastName, firstName FROM donors WHERE lastName = '" +lastName+"' AND firstName = '"+firstName+"';" ;
            ResultSet rs = stmt.executeQuery(sql);
            //System.out.println(rs);
            
            if (rs.next()){
                // donor is in table
                System.out.println("Donor already in the table.");
            }
            else{
                System.out.println("Adding donor to table");
                int donorID = 1; // set donorID to start from 1
                // check for free donorID
                sql = "SELECT donorID FROM donors WHERE donorID = " + donorID +";";
                rs = stmt.executeQuery(sql);
                // while donorID exists
                while (rs.next() == true){
                    // incriment donorID by 1 and try again
                    donorID += 1;
                    sql = "SELECT donorID FROM donors WHERE donorID = " + donorID +";";
                    rs = stmt.executeQuery(sql);
                }
                // add donor to table
                sql = "INSERT INTO donors VALUES ("+donorID+", '"+lastName+"', '"+firstName+"', '" +address+"', '" +city+"', '" +state+"', '" +zip+"');";
                stmt.executeUpdate(sql);
                conn.commit();
            }

            //STEP 6: Clean-up environment
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
    }// executeDonor()
    
    /**
     *
     * @param args
     * @throws SQLException
     * @throws IOException
     */
    public static void main(String[] args) throws SQLException, IOException {
        // start cycle
        whatToDo();
        
    }//end main
    
}//end CharityDB

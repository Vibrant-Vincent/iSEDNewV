package socket.jar;

import main.java.common.utils.EventLog;


import java.sql.*;
import java.util.logging.Level;

public class DataBaseUtility {
    private DatabaseConnectionLIS dbConn;
    private Connection conn = null;
    private PreparedStatement st = null;
    private String error_message = "";
//    private EventLog eventLog = new EventLog(this.getClass().getName());
    private ResultSet records = null;

    public void initialize(String sql_statement, String serverName, boolean is_generated_keys) {
        try {
            if (serverName != null) {
                this.dbConn = new DatabaseConnectionLIS(serverName);
//                System.out.println("--------17 dbConn--------" + dbConn);
            } else {
                this.dbConn = new DatabaseConnectionLIS("HP");
            }
            this.conn = this.dbConn.getConnection();

//            System.out.println("----------18 conn----------" + this.conn);

            if (is_generated_keys) {
                this.st = this.conn.prepareStatement(sql_statement, 1);
            } else {
                this.st = this.conn.prepareStatement(sql_statement);
            }
        } catch (SQLException var5) {
            this.error_message = "Error in Initializing " + this.getClass().getName() + " class!";
//            this.eventLog.errorLog(Level.SEVERE, this.error_message, var5);
//            this.close(this);
        }
        if (serverName != null) {
            this.dbConn = new DatabaseConnectionLIS(serverName);
        } else {
            this.dbConn = new DatabaseConnectionLIS("HP");
        }
        System.out.println("-------------->>> Database has connected <<<--------------" + this.dbConn);
        this.conn = this.dbConn.getConnection();
    }
    public DataBaseUtility(String sql_statement, String serverName) {
        this.initialize(sql_statement, serverName, false);
    }
    /**
     * Change the SQL statement from current to new one, for executing a new database operation.
     *
     * @param sql_statement
     *          the new SQL statement to be appended
     */
    public void changeSQL(final String sql_statement) {
        changeSQL(sql_statement, false);
    }
    /**
     * Change the SQL statement from current to new one, for executing a new database operation.
     *
     * @param sql_statement
     *          the new SQL statement to be appended
     * @param is_generated_keys
     *          If the unique key generated needs to be returned.
     */
    public void changeSQL(final String sql_statement, final boolean is_generated_keys) {
        try {
            if (is_generated_keys) {
                st = conn.prepareStatement(sql_statement, Statement.RETURN_GENERATED_KEYS);
            } else {
//                System.out.println("-------15-------" + sql_statement);
//                System.out.println("-------16-------" + conn);
                st = conn.prepareStatement(sql_statement);
            }
        } catch (final SQLException e) {
            error_message = "Error in changing the SQL statement!";
//            eventLog.errorLog(Level.SEVERE, error_message, e);
            // System.out.println(error_message);
            // e.printStackTrace();
//            close(this); // close the connections, used instead of finally
        }
    }

    /**
     * Generate {@link ResultSet} of records using {@link PreparedStatement#executeQuery()}
     *
     */
    public boolean generateRecords() {
        if ((st == null) || (conn == null))
            return false;
        // Get the records
        try {
            records = st.executeQuery();
        } catch (final SQLException e) {
            error_message = "Error in generating Records!";
//            eventLog.errorLog(Level.SEVERE, error_message, e);
//            close(this); // close the connections, used instead of finally
            return false;
        }
        return true;
    }


    /**
     * Sets the designated parameter to the given Java {@link Integer} value, using
     * {@link PreparedStatement#setInt}
     *
     * @param parameterIndex
     *          parameterIndex - the first parameter is 1, the second is 2, ...
     * @param value
     *          the parameter value
     */
    public void setInt(final int parameterIndex, final int value) {
        try {
            st.setInt(parameterIndex, value);
        } catch (final SQLException e) {
            error_message = "Error in settng value for the parameter " + parameterIndex
                    + ", with value = " + value;
//            eventLog.errorLog(Level.SEVERE, error_message, e);
            // System.out.println(error_message);
            // e.printStackTrace();
//            close(this);
        }
    }
    /**
     * Sets the designated parameter to the given Java String value, using
     * {@link PreparedStatement#setString}
     *
     * @param parameterIndex
     *          parameterIndex - the first parameter is 1, the second is 2, ...
     * @param value
     *          the parameter value
     */
    public void setString(final int parameterIndex, final String value) {
        try {
            st.setString(parameterIndex, value);
        } catch (final SQLException e) {
            error_message = "Error in settng value for the parameter " + parameterIndex
                    + ", with value = " + value;
//            eventLog.errorLog(Level.SEVERE, error_message, e);
            // System.out.println(error_message);
            // e.printStackTrace();
//            close(this);
        }
    }

    /**
     * Execute the given SQL statement using {@link PreparedStatement#executeUpdate()} must be an
     * INSERT, UPDATE or DELETE statement <br/>
     *
     * <b> Note:</b> Use executeUpdate > the no of rows expected to be updated
     *
//     * @param sql_statement
     *          The SQL statement to be executed.
     * @return either (1) the row count for SQL Data Manipulation Language (DML) statements or (2) 0
     *         for SQL statements that return nothing.
     */
    public int executeUpdate() {
        try {
            return st.executeUpdate();
        } catch (final SQLException e) {
            error_message = "Error in executing update statement!";
//            eventLog.errorLog(Level.SEVERE, error_message, e);
            // System.out.println(error_message);
            // e.printStackTrace();
//            close(this); // close the connections,
            return -1;
        }
    }
    /**
     * Get the result of executing the query
     *
     * @return {@link ResultSet} of the query results.
     */
    public ResultSet getRecords() {
        return records;
    }

}

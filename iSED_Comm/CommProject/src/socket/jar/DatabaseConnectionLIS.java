package socket.jar;


import java.sql.*;
import java.util.Properties;
import java.util.concurrent.locks.Lock;
import java.util.logging.Level;
import main.java.common.utils.EventLog;

public class DatabaseConnectionLIS {
    private Connection connection;
    private final String mySqlDriver = "com.mysql.jdbc.Driver";
    private String mySqlServerName;
    private String mySqlDefaultServerName;
    private String serverIP;
    private String mySqlUsername;
    private String mySqlPassword;
    public String DATABASE_IP_ADDRESS;
    private Properties prop = new Properties();
//    private EventLog eventLog = new EventLog(EventLog.class.getName());
    private String error_message = "";
    private String mySqlUrl;

    private void setDatabaseConnectionParams(String databaseName) {
//        System.out.println("--------2------->" + databaseName);
//        System.out.println("--------3------->" + databaseName.hashCode());

        byte var3 = -1;
        switch(databaseName.hashCode()) {
            case -1939035992:
                if (databaseName.equals("ROCHE_COMM")) {
                    var3 = 7;
                }
                break;
            case -1852497085:
                if (databaseName.equals("SERVER")) {
                    var3 = 0;
                }
                break;
            case -1214377721:
                if (databaseName.equals("ESR_COMM")) {
                    var3 = 11;
                }
                break;
            case 2312:
                if (databaseName.equals("HP")) {
                    var3 = 3;
                }
                break;
            case 2737:
                if (databaseName.equals("VG")) {
                    var3 = 4;
                }
                break;
            case 75117:
                if (databaseName.equals("LAB")) {
                    var3 = 2;
                }
                break;
            case 83377:
                if (databaseName.equals("TSP")) {
                    var3 = 5;
                }
                break;
            case 2571410:
                if (databaseName.equals("TEST")) {
                    var3 = 6;
                }
                break;
            case 54267224:
                if (databaseName.equals("PHADIA_COMM")) {
                    var3 = 8;
                }
                break;
            case 72607563:
                if (databaseName.equals("LOCAL")) {
                    var3 = 1;
                }
                break;
            case 683463768:
                if (databaseName.equals("DIAZYME_COMM")) {
                    var3 = 9;
                }
                break;
            case 1647023064:
                if (databaseName.equals("SYSMEX_COMM")) {
                    var3 = 10;
                }
        }

        switch(var3) {
            case 0:
                this.mySqlUsername = "cleanroom";
                this.mySqlPassword = "VibIntCleanroom_1649";

                this.serverIP = "192.168.10.153";
                this.DATABASE_IP_ADDRESS = this.serverIP.isEmpty() ? "localhost" : this.serverIP;
//                System.out.println("------------------>" +  this.DATABASE_IP_ADDRESS);
                this.mySqlDefaultServerName = "";
                break;

                //>>>>>>>>>>>>>>>>>changes here !!!!<<<<<<<<<<<<<<<<<<<
            case 1:
                this.mySqlUsername = "root";
                this.mySqlPassword = "root";
                this.serverIP = "localhost";
                this.DATABASE_IP_ADDRESS = this.serverIP.isEmpty() ? "localhost" : this.serverIP;
//                System.out.println("---------4---------->" + this.DATABASE_IP_ADDRESS);
                this.mySqlDefaultServerName = "";
                break;


            case 2:
                this.mySqlUsername = "cleanroom";
                this.mySqlPassword = "VibIntCleanroom_1649";
                this.serverIP = "192.168.10.101";
                this.DATABASE_IP_ADDRESS = this.serverIP.isEmpty() ? "localhost" : this.serverIP;
                this.mySqlDefaultServerName = "";
                break;
            case 3:
                this.mySqlUsername = "cleanroom";
                this.mySqlPassword = "VibIntCleanroom_1649";
                this.serverIP = "192.168.10.101";
                this.DATABASE_IP_ADDRESS = this.serverIP.isEmpty() ? "localhost" : this.serverIP;
                this.mySqlDefaultServerName = "";
                break;
            case 4:
                this.mySqlUsername = "VG";
                this.mySqlPassword = "vibrant@2015";
                this.serverIP = "192.168.10.187";
                this.DATABASE_IP_ADDRESS = this.serverIP.isEmpty() ? "localhost" : this.serverIP;
                this.mySqlDefaultServerName = "";
                break;
            case 5:
                this.mySqlUsername = "TSPI3";
                this.mySqlPassword = "000028";
                this.serverIP = "192.168.10.121";
                this.DATABASE_IP_ADDRESS = this.serverIP.isEmpty() ? "localhost" : this.serverIP;
                this.mySqlDefaultServerName = "";
                break;
            case 6:
                this.mySqlUsername = "cleanroom";
                this.mySqlPassword = "VibIntCleanroom_1649";
                this.serverIP = "192.168.10.169";
                this.DATABASE_IP_ADDRESS = this.serverIP.isEmpty() ? "localhost" : this.serverIP;
                this.mySqlDefaultServerName = "";
                break;
            case 7:
                this.mySqlUsername = "lis";
                this.mySqlPassword = "Vibrant@2016";
                this.serverIP = "192.168.10.150";
                this.DATABASE_IP_ADDRESS = this.serverIP.isEmpty() ? "localhost" : this.serverIP;
                this.mySqlDefaultServerName = "";
                break;
            case 8:
                this.mySqlUsername = "phadia";
                this.mySqlPassword = "000028";
                this.serverIP = "192.168.10.188";
                this.DATABASE_IP_ADDRESS = this.serverIP.isEmpty() ? "localhost" : this.serverIP;
                this.mySqlDefaultServerName = "";
                break;
            case 9:
                this.mySqlUsername = "diazyme";
                this.mySqlPassword = "000028";
                this.serverIP = "192.168.10.227";
                this.DATABASE_IP_ADDRESS = this.serverIP.isEmpty() ? "localhost" : this.serverIP;
                this.mySqlDefaultServerName = "";
                break;
            case 10:
                this.mySqlUsername = "sysmex";
                this.mySqlPassword = "000028";
                this.serverIP = "192.168.10.231";
                this.DATABASE_IP_ADDRESS = this.serverIP.isEmpty() ? "localhost" : this.serverIP;
                this.mySqlDefaultServerName = "";
                break;
            case 11:
                this.mySqlUsername = "esr";
                this.mySqlPassword = "000028";
                this.serverIP = "192.168.10.181";
                this.DATABASE_IP_ADDRESS = this.serverIP.isEmpty() ? "localhost" : this.serverIP;
                this.mySqlDefaultServerName = "";
        }

        this.mySqlUrl = "jdbc:mysql://" + this.DATABASE_IP_ADDRESS + ":3306/";
//        System.out.println("-------5-------->" + this.mySqlUrl);
    }

    private void setProperties() {
        this.prop.put("user", this.mySqlUsername);
        this.prop.put("password", this.mySqlPassword);
        this.prop.put("characterEncoding", "utf8");
    }

    public DatabaseConnectionLIS(String serverName) {
//        System.out.println("-------7-------> " + this.DATABASE_IP_ADDRESS);

        this.mySqlUrl = "jdbc:mysql://" + this.DATABASE_IP_ADDRESS + ":3306/";
//        System.out.println("-------1-------> " + this.mySqlUrl);
        this.setDatabaseConnectionParams(serverName);
//        System.out.println("-------8-------> " + this.mySqlUrl);
        this.mySqlServerName = this.mySqlDefaultServerName;
        this.startConnection();
    }

    private void startConnection() {
        this.setProperties();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
//            System.out.println("-------9-------> " + this.mySqlUrl);
//            System.out.println(   "-------10-------> " +this.mySqlUsername);
//            System.out.println(   "-------11-------> " +this.mySqlPassword);
            String temp = this.mySqlUrl + "commproject";
//            System.out.println("-------12-------> " + this.mySqlUrl + "commproject");
            this.connection = DriverManager.getConnection(temp, this.prop);//block
//            System.out.println("-------14-------> " );
//
//            Statement stmt = this.connection.createStatement();
//
//            ResultSet rs = stmt.executeQuery("select frame_content from receive_status_check");//选择import java.sql.ResultSet;
//            //如果对象中有数据，就会循环打印出来
//            while(rs.next()) System.out.println(rs.getString("frame_content"));

        } catch (ClassNotFoundException var2) {
            this.error_message = "Class not found error!";
//            this.eventLog.errorLog(Level.SEVERE, this.error_message, var2);
        } catch (Exception var3) {
            this.error_message = "Error in connecting to database!";
//            this.eventLog.errorLog(Level.SEVERE, this.error_message, var3);
        }

    }

    public Connection getConnection() {
        return this.connection;
    }

    public void close(Object... things) {
        if (this.connection != null) {
            try {
                this.connection.close();
            } catch (SQLException var10) {
                this.error_message = "Error in closing Connection!";
//                this.eventLog.errorLog(Level.SEVERE, this.error_message, var10);
            }

            this.connection = null;
        }

        Object[] var2 = things;
        int var3 = things.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            Object thing = var2[var4];
            if (null != thing) {
                try {
                    if (thing instanceof Connection) {
                        try {
                            ((Connection)thing).close();
                        } catch (SQLException var7) {
                        }
                    }

                    if (thing instanceof Lock) {
                        try {
                            ((Lock)thing).unlock();
                        } catch (IllegalMonitorStateException var8) {
                        }
                    }
                } catch (RuntimeException var9) {
                }
            }
        }

    }
}

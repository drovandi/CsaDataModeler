
package et.csa;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Example of connection to a MySQL database
 * 
 * @author Istat Cooperation Unit
 */
public class DbTest {
    
    private static final Logger LOGGER = Logger.getLogger(DbTest.class.getName());
    
    public static void main(String[] agrs) {
        /*
        CREATE USER 'guido'@'localhost' IDENTIFIED BY 'guido';
        GRANT ALL PRIVILEGES ON *.* TO 'guido'@'localhost' WITH GRANT OPTION;
        CREATE USER 'guido'@'%' IDENTIFIED BY 'guido';
        GRANT ALL PRIVILEGES ON *.* TO 'guido'@'%' WITH GRANT OPTION;
        */
        Properties prop = new Properties();
        try (InputStream in = SchemaEngine.class.getResourceAsStream("database.properties")) {
            prop.load(in);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Cannot read properties file", ex);
            return;
        }
        Connection conn = null;
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            conn = DriverManager.getConnection(
                    prop.getProperty("db.uri")+"/"+prop.getProperty("db.schema")+"?autoReconnect=true&useSSL=false",
                    prop.getProperty("db.username"),
                    prop.getProperty("db.password"));
            conn.setAutoCommit(false);
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("insert into ethiopian_census.HOUSEHOLD_QUEST (ID101,ID102,ID103,ID104,ID105,ID106,ID107,ID108,ID109,ID202,ID203) values (null,null,null,null,null,null,null,null,null,null,null)");
            ResultSet result = stmt.executeQuery("select ID from HOUSEHOLD_QUEST where ID101 is null and ID102 is null and ID103 is null and ID104 is null and ID105 is null and ID106 is null and ID107 is null and ID108 is null and ID109 is null and ID202 is null and ID203 is null");
            result.next();
            int id = result.getInt("ID")+1;
            if (result.next()) {
                throw new Exception("More idItems with the same values");
            }
            stmt.execute("insert into ethiopian_census.INDIVIDUAL (P301,P302,P303,P304A,P304B,P305,P306,P307,P308,P309,P310,P311,P312,P313,P314,P315A,P315B,P315C,P315D,P315E,P315F,P315G,P316,P317,P318,P319R,P319Z,P320,P321R,P321Z,P322,P323,P324,P325,P401,P402,P403,P404,P405,P406,P501,P502,P503,P504,P505,P506,P507,P601_MALE,P601_FEMALE,P601_TOTAL,P602_MALE,P602_FEMALE,P603_MALE,P603_FEMALE,P604_MALE,P604_FEMALE,P605,P606,P607_DATE,P607_DD,P607_MM,P607_YYYY,P608,P609,P610,P611,COUNTER,HOUSEHOLD_QUEST) values (null,\"null\",null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,1,"+id+")");
            conn.commit();
        } catch (Exception ex) {
            try {
                if (conn!=null)
                    conn.rollback();
            } catch (SQLException ex1) {
                LOGGER.log(Level.SEVERE, "Rollback failure", ex1);
            }
            LOGGER.log(Level.SEVERE, "Database exception", ex);
        } finally {
            try {
                if (conn!=null) conn.close();
            } catch (SQLException ex) {
                LOGGER.log(Level.WARNING, "Impossible to close the db conenction", ex);
            }
        }
    }
    
}

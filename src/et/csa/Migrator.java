
package et.csa;

import et.csa.bean.Dictionary;
import et.csa.bean.Record;
import et.csa.reader.DictionaryReader;
import et.csa.reader.QuestionnaireParser;
import et.csa.writer.InsertCreator;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Migrator {
    
    private static final Logger LOGGER = Logger.getLogger(Migrator.class.getName());
    
    public static void main(String[] args) {
        Dictionary dictionary;
        try {
            dictionary = DictionaryReader.read("ipums","IPUMS.dcf");
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Impossible to read dictionary file", ex);
            return;
        }
        Properties prop = new Properties();
        try (InputStream in = SchemaEngine.class.getResourceAsStream("database.properties")) {
            prop.load(in);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Cannot read properties file", ex);
            return;
        }
        Connection connSrc = null;
        Connection connDst = null;
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            connSrc = DriverManager.getConnection(
                    prop.getProperty("db.source.uri")+"/"+prop.getProperty("db.source.schema")+"?autoReconnect=true&useSSL=false",
                    prop.getProperty("db.source.username"),
                    prop.getProperty("db.source.password"));
            connSrc.setReadOnly(true);
            connDst = DriverManager.getConnection(
                    prop.getProperty("db.dest.uri")+"/"+prop.getProperty("db.dest.schema")+"?autoReconnect=true&useSSL=false",
                    prop.getProperty("db.dest.username"),
                    prop.getProperty("db.dest.password"));
            connDst.setAutoCommit(false);
            
            Statement stmtSrc = connSrc.createStatement();
            Statement stmtDst = connDst.createStatement();
            ResultSet result = stmtSrc.executeQuery("select questionnaire from ipums.ipums_data_dict limit 100");
            while (result.next()) {
                String questionnaire = result.getString(1);
                Map<Record, List<List<String>>> descr = QuestionnaireParser.parse(dictionary, questionnaire);
                InsertCreator.create(dictionary, descr, stmtDst);
            }
            connDst.commit();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | SQLException ex) {
            try {
                if (connDst!=null) connDst.rollback();
            } catch (SQLException ex1) {
                LOGGER.log(Level.SEVERE, "Rollback failure", ex1);
            }
            LOGGER.log(Level.SEVERE, "Database exception", ex);
        } finally {
            try {
                if (connSrc!=null) connSrc.close();
            } catch (SQLException ex) {
                LOGGER.log(Level.WARNING, "Impossible to close the db conenction", ex);
            }
            try {
                if (connDst!=null) connDst.close();
            } catch (SQLException ex) {
                LOGGER.log(Level.WARNING, "Impossible to close the db conenction", ex);
            }
        }
    }
    
}

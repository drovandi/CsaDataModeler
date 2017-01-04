
package et.csa;

import et.csa.bean.Dictionary;
import et.csa.bean.Record;
import et.csa.reader.DictionaryReader;
import et.csa.reader.QuestionnaireReader;
import et.csa.writer.InsertWriter;
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

public class LoaderEngine {
    
    private static final Logger LOGGER = Logger.getLogger(LoaderEngine.class.getName());
    
    public static void main(String[] args) {
        
    	Dictionary dictionary;
        Properties prop = new Properties();
        
        //Load property file
        try (InputStream in = SchemaEngine.class.getResourceAsStream("/database.properties")) {
            prop.load(in);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Cannot read properties file", ex);
            return;
        }
        
        //Parse dictionary file
        try {
            dictionary = DictionaryReader.read(
                    prop.getProperty("dictionary.filename"),
                    prop.getProperty("db.dest.table.prefix"));
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Impossible to read dictionary file", ex);
            return;
        }
        
        Connection connSrc = null;
        Connection connDst = null;
        
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            String srcSchema = prop.getProperty("db.source.schema");
            String srcDataTable = prop.getProperty("db.source.data.table");
            
            //Connect to the source database
            connSrc = DriverManager.getConnection(
                    prop.getProperty("db.source.uri")+"/"+srcSchema+"?autoReconnect=true&useSSL=false",
                    prop.getProperty("db.source.username"),
                    prop.getProperty("db.source.password"));
            connSrc.setReadOnly(true);
            
            //Connect to the destination database
            connDst = DriverManager.getConnection(
                    prop.getProperty("db.dest.uri")+"/"+prop.getProperty("db.dest.schema")+"?autoReconnect=true&useSSL=false",
                    prop.getProperty("db.dest.username"),
                    prop.getProperty("db.dest.password"));
            connDst.setAutoCommit(false);
            
            Statement stmtSrc = connSrc.createStatement();
            Statement stmtDst = connDst.createStatement();
            
            //Get questionnaires from source database (CSPro plain text files)
            ResultSet result = stmtSrc.executeQuery("select questionnaire from "+srcSchema+"."+srcDataTable+" limit 100");
            while (result.next()) {
            	
                String questionnaire = result.getString(1);
                
                //Get the microdata parsing CSPro plain text files according to its dictionary
                Map<Record, List<List<String>>> microdata = QuestionnaireReader.parse(dictionary, questionnaire);
                
                //Generate the insert statements (to store microdata into the destination database)
                InsertWriter.create(prop.getProperty("db.dest.schema"), dictionary, microdata, stmtDst);
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

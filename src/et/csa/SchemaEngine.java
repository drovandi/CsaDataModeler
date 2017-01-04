
package et.csa;

import et.csa.bean.Dictionary;
import et.csa.reader.DictionaryReader;
import et.csa.writer.SchemaWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Use this class to create the schema script for the database
 * 
 * @author Istat Cooperation Unit
 */
public class SchemaEngine {

    private static final Logger LOGGER = Logger.getLogger(SchemaEngine.class.getName());

    public static void main(String[] args) throws Exception {

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
        
        //Generate the database schema sql script
        SchemaWriter.write(prop.getProperty("db.dest.schema"), dictionary, System.out);
    }
        
}

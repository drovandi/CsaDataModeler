
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
        Properties prop = new Properties();
        try (InputStream in = SchemaEngine.class.getResourceAsStream("/database.properties")) {
            prop.load(in);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Cannot read properties file", ex);
            return;
        }
        Dictionary dictionary = DictionaryReader.read(prop.getProperty("db.source.schema"),prop.getProperty("dictionary.filename"));
        SchemaWriter.execute(dictionary.getSchema(), dictionary, System.out);
    }
        
}

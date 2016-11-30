
package et.csa;

import et.csa.bean.Dictionary;
import et.csa.reader.DictionaryReader;
import et.csa.writer.SchemaWriter;

/**
 * Use this class to create the schema script for the database
 * 
 * @author Istat Cooperation Unit
 */
public class SchemaEngine {

    public static void main(String[] args) throws Exception {
        Dictionary dictionary = DictionaryReader.read("ipums","IPUMS.dcf");
        SchemaWriter.execute(dictionary.getSchema(), dictionary, System.out);
    }
        
}

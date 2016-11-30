
package et.csa;

import et.csa.bean.CsvRow;
import et.csa.bean.Dictionary;
import et.csa.bean.Record;
import et.csa.reader.CsvReader;
import et.csa.reader.DictionaryReader;
import et.csa.writer.InsertWriter;
import java.util.List;

/**
 * Use this class to create the inserts script to the database
 * 
 * @author Istat Cooperation Unit
 */
public class InsertEngine {

    public static void main(String[] args) throws Exception {
        Dictionary dictionary = DictionaryReader.read("ethiopian_census","Household.dcf");
        for (Record record : dictionary.getRecords()) {
            // TODO
            List<CsvRow> rows = CsvReader.read(record);
            InsertWriter.execute(dictionary.getSchema(), record, rows, System.out);
        }
    }
        
}

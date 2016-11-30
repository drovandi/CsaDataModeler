
package et.csa.reader;

import et.csa.bean.CsvRow;
import et.csa.bean.Record;
import java.util.LinkedList;
import java.util.List;

/**
 * This class reads the a csv file
 * 
 * @author Istat Cooperation Unit
 */
public class CsvReader {
    
    // TODO
    
    public static List<CsvRow> read(Record record) throws Exception {
        // TODO
        List<CsvRow> rows = new LinkedList<>();
        rows.add(new CsvRow());
        
        return rows;
    }
    
}


package et.csa.bean;

import java.util.HashMap;
import java.util.Map;

/**
 * This class represents a row of a csv file
 * 
 * @author Istat Cooperation Unit
 */
public final class CsvRow {
    
    private final Map<String,String> columns = new HashMap<>();
    
    public void addColumn(String columnName, String columnValue) {
        columns.put(columnName,columnValue);
    }
    
    public String getColumn(String columnName) {
        return columns.get(columnName);
    }
    
}

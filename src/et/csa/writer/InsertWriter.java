
package et.csa.writer;

import et.csa.bean.CsvRow;
import et.csa.bean.Item;
import et.csa.bean.Record;
import java.io.PrintStream;
import java.util.List;

/**
 * This class writes the SQL scripts to upload csv microdata (export using CSPro
 * export functionality) to an existing MySQL DB schema
 * 
 * @author Istat Cooperation Unit
 */
public class InsertWriter {
    
    public static void execute(String schema, Record record, List<CsvRow> rows, PrintStream ps) {
        int counter = 0;
        for (CsvRow row : rows) {
            counter++;
            if (!record.isMainRecord()) {
                getMainRecordId(record, row, ps);
            }
            ps.print("insert into "+schema+"."+record.getName()+" (");
            boolean first = true;
            for (Item item : record.getItems()) {
                if (first) first = false;
                else ps.print(",");
                ps.print(item.getName());
            }
            if (!record.isMainRecord()) {
                ps.print(",COUNTER,"+record.getMainRecord().getName().toUpperCase());
            }
            ps.print(") values (");
            first = true;
            for (Item item : record.getItems()) {
                if (first) first = false;
                else ps.print(",");
                if (row.getColumn(item.getName())==null) {
                    ps.print("null");
                } else {
                    switch (item.getDataType()) {
                        case "Number":
                            ps.print(row.getColumn(item.getName()));
                            break;
                        case "Alpha":
                            ps.print("\"");
                            ps.print(row.getColumn(item.getName()));
                            ps.print("\"");
                            break;
                    }
                }
            }
            if (!record.isMainRecord()) {
                ps.print(","+counter+",@id");
            }
            ps.println(");");
        }
    }
    
    private static void getMainRecordId(Record record, CsvRow row, PrintStream ps) {
        ps.print("select @id:=ID from "+record.getMainRecord().getName()+" where ");
        boolean first = true;
        for (Item item : record.getMainRecord().getItems()) {
            if (first) first = false;
            else ps.print(" and ");
            if (row.getColumn(item.getName())==null) {
                ps.print(item.getName()+" is null");
            } else {
                ps.print(item.getName()+" = "+row.getColumn(item.getName()));
            }
        }
        ps.println(";");
    }
    
}

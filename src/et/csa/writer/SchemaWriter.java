
package et.csa.writer;

import et.csa.bean.Dictionary;
import et.csa.bean.Item;
import et.csa.bean.Record;
import et.csa.bean.ValueSet;
import java.io.PrintStream;
import java.util.Map;

/**
 * This class writes the SQL scripts to create a MySQL DB schema parsing the
 * data structure representing the CSPro Dictionary
 *
 * @author Istat Cooperation Unit
 */
public class SchemaWriter {

    public static void execute(String schema, Dictionary dictionary, PrintStream ps) {
        ps.println("CREATE SCHEMA " + schema + ";");
        ps.println();

        for (Record record : dictionary.getRecords()) {
            for (Item item : record.getItems()) {
                printValueSet(schema,item,ps);
            }
        }

        for (Record record : dictionary.getRecords()) {
            ps.println("CREATE TABLE " + schema + "." + record.getName() + " (");
            ps.println("    ID INT(9) UNSIGNED AUTO_INCREMENT,");
            if (!record.isMainRecord()) {
                ps.println("    " + record.getMainRecord().getName() + " INT(9) UNSIGNED NOT NULL,");
                ps.println("    COUNTER INT(9) UNSIGNED NOT NULL,");
            }
            for (Item item : record.getItems()) {
                printItem(schema,item,ps);
            }
            if (!record.isMainRecord()) {
                ps.println("    INDEX (" + record.getMainRecord().getName() + "),");
                ps.println("    FOREIGN KEY (" + record.getMainRecord().getName() + ") REFERENCES " + schema + "." + record.getMainRecord().getName() + "(id),");
            }
            ps.println("    PRIMARY KEY (ID)");
            ps.println(") ENGINE=INNODB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;");
            ps.println();
        }
    }

    private static void printItem(String schema, Item item, PrintStream ps) {
        String name = item.getName();
        int length = item.getLength();
        switch (item.getDataType()) {
            case Dictionary.ITEM_ALPHA:
                ps.println("    " + name + " CHAR(" + length + "),");
                break;
            case "Number":
                ps.println("    " + name + " INT(" + length + "),");
                break;
        }
        if (item.hasValueSets()) {
            ps.println("    FOREIGN KEY (" + name + ") REFERENCES " + schema + "." + item.getValueSetName() + "(ID),");
        }
        for (Item subItem : item.getSubItems()) {
            printItem(schema,subItem,ps);
        }
    }
    
    private static void printValueSet(String schema, Item item, PrintStream ps) {
        if (item.hasValueSets()) {
            ps.println("CREATE TABLE " + schema + "." + item.getValueSetName() + " (");
            switch (item.getDataType()) {
                case Dictionary.ITEM_ALPHA:
                    ps.println("    ID CHAR(" + item.getLength()+ "),");
                    break;
                case "Number":
                    ps.println("    ID INT(" + item.getLength() + "),");
                    break;
            }
            ps.println("    VALUE CHAR(" + item.getValueSetsValueLength() + "),");
            ps.println("    PRIMARY KEY (ID)");
            ps.println(") ENGINE=INNODB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;");
            ps.println();
            ps.println("INSERT INTO " + schema + "." + item.getValueSetName() + "(ID,VALUE) VALUES ");
            for (int i=0; i<item.getValueSets().size(); i++) {
                ValueSet valueSet = item.getValueSets().get(i);
                int j=0;
                for (Map.Entry<String, String> e : valueSet.getValues().entrySet()) {
                    ps.print("    (" + e.getKey() + ",\"" + e.getValue() + "\")");
                    if (++j==valueSet.getValues().size()) ps.println(";");
                    else ps.println(",");
                }
            }
            ps.println();
        }
        for (Item subItem : item.getSubItems()) {
            printValueSet(schema,subItem,ps);
        }
    }
    /*
    private static void printValueSet(String schema, Item item, PrintStream ps) {
        if (item.hasValueSets()) {
            ps.println("CREATE TABLE " + schema + "." + item.getValueSetName() + " (");
            ps.println("    ID INT(9) UNSIGNED AUTO_INCREMENT,");
            ps.println("    VS_COUNT INT(" + ((int)Math.floor(Math.log10(item.getValueSets().size()))+1)+ ") UNSIGNED,");
            switch (item.getDataType()) {
                case Dictionary.ITEM_ALPHA:
                    ps.println("    VS_KEY CHAR(" + item.getLength()+ "),");
                    break;
                case "Number":
                    ps.println("    VS_KEY INT(" + item.getLength() + "),");
                    break;
            }
            ps.println("    VS_VALUE CHAR(" + item.getValueSetsValueLength() + "),");
            ps.println("    PRIMARY KEY (ID)");
            ps.println(") ENGINE=INNODB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;");
            ps.println();
            ps.println("INSERT INTO " + schema + "." + item.getValueSetName() + "(VS_COUNT,VS_KEY,VS_VALUE) VALUES ");
            for (int i=0; i<item.getValueSets().size(); i++) {
                ValueSet valueSet = item.getValueSets().get(i);
                int j=0;
                for (Map.Entry<String, String> e : valueSet.getValues().entrySet()) {
                    ps.print("    (" + i + "," + e.getKey() + ",\"" + e.getValue() + "\")");
                    if (++j==valueSet.getValues().size()) ps.println(";");
                    else ps.println(",");
                }
            }
            ps.println();
            ps.println("CREATE INDEX IDX_" + item.getValueSetName() + "_KEY ON " + schema + "." + item.getValueSetName() + "(VS_KEY) USING BTREE;");
            ps.println();
        }
        for (Item subItem : item.getSubItems()) {
            printValueSet(schema,subItem,ps);
        }
    }
    */
}

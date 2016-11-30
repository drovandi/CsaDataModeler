
package et.csa.writer;

import et.csa.bean.Dictionary;
import et.csa.bean.Item;
import et.csa.bean.Record;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

public class InsertCreator {

    public static void create(Dictionary dictionary, Map<Record, List<List<String>>> descr, Statement stmt) throws SQLException {
        int id = 0;
        for (Map.Entry<Record, List<List<String>>> e : descr.entrySet()) {
            Record record = e.getKey();
            String sql = "insert into " + dictionary.getSchema() + "." + record.getName() + " (";
            boolean first = true;
            if (!record.isMainRecord()) {
                first = false;
                sql += dictionary.getMainRecord().getName() + ",COUNTER";
            }
            for (Item item : record.getItems()) {
                if (first) first = false;
                else sql += ",";
                sql += item.getName();
            }
            sql += ") values ";
            for (int i=0; i<e.getValue().size(); i++) {
                List<String> values = e.getValue().get(i);
                if (i>0) sql += ",";
                sql += "\n\t\t(";
                first = true;
                if (!record.isMainRecord()) {
                    first = false;
                    sql += id + "," + i;
                }
                for (String v : values) {
                    if (first) first = false;
                    else sql += ",";
                    sql += v;
                }
                sql += ")";
            }
            
            System.out.println(sql);
            if (record.isMainRecord()) {
                System.out.println("select last_insert_id()");
            }
            
            /*
            stmt.executeUpdate(sql);
            if (record.isMainRecord()) {
                ResultSet lastInsertId = stmt.executeQuery("select last_insert_id()");
                lastInsertId.next();
                id = lastInsertId.getInt(1);
            }
            */
        }
    }

}

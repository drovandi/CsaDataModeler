
package et.csa.bean;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public final class Dictionary {

    public static final String DICT_HEADER = "[Dictionary]";
    public static final String DICT_LANGUAGES = "[Languages]";
    public static final String DICT_LEVEL = "[Level]";
    public static final String DICT_RECORD = "[Record]";
    public static final String DICT_IDITEMS = "[IdItems]";
    public static final String DICT_ITEM = "[Item]";
    public static final String DICT_VALUESET = "[ValueSet]";
    public static final String DICT_RELATION = "[Relation]";

    public static final String DICT_LABEL = "Label";
    public static final char DICT_LABEL_LANGUAGE_SEPARATOR = '|';
    public static final String DICT_NAME = "Name";
    public static final String DICT_NOTE = "Note";
    public static final String DICT_NOTENEWLINE = "\r\n";
    public static final String DICT_YES = "Yes";
    public static final String DICT_NO = "No";
    public static final String DICT_OCCLABEL = "OccurrenceLabel";

    public static final String HEADER_VERSION = "Version";
    public static final String HEADER_VERSION_CSPRO = "CSPro";
    public static final String HEADER_RECSTART = "RecordTypeStart";
    public static final String HEADER_RECLEN = "RecordTypeLen";
    public static final String HEADER_POSITIONS = "Positions";
    public static final String HEADER_ABSOLUTE = "Absolute";
    public static final String HEADER_RELATIVE = "Relative";
    public static final String HEADER_ZEROFILL = "ZeroFill";
    public static final String HEADER_DECCHAR = "DecimalChar";
    public static final String HEADER_VALUESETIMAGES = "ValueSetImages";

    public static final String RECORD_TYPE = "RecordTypeValue";
    public static final String RECORD_REQUIRED = "Required";
    public static final String RECORD_MAX = "MaxRecords";
    public static final String RECORD_LEN = "RecordLen";

    public static final String ITEM_START = "Start";
    public static final String ITEM_LEN = "Len";
    public static final String ITEM_DATATYPE = "DataType";
    public static final String ITEM_ITEMTYPE = "ItemType";
    public static final String ITEM_OCCS = "Occurrences";
    public static final String ITEM_DECIMAL = "Decimal";
    public static final String ITEM_DECCHAR = "DecimalChar";
    public static final String ITEM_ZEROFILL = "ZeroFill";
    public static final String ITEM_ALPHA = "Alpha";
    public static final String ITEM_SUBITEM = "SubItem";

    public static final String VALUE_VALUE = "Value";
    public static final String VALUE_LINK = "Link";
    public static final String VALUE_IMAGE = "Image";
    public static final String VALUE_SPECIAL = "Special";
    public static final String VALUE_MISSING = "MISSING";
    public static final String VALUE_NOTAPPL = "NOTAPPL";
    public static final String VALUE_DEFAULT = "DEFAULT";

    public static final String RELATION_PRIMARY = "Primary";
    public static final String RELATION_PRIMARYLINK = "PrimaryLink";
    public static final String RELATION_SECONDARY = "Secondary";
    public static final String RELATION_SECONDARYLINK = "SecondaryLink";

    private final String schema;
    private final List<Record> records = new LinkedList<>();
    private final Map<String,Record> recordsByName = new LinkedHashMap<>();

    private Record lastRecord;
    private Item lastItem;
    private Item lastItemNotSubItem;

    public Dictionary(String schema) {
        this.schema = schema;
    }

    public String getSchema() {
        return schema;
    }

    public void addRecord(Record record) {
        if (this.records.isEmpty()) {
            // idItems record
            record.setIsMainRecord(true);
        } else {
            record.setMainRecord(getMainRecord());
        }
        this.lastRecord = record;
        this.records.add(this.lastRecord);
        this.recordsByName.put(record.getRecordTypeValue(),record);
    }

    public void addItem(Item item) {
        if (item.isSubItem()) {
            this.lastItemNotSubItem.addSubItem(item);
        } else {
            this.lastItemNotSubItem = item;
            this.lastRecord.addItem(item);
        }
        this.lastItem = item;
    }

    public void addValueSet(ValueSet valueSet) {
        this.lastItem.addValueSet(valueSet);
    }
    
    public Record getMainRecord() {
        return this.records.get(0);
    }

    public List<Record> getRecords() {
        return records;
    }
    
    public Record getRecord(char name) {
        return recordsByName.get(""+name);
    }
    
    public Record getRecord(String name) {
        return recordsByName.get(name);
    }

}

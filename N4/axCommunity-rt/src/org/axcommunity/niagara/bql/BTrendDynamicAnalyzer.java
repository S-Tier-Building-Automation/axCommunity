package org.axcommunity.niagara.bql;


import com.tridium.bql.util.BDynamicTimeRange;
import javax.baja.collection.BITable;
import javax.baja.collection.ColumnList;
import javax.baja.collection.TableCursor;
import javax.baja.naming.BOrd;
import javax.baja.status.BStatusNumeric;
import javax.baja.sys.*;
import javax.baja.util.BAbsTimeRange;

public class BTrendDynamicAnalyzer extends BComponent {
    public static final Property history = newProperty(0, BOrd.NULL, null);
    public static final Property dateRange = newProperty(0, new BAbsTimeRange(BAbsTime.make(2000, BMonth.make(0), 0, 0, 0), BAbsTime.make()), null);
    public static final Property timeRange = newProperty(8, BDynamicTimeRange.make("lastMonth"), null);
    public static final Property timeRangeDelta = newProperty(10, false, null);
    public static final Property count = newProperty(8, new BStatusNumeric(0.0D), null);
    public static final Property min = newProperty(8, new BStatusNumeric(0.0D), null);
    public static final Property max = newProperty(8, new BStatusNumeric(0.0D), null);
    public static final Property average = newProperty(8, new BStatusNumeric(0.0D), null);
    public static final Property sum = newProperty(8, new BStatusNumeric(0.0D), null);
    public static final Action execute = newAction(0, null);
     static Class class$org$axcommunity$niagara$bql$BTrendDynamicAnalyzer;

    public BOrd getHistory() {
        return ((BOrd) get(history));
    }

    public void setHistory(BOrd paramBOrd) {
        set(history, paramBOrd, null);
    }

    public BAbsTimeRange getDateRange() {
        return ((BAbsTimeRange) get(dateRange));
    }

    public void setDateRange(BAbsTimeRange paramBAbsTimeRange) {
        set(dateRange, paramBAbsTimeRange, null);
    }

    public BDynamicTimeRange getTimeRange() {
        return ((BDynamicTimeRange) get(timeRange));
    }

    public void setTimeRange(BDynamicTimeRange paramBDynamicTimeRange) {
        set(timeRange, paramBDynamicTimeRange, null);
    }

    public boolean getTimeRangeDelta() {
        return getBoolean(timeRangeDelta);
    }

    public void setTimeRangeDelta(boolean paramBoolean) {
        setBoolean(timeRangeDelta, paramBoolean, null);
    }

    public BStatusNumeric getCount() {
        return ((BStatusNumeric) get(count));
    }

    public void setCount(BStatusNumeric paramBStatusNumeric) {
        set(count, paramBStatusNumeric, null);
    }

    public BStatusNumeric getMin() {
        return ((BStatusNumeric) get(min));
    }

    public void setMin(BStatusNumeric paramBStatusNumeric) {
        set(min, paramBStatusNumeric, null);
    }

    public BStatusNumeric getMax() {
        return ((BStatusNumeric) get(max));
    }

    public void setMax(BStatusNumeric paramBStatusNumeric) {
        set(max, paramBStatusNumeric, null);
    }

    public BStatusNumeric getAverage() {
        return ((BStatusNumeric) get(average));
    }

    public void setAverage(BStatusNumeric paramBStatusNumeric) {
        set(average, paramBStatusNumeric, null);
    }

    public BStatusNumeric getSum() {
        return ((BStatusNumeric) get(sum));
    }

    public void setSum(BStatusNumeric paramBStatusNumeric) {
        set(sum, paramBStatusNumeric, null);
    }

    public void execute() {
        invoke(execute, null, null);
    }

    public void doExecute() {
        String str1 = "1577846298735";

        String str2 = getHistory().toString();

        BOrd localBOrd = BOrd.make(str2 + "?period=" + getTimeRange() + ";delta=" + getTimeRangeDelta() + "|bql:historyFunc:HistoryRollup.rollup(select *, baja:RelTime '" + str1 + "')");

        BITable localBITable = (BITable) localBOrd.resolve(Sys.getStation()).get();
        ColumnList localColumnList = localBITable.getColumns();
        TableCursor localTableCursor = localBITable.cursor();

        localTableCursor.next();

        double d1 = Double.parseDouble(localTableCursor.cell(localColumnList.get(2)).toString());
        double d2 = Double.parseDouble(localTableCursor.cell(localColumnList.get(3)).toString());
        double d3 = Double.parseDouble(localTableCursor.cell(localColumnList.get(4)).toString());
        double d4 = Double.parseDouble(localTableCursor.cell(localColumnList.get(5)).toString());
        double d5 = Double.parseDouble(localTableCursor.cell(localColumnList.get(6)).toString());

        getCount().setValue(d1);
        getMin().setValue(d2);
        getMax().setValue(d3);
        getAverage().setValue(d4);
        getSum().setValue(d5);
    }

    public BIcon getIcon() {
        return icon;
    }

    private static final BIcon icon = BIcon.make("module://axCommunity/org/axcommunity/niagara/graphics/BAS_Logo.png");

    public Type getType() {
        return TYPE;
    }

    public static final Type TYPE = Sys.loadType(BTrendDynamicAnalyzer.class);
}
package io.rong.dial;

import com.basis.utils.ObjToSP;
import com.basis.wapper.IResultBack;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class RecordManager {
    private final static RecordManager _manager = new RecordManager();
    private RecordCacher cacher;
    private ArrayList<DialInfo> records;

    private RecordManager() {
        cacher = new RecordCacher();
        records = cacher.getRecord();
    }

    private IResultBack<List<DialInfo>> resultBack;

    public static void observeCache(IResultBack<List<DialInfo>> resultBack) {
        _manager.resultBack = resultBack;
        _manager.handleObserve();
    }

    public static void save(DialInfo record) {
        _manager.saveRecord(record);
    }

    List<DialInfo> getRecords() {
        if ((null == records || records.isEmpty()) && null != cacher) {
            records = cacher.getRecord();
        }
        return records;
    }

    void saveRecord(DialInfo record) {
        if (null != record && null != records) {
            if (!records.contains(record)) {
                records.add(record);
                if (null != cacher) cacher.saveRecord(records);
            }
        }
        handleObserve();
    }

    void handleObserve() {
        if (null != resultBack) {
            resultBack.onResult(getRecords());
        }
    }

    class RecordCacher extends ObjToSP<ArrayList<DialInfo>> {
        public RecordCacher() {
            super("SP_CALL_RECORD", new TypeToken<ArrayList<DialInfo>>() {
            });
        }

        public ArrayList<DialInfo> getRecord() {
            ArrayList<DialInfo> data = getEntity(TAG);
            return null == data ? new ArrayList<>() : data;
        }

        public void saveRecord(ArrayList<DialInfo> records) {
            saveEntity(TAG, records);
        }
    }
}

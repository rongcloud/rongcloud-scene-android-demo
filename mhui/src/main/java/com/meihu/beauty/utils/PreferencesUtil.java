package com.meihu.beauty.utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Base64;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * sp管理类，提供对sp操作的包装，提交时统一做处理，2.2版本以上使用apply方法；
 */
public final class PreferencesUtil {
    private SharedPreferences mPreferences;
    private Editor mEditor;

    private PreferencesUtil() {
    }

    /**
     * 获取默认的sp文件default_cfg.xml，默认模式为${@link Context#MODE_PRIVATE}
     */
    static PreferencesUtil getDefaultSharedPreference(Context context) {
        return getSharedPreference(context, "mhsdk_default_cfg", Context.MODE_PRIVATE | Context.MODE_MULTI_PROCESS);
    }

    /**
     * 获取sp
     *
     * @param name 文件名称
     * @param mode 模式
     */
    @SuppressLint("CommitPrefEdits")
    private static PreferencesUtil getSharedPreference(Context context, String name, int mode) {
        if (context != null) {
            try {
                PreferencesUtil preferencesManager = new PreferencesUtil();
                preferencesManager.mPreferences = context.getSharedPreferences(name, mode);
                return preferencesManager;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 异步提交.<br>
     */
    private static void apply(final Editor editor) {
        editor.apply();
    }

    /**
     * 清除数据
     */
    public void clear() {
        if (mEditor != null) {
            mEditor.apply();
        } else if (mPreferences != null) {
            edit();
            mEditor.apply();
        }
    }

    public void remove(String key) {
        try {
            mPreferences.edit().remove(key).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Map<String, ?> getAll() {
        Map<String, ?> all = mPreferences.getAll();
        Map<String, String> dAll = new HashMap<>(all.size());
        if (all.size() > 0) {
            for (String key : all.keySet()) {
                try {
                    dAll.put(key, (String) all.get(key));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return dAll;
    }

    public void putBoolean(String key, boolean b) {
        String val = Boolean.toString(b);
        putString(key, val);
    }

    public void putInt(String key, int i) {
        mEditor.putInt(key, i);
    }

    public void putFloat(String key, float f) {
        mEditor.putFloat(key, f);
    }

    public void putLong(String key, long l) {
        mEditor.putLong(key, l);
    }

    public void putString(String key, String s) {
        mEditor.putString(key, s);
    }

    /**
     * 异步提交.<br>
     */
    public void commit() {
        if (mEditor != null) {
            apply(mEditor);
        }
    }

    @SuppressLint("CommitPrefEdits")
    public void edit() {
        if (mEditor == null && mPreferences != null) {
            mEditor = new Editor();
        }
    }

    public String getString(String key, String defValue) {
        try {

            String value = mPreferences.getString(key, null);
            if (value != null) {
                return value;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defValue;
    }

    public Set<String> getStringSet(String key, Set<String> defValues) {
        try {
            Set<String> eSet = mPreferences.getStringSet(key, null);
            if (eSet != null) {
                Set<String> dSet = new HashSet<>(eSet.size());
                dSet.addAll(eSet);
                return dSet;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return defValues;
    }

    public int getInt(String key, int defValue) {
        String value = getString(key, null);
        if (value != null) {
            return Integer.parseInt(value);
        }
        return defValue;
    }

    public long getLong(String key, long defValue) {
        String value = getString(key, null);
        if (value != null) {
            return Long.parseLong(value);
        }
        return defValue;
    }

    public float getFloat(String key, float defValue) {
        String value = getString(key, null);
        if (value != null) {
            return Float.parseFloat(value);
        }
        return defValue;
    }

    public boolean getBoolean(String key, boolean defValue) {
        String value = getString(key, null);
        if (value != null) {
            return Boolean.parseBoolean(value);
        }
        return defValue;
    }

    public byte[] getBytes(String key) {
        String val = getString(key, null);
        if (val != null) {
            return Base64.decode(val, Base64.DEFAULT);
        }

        return null;
    }

    public boolean contains(String key) {
        try {
            return mPreferences.contains(key);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public class Editor implements SharedPreferences.Editor {
        SharedPreferences.Editor mEditor;

        public Editor() {
            mEditor = mPreferences.edit();
        }

        @Override
        public SharedPreferences.Editor putString(String key, String value) {
            try {
                mEditor.putString(key, value);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return this;
        }

        @Override
        public SharedPreferences.Editor putStringSet(String key, Set<String> values) {
            try {
                Set<String> eSet = new HashSet<String>(values.size());
                eSet.addAll(values);
                mEditor.putStringSet(key, eSet);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return this;
        }

        @Override
        public SharedPreferences.Editor putInt(String key, int value) {
            String val = Integer.toString(value);
            return putString(key, val);
        }

        @Override
        public SharedPreferences.Editor putLong(String key, long value) {
            String val = Long.toString(value);
            return putString(key, val);
        }

        @Override
        public SharedPreferences.Editor putFloat(String key, float value) {
            String val = Float.toString(value);
            return putString(key, val);
        }

        @Override
        public SharedPreferences.Editor putBoolean(String key, boolean value) {
            String val = Boolean.toString(value);
            return putString(key, val);
        }

        @TargetApi(Build.VERSION_CODES.O)
        public SharedPreferences.Editor putBytes(String key, byte[] bytes) {
            if (bytes != null) {
                byte[] encode = Base64.encode(bytes, Base64.DEFAULT);
                return putString(key, new String(encode));
            } else return remove(key);
        }

        @Override
        public SharedPreferences.Editor remove(String key) {
            try {
                mEditor.remove(key);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return this;
        }

        @Override
        public SharedPreferences.Editor clear() {
            mEditor.clear();

            return this;
        }

        @Override
        public boolean commit() {
            return mEditor.commit();
        }

        @Override
        public void apply() {
            mEditor.apply();
        }
    }
}

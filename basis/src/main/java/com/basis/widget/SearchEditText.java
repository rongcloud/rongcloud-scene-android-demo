package com.basis.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * 带键盘监听的search的编辑框
 */
public class SearchEditText extends EditText {

    private Context context;
    private OnSearchListener mSearch;//实现搜索接口子类的实例对象

    public SearchEditText(Context context) {
        super(context);
        this.context = context;
    }

    public SearchEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public SearchEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
    }

    /**
     * 键盘key点击监听
     */
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            // 隐藏键盘
            if (inputMethodManager.isActive()) {
                inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            }
            if (mSearch != null) {
                String search = getText().toString().trim();
                mSearch.onSearch(search);
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    /**
     * 接口监听
     * 此接口是更改enter键为search，并处理search事件的 监听
     * 注意：singleLine属性  必须设置为true 否则enter图标不是更改
     *
     * @param onSearch 实现的搜索接口子类的实例对象
     */
    public void setOnSearchListener(OnSearchListener onSearch) {
        mSearch = onSearch;
    }

    /**
     * 搜索接口
     * 需要子类具体实现的
     */
    public interface OnSearchListener {
        void onSearch(String search);
    }
}

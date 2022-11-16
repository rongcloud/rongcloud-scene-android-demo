/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.roomkit.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

import cn.rongcloud.roomkit.R;


/**
 * Created by SongSenior on 2021/4/16
 */
public class VerifyEditText extends LinearLayout {
    //默认 item 个数为 4 个
    private final static int DEFAULT_ITEM_COUNT = 4;
    //默认每个 item 的宽度为 100
    private final static int DEFAULT_ITEM_WIDTH = 100;
    //默认每个 item 的间距为 50
    private final static int DEFAULT_ITEM_MARGIN = 50;
    //默认每个 item 的字体大小为 14
    private final static int DEFAULT_ITEM_TEXT_SIZE = 14;
    //默认密码明文显示时间为 200ms，之后密文显示
    private final static int DEFAULT_PASSWORD_VISIBLE_TIME = 200;

    private final List<TextView> mTextViewList = new ArrayList<>();
    private EditText mEditText;
    private Drawable drawableNormal, drawableSelected;
    private Context mContext;
    //输入完成监听
    private InputCompleteListener mInputCompleteListener;

    public VerifyEditText(Context context) {
        this(context, null);
    }

    public VerifyEditText(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VerifyEditText(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        mContext = context;
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER);
        @SuppressLint("CustomViewStyleable") TypedArray obtainStyledAttributes =
                getContext().obtainStyledAttributes(attrs, R.styleable.verify_EditText);
        drawableNormal = obtainStyledAttributes.getDrawable(R.styleable.verify_EditText_verify_background_normal);
        drawableSelected = obtainStyledAttributes.getDrawable(R.styleable.verify_EditText_verify_background_selected);
        int textColor = obtainStyledAttributes.getColor(R.styleable.verify_EditText_verify_textColor,
                ContextCompat.getColor(context, android.R.color.black));
        int count = obtainStyledAttributes.getInt(R.styleable.verify_EditText_verify_count, DEFAULT_ITEM_COUNT);
        int inputType = obtainStyledAttributes.getInt(R.styleable.verify_EditText_verify_inputType, InputType.TYPE_CLASS_NUMBER);
        int passwordVisibleTime = obtainStyledAttributes.getInt(R.styleable.verify_EditText_verify_password_visible_time, DEFAULT_PASSWORD_VISIBLE_TIME);
        int width = (int) obtainStyledAttributes.getDimension(R.styleable.verify_EditText_verify_width, DEFAULT_ITEM_WIDTH);
        int height = (int) obtainStyledAttributes.getDimension(R.styleable.verify_EditText_verify_height, 0);
        int margin = (int) obtainStyledAttributes.getDimension(R.styleable.verify_EditText_verify_margin, DEFAULT_ITEM_MARGIN);
        float textSize = px2sp(context, obtainStyledAttributes.getDimension(R.styleable.verify_EditText_verify_textSize, sp2px(context, DEFAULT_ITEM_TEXT_SIZE)));
        boolean password = obtainStyledAttributes.getBoolean(R.styleable.verify_EditText_verify_password, false);
        obtainStyledAttributes.recycle();
        if (count < 2) count = 2;//最少 2 个 item

        mEditText = new EditText(context);
        mEditText.setInputType(inputType);
        mEditText.setLayoutParams(new LayoutParams(1, 1));
        mEditText.setCursorVisible(false);
        mEditText.setBackground(null);
        mEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(count)});//限制输入长度为1
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                TextView textView = mTextViewList.get(start);//获取对应的 textview
                if (before == 0) {//输入
                    setTextViewBackground(textView, drawableSelected);
                    CharSequence input = s.subSequence(start, s.length());//获取新输入的字
                    textView.setText(input);
                    if (password) {//如果需要密文显示
                        textView.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        //passwordVisibleTime 毫秒后设置为密文显示
                        textView.postDelayed(() ->
                                        textView.setTransformationMethod(PasswordTransformationMethod.getInstance()),
                                passwordVisibleTime);
                    }

                } else {//删除
                    textView.setText("");
                    setTextViewBackground(textView, drawableNormal);
                }
                if (mInputCompleteListener != null && s.length() == mTextViewList.size())
                    mInputCompleteListener.complete(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        addView(mEditText);
        //点击弹出软键盘
        setOnClickListener(v -> {
            showSoftKeyBoard();
        });
        //遍历生成 textview
        for (int i = 0; i < count; i++) {
            TextView textView = new TextView(context);
            textView.setTextSize(textSize);
            textView.setGravity(Gravity.CENTER);
            textView.setTextColor(textColor);
            LayoutParams layoutParams = new LayoutParams(width, height == 0 ? ViewGroup.LayoutParams.WRAP_CONTENT : height);
            if (i == 0)
                layoutParams.leftMargin = -1;
            else
                layoutParams.leftMargin = margin;
            textView.setLayoutParams(layoutParams);
            setTextViewBackground(textView, drawableNormal);
            addView(textView);
            mTextViewList.add(textView);
        }
    }

    /**
     * view 添加到窗口时，延迟 500ms 弹出软键盘
     */
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mEditText.postDelayed(this::showSoftKeyBoard, 200);
    }

    /**
     * 设置背景
     *
     * @param textView
     * @param drawable
     */
    private void setTextViewBackground(TextView textView, Drawable drawable) {
        textView.setBackground(drawable);
    }

    @Override
    protected void onDetachedFromWindow() {
        hideSoftKeyBoard();
        super.onDetachedFromWindow();
    }

    /**
     * 获取当前输入的内容
     *
     * @return
     */
    public String getContent() {
        Editable text = mEditText.getText();
        if (TextUtils.isEmpty(text)) return "";
        return mEditText.getText().toString();
    }

    /**
     * 清除内容
     */
    public void clearContent() {
        mEditText.setText("");
        for (int i = 0; i < mTextViewList.size(); i++) {
            TextView textView = mTextViewList.get(i);
            textView.setText("");
            setTextViewBackground(textView, drawableNormal);
        }
    }

    /**
     * 设置默认的内容
     *
     * @param content
     */
    public void setDefaultContent(String content) {
        mEditText.setText(content);
        mEditText.requestFocus();
        char[] chars = content.toCharArray();
        int min = Math.min(chars.length, mTextViewList.size());
        for (int i = 0; i < min; i++) {
            char aChar = chars[i];
            String s = String.valueOf(aChar);
            TextView textView = mTextViewList.get(i);
            textView.setText(s);
            setTextViewBackground(textView, drawableSelected);
        }
        if (mInputCompleteListener != null && min == mTextViewList.size())
            mInputCompleteListener.complete(content.substring(0, min));

    }

    /**
     * 显示软键盘
     */
    private void showSoftKeyBoard() {
        mEditText.requestFocus();
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mEditText, InputMethodManager.SHOW_IMPLICIT);
    }

    private void hideSoftKeyBoard() {
        mEditText.clearFocus();
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
    }

    /**
     * 添加输入完成的监听
     *
     * @param inputCompleteListener
     */
    public void addInputCompleteListener(InputCompleteListener inputCompleteListener) {
        mInputCompleteListener = inputCompleteListener;
        Editable content = mEditText.getText();
        if (!TextUtils.isEmpty(content) && content.toString().length() == mTextViewList.size()) {
            mInputCompleteListener.complete(content.toString());
        }
    }

    public interface InputCompleteListener {
        void complete(String content);
    }

    private int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    private int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }
}
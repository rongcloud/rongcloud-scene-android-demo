/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.rong.callkit.dialpad.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import io.rong.callkit.R;

public class DialpadView extends LinearLayout {
    private static final String TAG = DialpadView.class.getSimpleName();
    private final int[] mButtonIds = new int[]{R.id.zero, R.id.one, R.id.two, R.id.three,
            R.id.four, R.id.five, R.id.six, R.id.seven, R.id.eight, R.id.nine, R.id.star,
            R.id.pound};
    private EditText mDigits;
    private ImageButton mDelete;
    private View mOverflowMenuButton;
    private ColorStateList mRippleColor;
    private ViewGroup mRateContainer;
    private TextView mIldCountry;
    private TextView mIldRate;
    private boolean mCanDigitsBeEdited;


    public DialpadView(Context context) {
        this(context, null);
    }

    public DialpadView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DialpadView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Dialpad);
        mRippleColor = a.getColorStateList(R.styleable.Dialpad_dialpad_key_button_touch_tint);
        a.recycle();
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onFinishInflate() {
        setupKeypad();
        mDigits = (EditText) findViewById(R.id.digits);
        mDelete = (ImageButton) findViewById(R.id.deleteButton);
        mOverflowMenuButton = findViewById(R.id.dialpad_overflow);
        mRateContainer = (ViewGroup) findViewById(R.id.rate_container);
        mIldCountry = (TextView) mRateContainer.findViewById(R.id.ild_country);
        mIldRate = (TextView) mRateContainer.findViewById(R.id.ild_rate);

        AccessibilityManager accessibilityManager = (AccessibilityManager)
                getContext().getSystemService(Context.ACCESSIBILITY_SERVICE);
        if (accessibilityManager.isEnabled()) {
            // The text view must be selected to send accessibility events.
            mDigits.setSelected(true);
        }
    }

    private void setupKeypad() {
        final int[] letterIds = new int[]{
                R.string.dialpad_0_letters,
                R.string.dialpad_1_letters,
                R.string.dialpad_2_letters,
                R.string.dialpad_3_letters,
                R.string.dialpad_4_letters,
                R.string.dialpad_5_letters,
                R.string.dialpad_6_letters,
                R.string.dialpad_7_letters,
                R.string.dialpad_8_letters,
                R.string.dialpad_9_letters,
                R.string.dialpad_star_letters,
                R.string.dialpad_pound_letters
        };

        final Resources resources = getContext().getResources();

        DialpadKeyButton dialpadKey;
        TextView numberView;
        TextView lettersView;

        final Locale currentLocale = resources.getConfiguration().locale;
        final NumberFormat nf;
        // We translate dialpad numbers only for "fa" and not any other locale
        // ("ar" anybody ?).
        if ("fa".equals(currentLocale.getLanguage())) {
            nf = DecimalFormat.getInstance(resources.getConfiguration().locale);
        } else {
            nf = DecimalFormat.getInstance(Locale.ENGLISH);
        }

        for (int i = 0; i < mButtonIds.length; i++) {
            dialpadKey = (DialpadKeyButton) findViewById(mButtonIds[i]);
            numberView = (TextView) dialpadKey.findViewById(R.id.dialpad_key_number);
            lettersView = (TextView) dialpadKey.findViewById(R.id.dialpad_key_letters);

            final String numberString;
            if (mButtonIds[i] == R.id.pound) {
                numberString = resources.getString(R.string.dialpad_pound_number);
            } else if (mButtonIds[i] == R.id.star) {
                numberString = resources.getString(R.string.dialpad_star_number);
            } else {
                numberString = nf.format(i);
            }

            final RippleDrawable rippleBackground = (RippleDrawable)
                    getDrawableCompat(getContext(), R.drawable.btn_dialpad_key);
            if (mRippleColor != null) {
                rippleBackground.setColor(mRippleColor);
            }
            if (null != numberView) {
                numberView.setText(numberString);
                numberView.setTextColor(resources.getColor(R.color.app_color_black));
            }
            if (lettersView != null) {
                lettersView.setText(resources.getString(letterIds[i]));
                lettersView.setTextColor(resources.getColor(R.color.app_color_grey));
            }
        }
    }

    private Drawable getDrawableCompat(Context context, int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return context.getDrawable(id);
        } else {
            return context.getResources().getDrawable(id);
        }
    }

    public void setShowVoicemailButton(boolean show) {
        View view = findViewById(R.id.dialpad_key_voicemail);
        if (view != null) {
            view.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
        }
    }

    /**
     * Whether or not the digits above the dialer can be edited.
     *
     * @param canBeEdited If true, the backspace button will be shown and the digits EditText
     *                    will be configured to allow text manipulation.
     */
    public void setCanDigitsBeEdited(boolean canBeEdited) {
        View deleteButton = findViewById(R.id.deleteButton);
        deleteButton.setVisibility(canBeEdited ? View.VISIBLE : View.GONE);
        EditText digits = findViewById(R.id.digits);
        digits.setClickable(canBeEdited);
        digits.setLongClickable(canBeEdited);
        digits.setFocusableInTouchMode(canBeEdited);
        digits.setCursorVisible(false);

        mCanDigitsBeEdited = canBeEdited;
    }

    @Override
    public boolean onHoverEvent(MotionEvent event) {
        return true;
    }

    public EditText getDigits() {
        return mDigits;
    }

    public ImageButton getDeleteButton() {
        return mDelete;
    }

    public View getOverflowMenuButton() {
        return mOverflowMenuButton;
    }
}

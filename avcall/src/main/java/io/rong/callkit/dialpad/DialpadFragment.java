/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package io.rong.callkit.dialpad;

import android.app.Fragment;
import android.content.Context;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Trace;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.regex.Pattern;

import io.rong.callkit.DialActivity;
import io.rong.callkit.R;
import io.rong.callkit.dialpad.widget.DialpadKeyButton;
import io.rong.callkit.dialpad.widget.DialpadView;
import io.rong.callkit.dialpad.widget.FloatingActionButtonController;

/**
 * Fragment that displays a twelve-key phone dialpad.
 */
public class DialpadFragment extends Fragment
        implements View.OnClickListener, View.OnKeyListener, TextWatcher, DialpadKeyButton.OnPressedListener {
    private static final String TAG = "DialpadFragment";
    public final static String DEFAU_INPUT = "defau_input";
    private static final String PREF_DIGITS_FILLED_BY_INTENT = "pref_digits_filled_by_intent";
    // 添加166号段
    private final static String telRegex = "^((1[3,5,6,7,8][0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$";
    private static final boolean DEBUG = true;

    // This is the amount of screen the dialpad fragment takes up when fully displayed
    private static final String EMPTY_NUMBER = "";
    /**
     * The length of DTMF tones in milliseconds
     */
    private static final int TONE_LENGTH_MS = 150;
    private static final int TONE_LENGTH_INFINITE = -1;

    /**
     * The DTMF tone volume relative to other sounds in the stream
     */
    private static final int TONE_RELATIVE_VOLUME = 80;

    /**
     * Stream type used to play the DTMF tones off call, and mapped to the volume control keys
     */
    private static final int DIAL_TONE_STREAM_TYPE = AudioManager.STREAM_DTMF;

    private DialpadView mDialpadView;
    private EditText mDigits;

    /**
     * Remembers if we need to clear digits field when the screen is completely gone.
     */
    private boolean mClearDigitsOnStop;
    private View mDelete, mLeft;
    private ToneGenerator mToneGenerator;
    public static WeakReference<DialpadListener> dialpadListener;
    private View mSpacer;

    private FloatingActionButtonController mFloatingActionButtonController;
    private final Object mToneGeneratorLock = new Object();
    private boolean mDigitsFilledByIntent;
    /**
     * Set of dialpad keys that are currently being pressed
     */
    private final HashSet<View> mPressedDialpadKeys = new HashSet<View>(12);

    @Override
    public Context getContext() {
        return getActivity();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence input, int start, int before, int changeCount) {
    }

    @Override
    public void afterTextChanged(Editable input) {
        if (isDigitsEmpty()) {
            mDigitsFilledByIntent = false;
            mDigits.setCursorVisible(false);
            if (mDelete != null) {
                mDelete.setEnabled(false);
                mDelete.setAlpha(0.3f);
            }
        } else {
            if (mDelete != null) {
                mDelete.setEnabled(true);
                mDelete.setAlpha(1.0f);
            }
        }
        if (null != dialpadListener && null != dialpadListener.get()) {
            getView().post(new Runnable() {
                @Override
                public void run() {

                    dialpadListener.get().onInputFiltter(mDigits.getText());
                }
            });
        }
    }

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        if (state != null) {
            mDigitsFilledByIntent = state.getBoolean(PREF_DIGITS_FILLED_BY_INTENT);
        }
    }

    public void setInputNum(String inputNum) {
        if (null != mDigits && !TextUtils.isEmpty(inputNum) && isMobileNO(inputNum)) {
            mDigits.setText(inputNum);
            mDigits.setSelection(inputNum.length());
            mDigits.requestFocus();
        }
    }

    private ImageButton mFloatingActionButton;


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mDigits.setKeyListener(UnicodeDialerKeyListener.INSTANCE);
        mDigits.setOnClickListener(this);
        mDigits.setOnKeyListener(this);
        mDigits.addTextChangedListener(this);
        if (mDelete != null && null != mLeft) {
            mDelete.setOnClickListener(this);
            mLeft.setOnClickListener(this);
        }
        mFloatingActionButton.setOnClickListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedState) {

        final View fragmentView = inflater.inflate(R.layout.dialpad_fragment, container,
                false);
        fragmentView.buildLayer();
        mDialpadView = (DialpadView) fragmentView.findViewById(R.id.dialpad_view);
        mDialpadView.setCanDigitsBeEdited(true);
        mDigits = mDialpadView.getDigits();

        mDigits.setElegantTextHeight(false);
        String defaultInput = getArguments().getString(DEFAU_INPUT);
        setInputNum(defaultInput);
        // Check for the presence of the keypad
        View oneButton = fragmentView.findViewById(R.id.one);
        if (oneButton != null) {
            configureKeypadListeners(fragmentView);
        }

        //隐藏menu 和delete
        mDialpadView.getDeleteButton().setVisibility(View.GONE);
        mDialpadView.getOverflowMenuButton().setVisibility(View.GONE);
        mDelete = fragmentView.findViewById(R.id.dialpad_right);
        mLeft = fragmentView.findViewById(R.id.dialpad_left);


        mSpacer = fragmentView.findViewById(R.id.spacer);
        mSpacer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return hideSelf();
            }
        });

        mDigits.setCursorVisible(false);

        final View floatingActionButtonContainer =
                fragmentView.findViewById(R.id.dialpad_floating_action_button_container);
        mFloatingActionButton = fragmentView.findViewById(R.id.dialpad_floating_action_button);

        mFloatingActionButtonController = new FloatingActionButtonController(getActivity(),
                floatingActionButtonContainer, mFloatingActionButton);
        Trace.endSection();
        Trace.endSection();
        return fragmentView;
    }

    private boolean hideSelf() {
        if (getActivity() != null && getActivity() instanceof DialActivity) {
            ((DialActivity) getActivity()).hideDialpadFragment(true);
            return true;
        }
        return false;
    }

    @VisibleForTesting
    public EditText getDigitsWidget() {
        return mDigits;
    }

    private void configureKeypadListeners(View fragmentView) {
        final int[] buttonIds = new int[]{R.id.one, R.id.two, R.id.three, R.id.four, R.id.five,
                R.id.six, R.id.seven, R.id.eight, R.id.nine, R.id.star, R.id.zero, R.id.pound};
        DialpadKeyButton dialpadKey;
        for (int i = 0; i < buttonIds.length; i++) {
            dialpadKey = fragmentView.findViewById(buttonIds[i]);
            dialpadKey.setOnPressedListener(this);
        }
    }

    @Override
    public void onStart() {
        Trace.beginSection(TAG + " onStart");
        super.onStart();
        // if the mToneGenerator creation fails, just continue without it.  It is
        // a local audio signal, and is not as important as the dtmf tone itself.
        final long start = System.currentTimeMillis();
        synchronized (mToneGeneratorLock) {
            if (mToneGenerator == null) {
                try {
                    mToneGenerator = new ToneGenerator(DIAL_TONE_STREAM_TYPE, TONE_RELATIVE_VOLUME);
                } catch (RuntimeException e) {
                    Log.w(TAG, "Exception caught while creating local tone generator: " + e);
                    mToneGenerator = null;
                }
            }
        }
        final long total = System.currentTimeMillis() - start;
        if (total > 50) {
            Log.i(TAG, "Time for ToneGenerator creation: " + total);
        }
        Trace.endSection();
    }

    @Override
    public void onPause() {
        super.onPause();

        // Make sure we don't leave this activity with a tone still playing.
        stopTone();
        mPressedDialpadKeys.clear();
    }

    @Override
    public void onStop() {
        super.onStop();
        synchronized (mToneGeneratorLock) {
            if (mToneGenerator != null) {
                mToneGenerator.release();
                mToneGenerator = null;
            }
        }

        if (mClearDigitsOnStop) {
            mClearDigitsOnStop = false;
            clearDialpad();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(PREF_DIGITS_FILLED_BY_INTENT, mDigitsFilledByIntent);
    }

    private void keyPressed(int keyCode) {
        if (getView() == null || getView().getTranslationY() != 0) {
            return;
        }
        switch (keyCode) {
            case KeyEvent.KEYCODE_1:
                playTone(ToneGenerator.TONE_DTMF_1, TONE_LENGTH_INFINITE);
                break;
            case KeyEvent.KEYCODE_2:
                playTone(ToneGenerator.TONE_DTMF_2, TONE_LENGTH_INFINITE);
                break;
            case KeyEvent.KEYCODE_3:
                playTone(ToneGenerator.TONE_DTMF_3, TONE_LENGTH_INFINITE);
                break;
            case KeyEvent.KEYCODE_4:
                playTone(ToneGenerator.TONE_DTMF_4, TONE_LENGTH_INFINITE);
                break;
            case KeyEvent.KEYCODE_5:
                playTone(ToneGenerator.TONE_DTMF_5, TONE_LENGTH_INFINITE);
                break;
            case KeyEvent.KEYCODE_6:
                playTone(ToneGenerator.TONE_DTMF_6, TONE_LENGTH_INFINITE);
                break;
            case KeyEvent.KEYCODE_7:
                playTone(ToneGenerator.TONE_DTMF_7, TONE_LENGTH_INFINITE);
                break;
            case KeyEvent.KEYCODE_8:
                playTone(ToneGenerator.TONE_DTMF_8, TONE_LENGTH_INFINITE);
                break;
            case KeyEvent.KEYCODE_9:
                playTone(ToneGenerator.TONE_DTMF_9, TONE_LENGTH_INFINITE);
                break;
            case KeyEvent.KEYCODE_0:
                playTone(ToneGenerator.TONE_DTMF_0, TONE_LENGTH_INFINITE);
                break;
            case KeyEvent.KEYCODE_POUND:
                playTone(ToneGenerator.TONE_DTMF_P, TONE_LENGTH_INFINITE);
                break;
            case KeyEvent.KEYCODE_STAR:
                playTone(ToneGenerator.TONE_DTMF_S, TONE_LENGTH_INFINITE);
                break;
            default:
                break;
        }

        getView().performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        KeyEvent event = new KeyEvent(KeyEvent.ACTION_DOWN, keyCode);
        mDigits.onKeyDown(keyCode, event);

        // If the cursor is at the end of the text we hide it.
        final int length = mDigits.length();
        if (length == mDigits.getSelectionStart() && length == mDigits.getSelectionEnd()) {
            mDigits.setCursorVisible(false);
        }
    }

    @Override
    public boolean onKey(View view, int keyCode, KeyEvent event) {
//        if (view.getId() == R.id.digits) {
//            if (keyCode == KeyEvent.KEYCODE_ENTER) {
//                return true;
//            }
//
//        }
        return false;
    }

    /**
     * When a key is pressed, we start playing DTMF tone, do vibration, and enter the digit
     * immediately. When a key is released, we stop the tone. Note that the "key press" event will
     * be delivered by the system with certain amount of delay, it won't be synced with user's
     * actual "touch-down" behavior.
     */
    @Override
    public void onPressed(View view, boolean pressed) {
        if (DEBUG) Log.d(TAG, "onPressed(). view: " + view + ", pressed: " + pressed);
        if (pressed) {
            int resId = view.getId();
            if (resId == R.id.one) {
                keyPressed(KeyEvent.KEYCODE_1);
            } else if (resId == R.id.two) {
                keyPressed(KeyEvent.KEYCODE_2);
            } else if (resId == R.id.three) {
                keyPressed(KeyEvent.KEYCODE_3);
            } else if (resId == R.id.four) {
                keyPressed(KeyEvent.KEYCODE_4);
            } else if (resId == R.id.five) {
                keyPressed(KeyEvent.KEYCODE_5);
            } else if (resId == R.id.six) {
                keyPressed(KeyEvent.KEYCODE_6);
            } else if (resId == R.id.seven) {
                keyPressed(KeyEvent.KEYCODE_7);
            } else if (resId == R.id.eight) {
                keyPressed(KeyEvent.KEYCODE_8);
            } else if (resId == R.id.nine) {
                keyPressed(KeyEvent.KEYCODE_9);
            } else if (resId == R.id.zero) {
                keyPressed(KeyEvent.KEYCODE_0);
            } else if (resId == R.id.pound) {
                keyPressed(KeyEvent.KEYCODE_POUND);
            } else if (resId == R.id.star) {
                keyPressed(KeyEvent.KEYCODE_STAR);
            } else {
                Log.wtf(TAG, "Unexpected onTouch(ACTION_DOWN) event from: " + view);
            }
            mPressedDialpadKeys.add(view);
        } else {
            mPressedDialpadKeys.remove(view);
            if (mPressedDialpadKeys.isEmpty()) {
                stopTone();
            }
        }
    }

    @Override
    public void onClick(View view) {
        int resId = view.getId();
        if (resId == R.id.dialpad_floating_action_button) {
            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            String num = mDigits.getText().toString().trim();
            if (TextUtils.isEmpty(num)) {
                hideSelf();
                return;
            }
            if (!isMobileNO(num)) {
                mDigits.getText().clear();
                Toast.makeText(getContext(), "请输入正确的电话号码", Toast.LENGTH_LONG).show();
                return;
            }
            if (null != dialpadListener && null != dialpadListener.get()) {
                dialpadListener.get().onDialpad(num);
            }
        } else if (resId == R.id.deleteButton || resId == R.id.dialpad_right) {
            keyPressed(KeyEvent.KEYCODE_DEL);
        } else if (resId == R.id.digits) {
            if (!isDigitsEmpty()) {
                mDigits.setCursorVisible(true);
            }
        } else if (resId == R.id.dialpad_left) {
            hideSelf();
        } else {
            Log.wtf(TAG, "Unexpected onClick() event from: " + view);
        }
    }

    public interface DialpadListener {
        // 拨号回调
        void onDialpad(String num);

        /**
         * 输入过滤
         *
         * @param input
         * @return 过滤后的string
         */
        void onInputFiltter(Editable input);
    }

    private boolean isMobileNO(String mobiles) {
        if (TextUtils.isEmpty(mobiles)) {
            return false;
        } else {
            Pattern p = Pattern.compile(telRegex);
            return p.matcher(mobiles).matches();
        }
    }

    public void clearDialpad() {
        if (mDigits != null) {
            mDigits.getText().clear();
        }
    }

    /**
     * Play the specified tone for the specified milliseconds
     * <p>
     * The tone is played locally, using the audio stream for phone calls.
     * Tones are played only if the "Audible touch tones" user preference
     * is checked, and are NOT played if the device is in silent mode.
     * <p>
     * The tone length can be -1, meaning "keep playing the tone." If the caller does so, it should
     * call stopTone() afterward.
     *
     * @param tone       a tone code from {@link ToneGenerator}
     * @param durationMs tone length.
     */
    private void playTone(int tone, int durationMs) {
        AudioManager audioManager =
                (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        int ringerMode = audioManager.getRingerMode();
        if ((ringerMode == AudioManager.RINGER_MODE_SILENT)
                || (ringerMode == AudioManager.RINGER_MODE_VIBRATE)) {
            return;
        }

        synchronized (mToneGeneratorLock) {
            if (mToneGenerator == null) {
                Log.w(TAG, "playTone: mToneGenerator == null, tone: " + tone);
                return;
            }
            mToneGenerator.startTone(tone, durationMs);
        }
    }

    /**
     * Stop the tone if it is played.
     */
    private void stopTone() {
        synchronized (mToneGeneratorLock) {
            if (mToneGenerator == null) {
                Log.w(TAG, "stopTone: mToneGenerator == null");
                return;
            }
            mToneGenerator.stopTone();
        }
    }

    /**
     * @return true if the widget with the phone number digits is empty.
     */
    private boolean isDigitsEmpty() {
        return mDigits.length() == 0;
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        final DialActivity activity = (DialActivity) getActivity();
        if (activity == null) return;
        if (!hidden) {
            mFloatingActionButtonController.setVisible(false);
            mFloatingActionButtonController.scaleIn(100);
            mDigits.requestFocus();
        } else {
            mFloatingActionButtonController.scaleOut();
        }
    }
}

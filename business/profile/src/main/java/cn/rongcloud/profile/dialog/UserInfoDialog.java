package cn.rongcloud.profile.dialog;

import android.content.Context;
import android.net.Uri;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.basis.ui.BaseDialog;
import com.basis.utils.ImageLoader;
import com.basis.utils.KToast;
import com.basis.widget.ChineseLengthFilter;

import cn.rongcloud.config.UserManager;
import cn.rongcloud.profile.R;


public class UserInfoDialog extends BaseDialog implements View.OnClickListener {

    private OnUserListener listener;

    public interface OnUserListener {
        void onSave(String userName, Uri portrait);

        void onSelected();

//        void onLogout();
    }

    public UserInfoDialog(Context context, OnUserListener listener) {
        super(context, R.layout.layout_user_info_popup_window, true);
        this.listener = listener;
    }

    @Override
    public void initListener() {

    }

    private Uri selectedPicPath;
    private ImageView portrait;
    private EditText etName;

    @Override
    public void initView() {
        portrait = rootView.findViewById(R.id.iv_portrait);
        ImageLoader.loadUri(portrait, UserManager.get().getPortraitUri(), R.drawable.default_portrait);
        etName = rootView.findViewById(R.id.et_user_name);
        etName.setFilters(new InputFilter[]{new ChineseLengthFilter(20)});
        etName.setText(UserManager.get().getUserName());
        rootView.findViewById(R.id.iv_close).setOnClickListener(this);
        rootView.findViewById(R.id.tv_save_user_info).setOnClickListener(this);
        rootView.findViewById(R.id.tv_logout).setOnClickListener(this);
        portrait.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (R.id.iv_close == id) {
            dismiss();
        } else if (R.id.tv_save_user_info == id) {
            String userName = etName.getText().toString().trim();
            if (TextUtils.isEmpty(userName)) {
                KToast.show(R.string.username_can_not_be_empty);
                return;
            }
            if (null != listener) listener.onSave(userName, selectedPicPath);
        } else if (R.id.tv_logout == id) {
//            if (null != listener) listener.onLogout();
        } else if (R.id.iv_portrait == id) {
            if (null != listener) listener.onSelected();
        }
    }


    public void setUserPortrait(Uri uri) {
        if (null == uri) return;
        selectedPicPath = uri;
        if (null != portrait) {
            ImageLoader.loadUri(portrait, selectedPicPath, R.drawable.default_portrait);
        }
    }
}

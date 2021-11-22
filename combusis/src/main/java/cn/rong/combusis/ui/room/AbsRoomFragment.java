package cn.rong.combusis.ui.room;

import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.ViewTreeObserver;

import androidx.annotation.Nullable;

import com.basis.mvp.BasePresenter;
import com.basis.ui.BaseFragment;
import com.kit.utils.Logger;
import com.rongcloud.common.utils.UiUtils;

import cn.rong.combusis.common.ui.dialog.ConfirmDialog;
import cn.rong.combusis.widget.miniroom.MiniRoomManager;
import io.rong.imkit.utils.PermissionCheckUtil;
import io.rong.imkit.utils.StatusBarUtil;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;

/**
 * @author gyn
 * @date 2021/9/17
 */
public abstract class AbsRoomFragment<P extends BasePresenter> extends BaseFragment<P> implements SwitchRoomListener {

    public static final String ROOM_ID = "ROOM_ID";

    // 是否执行了joinRoom
    private boolean isExecuteJoinRoom = false;
    private int bottomMargin = 0;
    private int screenHeight = 0;
    // 是否是请求开启悬浮窗权限的过程中
    private boolean checkingOverlaysPermission;
    private ViewTreeObserver.OnGlobalLayoutListener globalLayoutListener = () -> {
        Rect rect = new Rect();
        getLayout().getWindowVisibleDisplayFrame(rect);
        int height = screenHeight - rect.bottom;
        // 假定高度超过屏幕的1/4代表键盘弹出了
        if (height < screenHeight / 4) {
            height = 0;
        }
        if (bottomMargin != height) {
            bottomMargin = height;
            onSoftKeyboardChange(bottomMargin);
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.d("==================================onCreate:" + getTag());
    }

    @Override
    public void preJoinRoom() {
        if (!isExecuteJoinRoom) {
            isExecuteJoinRoom = true;
            joinRoom();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        MiniRoomManager.getInstance().close();
        Logger.d("==================================onStart:" + getTag());
    }

    @Override
    public void destroyRoom() {
        isExecuteJoinRoom = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        // viewPager2的onPageSelected，当从列表点击最后一个时，viewpager2选中最后一个时，会先执行onPageSelected，才执行fragment的onCreate,
        // 导致addSwitchRoomListener没执行，joinRoom就不会执行，这里判断一下没执行再执行一下。
        preJoinRoom();
        Logger.d("==================================onResume:" + getTag());
    }

    @Override
    public void onPause() {
        super.onPause();
        Logger.d("==================================onPause:" + getTag());
    }

    @Override
    public void onStop() {
        super.onStop();
        Logger.d("==================================onStop:" + getTag());
    }

    public boolean checkDrawOverlaysPermission(boolean needOpenPermissionSetting) {
        if (Build.BRAND.toLowerCase().contains("xiaomi") || Build.VERSION.SDK_INT >= 23) {
            if (PermissionCheckUtil.canDrawOverlays(requireContext(), needOpenPermissionSetting)) {
                checkingOverlaysPermission = false;
                return true;
            } else {
                if (needOpenPermissionSetting && !Build.BRAND.toLowerCase().contains("xiaomi")) {
                    checkingOverlaysPermission = true;
                }
                return false;
            }
        } else {
            checkingOverlaysPermission = false;
            return true;
        }
    }

    public void showOpenOverlaysPermissionDialog() {
        new ConfirmDialog(requireContext(), "前往设置打开悬浮窗权限",
                true, "确定", "取消", null, new Function0<Unit>() {
            @Override
            public Unit invoke() {
                Intent intent = new Intent("android.settings.action.MANAGE_OVERLAY_PERMISSION", Uri.parse("package:" + requireActivity().getPackageName()));
                requireActivity().startActivity(intent);
                return null;
            }
        }).show();
    }

    @Override
    public void initListener() {
        addSwitchRoomListener();
        initSoftKeyboardListener();
    }

    @Override
    public void onDestroyView() {
        removeSwitchRoomListener();
        getLayout().getViewTreeObserver().removeOnGlobalLayoutListener(globalLayoutListener);
        super.onDestroyView();
    }

    private void initSoftKeyboardListener() {
        // 由于状态栏透明和键盘有冲突，所以监听键盘弹出时顶起布局
        screenHeight = UiUtils.INSTANCE.getScreenHeight(requireContext()) + StatusBarUtil.getStatusBarHeight(requireContext());
        getLayout().getViewTreeObserver().addOnGlobalLayoutListener(globalLayoutListener);
    }

    public abstract void onSoftKeyboardChange(int height);
}


package cn.rong.combusis.userprovide;

import androidx.annotation.NonNull;

import com.rongcloud.common.net.ApiConstant;
import com.rongcloud.common.net.IResultBack;

import org.jetbrains.annotations.Nullable;

import io.rong.imlib.model.UserInfo;

public interface IUserProvider {
    String API_BATCH = ApiConstant.INSTANCE.getBASE_URL() + "/user/batch";

    /**
     * 同步获取 会阻塞调用线程 必须非主线程调用
     *
     * @param userId
     */
    @Deprecated
    @Nullable
    UserInfo getUser(String userId);

    /**
     * 异步获取
     *
     * @param userId
     * @param resultBack
     */
    void getUserAsyn(@NonNull String userId, IResultBack<UserInfo> resultBack);
}

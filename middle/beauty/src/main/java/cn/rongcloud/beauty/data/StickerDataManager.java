package cn.rongcloud.beauty.data;

import android.text.TextUtils;
import android.util.Log;

import com.basis.net.oklib.OkApi;
import com.basis.net.oklib.OkParams;
import com.basis.net.oklib.WrapperCallBack;
import com.basis.net.oklib.api.callback.FileIOCallBack;
import com.basis.net.oklib.wrapper.Wrapper;
import com.basis.utils.GsonUtil;
import com.basis.utils.KToast;
import com.basis.utils.SharedPreferUtil;
import com.basis.utils.UIKit;
import com.faceunity.core.entity.FUBundleData;
import com.faceunity.core.enumeration.CameraFacingEnum;
import com.faceunity.core.enumeration.FUTransformMatrixEnum;
import com.faceunity.core.faceunity.FURenderKit;
import com.faceunity.core.model.prop.Prop;
import com.faceunity.core.model.prop.sticker.FineSticker;
import com.faceunity.nama.FURenderer;

import java.io.File;
import java.util.List;

import cn.rongcloud.beauty.entity.StickerBean;
import cn.rongcloud.beauty.entity.StickerCategory;
import cn.rongcloud.beauty.listener.DataCallback;
import cn.rongcloud.beauty.listener.StickerDataSourceListener;
import cn.rongcloud.config.AppConfig;

/**
 * @author gyn
 * @date 2022/10/14
 */
public class StickerDataManager implements StickerDataSourceListener {

    private String bundlePath;
    private static final String KEY_STICKER_DATA = "KEY_STICKER_DATA";
    private boolean isFrontCamera = true;
    public final static String HOST = AppConfig.get().getBaseServerAddress().substring(0, AppConfig.get().getBaseServerAddress().length() - 1);
    private static final String STICKER_URL = HOST + "/props/queryProps";
    private static final String STICKER_DOWNLOAD_URL = HOST + "/props/download";

    public StickerDataManager() {
        bundlePath = UIKit.getContext().getExternalFilesDir("Bundle").getAbsolutePath();
    }

    public static StickerDataManager getInstance() {
        return Holder.instance;
    }

    private static class Holder {
        private static final StickerDataManager instance = new StickerDataManager();
    }

    private List<StickerCategory> stickerCategories;

    private StickerBean selectedSticker;

    public void clear() {
        selectedSticker = null;
        stickerCategories = null;
        isFrontCamera = true;
    }

    @Override
    public void getStickerCategories(DataCallback<List<StickerCategory>> dataCallback) {
        if (checkEmptyAndBundleLoaded(stickerCategories)) {
            dataCallback.onResult(stickerCategories);
        } else {
            String json = SharedPreferUtil.get(KEY_STICKER_DATA);
            if (!TextUtils.isEmpty(json)) {
                stickerCategories = GsonUtil.json2List(json, StickerCategory.class);
            }
            if (checkEmptyAndBundleLoaded(stickerCategories)) {
                dataCallback.onResult(stickerCategories);
            } else {
                OkApi.get(STICKER_URL, null, new WrapperCallBack() {
                    @Override
                    public void onResult(Wrapper result) {
                        stickerCategories = result.getList(StickerCategory.class);
                        if (checkEmptyAndBundleLoaded(stickerCategories)) {
                            SharedPreferUtil.set(KEY_STICKER_DATA, GsonUtil.obj2Json(stickerCategories));
                            dataCallback.onResult(stickerCategories);
                        }
                    }
                });
            }
        }
    }

    private boolean checkEmptyAndBundleLoaded(List<StickerCategory> list) {
        if (list == null || list.size() == 0) {
            return false;
        }
        for (StickerCategory stickerCategory : list) {
            for (StickerBean stickerBean : stickerCategory.getStickerBeanList()) {
                stickerBean.initLocalPath(bundlePath);
            }
        }
        return true;
    }

    @Override
    public StickerBean getSelectedSticker() {
        return selectedSticker;
    }

    @Override
    public void onSelectSticker(StickerBean stickerBean, DataCallback<Boolean> dataCallback) {
        if (selectedSticker == stickerBean) {
            return;
        }
        selectedSticker = stickerBean;
        if (!stickerBean.hasDownload()) {
            downloadSticker(selectedSticker, dataCallback);
        } else {
            setSticker(stickerBean);
            dataCallback.onResult(true);
        }
    }

    private void setSticker(StickerBean stickerBean) {
        Prop newProp = new FineSticker(new FUBundleData(stickerBean.getLocalPath()));
        FURenderKit.getInstance().getPropContainer().removeAllProp();
        FURenderKit.getInstance().getPropContainer().addProp(newProp);
        setRotation(isFrontCamera);
        Log.d(
                "===========",
                "externalInputType = "
                        + FURenderer.getInstance().getExternalInputType()
                        + ", inputOrientation = "
                        + FURenderer.getInstance().getInputOrientation()
                        + ", deviceOrientation = "
                        + FURenderer.getInstance().getDeviceOrientation()
                        + ", inputBufferMatrix = "
                        + FURenderer.getInstance().getInputBufferMatrix()
                        + ", inputTextureMatrix = "
                        + FURenderer.getInstance().getInputTextureMatrix()
                        + ", cameraFacing = "
                        + FURenderer.getInstance().getCameraFacing()
                        + ", outputMatrix = "
                        + FURenderer.getInstance().getOutputMatrix()
                        + ", maxFaces = "
                        + FURenderKit.getInstance().getFUAIController().getMaxFaces()
        );
    }

    public void switchCamera() {
        this.isFrontCamera = !this.isFrontCamera;
        setRotation(isFrontCamera);
    }

    // rtc插件内部默认的贴纸方向设置有问题，这里根据前后摄像头设置贴纸旋转
    private void setRotation(boolean isFrontCamera) {
        if (isFrontCamera) {
            FURenderer.getInstance().setCameraFacing(CameraFacingEnum.CAMERA_FRONT);
            FURenderer.getInstance().setInputOrientation(270);
            FURenderer.getInstance().setInputBufferMatrix(FUTransformMatrixEnum.CCROT90);
            FURenderer.getInstance().setInputTextureMatrix(FUTransformMatrixEnum.CCROT90);
            FURenderer.getInstance().setOutputMatrix(FUTransformMatrixEnum.CCROT90_FLIPHORIZONTAL);
        } else {
            FURenderer.getInstance().setCameraFacing(CameraFacingEnum.CAMERA_BACK);
            FURenderer.getInstance().setInputOrientation(90);
            FURenderer.getInstance().setInputBufferMatrix(FUTransformMatrixEnum.CCROT90_FLIPVERTICAL);
            FURenderer.getInstance().setInputTextureMatrix(FUTransformMatrixEnum.CCROT90_FLIPVERTICAL);
            FURenderer.getInstance().setOutputMatrix(FUTransformMatrixEnum.CCROT90);
        }
    }

    @Override
    public void onClearSticker() {
        selectedSticker = null;
        FURenderKit.getInstance().getPropContainer().removeAllProp();
    }

    public void downloadSticker(StickerBean stickerBean, DataCallback<Boolean> dataCallback) {
        if (stickerBean == null) {
            return;
        }
        String cacheName = stickerBean.getFileName();
        String dir = bundlePath;
        File file = new File(dir, cacheName);
        if (file.exists()) {
            return;
        }
        OkApi.download(STICKER_DOWNLOAD_URL, OkParams.Builder().add("id", stickerBean.getId()).build(), new FileIOCallBack(dir, cacheName) {
            @Override
            public void onResult(File result) {
                super.onResult(result);
                stickerBean.setLocalPath(result.getAbsolutePath());
                stickerBean.setLoading(false);
                if (stickerBean == selectedSticker) {
                    setSticker(stickerBean);
                }
                dataCallback.onResult(true);
            }

            @Override
            public void onError(int code, String msg) {
                super.onError(code, msg);
                stickerBean.setLoading(false);
                dataCallback.onResult(true);
                KToast.show("下载失败，请重试！");
            }
        });
    }

}

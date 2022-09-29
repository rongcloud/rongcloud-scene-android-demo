package cn.rc.community.activity;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//


import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.viewpager2.widget.ViewPager2;
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.rong.common.FileUtils;
import io.rong.common.RLog;
import io.rong.imkit.IMCenter;
import io.rong.imkit.R.id;
import io.rong.imkit.R.layout;
import io.rong.imkit.R.string;
import io.rong.imkit.activity.RongBaseNoActionbarActivity;
import io.rong.imkit.event.actionevent.BaseMessageEvent;
import io.rong.imkit.event.actionevent.DeleteEvent;
import io.rong.imkit.event.actionevent.RecallEvent;
import io.rong.imkit.feature.destruct.DestructManager;
import io.rong.imkit.picture.widget.longimage.SubsamplingScaleImageView;
import io.rong.imkit.picture.widget.longimage.Utils;
import io.rong.imkit.utils.ExecutorHelper;
import io.rong.imkit.utils.KitStorageUtils;
import io.rong.imkit.utils.PermissionCheckUtil;
import io.rong.imkit.widget.dialog.OptionsPopupDialog;
import io.rong.imkit.widget.dialog.OptionsPopupDialog.OnOptionsItemClickedListener;
import io.rong.imlib.ChannelClient;
import io.rong.imlib.IRongCoreCallback;
import io.rong.imlib.IRongCoreEnum;
import io.rong.imlib.RongCommonDefine.GetMessageDirection;
import io.rong.imlib.RongIMClient.DestructCountDownTimerListener;
import io.rong.imlib.RongIMClient.OnRecallMessageListener;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Conversation.ConversationType;
import io.rong.imlib.model.HistoryMessageOption;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.Message.MessageDirection;
import io.rong.message.ImageMessage;
import io.rong.message.RecallNotificationMessage;
import io.rong.message.ReferenceMessage;

public class CoolViewActivity extends RongBaseNoActionbarActivity implements OnLongClickListener {
    private static final String TAG = "CoolViewActivity";
    private static final int IMAGE_MESSAGE_COUNT = 10;
    private static final int LOAD_PICTURE_TIMEOUT = 30000;
    protected ViewPager2 mViewPager;
    protected ImageMessage mCurrentImageMessage;
    protected Message mMessage;
    protected ConversationType mConversationType;
    protected int mCurrentMessageId;
    protected String mTargetId = null;
    protected CoolViewActivity.ImageAdapter mImageAdapter;
    protected boolean isFirstTime = false;
    protected OnPageChangeCallback mPageChangeListener = new OnPageChangeCallback() {
        public void onPageSelected(int position) {
            if (position == CoolViewActivity.this.mImageAdapter.getItemCount() - 1) {
                if (CoolViewActivity.this.mImageAdapter.getItemCount() > 0) {
                    ImageInfo imageInfo = mImageAdapter.getmImageList().get(mImageAdapter.getItemCount() - 1);
                    CoolViewActivity.this.getConversationImageUris(imageInfo.getMessage().getSentTime(), GetMessageDirection.BEHIND);
                }
            } else if (position == 0 && CoolViewActivity.this.mImageAdapter.getItemCount() > 0) {
                ImageInfo imageInfo = mImageAdapter.getmImageList().get(0);
                CoolViewActivity.this.getConversationImageUris(imageInfo.getMessage().getSentTime(), GetMessageDirection.FRONT);
            }

        }
    };
    OnRecallMessageListener mOnRecallMessageListener = new OnRecallMessageListener() {
        public boolean onMessageRecalled(Message message, RecallNotificationMessage recallNotificationMessage) {
            if (CoolViewActivity.this.mCurrentMessageId == message.getMessageId()) {
                (new Builder(CoolViewActivity.this, 5)).setMessage(CoolViewActivity.this.getString(string.rc_recall_success)).setPositiveButton(CoolViewActivity.this.getString(string.rc_dialog_ok), new OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        CoolViewActivity.this.finish();
                    }
                }).setCancelable(false).show();
            } else {
                CoolViewActivity.this.mImageAdapter.removeRecallItem(message.getMessageId());
                if (CoolViewActivity.this.mImageAdapter.getItemCount() == 0) {
                    CoolViewActivity.this.finish();
                }
            }

            return false;
        }
    };
    BaseMessageEvent mBaseMessageEvent = new BaseMessageEvent() {
        public void onDeleteMessage(DeleteEvent event) {
            RLog.d("CoolViewActivity", "MessageDeleteEvent");
            if (event.getMessageIds() != null) {
                int[] var2 = event.getMessageIds();
                int var3 = var2.length;

                for (int var4 = 0; var4 < var3; ++var4) {
                    int messageId = var2[var4];
                    CoolViewActivity.this.mImageAdapter.removeRecallItem(messageId);
                }

                CoolViewActivity.this.mImageAdapter.notifyDataSetChanged();
                if (CoolViewActivity.this.mImageAdapter.getItemCount() == 0) {
                    CoolViewActivity.this.finish();
                }
            }

        }

        public void onRecallEvent(RecallEvent event) {
            if (CoolViewActivity.this.mCurrentMessageId == event.getMessageId()) {
                (new Builder(CoolViewActivity.this, 5)).setMessage(CoolViewActivity.this.getString(string.rc_recall_success)).setPositiveButton(CoolViewActivity.this.getString(string.rc_dialog_ok), new OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        CoolViewActivity.this.finish();
                    }
                }).setCancelable(false).show();
            } else {
                CoolViewActivity.this.mImageAdapter.removeRecallItem(event.getMessageId());
                CoolViewActivity.this.mImageAdapter.notifyDataSetChanged();
                if (CoolViewActivity.this.mImageAdapter.getItemCount() == 0) {
                    CoolViewActivity.this.finish();
                }
            }

        }
    };
    private String channelId;

    public CoolViewActivity() {
    }

    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == 2 || newConfig.orientation == 1) {
            this.mImageAdapter.notifyDataSetChanged();
        }

    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(layout.rc_fr_photo);
        Message currentMessage = (Message) this.getIntent().getParcelableExtra("message");
        this.mMessage = currentMessage;
        if (currentMessage.getContent() instanceof ReferenceMessage) {
            ReferenceMessage referenceMessage = (ReferenceMessage) currentMessage.getContent();
            this.mCurrentImageMessage = (ImageMessage) referenceMessage.getReferenceContent();
        } else {
            this.mCurrentImageMessage = (ImageMessage) currentMessage.getContent();
        }

        this.mConversationType = currentMessage.getConversationType();
        this.mCurrentMessageId = currentMessage.getMessageId();
        this.mTargetId = currentMessage.getTargetId();
        this.channelId = currentMessage.getChannelId();
        this.mViewPager = (ViewPager2) this.findViewById(id.viewpager);
        this.mViewPager.registerOnPageChangeCallback(this.mPageChangeListener);
        this.mImageAdapter = new CoolViewActivity.ImageAdapter();
        this.isFirstTime = true;
        if (!this.mMessage.getContent().isDestruct() && !(this.mMessage.getContent() instanceof ReferenceMessage)) {
            this.getConversationImageUris(mMessage.getSentTime(), GetMessageDirection.FRONT);
            this.getConversationImageUris(mMessage.getSentTime(), GetMessageDirection.BEHIND);
        } else {
            ArrayList<CoolViewActivity.ImageInfo> lists = new ArrayList();
            lists.add(new CoolViewActivity.ImageInfo(this.mMessage, this.mCurrentImageMessage.getThumUri(), this.mCurrentImageMessage.getLocalUri() == null ? this.mCurrentImageMessage.getRemoteUri() : this.mCurrentImageMessage.getLocalUri()));
            this.mImageAdapter.addData(lists, true);
        }

        this.mViewPager.setAdapter(this.mImageAdapter);
        IMCenter.getInstance().addMessageEventListener(this.mBaseMessageEvent);
        IMCenter.getInstance().addOnRecallMessageListener(this.mOnRecallMessageListener);
    }

    protected void onDestroy() {
        super.onDestroy();
        IMCenter.getInstance().removeOnRecallMessageListener(this.mOnRecallMessageListener);
        IMCenter.getInstance().removeMessageEventListener(this.mBaseMessageEvent);
    }

    private void getConversationImageUris(long sentTime, final GetMessageDirection direction) {
        if (this.mConversationType != null && !TextUtils.isEmpty(this.mTargetId) && !TextUtils.isEmpty(this.channelId)) {
            HistoryMessageOption historyMessageOption = new HistoryMessageOption();
            historyMessageOption.setDataTime(sentTime);
            historyMessageOption.setCount(20);
            historyMessageOption.setOrder(direction == GetMessageDirection.FRONT ?
                    HistoryMessageOption.PullOrder.DESCEND : HistoryMessageOption.PullOrder.ASCEND);

            ChannelClient.getInstance().getMessages(Conversation.ConversationType.ULTRA_GROUP
                    , mTargetId, channelId, historyMessageOption,
                    new IRongCoreCallback.IGetMessageCallbackEx() {
                        @Override
                        public void onComplete(List<Message> messages, long syncTimestamp, boolean hasMoreMsg, IRongCoreEnum.CoreErrorCode errorCode) {
                            ArrayList<CoolViewActivity.ImageInfo> lists = new ArrayList();
                            if (messages != null) {
                                if (direction.equals(GetMessageDirection.FRONT)) {
                                    Collections.reverse(messages);
                                }

                                for (int i = 0; i < messages.size(); ++i) {
                                    Message message = (Message) messages.get(i);
                                    if (message.getContent() instanceof ImageMessage && !message.getContent().isDestruct()) {
                                        ImageMessage imageMessage = (ImageMessage) message.getContent();
                                        Uri largeImageUri = imageMessage.getLocalUri() == null ? imageMessage.getRemoteUri() : imageMessage.getLocalUri();
                                        if (imageMessage.getThumUri() != null && largeImageUri != null) {
                                            lists.add(CoolViewActivity.this.new ImageInfo(message, imageMessage.getThumUri(), largeImageUri));
                                        }
                                    }
                                }
                            }

                            if (direction.equals(GetMessageDirection.FRONT)) {
                                if (CoolViewActivity.this.isFirstTime) {
                                    lists.add(CoolViewActivity.this.new ImageInfo(CoolViewActivity.this.mMessage, CoolViewActivity.this.mCurrentImageMessage.getThumUri(), CoolViewActivity.this.mCurrentImageMessage.getLocalUri() == null ? CoolViewActivity.this.mCurrentImageMessage.getRemoteUri() : CoolViewActivity.this.mCurrentImageMessage.getLocalUri()));
                                }

                                CoolViewActivity.this.mImageAdapter.addData(lists, true);
                                if (CoolViewActivity.this.isFirstTime) {
                                    int index = CoolViewActivity.this.mImageAdapter.getIndexByMessageId(CoolViewActivity.this.mMessage.getMessageId());
                                    if (index != -1) {
                                        CoolViewActivity.this.mViewPager.setCurrentItem(index, false);
                                    }

                                    CoolViewActivity.this.isFirstTime = false;
                                }
                            } else if (lists.size() > 0) {
                                CoolViewActivity.this.mImageAdapter.addData(lists, false);
                            }
                        }

                        @Override
                        public void onFail(IRongCoreEnum.CoreErrorCode errorCode) {
                            Log.e(TAG, "onFail: ");
                        }
                    }
            );
        }

    }

    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    public boolean onLongClick(View v) {
        if (this.mCurrentImageMessage.isDestruct()) {
            return false;
        } else {
            CoolViewActivity.ImageInfo imageInfo = this.mImageAdapter.getItem(this.mViewPager.getCurrentItem());
            if (imageInfo != null && imageInfo.isDownload()) {
                Uri thumbUri = imageInfo.getThumbUri();
                final Uri largeImageUri = imageInfo.getLargeImageUri();
                if (this.onPictureLongClick(v, thumbUri, largeImageUri)) {
                    return true;
                }

                String[] items = new String[]{this.getString(string.rc_save_picture)};
                OptionsPopupDialog.newInstance(this, items).setOptionsPopupDialogListener(new OnOptionsItemClickedListener() {
                    public void onOptionsItemClicked(int which) {
                        if (which == 0) {
                            String[] permissions = new String[]{"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"};
                            if (!PermissionCheckUtil.requestPermissions(CoolViewActivity.this, permissions)) {
                                return;
                            }

                            ExecutorHelper.getInstance().diskIO().execute(new Runnable() {
                                public void run() {
                                    File file;
                                    if (!largeImageUri.getScheme().startsWith("http") && !largeImageUri.getScheme().startsWith("https")) {
                                        if (largeImageUri.getScheme().startsWith("file")) {
                                            file = new File(largeImageUri.toString().substring(7));
                                        } else {
                                            file = new File(largeImageUri.toString());
                                        }
                                    } else {
                                        try {
                                            file = (File) Glide.with(CoolViewActivity.this).asFile().load(largeImageUri).submit().get(10L, TimeUnit.SECONDS);
                                        } catch (ExecutionException var4) {
                                            file = null;
                                            RLog.e("CoolViewActivity", "onOptionsItemClicked", var4);
                                        } catch (InterruptedException var5) {
                                            file = null;
                                            RLog.e("CoolViewActivity", "onOptionsItemClicked", var5);
                                            Thread.currentThread().interrupt();
                                        } catch (TimeoutException var6) {
                                            file = null;
                                            RLog.e("CoolViewActivity", "onOptionsItemClicked", var6);
                                        }
                                    }

                                    final String toast;
                                    if (file != null && file.exists()) {
                                        boolean result = KitStorageUtils.saveMediaToPublicDir(CoolViewActivity.this, file, "image");
                                        if (result) {
                                            toast = CoolViewActivity.this.getString(string.rc_save_picture_at);
                                        } else {
                                            toast = CoolViewActivity.this.getString(string.rc_src_file_not_found);
                                        }
                                    } else {
                                        toast = CoolViewActivity.this.getString(string.rc_src_file_not_found);
                                    }

                                    ExecutorHelper.getInstance().mainThread().execute(new Runnable() {
                                        public void run() {
                                            Toast.makeText(CoolViewActivity.this, toast, 0).show();
                                        }
                                    });
                                }
                            });
                        }

                    }
                }).show();
            }

            return true;
        }
    }

    public boolean onPictureLongClick(View v, Uri thumbUri, Uri largeImageUri) {
        return false;
    }

    protected class ImageInfo {
        private Message message;
        private Uri thumbUri;
        private Uri largeImageUri;
        private boolean download;

        ImageInfo(Message message, Uri thumbnail, Uri largeImageUri) {
            this.message = message;
            this.thumbUri = thumbnail;
            this.largeImageUri = largeImageUri;
        }

        public Message getMessage() {
            return this.message;
        }

        public Uri getLargeImageUri() {
            return this.largeImageUri;
        }

        public Uri getThumbUri() {
            return this.thumbUri;
        }

        public boolean isDownload() {
            return this.download;
        }

        public void setDownload(boolean download) {
            this.download = download;
        }
    }

    protected class ImageAdapter extends Adapter<CoolViewActivity.ImageAdapter.ViewHolder> {
        private ArrayList<CoolViewActivity.ImageInfo> mImageList = new ArrayList();

        protected ImageAdapter() {
        }

        @NonNull
        public CoolViewActivity.ImageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(layout.rc_fr_image, parent, false);
            CoolViewActivity.ImageAdapter.ViewHolder holder = new CoolViewActivity.ImageAdapter.ViewHolder(view);
            return holder;
        }

        public void onBindViewHolder(@NonNull CoolViewActivity.ImageAdapter.ViewHolder holder, int position) {
            this.updatePhotoView(position, holder);
            holder.photoView.setOnLongClickListener(CoolViewActivity.this);
            holder.photoView.setOnClickListener(new android.view.View.OnClickListener() {
                public void onClick(View v) {
                    Window window = CoolViewActivity.this.getWindow();
                    if (window != null) {
                        window.setFlags(2048, 2048);
                    }

                    CoolViewActivity.this.finish();
                }
            });
        }

        public int getItemCount() {
            return this.mImageList.size();
        }

        private void updatePhotoView(final int position, final CoolViewActivity.ImageAdapter.ViewHolder holder) {
            final CoolViewActivity.ImageInfo imageInfo = (CoolViewActivity.ImageInfo) this.mImageList.get(position);
            final Uri originalUri = imageInfo.getLargeImageUri();
            final Uri thumbUri = imageInfo.getThumbUri();
            if (originalUri != null && thumbUri != null) {
                if (CoolViewActivity.this.mCurrentImageMessage.isDestruct() && CoolViewActivity.this.mMessage.getMessageDirection().equals(MessageDirection.RECEIVE)) {
                    DestructManager.getInstance().addListener(CoolViewActivity.this.mMessage.getUId(), new CoolViewActivity.DestructListener(holder, CoolViewActivity.this.mMessage.getUId()), "CoolViewActivity");
                }

                ((RequestBuilder) Glide.with(CoolViewActivity.this).asBitmap().load(originalUri).timeout(30000)).into(new CustomTarget<Bitmap>() {
                    private Runnable mLoadFailedAction = null;

                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        holder.itemView.removeCallbacks(this.mLoadFailedAction);
                        int maxLoader = Utils.getMaxLoader();
                        if (resource != null && resource.getWidth() < maxLoader && resource.getHeight() < maxLoader) {
                            resource = resource.copy(Config.ARGB_8888, true);
                            if (CoolViewActivity.this.mCurrentImageMessage.isDestruct() && CoolViewActivity.this.mMessage.getMessageDirection().equals(MessageDirection.RECEIVE)) {
                                DestructManager.getInstance().startDestruct(CoolViewActivity.this.mMessage);
                            }

                            holder.progressText.setVisibility(8);
                            holder.failImg.setVisibility(8);
                            holder.progressBar.setVisibility(8);
                            holder.photoView.setVisibility(0);
                            holder.photoView.setBitmapAndFileUri(resource, (Uri) null);
                            imageInfo.download = true;
                        } else {
                            if (FileUtils.uriStartWithFile(originalUri)) {
                                if (CoolViewActivity.this.mCurrentImageMessage.isDestruct() && CoolViewActivity.this.mMessage.getMessageDirection().equals(MessageDirection.RECEIVE)) {
                                    DestructManager.getInstance().startDestruct(CoolViewActivity.this.mMessage);
                                }

                                holder.progressText.setVisibility(8);
                                holder.failImg.setVisibility(8);
                                holder.progressBar.setVisibility(8);
                                holder.photoView.setVisibility(0);
                                holder.photoView.setBitmapAndFileUri((Bitmap) null, originalUri);
                                imageInfo.download = true;
                                return;
                            }

                            ((RequestBuilder) Glide.with(CoolViewActivity.this).asFile().load(originalUri).timeout(30000)).into(new CustomTarget<File>() {
                                public void onResourceReady(@NonNull File resource, @Nullable Transition<? super File> transition) {
                                    if (CoolViewActivity.this.mCurrentImageMessage.isDestruct() && CoolViewActivity.this.mMessage.getMessageDirection().equals(MessageDirection.RECEIVE)) {
                                        DestructManager.getInstance().startDestruct(CoolViewActivity.this.mMessage);
                                    }

                                    holder.progressText.setVisibility(8);
                                    holder.failImg.setVisibility(8);
                                    holder.progressBar.setVisibility(8);
                                    holder.photoView.setVisibility(0);
                                    holder.photoView.setBitmapAndFileUri((Bitmap) null, Uri.fromFile(resource));
                                    imageInfo.download = true;
                                }

                                public void onLoadCleared(@Nullable Drawable placeholder) {
                                    holder.progressText.setVisibility(0);
                                    holder.progressText.setText(string.rc_load_image_failed);
                                    holder.progressBar.setVisibility(8);
                                    holder.failImg.setVisibility(0);
                                    holder.failImg.setOnClickListener(new android.view.View.OnClickListener() {
                                        public void onClick(View v) {
                                            CoolViewActivity.this.finish();
                                        }
                                    });
                                    holder.photoView.setVisibility(4);
                                }
                            });
                        }

                    }

                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        holder.progressText.setVisibility(0);
                        holder.progressText.setText(string.rc_load_image_failed);
                        holder.progressBar.setVisibility(8);
                        holder.failImg.setVisibility(0);
                        holder.failImg.setOnClickListener(new android.view.View.OnClickListener() {
                            public void onClick(View v) {
                                CoolViewActivity.this.finish();
                            }
                        });
                        holder.photoView.setVisibility(4);
                    }

                    public void onLoadStarted(@Nullable Drawable placeholder) {
                        holder.itemView.removeCallbacks(this.mLoadFailedAction);
                        String thumbPath = null;
                        if ("file".equals(thumbUri.getScheme())) {
                            thumbPath = thumbUri.toString().substring(7);
                        }

                        if (thumbPath == null) {
                            RLog.e("CoolViewActivity", "thumbPath should not be null.");
                        } else {
                            Bitmap tempBitmap = BitmapFactory.decodeFile(thumbPath);
                            if (tempBitmap == null) {
                                RLog.e("CoolViewActivity", "tempBitmap should not be null.");
                            } else {
                                holder.photoView.setVisibility(0);
                                holder.photoView.setBitmapAndFileUri(tempBitmap, (Uri) null);
                                holder.progressBar.setVisibility(0);
                                holder.failImg.setVisibility(8);
                                holder.progressText.setVisibility(8);
                                holder.startLoadTime = SystemClock.elapsedRealtime();
                            }
                        }
                    }

                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        super.onLoadFailed(errorDrawable);
                        long delayMillis = holder.startLoadTime + 30000L - SystemClock.elapsedRealtime();
                        holder.itemView.removeCallbacks(this.mLoadFailedAction);
                        if (delayMillis > 0L) {
                            this.mLoadFailedAction = new Runnable() {
                                public void run() {
                                    loadFailed(holder);
                                }
                            };
                            holder.itemView.postDelayed(this.mLoadFailedAction, delayMillis);
                        } else {
                            this.loadFailed(holder);
                        }

                    }

                    private void loadFailed(CoolViewActivity.ImageAdapter.ViewHolder holder) {
                        holder.progressText.setVisibility(0);
                        holder.progressText.setText(string.rc_load_image_failed);
                        holder.progressBar.setVisibility(8);
                        holder.failImg.setVisibility(0);
                        holder.failImg.setOnClickListener(new android.view.View.OnClickListener() {
                            public void onClick(View v) {
                                CoolViewActivity.this.finish();
                            }
                        });
                        holder.photoView.setVisibility(4);
                    }
                });
            } else {
                RLog.e("CoolViewActivity", "large uri and thumbnail uri of the image should not be null.");
            }
        }

        public Bitmap zoomImg(Bitmap bm, int newWidth, int newHeight) {
            int width = bm.getWidth();
            int height = bm.getHeight();
            float scaleWidth = (float) newWidth / (float) width;
            float scaleHeight = (float) newHeight / (float) height;
            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleHeight);
            Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
            return newbm;
        }

        public void addData(ArrayList<CoolViewActivity.ImageInfo> newImages, boolean direction) {
            if (newImages != null && newImages.size() != 0) {
                if (direction && !this.isDuplicate(((CoolViewActivity.ImageInfo) newImages.get(0)).getMessage().getMessageId())) {
                    this.mImageList.addAll(0, newImages);
                    this.notifyItemRangeInserted(0, newImages.size());
                } else if (!direction && !this.isDuplicate(((CoolViewActivity.ImageInfo) newImages.get(0)).getMessage().getMessageId())) {
                    this.mImageList.addAll(this.mImageList.size(), newImages);
                    this.notifyItemRangeInserted(this.mImageList.size(), newImages.size());
                }

            }
        }

        public ArrayList<ImageInfo> getmImageList() {
            return mImageList;
        }

        private boolean isDuplicate(int messageId) {
            Iterator var2 = this.mImageList.iterator();

            CoolViewActivity.ImageInfo info;
            do {
                if (!var2.hasNext()) {
                    return false;
                }

                info = (CoolViewActivity.ImageInfo) var2.next();
            } while (info.getMessage().getMessageId() != messageId);

            return true;
        }

        public CoolViewActivity.ImageInfo getItem(int index) {
            return (CoolViewActivity.ImageInfo) this.mImageList.get(index);
        }

        public int getIndexByMessageId(int messageId) {
            int index = -1;

            for (int i = 0; i < this.mImageList.size(); ++i) {
                if (((CoolViewActivity.ImageInfo) this.mImageList.get(i)).getMessage().getMessageId() == messageId) {
                    index = i;
                    break;
                }
            }

            return index;
        }

        private void removeRecallItem(int messageId) {
            for (int i = this.mImageList.size() - 1; i >= 0; --i) {
                if (((CoolViewActivity.ImageInfo) this.mImageList.get(i)).message.getMessageId() == messageId) {
                    this.mImageList.remove(i);
                    this.notifyItemRemoved(i);
                    break;
                }
            }

        }

        public class ViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
            ProgressBar progressBar;
            TextView progressText;
            ImageView failImg;
            SubsamplingScaleImageView photoView;
            TextView mCountDownView;
            long startLoadTime;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                this.progressBar = (ProgressBar) itemView.findViewById(id.rc_progress);
                this.progressText = (TextView) itemView.findViewById(id.rc_txt);
                this.failImg = (ImageView) itemView.findViewById(id.rc_fail_image);
                this.photoView = (SubsamplingScaleImageView) itemView.findViewById(id.rc_photoView);
                this.mCountDownView = (TextView) itemView.findViewById(id.rc_count_down);
            }
        }
    }

    private static class DestructListener implements DestructCountDownTimerListener {
        private WeakReference<CoolViewActivity.ImageAdapter.ViewHolder> mHolder;
        private String mMessageId;

        public DestructListener(CoolViewActivity.ImageAdapter.ViewHolder pHolder, String pMessageId) {
            this.mHolder = new WeakReference(pHolder);
            this.mMessageId = pMessageId;
        }

        public void onTick(long millisUntilFinished, String pMessageId) {
            if (this.mMessageId.equals(pMessageId)) {
                CoolViewActivity.ImageAdapter.ViewHolder viewHolder = (CoolViewActivity.ImageAdapter.ViewHolder) this.mHolder.get();
                if (viewHolder != null) {
                    viewHolder.mCountDownView.setVisibility(0);
                    viewHolder.mCountDownView.setText(String.valueOf(Math.max(millisUntilFinished, 1L)));
                }
            }

        }

        public void onStop(String messageId) {
            if (this.mMessageId.equals(messageId)) {
                CoolViewActivity.ImageAdapter.ViewHolder viewHolder = (CoolViewActivity.ImageAdapter.ViewHolder) this.mHolder.get();
                if (viewHolder != null) {
                    viewHolder.mCountDownView.setVisibility(8);
                }
            }

        }
    }
}

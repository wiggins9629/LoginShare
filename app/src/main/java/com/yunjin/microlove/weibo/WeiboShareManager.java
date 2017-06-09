package com.yunjin.microlove.weibo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.share.WbShareCallback;
import com.sina.weibo.sdk.share.WbShareHandler;
import com.sina.weibo.sdk.utils.Utility;
import com.yunjin.microlove.R;
import com.yunjin.microlove.utils.ToastUtil;

import static com.yunjin.microlove.utils.UIUtils.getResources;

/**
 * @Description 实现微博分享功能的核心类
 * @Author 一花一世界
 */
public class WeiboShareManager {

    private Activity mActivity;
    public static final int SHARE_CLIENT = 1;// 客户端
    public static final int SHARE_ALL_IN_ONE = 2; // 客户端或微博
    private WbShareHandler shareHandler;
    private SelfWbShareCallback mListener;
    private static WeiboShareManager mInstance;

    public WeiboShareManager(Activity activity) {
        this.mActivity = activity;
        initWeiboShare();
    }

    /**
     * 获取WeiboShareManager实例，非线程安全，请在UI线程中操作
     */
    public static WeiboShareManager getInstance(Activity activity) {
        if (mInstance == null) {
            mInstance = new WeiboShareManager(activity);
        }
        return mInstance;
    }

    /**
     * 初始化数据
     */
    private void initWeiboShare() {
        // 初始化WbShareHandler并注册应用
        shareHandler = new WbShareHandler(mActivity);
        shareHandler.registerApp();
        mListener = new SelfWbShareCallback();
    }

    /**
     * 处理分享回调
     */
    public void onNewIntent(Intent intent) {
        if (shareHandler != null) {
            shareHandler.doResultIntent(intent, mListener);
        }
    }

    /**
     * 分享文字信息
     */
    public void shareToWeiboText(String text, String title, String actionUrl, int mShareType) {
        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
        weiboMessage.textObject = getTextObj(text, title, actionUrl);
        shareHandler.shareMessage(weiboMessage, mShareType == SHARE_CLIENT);
    }

    /**
     * 分享图片信息
     */
    public void shareToWeiboImage(int imageId, int mShareType) {
        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
        weiboMessage.imageObject = getImageObj(imageId);
        shareHandler.shareMessage(weiboMessage, mShareType == SHARE_CLIENT);
    }

    /**
     * 分享图文信息
     */
    public void shareToWeiboTextImage(String text, String title, String actionUrl, int imageId, int mShareType) {
        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
        weiboMessage.textObject = getTextObj(text, title, actionUrl);
        weiboMessage.imageObject = getImageObj(imageId);
        shareHandler.shareMessage(weiboMessage, mShareType == SHARE_CLIENT);
    }

    /**
     * 分享网页信息
     */
    public void shareToWeiboWebpage(String title, String description, int imageId, String actionUrl, String defaultText, int mShareType) {
        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
        weiboMessage.mediaObject = getWebpageObj(title, description, imageId, actionUrl, defaultText);
        shareHandler.shareMessage(weiboMessage, mShareType == SHARE_CLIENT);
    }

    /**
     * 分享回调
     */
    private class SelfWbShareCallback implements WbShareCallback {

        @Override
        public void onWbShareSuccess() {
            ToastUtil.showText(R.string.share_success);
        }

        @Override
        public void onWbShareCancel() {
            ToastUtil.showText(R.string.share_canceled);
        }

        @Override
        public void onWbShareFail() {
            ToastUtil.showText(R.string.share_failed);
        }
    }

    /**
     * @return 文本消息对象
     * 创建文本消息对象
     */
    private TextObject getTextObj(String text, String title, String actionUrl) {
        TextObject textObject = new TextObject();
        textObject.text = text;
        textObject.title = title;
        textObject.actionUrl = actionUrl;
        return textObject;
    }

    /**
     * @return 图片消息对象
     * 创建图片消息对象
     */
    private ImageObject getImageObj(int imageId) {
        ImageObject imageObject = new ImageObject();
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), imageId);
        imageObject.setImageObject(bitmap);
        return imageObject;
    }

    /**
     * @return 多媒体（网页）消息对象
     * 创建多媒体（网页）消息对象
     */
    private WebpageObject getWebpageObj(String title, String description, int imageId, String actionUrl, String defaultText) {
        WebpageObject mediaObject = new WebpageObject();
        mediaObject.identify = Utility.generateGUID();
        mediaObject.title = title;
        mediaObject.description = description;
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), imageId);
        // 设置缩略图，注意：最终压缩过的缩略图大小不得超过32kb
        mediaObject.setThumbImage(bitmap);
        mediaObject.actionUrl = actionUrl;
        mediaObject.defaultText = defaultText;
        return mediaObject;
    }
}

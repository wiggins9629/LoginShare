package com.yunjin.microlove.wechat;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXTextObject;
import com.tencent.mm.opensdk.modelmsg.WXVideoObject;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.yunjin.microlove.R;
import com.yunjin.microlove.utils.ToastUtil;
import com.yunjin.microlove.utils.UIUtils;

/**
 * @Description 实现微信分享功能的核心类
 * @Author 一花一世界
 */
public class WechatShareManager {

    private static final int THUMB_SIZE = 150;//缩略图大小
    public static final int WECHAT_SHARE_WAY_TEXT = 1;//文字
    public static final int WECHAT_SHARE_WAY_PICTURE = 2;//图片
    public static final int WECHAT_SHARE_WAY_WEBPAGE = 3;//链接
    public static final int WECHAT_SHARE_WAY_VIDEO = 4;//视频
    public static final int WECHAT_SHARE_TYPE_SESSION = SendMessageToWX.Req.WXSceneSession;//会话
    public static final int WECHAT_SHARE_TYPE_FRIENDS = SendMessageToWX.Req.WXSceneTimeline;//朋友圈
    public static final int WECHAT_SHARE_TYPE_FAVORITE = SendMessageToWX.Req.WXSceneFavorite;//收藏

    private ShareContent mShareContentText, mShareContentPicture, mShareContentWebpag, mShareContentVideo;
    private IWXAPI mWXApi;
    private Activity mActivity;
    private static WechatShareManager mInstance;

    public WechatShareManager(Activity activity) {
        this.mActivity = activity;
        initWechatShare();
    }

    /**
     * 获取WechatShareManager实例，非线程安全，请在UI线程中操作
     */
    public static WechatShareManager getInstance(Activity activity) {
        if (mInstance == null) {
            mInstance = new WechatShareManager(activity);
        }
        return mInstance;
    }

    /**
     * 初始化数据
     */
    private void initWechatShare() {
        if (mWXApi == null) {
            // 通过WXAPIFactory工厂获取IWXAPI的实例
            mWXApi = WXAPIFactory.createWXAPI(mActivity, WechatConstants.APP_ID);
        }
        // 将应用的appid注册到微信
        mWXApi.registerApp(WechatConstants.APP_ID);
        WechatConstants.isLoginMark = false;
    }

    /**
     * 通过微信分享
     *
     * @param shareContent 分享的方式（文本、图片、链接）
     * @param shareType    分享的类型（朋友圈，会话）
     */
    public void shareByWebchat(ShareContent shareContent, int shareType) {
        if (!isWebchatAvaliable()) {
            ToastUtil.showText(UIUtils.getString(R.string.please_install_wechat_client));
            return;
        }
        switch (shareContent.getShareWay()) {
            case WECHAT_SHARE_WAY_TEXT:
                shareText(shareContent, shareType);
                break;
            case WECHAT_SHARE_WAY_PICTURE:
                sharePicture(shareContent, shareType);
                break;
            case WECHAT_SHARE_WAY_WEBPAGE:
                shareWebPage(shareContent, shareType);
                break;
            case WECHAT_SHARE_WAY_VIDEO:
                shareVideo(shareContent, shareType);
                break;
        }
    }

    /**
     * 分享内容接口
     */
    private abstract class ShareContent {

        protected abstract int getShareWay();

        protected abstract String getContent();

        protected abstract String getTitle();

        protected abstract String getURL();

        protected abstract int getPictureResource();
    }

    /**
     * @Description 设置分享文字的内容
     */
    public class ShareContentText extends ShareContent {

        private String content;

        /**
         * 构造分享文字类
         *
         * @param content 分享的文字内容
         */
        public ShareContentText(String content) {
            this.content = content;
        }

        @Override
        protected int getShareWay() {
            return WECHAT_SHARE_WAY_TEXT;
        }

        @Override
        protected String getContent() {
            return content;
        }

        @Override
        protected String getTitle() {
            return null;
        }

        @Override
        protected String getURL() {
            return null;
        }

        @Override
        protected int getPictureResource() {
            return -1;
        }
    }

    /**
     * 获取文本分享对象
     */
    public ShareContent getShareContentText(String content) {
        if (mShareContentText == null) {
            mShareContentText = new ShareContentText(content);
        }
        return mShareContentText;
    }

    /**
     * 设置分享图片的内容
     */
    public class ShareContentPicture extends ShareContent {

        private int pictureResource;

        public ShareContentPicture(int pictureResource) {
            this.pictureResource = pictureResource;
        }

        @Override
        protected int getShareWay() {
            return WECHAT_SHARE_WAY_PICTURE;
        }

        @Override
        protected int getPictureResource() {
            return pictureResource;
        }

        @Override
        protected String getContent() {
            return null;
        }

        @Override
        protected String getTitle() {
            return null;
        }

        @Override
        protected String getURL() {
            return null;
        }
    }

    /**
     * 获取图片分享对象
     */
    public ShareContent getShareContentPicture(int pictureResource) {
        if (mShareContentPicture == null) {
            mShareContentPicture = new ShareContentPicture(pictureResource);
        }
        return mShareContentPicture;
    }

    /**
     * 设置分享链接的内容
     */
    public class ShareContentWebpage extends ShareContent {

        private String title;
        private String content;
        private String url;
        private int pictureResource;

        public ShareContentWebpage(String title, String content, String url, int pictureResource) {
            this.title = title;
            this.content = content;
            this.url = url;
            this.pictureResource = pictureResource;
        }

        @Override
        protected int getShareWay() {
            return WECHAT_SHARE_WAY_WEBPAGE;
        }

        @Override
        protected String getContent() {
            return content;
        }

        @Override
        protected String getTitle() {
            return title;
        }

        @Override
        protected String getURL() {
            return url;
        }

        @Override
        protected int getPictureResource() {
            return pictureResource;
        }
    }

    /**
     * 获取分享链接的内容
     *
     * @param title           别人看到的标题
     * @param content         别人看到的描述
     * @param url             链接
     * @param pictureResource 链接前显示的图标
     */
    public ShareContent getShareContentWebpag(String title, String content, String url, int pictureResource) {
        if (mShareContentWebpag == null) {
            mShareContentWebpag = new ShareContentWebpage(title, content, url, pictureResource);
        }
        return mShareContentWebpag;
    }

    /**
     * 设置分享视频的内容
     */
    public class ShareContentVideo extends ShareContent {

        private String url;

        public ShareContentVideo(String url) {
            this.url = url;
        }

        @Override
        protected int getShareWay() {
            return WECHAT_SHARE_WAY_VIDEO;
        }

        @Override
        protected String getContent() {
            return null;
        }

        @Override
        protected String getTitle() {
            return null;
        }

        @Override
        protected String getURL() {
            return url;
        }

        @Override
        protected int getPictureResource() {
            return -1;
        }

    }

    /**
     * 获取视频分享内容
     */
    public ShareContent getShareContentVideo(String url) {
        if (mShareContentVideo == null) {
            mShareContentVideo = new ShareContentVideo(url);
        }
        return mShareContentVideo;
    }

    /**
     * 分享文字
     */
    private void shareText(ShareContent shareContent, int shareType) {
        String text = shareContent.getContent();

        // 初始化一个WXTextObject对象
        WXTextObject textObj = new WXTextObject();
        textObj.text = text;

        // 用WXTextObject对象初始化一个WXMediaMessage对象
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = textObj;
        msg.description = text;

        // 构造一个Req
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        // transaction字段用于唯一标识一个请求
        req.transaction = buildTransaction("text");
        req.message = msg;
        req.scene = shareType;

        // 发送数据到微信
        mWXApi.sendReq(req);
    }

    /**
     * 分享图片
     */
    private void sharePicture(ShareContent shareContent, int shareType) {
        Bitmap bmp = BitmapFactory.decodeResource(mActivity.getResources(), shareContent.getPictureResource());

        // 初始化WXImageObject和WXMediaMessage对象
        WXImageObject imgObj = new WXImageObject(bmp);
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = imgObj;

        // 设置缩略图
        Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true);
        bmp.recycle();
        msg.thumbData = WechatUtil.bmpToByteArray(thumbBmp, true);

        // 构造一个Req
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("img");
        req.message = msg;
        req.scene = shareType;

        mWXApi.sendReq(req);
    }

    /**
     * 分享链接
     */
    private void shareWebPage(ShareContent shareContent, int shareType) {
        // 初始化一个WXWebpageObject对象
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = shareContent.getURL();

        // 用WXWebpageObject对象初始化一个WXMediaMessage对象
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = shareContent.getTitle();
        msg.description = shareContent.getContent();

        Bitmap bmp = BitmapFactory.decodeResource(mActivity.getResources(), shareContent.getPictureResource());
        Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true);
        bmp.recycle();
        msg.thumbData = WechatUtil.bmpToByteArray(thumbBmp, true);

        // 打造一个Req
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message = msg;
        req.scene = shareType;

        mWXApi.sendReq(req);
    }

    /**
     * 分享视频
     */
    private void shareVideo(ShareContent shareContent, int shareType) {
        // 初始化一个WXVideoObject对象
        WXVideoObject video = new WXVideoObject();
        video.videoUrl = shareContent.getURL();

        // 用WXVideoObject对象初始化一个WXMediaMessage对象
        WXMediaMessage msg = new WXMediaMessage(video);
        msg.title = shareContent.getTitle();
        msg.description = shareContent.getContent();
        Bitmap thumb = BitmapFactory.decodeResource(mActivity.getResources(), R.drawable.image_default);
        Bitmap thumbBitmap = Bitmap.createScaledBitmap(thumb, THUMB_SIZE, THUMB_SIZE, true);
        thumb.recycle();
        msg.thumbData = WechatUtil.bmpToByteArray(thumbBitmap, true);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("video");
        req.message = msg;
        req.scene = shareType;

        mWXApi.sendReq(req);
    }

    /**
     * 唯一标识一个请求
     */
    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    /**
     * @Description 检测手机上是否安装了微信
     */
    public boolean isWebchatAvaliable() {
        try {
            mWXApi.isWXAppInstalled();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

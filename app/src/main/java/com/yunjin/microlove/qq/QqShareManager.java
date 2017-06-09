package com.yunjin.microlove.qq;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.tencent.connect.common.Constants;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzonePublish;
import com.tencent.connect.share.QzoneShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.yunjin.microlove.R;
import com.yunjin.microlove.utils.ToastUtil;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * @Description 实现QQ分享功能的核心类
 * @Author 一花一世界
 */
public class QqShareManager {

    private Activity mActivity;
    private Tencent mTencent;
    private ShareListener mListener;
    private static QqShareManager mInstance;

    public QqShareManager(Activity activity) {
        this.mActivity = activity;
        initQqShare();
    }

    /**
     * 获取QqShareManager实例，非线程安全，请在UI线程中操作
     */
    public static QqShareManager getInstance(Activity activity) {
        if (mInstance == null) {
            mInstance = new QqShareManager(activity);
        }
        return mInstance;
    }

    /**
     * 初始化数据
     */
    private void initQqShare() {
        // Tencent类是SDK的主要实现类，开发者可通过Tencent类访问腾讯开放的OpenAPI。其中APP_ID是分配给第三方应用的app_id，类型为String。
        mTencent = Tencent.createInstance(QqConstants.APP_ID, mActivity);
        mListener = new ShareListener();
    }

    /**
     * 确保能接收到回调信息
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_QZONE_SHARE || requestCode == Constants.REQUEST_QQ_SHARE) {
            Tencent.onActivityResultData(requestCode, resultCode, data, mListener);
        }
    }

    /**
     * 分享图文消息 - QQ
     */
    public void shareToQqImageText(String title, String targetUrl, String summary, String imageUrl, String appName, int extInt) {
        final Bundle params = new Bundle();
        // 必填：分享的类型
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        // 必填：分享的标题，最长30个字符
        params.putString(QQShare.SHARE_TO_QQ_TITLE, title);
        // 必填：分享消息被好友点击后的跳转URL
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, targetUrl);
        // 可选：分享的消息摘要，最长40个字
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, summary);
        // 可选：分享图片的URL或者本地路径
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, imageUrl);
        // 可选：手Q客户端顶部替换“返回”按钮文字，如果为空则用“返回”代替
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, appName);
        // 可选：分享额外选项，有两种类型可选（默认是不隐藏分享到QZone按钮且不自动打开分享到QZone的对话框）。
        // QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN，分享时自动打开分享到QZone的对话框。
        // QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE，分享时隐藏分享到QZone按钮。
        params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, extInt);
        mTencent.shareToQQ(mActivity, params, mListener);
    }

    /**
     * 分享纯图片 - QQ
     */
    public void shareToQqImage(String imageLocalUrl, String appName, int extInt) {
        Bundle params = new Bundle();
        // 必填：分享的类型
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE);
        // 必填：分享的本地图片路径
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, imageLocalUrl);
        // 可选：手Q客户端顶部替换“返回”按钮文字，如果为空则用“返回”代替
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, appName);
        // 可选：分享额外选项，有两种类型可选（默认是不隐藏分享到QZone按钮且不自动打开分享到QZone的对话框）。
        // QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN，分享时自动打开分享到QZone的对话框。
        // QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE，分享时隐藏分享到QZone按钮。
        params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, extInt);
        mTencent.shareToQQ(mActivity, params, mListener);
    }

    /**
     * 分享音乐 - QQ
     */
    public void shareToQqAudio(String title, String targetUrl, String summary, String imageUrl, String audioUrl, String appName, int extInt) {
        final Bundle params = new Bundle();
        // 必填：分享的类型
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_AUDIO);
        // 必填：分享消息被好友点击后的跳转URL
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, targetUrl);
        // 必填：音乐文件的远程链接, 以URL的形式传入, 不支持本地音乐
        params.putString(QQShare.SHARE_TO_QQ_AUDIO_URL, audioUrl);
        // 可选：分享的标题, 最长30个字符
        params.putString(QQShare.SHARE_TO_QQ_TITLE, title);
        // 可选：分享的消息摘要，最长40个字符
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, summary);
        // 可选：分享图片的URL或者本地路径
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, imageUrl);
        // 可选：手Q客户端顶部替换“返回”按钮文字，如果为空则用“返回”代替
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, appName);
        // 可选：分享额外选项，有两种类型可选（默认是不隐藏分享到QZone按钮且不自动打开分享到QZone的对话框）。
        // QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN，分享时自动打开分享到QZone的对话框。
        // QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE，分享时隐藏分享到QZone按钮。
        params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, extInt);
        mTencent.shareToQQ(mActivity, params, mListener);
    }

    /**
     * 分享应用 - QQ
     */
    public void shareToQqApp(String title, String summary, String imageUrl, String appName, int extInt) {
        final Bundle params = new Bundle();
        // 必填：分享的类型
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_APP);
        // 可选：分享的标题, 最长30个字符
        params.putString(QQShare.SHARE_TO_QQ_TITLE, title);
        // 可选：分享的消息摘要，最长40个字符
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, summary);
        // 可选：分享图片的URL或者本地路径
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, imageUrl);
        // 可选：手Q客户端顶部替换“返回”按钮文字，如果为空则用“返回”代替
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, appName);
        // 可选：分享额外选项，有两种类型可选（默认是不隐藏分享到QZone按钮且不自动打开分享到QZone的对话框）。
        // QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN，分享时自动打开分享到QZone的对话框。
        // QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE，分享时隐藏分享到QZone按钮。
        params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, extInt);
        mTencent.shareToQQ(mActivity, params, mListener);
    }

    /**
     * 分享图文消息 - QQ空间
     */
    public void shareToQzone(String title, String targetUrl, String summary, ArrayList<String> imageUrlList) {
        final Bundle params = new Bundle();
        // 可选：分享的类型
        params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
        // 必填：分享的标题，最多200个字符
        params.putString(QzoneShare.SHARE_TO_QQ_TITLE, title);
        // 必填：需要跳转的链接，URL字符串
        params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, targetUrl);
        // 可选：分享的摘要，最多600字符
        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, summary);
        // 可选：分享的图片, 以ArrayList<String>的类型传入，以便支持多张图片（注：图片最多支持9张图片，多余的图片会被丢弃）
        params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imageUrlList);
        mTencent.shareToQzone(mActivity, params, mListener);
    }

    /**
     * 写说说
     */
    public void writeToQqTalk(String summary, ArrayList<String> imageUrlList) {
        final Bundle params = new Bundle();
        // 可选：分享的类型
        params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzonePublish.PUBLISH_TO_QZONE_TYPE_PUBLISHMOOD);
        // 可选：分享的摘要，最多600字符
        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, summary);
        // 可选：分享的图片, 以ArrayList<String>的类型传入，以便支持多张图片（注：图片最多支持9张图片，多余的图片会被丢弃）
        params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imageUrlList);
        mTencent.publishToQzone(mActivity, params, mListener);
    }

    /**
     * 短视频
     */
    public void shortToQqVideo(String summary, String videoPath) {
        final Bundle params = new Bundle();
        // 可选：分享的类型
        params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzonePublish.PUBLISH_TO_QZONE_TYPE_PUBLISHVIDEO);
        // 可选：分享的摘要，最多600字符
        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, summary);
        // 必填：分享的视频地址
        params.putString(QzonePublish.PUBLISH_TO_QZONE_VIDEO_PATH, videoPath);
        mTencent.publishToQzone(mActivity, params, mListener);
    }

    /**
     * 分享回调
     */
    private class ShareListener implements IUiListener {

        @Override
        public void onComplete(Object o) {
            if (null == o) {
                ToastUtil.showText(R.string.share_failed);
                return;
            }
            JSONObject jsonObject = (JSONObject) o;
            if (jsonObject.length() == 0) {
                ToastUtil.showText(R.string.share_failed);
                return;
            }
            ToastUtil.showText(R.string.share_success);
        }

        @Override
        public void onError(UiError uiError) {
            ToastUtil.showText(R.string.share_failed);
        }

        @Override
        public void onCancel() {
            ToastUtil.showText(R.string.share_canceled);
        }
    }
}

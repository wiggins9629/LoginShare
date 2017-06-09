package com.yunjin.microlove;

import android.app.Activity;
import android.content.Intent;

import com.tencent.connect.share.QQShare;
import com.yunjin.microlove.qq.QqLoginManager;
import com.yunjin.microlove.qq.QqShareManager;
import com.yunjin.microlove.utils.ApkUtil;
import com.yunjin.microlove.wechat.WechatLoginManager;
import com.yunjin.microlove.wechat.WechatShareManager;
import com.yunjin.microlove.wechat.WechatShareManager.ShareContentPicture;
import com.yunjin.microlove.wechat.WechatShareManager.ShareContentText;
import com.yunjin.microlove.wechat.WechatShareManager.ShareContentVideo;
import com.yunjin.microlove.wechat.WechatShareManager.ShareContentWebpage;
import com.yunjin.microlove.weibo.WeiboLoginManager;
import com.yunjin.microlove.weibo.WeiboShareManager;

import java.util.ArrayList;

/**
 * @Description 第三方登录分享帮助类
 * @Author 一花一世界
 */
public class LoginShareHelp {

    private Activity mActivity;
    private static LoginShareHelp mInstance;
    // QQ登录授权
    private QqLoginManager mQqLoginManager;
    // QQ信息分享
    private QqShareManager mQqShareManager;
    // 微博登录授权
    private WeiboLoginManager mWeiboLoginManager;
    // 微博信息分享
    private WeiboShareManager mWeiboShareManager;
    // 微信登录授权
    private WechatLoginManager mWechatLoginManager;
    // 微信信息分享
    private WechatShareManager mWechatShareManager;

    public LoginShareHelp(Activity activity) {
        this.mActivity = activity;
        initLoginShare();
    }

    /**
     * 获取LoginShareHelp实例，非线程安全，请在UI线程中操作
     */
    public static LoginShareHelp getInstance(Activity activity) {
        if (mInstance == null) {
            mInstance = new LoginShareHelp(activity);
        }
        return mInstance;
    }

    /**
     * 初始化数据
     */
    private void initLoginShare() {
        mQqLoginManager = QqLoginManager.getInstance(mActivity);
        mQqShareManager = QqShareManager.getInstance(mActivity);
        mWeiboLoginManager = WeiboLoginManager.getInstance(mActivity);
        mWeiboShareManager = WeiboShareManager.getInstance(mActivity);
        mWechatLoginManager = WechatLoginManager.getInstance(mActivity);
        mWechatShareManager = WechatShareManager.getInstance(mActivity);
    }

    /**
     * QQ授权登录
     */
    public void qqAuthorizedLogin() {
        mQqLoginManager.login();
    }

    /**
     * QQ - 分享图文消息
     *
     * @param title     必填：分享的标题，最长30个字符
     * @param targetUrl 必填：分享消息被好友点击后的跳转URL
     * @param summary   可选：分享的消息摘要，最长40个字
     * @param imageUrl  可选：分享图片的URL或者本地路径
     */
    public void shareToQqImageText(String title, String targetUrl, String summary, String imageUrl) {
        String appName = ApkUtil.getAppName();
        int extInt = QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE;
        mQqShareManager.shareToQqImageText(title, targetUrl, summary, imageUrl, appName, extInt);
    }

    /**
     * QQ - 分享纯图片
     *
     * @param imageLocalUrl 必填：分享的本地图片路径
     */
    public void shareToQqImage(String imageLocalUrl) {
        String appName = ApkUtil.getAppName();
        int extInt = QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE;
        mQqShareManager.shareToQqImage(imageLocalUrl, appName, extInt);
    }

    /**
     * QQ - 分享音乐
     *
     * @param title     可选：分享的标题, 最长30个字符
     * @param targetUrl 必填：分享消息被好友点击后的跳转URL
     * @param summary   可选：分享的消息摘要，最长40个字符
     * @param imageUrl  可选：分享图片的URL或者本地路径
     * @param audioUrl  必填：音乐文件的远程链接, 以URL的形式传入, 不支持本地音乐
     */
    public void shareToQqAudio(String title, String targetUrl, String summary, String imageUrl, String audioUrl) {
        String appName = ApkUtil.getAppName();
        int extInt = QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE;
        mQqShareManager.shareToQqAudio(title, targetUrl, summary, imageUrl, audioUrl, appName, extInt);
    }

    /**
     * QQ - 分享应用
     *
     * @param title    可选：分享的标题, 最长30个字符
     * @param summary  可选：分享的消息摘要，最长40个字符
     * @param imageUrl 可选：分享图片的URL或者本地路径
     */
    public void shareToQqApp(String title, String summary, String imageUrl) {
        String appName = ApkUtil.getAppName();
        int extInt = QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE;
        mQqShareManager.shareToQqApp(title, summary, imageUrl, appName, extInt);
    }

    /**
     * QQ空间 - 分享图文消息
     *
     * @param title        必填：分享的标题，最多200个字符
     * @param targetUrl    必填：需要跳转的链接，URL字符串
     * @param summary      可选：分享的摘要，最多600字符
     * @param imageUrlList 可选：分享的图片, 以ArrayList<String>的类型传入，以便支持多张图片（注：图片最多支持9张图片，多余的图片会被丢弃）
     */
    public void shareToQzone(String title, String targetUrl, String summary, ArrayList<String> imageUrlList) {
        mQqShareManager.shareToQzone(title, targetUrl, summary, imageUrlList);
    }

    /**
     * 写说说
     *
     * @param summary      可选：分享的摘要，最多600字符
     * @param imageUrlList 可选：分享的图片, 以ArrayList<String>的类型传入，以便支持多张图片（注：图片最多支持9张图片，多余的图片会被丢弃）
     */
    public void writeToQqTalk(String summary, ArrayList<String> imageUrlList) {
        mQqShareManager.writeToQqTalk(summary, imageUrlList);
    }

    /**
     * 短视频
     *
     * @param summary   可选：分享的摘要，最多600字符
     * @param videoPath 必填：分享的视频地址
     */
    public void shortToQqVideo(String summary, String videoPath) {
        mQqShareManager.shortToQqVideo(summary, videoPath);
    }

    /**
     * 微博授权登录
     */
    public void weiboAuthorizedLogin() {
        mWeiboLoginManager.authorize(WeiboLoginManager.AUTHORIZE);
    }

    public void onNewIntent(Intent intent) {
        mWeiboShareManager.onNewIntent(intent);
    }

    /**
     * 微博  - 分享文本消息
     *
     * @param text      文本内容
     * @param title     文本内容标题
     * @param actionUrl
     */
    public void shareToWeiboText(String text, String title, String actionUrl) {
        mWeiboShareManager.shareToWeiboText(text, title, actionUrl, WeiboShareManager.SHARE_ALL_IN_ONE);
    }

    /**
     * 微博  - 分享图片消息
     *
     * @param imageId 图片ID
     */
    public void shareToWeiboImage(int imageId) {
        mWeiboShareManager.shareToWeiboImage(imageId, WeiboShareManager.SHARE_ALL_IN_ONE);
    }

    /**
     * 微博  - 分享图文消息
     *
     * @param text      文本内容
     * @param title     文本内容标题
     * @param actionUrl
     * @param imageId   图片ID
     */
    public void shareToWeiboTextImage(String text, String title, String actionUrl, int imageId) {
        mWeiboShareManager.shareToWeiboTextImage(text, title, actionUrl, imageId, WeiboShareManager.SHARE_ALL_IN_ONE);
    }

    /**
     * 微博  - 分享网页消息
     *
     * @param title       网页标题
     * @param description 网页描述
     * @param imageId     网页图片
     * @param actionUrl
     * @param defaultText 网页默认文本内容
     */
    public void shareToWeiboWebpage(String title, String description, int imageId, String actionUrl, String defaultText) {
        mWeiboShareManager.shareToWeiboWebpage(title, description, imageId, actionUrl, defaultText, WeiboShareManager.SHARE_ALL_IN_ONE);
    }


    /**
     * 微信授权登录
     */
    public void wechatAuthorizedLogin() {
        mWechatLoginManager.loginByWebchat();
    }

    /**
     * 微信 - 分享文本内容
     *
     * @param content   文本内容
     * @param shareType 分享类型
     *                  WechatShareManager.WECHAT_SHARE_TYPE_SESSION   会话
     *                  WechatShareManager.WECHAT_SHARE_TYPE_FRIENDS   朋友圈
     *                  WechatShareManager.WECHAT_SHARE_TYPE_FAVORITE  收藏
     */
    public void shareToWechatText(String content, int shareType) {
        ShareContentText mShareContentText = (ShareContentText) mWechatShareManager.getShareContentText(content);
        mWechatShareManager.shareByWebchat(mShareContentText, shareType);
    }

    /**
     * 微信 - 分享图片信息
     *
     * @param pictureResource 图片ID
     * @param shareType       分享类型
     *                        WechatShareManager.WECHAT_SHARE_TYPE_SESSION   会话
     *                        WechatShareManager.WECHAT_SHARE_TYPE_FRIENDS   朋友圈
     *                        WechatShareManager.WECHAT_SHARE_TYPE_FAVORITE  收藏
     */
    public void shareToWechatImage(int pictureResource, int shareType) {
        ShareContentPicture mShareContentPicture = (ShareContentPicture) mWechatShareManager.getShareContentPicture(pictureResource);
        mWechatShareManager.shareByWebchat(mShareContentPicture, shareType);
    }

    /**
     * 微信 - 分享网页链接
     *
     * @param title           别人看到的标题
     * @param content         别人看到的描述
     * @param url             链接
     * @param pictureResource 链接前显示的图标
     * @param shareType       分享类型
     *                        WechatShareManager.WECHAT_SHARE_TYPE_SESSION   会话
     *                        WechatShareManager.WECHAT_SHARE_TYPE_FRIENDS   朋友圈
     *                        WechatShareManager.WECHAT_SHARE_TYPE_FAVORITE  收藏
     */
    public void shareToWechatWebpage(String title, String content, String url, int pictureResource, int shareType) {
        ShareContentWebpage mShareContentWebpage = (ShareContentWebpage) mWechatShareManager.getShareContentWebpag(title, content, url, pictureResource);
        mWechatShareManager.shareByWebchat(mShareContentWebpage, shareType);
    }

    /**
     * 微信 - 分享视频内容
     *
     * @param url       视频地址
     * @param shareType 分享类型
     *                  WechatShareManager.WECHAT_SHARE_TYPE_SESSION   会话
     *                  WechatShareManager.WECHAT_SHARE_TYPE_FRIENDS   朋友圈
     *                  WechatShareManager.WECHAT_SHARE_TYPE_FAVORITE  收藏
     */
    public void shareToWechatVideo(String url, int shareType) {
        ShareContentVideo mShareContentVideo = (ShareContentVideo) mWechatShareManager.getShareContentVideo(url);
        mWechatShareManager.shareByWebchat(mShareContentVideo, shareType);
    }

    /**
     * 确保能接收到回调信息
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mQqLoginManager.onActivityResult(requestCode, resultCode, data);
        mQqShareManager.onActivityResult(requestCode, resultCode, data);
        mWeiboLoginManager.onActivityResult(requestCode, resultCode, data);
    }
}

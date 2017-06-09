package com.yunjin.microlove.weibo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.kymjs.rxvolley.RxVolley;
import com.kymjs.rxvolley.client.HttpCallback;
import com.sina.weibo.sdk.WbSdk;
import com.sina.weibo.sdk.auth.AccessTokenKeeper;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WbAuthListener;
import com.sina.weibo.sdk.auth.WbConnectErrorMessage;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.yunjin.microlove.MainActivity;
import com.yunjin.microlove.R;
import com.yunjin.microlove.utils.Constant;
import com.yunjin.microlove.utils.DialogUtil;
import com.yunjin.microlove.utils.LogUtil;
import com.yunjin.microlove.utils.StringUtil;
import com.yunjin.microlove.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;

/**
 * @Description 实现微博登录授权功能的核心类
 * @Author 一花一世界
 */
public class WeiboLoginManager {

    private Activity mActivity;
    // SsoHandler仅当SDK支持SSO时有效
    private SsoHandler mSsoHandler;
    private Oauth2AccessToken mAccessToken;
    // 授权相关信息
    private String authorizeInfo;

    // SSO 授权, 仅客户端
    public static final int AUTHORIZE_CLIENT_SSO = 0;
    // SSO 授权, 仅Web
    public static final int AUTHORIZE_WEB = 1;
    // SSO 授权, ALL IN ONE, 如果手机安装了微博客户端则使用客户端授权，没有则进行网页授权
    public static final int AUTHORIZE = 3;

    private StringBuilder stringBuilder;
    private static WeiboLoginManager mInstance;

    public WeiboLoginManager(Activity activity) {
        this.mActivity = activity;
        initWeiboLogin();
    }

    /**
     * 获取WeiboLoginManager实例，非线程安全，请在UI线程中操作
     */
    public static WeiboLoginManager getInstance(Activity activity) {
        if (mInstance == null) {
            mInstance = new WeiboLoginManager(activity);
        }
        return mInstance;
    }

    /**
     * 初始化数据
     */
    private void initWeiboLogin() {
        // 初始化WbSdk对象
        WbSdk.install(mActivity, new AuthInfo(mActivity, WeiboConstants.APP_KEY, WeiboConstants.REDIRECT_URL, WeiboConstants.SCOPE));
        // 创建微博实例
        mSsoHandler = new SsoHandler(mActivity);
        // 是否已存在Token信息
        isTokenExist();
    }

    /**
     * 三种授权方式
     */
    public void authorize(int type) {
        if (type == AUTHORIZE_CLIENT_SSO) {
            // SSO 授权, 仅客户端
            mSsoHandler.authorizeClientSso(new SelfWbAuthListener());
        } else if (type == AUTHORIZE_WEB) {
            // SSO 授权, 仅Web
            mSsoHandler.authorizeWeb(new SelfWbAuthListener());
        } else {
            // SSO 授权, ALL IN ONE, 如果手机安装了微博客户端则使用客户端授权，没有则进行网页授权
            mSsoHandler.authorize(new SelfWbAuthListener());
        }
    }

    /**
     * 授权回调
     */
    private class SelfWbAuthListener implements WbAuthListener {

        @Override
        public void onSuccess(final Oauth2AccessToken token) {
            mAccessToken = token;
            if (mAccessToken.isSessionValid()) {
                // 保存Token到SharedPreferences
                AccessTokenKeeper.writeAccessToken(mActivity, mAccessToken);
                ToastUtil.showText(R.string.auth_success);
                getUserInfo(mAccessToken.getToken(), mAccessToken.getUid());
            }
        }

        @Override
        public void cancel() {
            ToastUtil.showText(R.string.auth_canceled);
        }

        @Override
        public void onFailure(WbConnectErrorMessage errorMessage) {
            ToastUtil.showText(errorMessage.getErrorMessage());
        }
    }

    /**
     * SSO授权回调：当SSO授权Activity退出时，该函数被调用
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 发起SSO登录的Activity必须重写onActivityResult
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    /**
     * 配置文件中是否已存在Token信息并且合法
     */
    public boolean isTokenExist() {
        // 从SharedPreferences中读取上次已保存好的AccessToken等信息，第一次启动应用时AccessToken不可用
        mAccessToken = AccessTokenKeeper.readAccessToken(mActivity);
        if (mAccessToken.isSessionValid()) {
            updateToken();
        }
        return mAccessToken.isSessionValid();
    }

    /**
     * 更新当前Token等信息
     */
    private void updateToken() {
        if (!StringUtil.isEmpty(authorizeInfo)) {
            authorizeInfo = "";
        }
        String date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(
                new java.util.Date(mAccessToken.getExpiresTime()));
        String format = mActivity.getString(R.string.token_to_string_format_1);
        authorizeInfo = String.format(format, mAccessToken.getToken(), date);
    }

    /**
     * 获取Token等相关信息
     */
    public String getAuthorizeInfo() {
        return authorizeInfo;
    }

    /**
     * 获取用户信息
     */
    public void getUserInfo(String access_token, String uid) {
        String url = "https://api.weibo.com/2/users/show.json?"
                + "access_token="
                + access_token
                + "&uid="
                + uid;

        new RxVolley.Builder()
                .url(url)
                .httpMethod(RxVolley.Method.GET)
                .callback(new HttpCallback() {
                    @Override
                    public void onSuccess(String t) {
                        super.onSuccess(t);
                        JSONObject jsonObject;
                        try {
                            stringBuilder = new StringBuilder();
                            jsonObject = new JSONObject(t);
                            String screen_name = jsonObject.getString("screen_name");
                            String gender = jsonObject.getString("gender");
                            String avatar_large = jsonObject.getString("avatar_large");
                            stringBuilder.append("screen_name-->" + screen_name);
                            stringBuilder.append("\ngender-->" + gender);
                            stringBuilder.append("\navatar_large-->" + avatar_large);
                            LogUtil.e("screen_name-->" + screen_name + "  gender-->" + gender + "  avatar_large-->" + avatar_large);

                            loginActivity(Constant.PLATFORM_TYPE_WEIBO, stringBuilder.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int errorNo, String strMsg) {
                        super.onFailure(errorNo, strMsg);
                    }
                })
                .doTask();
    }

    private void loginActivity(final String platformType, final String platformInfo) {
        DialogUtil.showDialogLoading(mActivity, "");
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                DialogUtil.hideDialogLoading();
                Bundle bundle = new Bundle();
                bundle.putString(Constant.PLATFORM_TYPE, platformType);
                bundle.putString(Constant.PLATFORM_TYPE_CONTENT, platformInfo);
                mActivity.startActivity(new Intent(mActivity, MainActivity.class).putExtras(bundle));
                mActivity.finish();
            }
        }, 3000);
    }
}

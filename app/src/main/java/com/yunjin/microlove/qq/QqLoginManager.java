package com.yunjin.microlove.qq;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQToken;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.yunjin.microlove.MainActivity;
import com.yunjin.microlove.R;
import com.yunjin.microlove.utils.Constant;
import com.yunjin.microlove.utils.DialogUtil;
import com.yunjin.microlove.utils.LogUtil;
import com.yunjin.microlove.utils.StringUtil;
import com.yunjin.microlove.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @Description 实现QQ登录授权功能的核心类
 * @Author 一花一世界
 */
public class QqLoginManager {

    private Activity mActivity;
    private Tencent mTencent;
    private LogInListener mListener;
    private StringBuilder stringBuilder;
    private static QqLoginManager mInstance;

    public QqLoginManager(Activity activity) {
        this.mActivity = activity;
        initQqLogin();
    }

    /**
     * 获取QqLoginManager实例，非线程安全，请在UI线程中操作
     */
    public static QqLoginManager getInstance(Activity activity) {
        if (mInstance == null) {
            mInstance = new QqLoginManager(activity);
        }
        return mInstance;
    }

    /**
     * 初始化数据
     */
    private void initQqLogin() {
        // Tencent类是SDK的主要实现类，开发者可通过Tencent类访问腾讯开放的OpenAPI。其中APP_ID是分配给第三方应用的app_id，类型为String。
        mTencent = Tencent.createInstance(QqConstants.APP_ID, mActivity);
        mListener = new LogInListener();
    }

    /**
     * 授权登录
     */
    public void login() {
        if (!mTencent.isSessionValid()) {
            mTencent.login(mActivity, "all", mListener);
        }
    }

    /**
     * 注销
     */
    public void logout() {
        mTencent.logout(mActivity);
    }

    /**
     * 登录授权回调
     */
    private class LogInListener implements IUiListener {

        @Override
        public void onComplete(Object o) {
            if (null == o) {
                ToastUtil.showText(R.string.auth_failed);
                return;
            }
            JSONObject jsonObject = (JSONObject) o;
            if (jsonObject.length() == 0) {
                ToastUtil.showText(R.string.auth_failed);
                return;
            }
            // 设置AccessToken和OpenId
            initOpenidAndToken(jsonObject);
            // 获取QQ用户信息
            getUserInfo();
            ToastUtil.showText(R.string.auth_success);
        }

        @Override
        public void onError(UiError uiError) {
            ToastUtil.showText(R.string.auth_failed);
        }

        @Override
        public void onCancel() {
            ToastUtil.showText(R.string.auth_canceled);
        }
    }

    /**
     * 确保能接收到回调信息
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_LOGIN || requestCode == Constants.REQUEST_APPBAR) {
            Tencent.onActivityResultData(requestCode, resultCode, data, mListener);
        }
    }

    /**
     * 设置AccessToken和OpenId
     */
    private void initOpenidAndToken(JSONObject jsonObject) {
        try {
            String access_token = jsonObject.getString(Constants.PARAM_ACCESS_TOKEN);
            String expires_in = jsonObject.getString(Constants.PARAM_EXPIRES_IN);
            String openid = jsonObject.getString(Constants.PARAM_OPEN_ID);

            if (!StringUtil.isEmpty(access_token) && !StringUtil.isEmpty(openid)) {
                mTencent.setAccessToken(access_token, expires_in);
                mTencent.setOpenId(openid);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取QQ用户信息
     */
    private void getUserInfo() {
        QQToken mQQToken = mTencent.getQQToken();
        UserInfo userInfo = new UserInfo(mActivity, mQQToken);
        userInfo.getUserInfo(new IUiListener() {
            @Override
            public void onComplete(Object o) {
                if (null == o) {
                    ToastUtil.showText(R.string.get_user_info_failed);
                    return;
                }

                JSONObject jsonObject = (JSONObject) o;
                if (jsonObject.length() == 0) {
                    ToastUtil.showText(R.string.get_user_info_failed);
                    return;
                }

                try {
                    String gender = jsonObject.getString("gender");
                    String nickname = jsonObject.getString("nickname");
                    String figureurl = jsonObject.getString("figureurl");

                    stringBuilder = new StringBuilder();
                    stringBuilder.append("gender-->" + gender);
                    stringBuilder.append("\nnickname-->" + nickname);
                    stringBuilder.append("\nfigureurl-->" + figureurl);
                    LogUtil.e("gender-->" + gender + "  nickname-->" + nickname + "  figureurl-->" + figureurl);

                    loginActivity(Constant.PLATFORM_TYPE_QQ, stringBuilder.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(UiError uiError) {
                ToastUtil.showText(R.string.get_user_info_failed);
            }

            @Override
            public void onCancel() {
                ToastUtil.showText(R.string.get_user_info_canceled);
            }
        });
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

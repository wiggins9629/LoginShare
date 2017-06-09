package com.yunjin.microlove.wechat;

import android.app.Activity;

import com.kymjs.rxvolley.RxVolley;
import com.kymjs.rxvolley.client.HttpCallback;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @Description 实现微信登录授权功能的核心类
 * @Author 一花一世界
 */
public class WechatLoginManager {

    private Activity mActivity;
    private IWXAPI mWXApi;
    private static WechatLoginManager mInstance;

    public WechatLoginManager(Activity activity) {
        this.mActivity = activity;
        initWechatLogin();
    }

    /**
     * 获取WechatLoginManager实例，非线程安全，请在UI线程中操作
     */
    public static WechatLoginManager getInstance(Activity activity) {
        if (mInstance == null) {
            mInstance = new WechatLoginManager(activity);
        }
        return mInstance;
    }

    /**
     * 初始化数据
     */
    private void initWechatLogin() {
        if (mWXApi == null) {
            // 通过WXAPIFactory工厂获取IWXAPI的实例
            mWXApi = WXAPIFactory.createWXAPI(mActivity, WechatConstants.APP_ID);
        }
        // 将应用的appid注册到微信
        mWXApi.registerApp(WechatConstants.APP_ID);
        WechatConstants.isLoginMark = true;
    }

    /**
     * 授权登录
     */
    public void loginByWebchat() {
        if (mWXApi != null && mWXApi.isWXAppInstalled()) {
            SendAuth.Req req = new SendAuth.Req();
            req.scope = "snsapi_userinfo";
            req.state = "login_state";
            mWXApi.sendReq(req);
        }
    }

    /**
     * 获取openid、accessToken值用于后期操作
     *
     * @param code 请求码
     */
    public void getAccessToken(String code) {
        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?"
                + "appid="
                + WechatConstants.APP_ID
                + "&secret="
                + WechatConstants.APP_SECRET
                + "&code="
                + code
                + "&grant_type=authorization_code";

        new RxVolley.Builder()
                .url(url)
                .httpMethod(RxVolley.Method.GET)
                .callback(new HttpCallback() {
                    @Override
                    public void onSuccess(String t) {
                        super.onSuccess(t);
                        JSONObject jsonObject;
                        try {
                            jsonObject = new JSONObject(t);
                            String access_token = jsonObject.getString("access_token");
                            String expires_in = jsonObject.getString("expires_in");
                            String refresh_token = jsonObject.getString("refresh_token");
                            String openid = jsonObject.getString("openid");
                            String scope = jsonObject.getString("scope");
                            isAccessTokenValid(access_token, openid);
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

    /**
     * 检验授权凭证（access_token）是否有效
     *
     * @param access_token 接口调用凭
     * @param openid       授权用户唯一标识
     */
    public void isAccessTokenValid(final String access_token, final String openid) {
        String url = "https://api.weixin.qq.com/sns/auth?"
                + "access_token="
                + access_token
                + "&openid="
                + openid;

        new RxVolley.Builder()
                .url(url)
                .httpMethod(RxVolley.Method.GET)
                .callback(new HttpCallback() {
                    @Override
                    public void onSuccess(String t) {
                        super.onSuccess(t);
                        JSONObject jsonObject;
                        try {
                            jsonObject = new JSONObject(t);
                            int errcode = jsonObject.getInt("errcode");
                            if (errcode == 0) {
                                getUserInfo(access_token, openid);
                            }
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

    /**
     * 获取用户个人信息
     *
     * @param access_token 接口调用凭
     * @param openid       授权用户唯一标识
     */
    public void getUserInfo(String access_token, String openid) {
        String url = "https://api.weixin.qq.com/sns/userinfo?"
                + "access_token="
                + access_token
                + "&openid="
                + openid;

        new RxVolley.Builder()
                .url(url)
                .httpMethod(RxVolley.Method.GET)
                .callback(new HttpCallback() {
                    @Override
                    public void onSuccess(String t) {
                        super.onSuccess(t);
                        JSONObject jsonObject;
                        try {
                            jsonObject = new JSONObject(t);
                            String nickname = jsonObject.getString("nickname");
                            int sex = Integer.parseInt(jsonObject.getString("sex"));
                            String headimgurl = jsonObject.getString("headimgurl");
                            String unionid = jsonObject.getString("unionid");
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
}

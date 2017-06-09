package com.yunjin.microlove.wxapi;

import android.content.Intent;
import android.os.Bundle;

import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.yunjin.microlove.R;
import com.yunjin.microlove.base.BaseActivity;
import com.yunjin.microlove.utils.ToastUtil;
import com.yunjin.microlove.utils.UIUtils;
import com.yunjin.microlove.wechat.WechatConstants;
import com.yunjin.microlove.wechat.WechatLoginManager;

/**
 * @Description 微信分享回调
 * @Author 一花一世界
 */
public class WXEntryActivity extends BaseActivity implements IWXAPIEventHandler {

    // 第三方app和微信通信的接口
    private IWXAPI mWXApi;
    private WechatLoginManager mLoginManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 通过WXAPIFactory工厂获取IWXAPI的实例
        mWXApi = WXAPIFactory.createWXAPI(this, WechatConstants.APP_ID);
        // 将应用的appid注册到微信
        mWXApi.registerApp(WechatConstants.APP_ID);
        // 如果分享的时候，该界面没有开启，那么微信开始这个Activity时会调用onCreate，所以这里要处理微信的返回结果。
        // 注意：第三方开发者如果使用透明界面来实现WXEntryActivity，则需要判断handleIntent的返回值。
        // 如果返回值为false，则说明入参不合法未被SDK处理，应finish当前透明界面，避免外部通过传递非法参数的Intent导致停留在透明界面，引起用户的疑惑。
        mWXApi.handleIntent(getIntent(), this);
        mLoginManager = WechatLoginManager.getInstance(this);
    }

    /**
     * @Description 如果分享的时候，该界面已经开启，那么微信开始这个Activity时会调用onNewIntent，所以这里要处理微信的返回结果
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        mWXApi.handleIntent(intent, this);
    }

    /**
     * 微信发送请求到第三方应用时，会回调到该方法
     */
    @Override
    public void onReq(BaseReq baseReq) {

    }

    /**
     * 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
     */
    @Override
    public void onResp(BaseResp baseResp) {
        String result;
        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK://发送成功
                result = UIUtils.getString(R.string.errcode_ok);
                if (WechatConstants.isLoginMark) {
                    SendAuth.Resp sendResp = (SendAuth.Resp) baseResp;
                    if (sendResp != null) {
                        String code = sendResp.code;
                        mLoginManager.getAccessToken(code);
                    }
                }
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL://发送取消
                result = UIUtils.getString(R.string.errcode_cancel);
                break;
            case BaseResp.ErrCode.ERR_SENT_FAILED://发送失败
                result = UIUtils.getString(R.string.errcode_failed);
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED://发送被拒绝
                result = UIUtils.getString(R.string.errcode_denied);
                break;
            case BaseResp.ErrCode.ERR_UNSUPPORT://不支持错误
                result = UIUtils.getString(R.string.errcode_unsupport);
                break;
            case BaseResp.ErrCode.ERR_COMM://一般错误
                result = UIUtils.getString(R.string.errcode_comm);
                break;
            default:
                result = UIUtils.getString(R.string.errcode_unknown);
                break;
        }

        ToastUtil.showText(result);
        this.finish();
    }
}

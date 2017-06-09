package com.yunjin.microlove.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.yunjin.microlove.LoginShareHelp;
import com.yunjin.microlove.MainActivity;
import com.yunjin.microlove.R;
import com.yunjin.microlove.base.BaseActivity;
import com.yunjin.microlove.utils.DialogUtil;
import com.yunjin.microlove.utils.StringUtil;
import com.yunjin.microlove.utils.ToastUtil;
import com.yunjin.microlove.utils.UIUtils;
import com.yunjin.microlove.widget.DeletableEditText;

/**
 * @Description 用户登录
 * @Author 一花一世界
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private LoginActivity mActivity = null;

    private DeletableEditText mEdtName;
    private DeletableEditText mEdtPwd;
    private Button mBtnLogin;
    private ImageView mIvSinaWeibo;
    private ImageView mIvQq;
    private ImageView mIvWechat;
    private LoginShareHelp mLoginShareHelp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mActivity = this;

        initView();
        setListener();
        initData();
    }

    private void initView() {
        mEdtName = (DeletableEditText) findViewById(R.id.edt_name);
        mEdtPwd = (DeletableEditText) findViewById(R.id.edt_pwd);
        mBtnLogin = (Button) findViewById(R.id.btn_login);
        mIvSinaWeibo = (ImageView) findViewById(R.id.iv_sinaweibo);
        mIvQq = (ImageView) findViewById(R.id.iv_qq);
        mIvWechat = (ImageView) findViewById(R.id.iv_wechat);
    }

    private void setListener() {
        mBtnLogin.setOnClickListener(this);
        mIvSinaWeibo.setOnClickListener(this);
        mIvQq.setOnClickListener(this);
    }

    private void initData() {
        mLoginShareHelp = LoginShareHelp.getInstance(mActivity);
    }

    private void login() {
        DialogUtil.showDialogLoading(mActivity, "");
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                DialogUtil.hideDialogLoading();
                startActivity(new Intent(mActivity, MainActivity.class));
                finish();
            }
        }, 3000);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                String name = mEdtName.getText().toString().trim();
                String pwd = mEdtPwd.getText().toString().trim();

                if (StringUtil.isEmpty(name) && StringUtil.isEmpty(pwd)) {
                    mEdtName.setShakeAnimation();
                    mEdtPwd.setShakeAnimation();
                    ToastUtil.showText(UIUtils.getString(R.string.please_enter_account_information));
                    return;
                }

                if (StringUtil.isEmpty(name)) {
                    mEdtName.setShakeAnimation();
                    ToastUtil.showText(UIUtils.getString(R.string.username_not_empty));
                    return;
                }

                if (StringUtil.isEmpty(pwd)) {
                    mEdtPwd.setShakeAnimation();
                    ToastUtil.showText(UIUtils.getString(R.string.password_not_empty));
                    return;
                }

                login();
                break;
            case R.id.iv_sinaweibo:
                mLoginShareHelp.weiboAuthorizedLogin();
                break;
            case R.id.iv_qq:
                mLoginShareHelp.qqAuthorizedLogin();
                break;
            case R.id.iv_wechat:
                mLoginShareHelp.wechatAuthorizedLogin();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mLoginShareHelp.onActivityResult(requestCode, resultCode, data);
    }
}

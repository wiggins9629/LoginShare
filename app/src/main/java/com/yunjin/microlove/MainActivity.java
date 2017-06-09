package com.yunjin.microlove;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.yunjin.microlove.base.BaseActivity;
import com.yunjin.microlove.bean.DialogGrid;
import com.yunjin.microlove.listener.OnDataListPositionListener;
import com.yunjin.microlove.utils.Constant;
import com.yunjin.microlove.utils.DialogUtil;
import com.yunjin.microlove.utils.StringUtil;
import com.yunjin.microlove.utils.UIUtils;
import com.yunjin.microlove.wechat.WechatShareManager;
import com.yunjin.microlove.widget.TitleView;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description 第三方账号登录分享
 * @Author 一花一世界
 */
public class MainActivity extends BaseActivity {

    private MainActivity mActivity = null;
    private TitleView titleView;
    private TextView mTvToken;
    private List<DialogGrid> dialogList;

    private String platformType;
    private String platformInfo;
    private LoginShareHelp mLoginShareHelp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mActivity = this;
        mLoginShareHelp = LoginShareHelp.getInstance(mActivity);

        initView();
        setListener();
        initData();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mLoginShareHelp.onNewIntent(intent);
    }

    private void initView() {
        titleView = (TitleView) findViewById(R.id.titleView);
        titleView.setAppTitle(UIUtils.getString(R.string.title));
        titleView.setLeftImageVisibility(View.GONE);
        titleView.setRightImageResource(R.drawable.share);
        mTvToken = (TextView) findViewById(R.id.tv_token);
    }

    private void initData() {
        Intent intent = getIntent();
        if (intent != null) {
            platformType = intent.getStringExtra(Constant.PLATFORM_TYPE);
            platformInfo = intent.getStringExtra(Constant.PLATFORM_TYPE_CONTENT);
        }

        if (!StringUtil.isEmpty(platformType)) {
            mTvToken.setText(platformType + ":\n" + platformInfo);
        }

        if (dialogList == null) {
            dialogList = new ArrayList<>();
        }
        dialogList.add(new DialogGrid(R.drawable.weibo, UIUtils.getString(R.string.weibo)));
        dialogList.add(new DialogGrid(R.drawable.qq, UIUtils.getString(R.string.qq)));
        dialogList.add(new DialogGrid(R.drawable.qzone, UIUtils.getString(R.string.qzone)));
        dialogList.add(new DialogGrid(R.drawable.wechat_session, UIUtils.getString(R.string.wechat_session)));
        dialogList.add(new DialogGrid(R.drawable.wechat_friends, UIUtils.getString(R.string.wechat_friends)));
        dialogList.add(new DialogGrid(R.drawable.wechat_facorite, UIUtils.getString(R.string.wechat_facorite)));
    }

    private void setListener() {
        titleView.getRightImageView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtil.showBottomGridSelection(mActivity, dialogList, new OnDataListPositionListener() {
                    @Override
                    public void onSelectItem(int position) {
                        switch (position) {
                            case 0://微博
                                mLoginShareHelp.shareToWeiboTextImage("我爱西红柿", "一花一世界",
                                        "http://hot.ynet.com/2017/06/08/197429t1593.html", R.drawable.image_share);
                                break;
                            case 1://QQ
                                mLoginShareHelp.shareToQqImageText("一花一世界", "http://hot.ynet.com/2017/06/08/197429t1593.html",
                                        "我爱西红柿", "http://img06.tooopen.com/images/20170514/tooopen_sy_210122159348.jpg");
                                break;
                            case 2://QQ空间
                                ArrayList<String> listUrl = new ArrayList<>();
                                listUrl.add("http://img06.tooopen.com/images/20170514/tooopen_sy_210122159348.jpg");
                                listUrl.add("http://img06.tooopen.com/images/20170304/tooopen_sy_200486614796.jpg");
                                mLoginShareHelp.shareToQzone("一花一世界", "http://hot.ynet.com/2017/06/08/197429t1593.html",
                                        "我爱西红柿", listUrl);
                                break;
                            case 3://微信会话
                                mLoginShareHelp.shareToWechatImage(R.drawable.image_share, WechatShareManager.WECHAT_SHARE_TYPE_SESSION);
                                break;
                            case 4://微信朋友圈
                                mLoginShareHelp.shareToWechatText("我爱西红柿", WechatShareManager.WECHAT_SHARE_TYPE_FRIENDS);
                                break;
                            case 5://微信收藏
                                mLoginShareHelp.shareToWechatWebpage("一花一世界", "我爱西红柿",
                                        "http://hot.ynet.com/2017/06/08/197429t1593.html",
                                        R.drawable.image_share, WechatShareManager.WECHAT_SHARE_TYPE_FAVORITE);
                                break;
                        }
                    }
                });
            }
        });
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mLoginShareHelp.onActivityResult(requestCode, resultCode, data);
    }*/
}

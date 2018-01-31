package com.ousy.unityapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import abc.abc.abc.AdManager;
import abc.abc.abc.nm.bn.BannerManager;
import abc.abc.abc.nm.bn.BannerViewListener;
import abc.abc.abc.nm.cm.ErrorCode;
import abc.abc.abc.nm.sp.SpotListener;
import abc.abc.abc.nm.sp.SpotManager;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
    private Context mContext;
    private PermissionHelper mPermissionHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;

        // 当系统为6.0以上时，需要申请权限
        mPermissionHelper = new PermissionHelper(this);
        mPermissionHelper.setOnApplyPermissionListener(new PermissionHelper.OnApplyPermissionListener()
        {
            @Override
            public void onAfterApplyAllPermission()
            {
                runAppLogic();
            }
        });
        if (Build.VERSION.SDK_INT < 23)
        {
            // 如果系统版本低于23，直接跑应用的逻辑
            runAppLogic();
        }
        else
        {
            // 如果权限全部申请了，那就直接跑应用逻辑
            if (mPermissionHelper.isAllRequestedPermissionGranted())
            {
                runAppLogic();
            }
            else
            {
                // 如果还有权限为申请，而且系统版本大于23，执行申请权限逻辑
                mPermissionHelper.applyPermissions();
            }
        }

        TextView textView = (TextView) findViewById(R.id.text);

        textView.setOnClickListener(this);
    }

    private void initImage()
    {
        RelativeLayout layout = (RelativeLayout) View.inflate(mContext,R.layout.banner_layout, null);
        ImageView imgLogo = new ImageView(mContext);
        imgLogo.setImageResource(R.drawable.ic_launcher);
        layout.addView(imgLogo);

        RelativeLayout.LayoutParams rlytParams = (RelativeLayout.LayoutParams) imgLogo.getLayoutParams();
        rlytParams.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE);
        imgLogo.setLayoutParams(rlytParams);

        ImageView imgClose = (ImageView) layout.findViewById(R.id.ad_close);
        imgClose.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.e("ousyx","hhahhaha");
            }
        });

        WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams
                .FLAG_NOT_FOCUSABLE;
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_ATTACHED_DIALOG;
        layoutParams.alpha = 1.0F;
        layoutParams.format = 1;
        layoutParams.gravity = Gravity.BOTTOM | Gravity.RIGHT; // 这里示例为：在右下角展示广告条
        windowManager.addView(layout, layoutParams);
    }

    private int dip2px(Context context, float dipValue)
    {
        Resources r = context.getResources();
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dipValue, r.getDisplayMetrics());
    }

    private void runAppLogic()
    {
        initSDK();
        setupSpotAd();
        showSpotAd();
    }

    private void initSDK()
    {
        AdManager.getInstance(this).init("85aa56a59eac8b3d", "a14006f66f58d5d7", true);
    }

    /**
     * 设置插屏广告
     */
    private void setupSpotAd()
    {
        Log.e("ousyx", "setupSpotAd");
        // 横图
        SpotManager.getInstance(mContext).setImageType(SpotManager.IMAGE_TYPE_HORIZONTAL);
        // 高级动画
        SpotManager.getInstance(mContext).setAnimationType(SpotManager.ANIMATION_TYPE_ADVANCED);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        // 插屏广告
        SpotManager.getInstance(mContext).onPause();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        // 插屏广告
        SpotManager.getInstance(mContext).onStop();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        // 插屏广告
        SpotManager.getInstance(mContext).onDestroy();
    }

    @Override
    public void onClick(View v)
    {
        //        Intent intent = new Intent(this,HelpActivity.class);
        //        startActivity(intent);
        Log.e("ousyx","show3");
        //showSpotAd();
        initImage();
    }

    private void showSpotAd()
    {
        // 展示插屏广告
        SpotManager.getInstance(mContext).showSpot(mContext, new SpotListener() {
            @Override
            public void onShowSuccess() {
                Log.e("ousyx","插屏展示成功");
            }

            @Override
            public void onShowFailed(int errorCode) {
                Log.e("ousyx",errorCode+"is ErrorCode");
                switch (errorCode) {
                    case ErrorCode.NON_NETWORK:
                        Log.e("ousyx","网络异常");
                        break;
                    case ErrorCode.NON_AD:
                        Log.e("ousyx","暂无插屏广告");
                        break;
                    case ErrorCode.RESOURCE_NOT_READY:
                        Log.e("ousyx","插屏资源还没准备好");
                        break;
                    case ErrorCode.SHOW_INTERVAL_LIMITED:
                        Log.e("ousyx","请勿频繁展示");
                        break;
                    case ErrorCode.WIDGET_NOT_IN_VISIBILITY_STATE:
                        Log.e("ousyx","请设置插屏为可见状态");
                        break;
                    default:
                        Log.e("ousyx","矛知哇");
                        break;
                }
            }

            @Override
            public void onSpotClosed() {
                Log.e("ousyx","插屏被关闭");
            }

            @Override
            public void onSpotClicked(boolean isWebPage) {
                Log.e("ousyx","插屏被点击");
            }
        });

        Log.e("ousyx","show4");
    }
}

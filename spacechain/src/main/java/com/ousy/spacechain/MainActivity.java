package com.ousy.spacechain;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.unity3d.player.UnityPlayer;
import com.unity3d.player.UnityPlayerActivity;

import net.youmi.android.AdManager;
import net.youmi.android.normal.banner.BannerManager;
import net.youmi.android.normal.banner.BannerViewListener;
import net.youmi.android.normal.common.ErrorCode;
import net.youmi.android.normal.spot.SpotListener;
import net.youmi.android.normal.spot.SpotManager;

/**
 * Created by ousyy on 2018/1/25.
 */

public class MainActivity extends UnityPlayerActivity
{
    // 无积分广告
    private final static int SHOW_SPOT_AD = 100;

    private final static int HIDE_SPOT_AD = 101;

    private final static int SHOW_BANNER_AD = 103;

    private final static int HIDE_BANNER_AD = 104;

    private Context mContext;
    private PermissionHelper mPermissionHelper;

    // 广告条的布局
    private RelativeLayout mBannerLayout;

    private static Handler sHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mPermissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        mPermissionHelper.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 运行应用逻辑
     */
    private void runAppLogic()
    {
        // 初始化SDK
        initSDK();
        // 设置插屏广告
        setupSpotAd();

        initHandler();
    }

    /**
     * 初始化SDK
     */
    private void initSDK()
    {
        // 初始化接口，应用启动的时候调用，参数：appId, appSecret, 是否开启调试模式, 是否开启有米日志
        AdManager.getInstance(this).init("df4b083e49d87d89", "1dfd1d57ff37ff25", false, false);
    }

    /**
     * 设置插屏广告
     */
    private void setupSpotAd()
    {
        // 横图
        SpotManager.getInstance(mContext).setImageType(SpotManager.IMAGE_TYPE_HORIZONTAL);
        // 高级动画
        SpotManager.getInstance(mContext).setAnimationType(SpotManager.ANIMATION_TYPE_ADVANCED);
        internalShowSpotAd();
    }

    private void initHandler()
    {
        sHandler = new Handler()
        {
            @Override
            public void handleMessage(Message msg)
            {
                super.handleMessage(msg);
                switch (msg.what)
                {
                    case SHOW_SPOT_AD:
                        internalShowSpotAd();
                        break;
                    case HIDE_SPOT_AD:
                        internalHideSpotAd();
                        break;
                    case SHOW_BANNER_AD:
                        internalShowBannerAd();
                        break;
                    case HIDE_BANNER_AD:
                        internalHideBannerAd();
                        break;
                    default:
                        break;
                }
            }
        };
    }

    /**
     * 展示插屏广告
     */
    private void internalShowSpotAd()
    {
        SpotManager.getInstance(mContext).showSpot(mContext, new SpotListener()
        {
            @Override
            public void onShowSuccess()
            {
                Log.e("ousyx", "插屏展示成功");
            }

            @Override
            public void onShowFailed(int errorCode)
            {
                Log.e("ousyx", errorCode + "is ErrorCode");
                switch (errorCode)
                {
                    case ErrorCode.NON_NETWORK:
                        Log.e("ousyx", "网络异常");
                        break;
                    case ErrorCode.NON_AD:
                        Log.e("ousyx", "暂无插屏广告");
                        break;
                    case ErrorCode.RESOURCE_NOT_READY:
                        Log.e("ousyx", "插屏资源还没准备好");
                        break;
                    case ErrorCode.SHOW_INTERVAL_LIMITED:
                        Log.e("ousyx", "请勿频繁展示");
                        break;
                    case ErrorCode.WIDGET_NOT_IN_VISIBILITY_STATE:
                        Log.e("ousyx", "请设置插屏为可见状态");
                        break;
                    default:
                        Log.e("ousyx", "矛知哇");
                        break;
                }
            }

            @Override
            public void onSpotClosed()
            {
                Log.e("ousyx", "插屏被关闭");
            }

            @Override
            public void onSpotClicked(boolean isWebPage)
            {
                Log.e("ousyx", "插屏被点击");
            }
        });
    }

    /**
     * 隐藏插屏广告
     */
    private void internalHideSpotAd()
    {
        SpotManager.getInstance(mContext).hideSpot();
    }

    /**
     * 展示广告条
     */
    private void internalShowBannerAd()
    {
        if (mBannerLayout == null)
        {
            intiBannerLayout();
        }
    }

    // 初始化广告条布局
    private void intiBannerLayout()
    {
        mBannerLayout = (RelativeLayout) View.inflate(mContext, R.layout.banner_layout, null);
        ImageView imgClose = (ImageView) mBannerLayout.findViewById(R.id.ad_close);
        imgClose.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                HideBannerAd();
            }
        });

        View bannerView = BannerManager.getInstance(mContext)
                .getBannerView(mContext, new BannerViewListener()
                {
                    @Override
                    public void onRequestSuccess()
                    {
                        Log.e("ousyx", "请求广告条成功");
                    }

                    @Override
                    public void onSwitchBanner()
                    {
                        Log.e("ousyx", "广告条切换");
                    }

                    @Override
                    public void onRequestFailed()
                    {
                        Log.e("ousyx", "请求广告条失败");
                    }
                });

        mBannerLayout.addView(bannerView);
        // 把关闭按钮移到最上层，要不会被bannerView覆盖
        imgClose.bringToFront();
        // 使用WindowManager来展示广告条
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
        windowManager.addView(mBannerLayout, layoutParams);
    }

    /**
     * 隐藏广告条
     */
    private void internalHideBannerAd()
    {
        if (mBannerLayout != null)
        {
            ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).removeView(mBannerLayout);
            mBannerLayout = null;
        }
    }

    private synchronized void sendMsgToHandler(int type)
    {
        Message msg = sHandler.obtainMessage();
        msg.what = type;
        msg.sendToTarget();
    }

    /**
     * 展示插屏广告
     */
    public void ShowSpotAd()
    {
        sendMsgToHandler(SHOW_SPOT_AD);
    }

    /**
     * 隐藏插屏广告
     */
    public void HideSpotAd()
    {
        if (SpotManager.getInstance(mContext).isSpotShowing())
        {
            sendMsgToHandler(HIDE_SPOT_AD);
        }
    }

    /**
     * 展示广告条
     */
    public void ShowBannerAd()
    {
        sendMsgToHandler(SHOW_BANNER_AD);
    }

    /**
     * 隐藏广告条
     */
    public void HideBannerAd()
    {
        sendMsgToHandler(HIDE_BANNER_AD);
    }

    public void ShowDialog(final String _title, final String _content, final String isCancelable)
    {

        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setCancelable(isCancelable.equals("true"));
                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        callUnityFunc("GameManager", "QuitGame", "");
                    }
                });
                dialog.setNegativeButton("取消", null);
                dialog.setTitle(_title);
                dialog.setMessage(_content);
                dialog.show();
            }
        });
    }

    // 定义一个显示Toast的方法，在Unity中调用此方法
    public void ShowToast(final String mStr2Show)
    {
        // 同样需要在UI线程下执行
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                Toast.makeText(getApplicationContext(), mStr2Show, Toast.LENGTH_SHORT).show();
            }
        });
    }

    //  定义一个手机振动的方法，在Unity中调用此方法
    public void SetVibrator()
    {
        Vibrator mVibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        mVibrator.vibrate(200);
    }

    // 第一个参数是unity中的对象名字，记住是对象名字，不是脚本类名
    // 第二个参数是函数名
    // 第三个参数是传给函数的参数，目前只看到一个参数，并且是string的，自己传进去转吧
    public void callUnityFunc(String _objName, String _funcStr, String _content)
    {
        UnityPlayer.UnitySendMessage(_objName, _funcStr, _content);
    }

    // 退出应用时，释放资源
    public void ReleaseRes()
    {
        // 广告条
        BannerManager.getInstance(mContext).onDestroy();
        // 插屏广告（包括普通插屏广告、轮播插屏广告、原生插屏广告）
        SpotManager.getInstance(mContext).onDestroy();
        SpotManager.getInstance(mContext).onAppExit();
    }
}

package com.ousy.spacechain;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Vibrator;
import android.widget.Toast;

import com.unity3d.player.UnityPlayer;
import com.unity3d.player.UnityPlayerActivity;

/**
 * Created by ousyy on 2018/1/25.
 */

public class MainActivity extends UnityPlayerActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
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
}

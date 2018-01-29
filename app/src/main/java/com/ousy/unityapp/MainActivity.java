package com.ousy.unityapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import abc.abc.abc.AdManager;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AdManager.getInstance(this).init("df4b083e49d87d89", "1dfd1d57ff37ff25", true);
        TextView textView = (TextView) findViewById(R.id.text);

        textView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
//        Intent intent = new Intent(this,HelpActivity.class);
//        startActivity(intent);
        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
//        dialog.setTitle("This is Dialog");
        dialog.setMessage("是否退出游戏");
        dialog.setCancelable(true);
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                Log.e("ousy","确定");
            }
        });
        dialog.setNegativeButton("取消",null);
        dialog.show();
    }
}

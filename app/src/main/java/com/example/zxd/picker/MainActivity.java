package com.example.zxd.picker;


import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.zxd.picker.bean.Bean;
import com.example.zxd.picker.util.VibratorUtil;

import org.apache.http.util.EncodingUtils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static javax.xml.transform.OutputKeys.ENCODING;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener, SensorEventListener {

    /**
     * UI
     */
    private Button mBtn;
    private TextView mTvInfo;
    private TextView mTvAction;

    /**
     * Sensor
     */
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;

    /**
     * 存放需要写入的信息
     */
    private List<Bean> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
        initEvent();
        initData();
    }

    private void initData() {
        dataList = new ArrayList<>();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    private void initEvent() {
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    private void initUI() {
        mBtn = (Button) findViewById(R.id.btn);
        mBtn.setOnTouchListener(this);
        mTvInfo = (TextView) findViewById(R.id.tv_info);
        mTvAction = (TextView) findViewById(R.id.tv_action);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        mTvAction.setText(item.getTitle());
        switch (id) {
            case R.id.action_1:
                break;
            case R.id.action_2:
            default:
                break;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v.getId() == R.id.btn) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                Log.d("test", "cansal button ---> cancel");
                mBtn.setBackgroundResource(R.color.btn_up);
                VibratorUtil.Vibrate(MainActivity.this, 50);   //震动100ms
//                Log.i("zzz", readFileData("1.txt"));
                Log.i("zzz", dataList.toString());
                String filename = mTvAction.getText().toString() + "-" + generaterNumber();
                Log.i("zzz", filename);
                writeFileData(filename, dataList.toString());
                dataList.clear();
            }
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                Log.d("test", "cansal button ---> down");
                mBtn.setBackgroundResource(R.color.btn_pressed);
                VibratorUtil.Vibrate(MainActivity.this, 100);   //震动100ms
//                writeFileData("1.txt", "sfdfsd");
            }
        }
        return false;
    }


    public String generaterNumber() {
        String id = null;
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        id = formatter.format(date);
        return id;
    }

    //向指定的文件中写入指定的数据
    public void writeFileData(String filename, String message) {
        try {
            FileOutputStream fout = openFileOutput(filename, MODE_PRIVATE);//获得FileOutputStream
            //将要写入的字符串转换为byte数组
            byte[] bytes = message.getBytes();
            fout.write(bytes);//将byte数组写入文件
            fout.close();//关闭文件输出流
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //打开指定文件，读取其数据，返回字符串对象
    public String readFileData(String fileName) {
        String result = "";
        try {
            FileInputStream fin = openFileInput(fileName);
            //获取文件长度
            int lenght = fin.available();
            byte[] buffer = new byte[lenght];
            fin.read(buffer);
            //将byte数组转换成指定格式的字符串
            result = EncodingUtils.getString(buffer, ENCODING);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.d("linc", "value size: " + event.values.length);
        float xValue = event.values[0];// Acceleration minus Gx on the x-axis
        float yValue = event.values[1];//Acceleration minus Gy on the y-axis
        float zValue = event.values[2];//Acceleration minus Gz on the z-axis
        dataList.add(new Bean(xValue, yValue, zValue));
        mTvInfo.setText("x轴： " + xValue + "  y轴： " + yValue + "  z轴： " + zValue);
        if (xValue > SensorManager.STANDARD_GRAVITY) {
            mTvInfo.append("\n重力指向设备左边");
        } else if (xValue < -SensorManager.STANDARD_GRAVITY) {
            mTvInfo.append("\n重力指向设备右边");
        } else if (yValue > SensorManager.STANDARD_GRAVITY) {
            mTvInfo.append("\n重力指向设备下边");
        } else if (yValue < -SensorManager.STANDARD_GRAVITY) {
            mTvInfo.append("\n重力指向设备上边");
        } else if (zValue > SensorManager.STANDARD_GRAVITY) {
            mTvInfo.append("\n屏幕朝上");
        } else if (zValue < -SensorManager.STANDARD_GRAVITY) {
            mTvInfo.append("\n屏幕朝下");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}

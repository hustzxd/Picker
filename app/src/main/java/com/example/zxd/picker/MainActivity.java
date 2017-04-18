package com.example.zxd.picker;


import android.Manifest;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zxd.picker.bean.Bean;
import com.example.zxd.picker.util.VibratorUtil;

import org.apache.http.util.EncodingUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

import static javax.xml.transform.OutputKeys.ENCODING;

@RuntimePermissions
public class MainActivity extends AppCompatActivity implements View.OnTouchListener, SensorEventListener {

    /**
     * UI
     */
    private Button mBtn;
    private TextView mTvAcc;
    private TextView mTvGyr;
    private TextView mTvAction;

    /**
     * Sensor
     */
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mGyroscope;

    /**
     * 存放需要写入的信息
     */
    private List<Bean> mAccList;
    private List<Bean> mGyrList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
        initEvent();
        initData();
    }

    private void initData() {
        mAccList = new ArrayList<>();
        mGyrList = new ArrayList<>();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mGyroscope, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    private void initEvent() {
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);//三轴加速度传感器
        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);//陀螺仪
        //第一：默认初始化
//        Bmob.initialize(this, "b50de1ca8c7269f161b4ab7060824c84");
    }

    private void initUI() {
        mBtn = (Button) findViewById(R.id.btn);
        mBtn.setOnTouchListener(this);
        mTvAction = (TextView) findViewById(R.id.tv_action);
        mTvAcc = (TextView) findViewById(R.id.tv_acc);
        mTvGyr = (TextView) findViewById(R.id.tv_gyroscope);
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
                Log.i("zzz", mAccList.toString());
                Log.i("zzz", mGyrList.toString());
                String filename = Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_DOWNLOADS + "/" + mTvAction.getText().toString() + "-" + generaterNumber();
                Log.i("zzz", filename);
                MainActivityPermissionsDispatcher.writeFileSdcardFileWithCheck(this, filename, mTvAction.getText().toString() + " " + mAccList.toString() + mGyrList.toString());
//                writeFileData(filename, dataList.toString());
//                Gesture gesture = new Gesture();
//                gesture.setAccList(mAccList.toString());
//                gesture.setGyrList(mGyrList.toString());
//                gesture.save(new SaveListener<String>() {
//                    @Override
//                    public void done(String s, BmobException e) {
//                        if (e == null) {
//                            Log.i("zzz", "创建数据成功: " + s);
//                            Toast.makeText(getApplicationContext(), "创建数据成功：" + s, Toast.LENGTH_SHORT).show();
//                        } else {
//                            Log.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
//                        }
//                    }
//                });
                mAccList.clear();
                mGyrList.clear();
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
//
//
//    //写数据到SD中的文件
//    public void writeFileSdcardFile(String fileName, String write_str) throws IOException {
//        try {
//
//            FileOutputStream fout = new FileOutputStream(fileName);
//            byte[] bytes = write_str.getBytes();
//
//            fout.write(bytes);
//            fout.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }


    //读SD中的文件
    public String readFileSdcardFile(String fileName) throws IOException {
        String res = "";
        try {
            FileInputStream fin = new FileInputStream(fileName);

            int length = fin.available();

            byte[] buffer = new byte[length];
            fin.read(buffer);

            res = EncodingUtils.getString(buffer, "UTF-8");

            fin.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
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
        Sensor sensor = event.sensor;
        if (sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            Log.d("zzz-gyr", System.currentTimeMillis() + "");
            float xValue = event.values[0];// Acceleration minus Gx on the x-axis
            float yValue = event.values[1];//Acceleration minus Gy on the y-axis
            float zValue = event.values[2];//Acceleration minus Gz on the z-axis
            mTvGyr.setText(xValue + " " + yValue + " " + zValue);
            mGyrList.add(new Bean(xValue * 10000, yValue * 10000, zValue * 10000));
        } else if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
//            Log.d("linc", "value size: " + event.values.length);
            Log.d("zzz-acc", System.currentTimeMillis() + "");

            float xValue = event.values[0];// Acceleration minus Gx on the x-axis
            float yValue = event.values[1];//Acceleration minus Gy on the y-axis
            float zValue = event.values[2];//Acceleration minus Gz on the z-axis
            mAccList.add(new Bean(xValue, yValue, zValue));
            mTvAcc.setText("x轴： " + xValue + "  y轴： " + yValue + "  z轴： " + zValue);
            if (xValue > SensorManager.STANDARD_GRAVITY) {
                mTvAcc.append("\n重力指向设备左边");
            } else if (xValue < -SensorManager.STANDARD_GRAVITY) {
                mTvAcc.append("\n重力指向设备右边");
            } else if (yValue > SensorManager.STANDARD_GRAVITY) {
                mTvAcc.append("\n重力指向设备下边");
            } else if (yValue < -SensorManager.STANDARD_GRAVITY) {
                mTvAcc.append("\n重力指向设备上边");
            } else if (zValue > SensorManager.STANDARD_GRAVITY) {
                mTvAcc.append("\n屏幕朝上");
            } else if (zValue < -SensorManager.STANDARD_GRAVITY) {
                mTvAcc.append("\n屏幕朝下");
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        //写数据到SD中的文件
    void writeFileSdcardFile(String fileName, String write_str) {
        try {
            FileOutputStream fout = null;
            fout = new FileOutputStream(fileName);
            byte[] bytes = write_str.getBytes();
            fout.write(bytes);
            fout.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @OnShowRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void showRationaleForRecord(final PermissionRequest request) {
        showRationaleDialog(R.string.permission_record_rationale, request);
    }

    @OnPermissionDenied(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void showDeniedForCamera() {
        Toast.makeText(this, "Denied", Toast.LENGTH_SHORT).show();
    }

    @OnNeverAskAgain(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void showNeverAskForCamera() {
        Toast.makeText(this, "neverAsk", Toast.LENGTH_SHORT).show();
    }

    private void showRationaleDialog(@StringRes int messageRestId, final PermissionRequest request) {
        new AlertDialog.Builder(this)
                .setPositiveButton("allow", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        request.proceed();
                    }
                })
                .setNegativeButton("deny", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        request.cancel();
                    }
                })
                .setCancelable(false)
                .setMessage(messageRestId)
                .show();
    }

}

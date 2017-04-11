package com.example.zxd.picker.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.os.Environment;


/**
 * 文件操作类，用来进行文件操作
 */
public class FileOperate {
    private Context context;// 方便使用操作文件的方法

    /**
     * 使用构造方法设置context
     *
     * @param context
     */
    public FileOperate(Context context) {
        this.context = context;
    }

    /**
     * 使用private模式创建文件
     *
     * @throws IOException
     */
    public void saveContent(String filename, String content, int mode) throws IOException {
        // 创建写入文件的输出流对象
        FileOutputStream outStream = context.openFileOutput(filename, mode);
        // 写入数据内容
        saveFile(content, outStream);
    }

    /**
     * 保存文件(方便以后的调用)
     *
     * @throws IOException
     */
    private void saveFile(String content, FileOutputStream outStream) throws IOException {
        outStream.write(content.getBytes());// 写入数据内容
        outStream.close();// 关闭文件输出流
    }

    /**
     * 获取/data/data/package name/files根目录
     *
     * @return
     */
    public String getFilesDir() {
        // 首先获取文件对象
        File file = context.getFilesDir();
        return file.getAbsolutePath();// 返回文件夹路径
    }

    /**
     * 获取/data/data/package name/cache根目录
     *
     * @return
     */
    public String getCacheDir() {
        // 首先获取文件对象
        File file = context.getCacheDir();
        return file.getAbsolutePath();// 返回文件夹路径
    }

    /**
     * 读取私有文件内容的方法
     *
     * @param filename
     * @return
     * @throws IOException
     */
    public String read(String filename) throws IOException {
        // 创建文件输入流对象
        FileInputStream inStream = context.openFileInput(filename);
        // 构建将转换输出流对象
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        // 定义一个字节缓存数组
        byte[] buffer = new byte[4096];
        // 读取的数据长度
        int len = 0;
        // 循环读取内容
        while ((len = inStream.read(buffer)) != -1) {
            // 将读取的内容写入流当中
            outStream.write(buffer, 0, len);
        }
        // 将读取的数据转换为字节数组
        byte[] datas = outStream.toByteArray();
        // 关闭输入流和输出流对象
        outStream.close();
        inStream.close();
        return new String(datas);// 返回读取字符串
    }

    /**
     * 获取SDcard路径
     */
    private String getSdcardPath() {
        // 判断sdcard是否存在并且可读写
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            // 返回Sdcard的根目录
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        } else {
            return null;
        }
    }

    /**
     * 将数据写入SDCard中
     *
     * @param name
     * @param content
     * @throws IOException
     */
    public void writeToSdcard(String name, String content) throws IOException {
        // 获取SDcard的根目录
        String sdcardPath = getSdcardPath();
        // 判断是否为空
        if (sdcardPath != null) {// 不为空
            // 创建文件对象
            File file = new File(getSdcardPath(), name);
            // 判断文件是否存在
            if (!file.exists()) {// 不存在
                // 创建文件
                file.createNewFile();
            }
            // 创建输出流对象
            FileOutputStream outStream = new FileOutputStream(file);
            // 保存文件
            saveFile(content, outStream);
        } else {// 为空
            // MyUtil.showMsg(context ,context.getString(R.string.sdcard_error));

        }
    }
}
package com.example.zxd.picker.bean;

import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * 存储手势
 * Created by zxd on 17-4-17.
 */

public class Gesture extends BmobObject {

    public String getAccList() {
        return accList;
    }

    public void setAccList(String accList) {
        this.accList = accList;
    }

    public String getGyrList() {
        return gyrList;
    }

    public void setGyrList(String gyrList) {
        this.gyrList = gyrList;
    }

    //    private BmobFile gestureFile;
    private String accList;
    private String gyrList;

}

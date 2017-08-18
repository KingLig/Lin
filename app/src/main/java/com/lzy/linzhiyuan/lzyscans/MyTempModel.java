package com.lzy.linzhiyuan.lzyscans;

/**
 * Created by linzhiyuan on 2017/4/8.
 */

public class MyTempModel {

    private String txtBarcode;
//    private boolean Checkeds;
    private boolean ckremobe=true;

    public MyTempModel(String txtBarcode, boolean ckremobe) {
        this.txtBarcode = txtBarcode;
        this.ckremobe = ckremobe;
    }
    public MyTempModel(){

    }
    public boolean isCkremobe() {
        return ckremobe;
    }

    public void setCkremobe(boolean ckremobe) {
        this.ckremobe = ckremobe;
    }

    public String getTxtBarcode() {
        return txtBarcode;
    }

    public void setTxtBarcode(String txtBarcode) {
        this.txtBarcode = txtBarcode;
    }

    private int number;

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

}

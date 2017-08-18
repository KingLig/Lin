package com.lzy.linzhiyuan.lzyscans;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.linzhiyuan.lzyscans.adapters.ListCodeAdapter;
import com.lzy.linzhiyuan.lzyscans.adapters.SpinnerAdapter;
import com.lzy.linzhiyuan.lzyscans.db.DBHelper;
import com.lzy.linzhiyuan.lzyscans.sound.SoundVibratorManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by linzhiyuan on 2017/7/14.
 */

public class MainNewActivity extends Activity implements AdapterView.OnItemClickListener,View.OnClickListener {

   private ImageView img_save;
    private ImageView img_deleback;
    private ImageView img_exit;

    private ImageView img_delete;

    private EditText editbarcode,et_mark,edit_sex;
    Spinner spn_fwfs;
    private DBHelper helper;

    ListView mListView;
    private static Stack<Activity> activityStack;
    MainNewActivity.Receiver receiver;
    private Button btn_chosses;


    private TextView tv_number,tv_onenumber;
    ListCodeAdapter myAdapternew=null;
    List<MyTempModel> list_AdapterData = new ArrayList<MyTempModel>();

    SpinnerAdapter<String> mPackAdapter_fwfs;

    public ArrayList<HashMap<String, Object>>  listbarcode = new ArrayList<HashMap<String, Object>>();;// 查询所有单号的数据
    /**
     * 获得SD卡目录
     */
    private static File SDPATH = Environment.getExternalStorageDirectory();
    /**
     * 日志存放目录
     */
    private static String LOGFILENAME = SDPATH.getAbsolutePath() + "/TEXT文件/";
    // 得到是否存在SD卡
    private static boolean sdExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newmain);
        init();
    }

    private void init(){

        img_save=(ImageView)findViewById(R.id.img_save);
        img_deleback=(ImageView)findViewById(R.id.img_deleback);
        img_exit=(ImageView)findViewById(R.id.img_exit);
        img_delete=(ImageView)findViewById(R.id.img_delete);
        editbarcode=(EditText)findViewById(R.id.editbarcode);
        et_mark=(EditText)findViewById(R.id.et_mark);
        mListView=(ListView)findViewById(R.id.list1);
        edit_sex=(EditText)findViewById(R.id.edit_sex) ;
        spn_fwfs = (Spinner)findViewById(R.id.spn_fwf);
        btn_chosses=(Button) findViewById(R.id.btn_chosses) ;
        tv_number=(TextView)findViewById(R.id.tv_number);
        tv_onenumber=(TextView)findViewById(R.id.tv_onenumber) ;
        mPackAdapter_fwfs = new SpinnerAdapter<String>(this, R.layout.spinner_item, Constants.SERVICES);
        spn_fwfs.setAdapter(mPackAdapter_fwfs);
        spn_fwfs.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {


                et_mark.setText(spn_fwfs.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        helper = new DBHelper(MainNewActivity.this);
        img_save.setOnClickListener(this);
        img_deleback.setOnClickListener(this);
        img_exit.setOnClickListener(this);
        img_delete.setOnClickListener(this);
        btn_chosses.setOnClickListener(this);
        SoundVibratorManager.initSounds(this);
        SoundVibratorManager.initSounds(this);
        SoundVibratorManager.addSound(2, R.raw.errorflag);
        SoundVibratorManager.addSound(1, R.raw.scan);
        /************************列表初始化start*********************/
//        mListView.setChoiceMode(mListView.CHOICE_MODE_SINGLE);



        mListView.setOnItemClickListener(this);








    }


    private void Test(){

//
        for(int i=0;i<10;i++){

            refreshData(Integer.toString(i)+"1234567890123");
        }



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        helper.close();
        finish();

    }

    @Override
    protected void onStart() {
        super.onStart();
        receiver = new MainNewActivity.Receiver();
        IntentFilter filter = new IntentFilter(ScannerHelper.ACTION_SCANNER_SEND_BARCODE);
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(receiver!=null){
            unregisterReceiver(receiver);
        }
    }






    private  boolean chesofalse=false;

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.img_exit:


                Extfind();
                break;
            case R.id.img_save:
                if(list_AdapterData.size()>0){
                    dialog3();
                }else {
                    Toast.makeText(MainNewActivity.this, "请扫描条码", Toast.LENGTH_LONG).show();

                }
                break;
            case R.id.btn_chosses:

                if(list_AdapterData.size()>0) {
                    if (btn_chosses.getText().toString().trim().equals("全选")) {
                        myAdapternew.configCheckMap(true);
                        myAdapternew.notifyDataSetChanged();
                        btn_chosses.setText("全不选");
                        chesofalse=true;
                    } else {
                        // 所有项目全部不选中
                        myAdapternew.configCheckMap(false);
                        myAdapternew.notifyDataSetChanged();
                        btn_chosses.setText("全选");
                    }
                }else {
                    Toast.makeText(MainNewActivity.this, "请扫描条码再操作", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.img_delete:


                Delebarcode();//删除表的条码
                break;


        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View itemLayout, int position, long id) {


        if (itemLayout.getTag() instanceof ListCodeAdapter.ViewHolder) {

            ListCodeAdapter.ViewHolder holder = (ListCodeAdapter.ViewHolder) itemLayout.getTag();

            // 会自动出发CheckBox的checked事件
            holder.cbCheck.toggle();

        }




    }


    private int scanNumber=0;
    private int scnaoneNumber=0;

    /**
     * 扫描获取条码
     *

     */
    public class Receiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            Context mContext = null;

            String barcode = intent.getExtras().getString("scannerdata");

            editbarcode.setText(barcode);

           int getindest= FindListBox(barcode);
            if(getindest==1){



                if(onechcode) {
                    scanNumber++;
                    tv_number.setText("二维码录入数：" + Integer.toString(scanNumber));
                }else {

                    scnaoneNumber++;
                    tv_onenumber.setText("一维码录入数：" + Integer.toString(scnaoneNumber));
                }



            }



        }};



    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP){
            String scanText= editbarcode.getText().toString();

            int getindest= FindListBox(scanText);
            if(getindest==1){

                if(onechcode) {
                    scanNumber++;
                    tv_number.setText("二维码录入数：" + Integer.toString(scanNumber));
                }else {

                    scnaoneNumber++;
                    tv_onenumber.setText("一维码录入数：" + Integer.toString(scnaoneNumber));
                }

            }

        }
        if (keyCode == 4) {


            return false;
        }
        return super.onKeyUp(keyCode, event);
    }
    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        if (keyCode!=0)
            finish();
        return super.onKeyLongPress(keyCode, event);

    }

    //移除
    private void Delebarcode() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("确定删除条码数据？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Deledate();

                    }
                })
                .setNegativeButton("取消", null)
                .create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    String filename="";
    protected void dialog3() {
        LayoutInflater lf = (LayoutInflater) MainNewActivity.this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewGroup vg = (ViewGroup) lf.inflate(R.layout.filenamet,
                null);
        final EditText etShow = (EditText) vg
                .findViewById(R.id.et_show);
        new AlertDialog.Builder(MainNewActivity.this)
                .setView(vg)
                .setTitle("标题")
                .setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                String str = etShow.getText()
                                        .toString();

                                if(!str.equals("")){
                                    WriteDate(str);

                                }else{

                                    Toast.makeText(MainNewActivity.this, "请输入保存文件名", Toast.LENGTH_LONG).show();
                                }

                            }
                        })
                .setNegativeButton("取消", null).show();
    }
    public void Extfind(){

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("是否退出程序？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        new Thread(){
                            @Override
                            public void run() {
                                super.run();
                                try {
                                    helper.Deleteform();
                                    finish();

                                }catch (Exception ex){

                                }
                            }
                        }.start();


                    }
                })
                .setNegativeButton("取消", null)
                .create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }
    public void Deledata(){

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("是否确定清空所有数据？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Clear();

                    }
                })
                .setNegativeButton("取消", null)
                .create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    public void Comfind(){

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("条码已扫过请确认")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                    }
                })

                .create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void Clear(){


        list_AdapterData.removeAll(list_AdapterData);

    }
    Boolean thisok=false;
    //删除选择数据
    private void Deledate(){
        /*
			 * 删除算法最复杂,拿到checkBox选择寄存map
			 */
        try {
            Map<Integer, Boolean> map = myAdapternew.getCheckMap();
            // 获取当前的数据数量
            int count = myAdapternew.getCount();
            // 进行遍历
            for (int i = 0; i < count; i++) {
                // 因为List的特性,删除了2个item,则3变成2,所以这里要进行这样的换算,才能拿到删除后真正的position
                int position = i - (count - myAdapternew.getCount());
                if (map.get(i) != null && map.get(i)) {
                    thisok=true;
                    MyTempModel bean = (MyTempModel) myAdapternew.getItem(position);
                    if (bean.isCkremobe()) {
                        bean.getTxtBarcode();
                        listbarcode=helper.querycodeall(bean.getTxtBarcode());
                        if(listbarcode.size()>0){//查找一给码是否存在
                            if(scnaoneNumber>0){
                                --scnaoneNumber;
                            }
                            helper.Delete(bean.getTxtBarcode());

                        }else {

                            if(scanNumber>0){
                                --scanNumber;
                            };
                        }
                        myAdapternew.getCheckMap().remove(i);
                        myAdapternew.remove(position);

                    } else {
                        map.put(position, false);
                    }
                }
            }

            if(chesofalse){
                chesofalse=false;
                scanNumber=0;
                scnaoneNumber=0;

                tv_number.setText("二维码录入数："+Integer.toString(scanNumber));
                tv_onenumber.setText("一维码录入数：" + Integer.toString(scnaoneNumber));

            }else{

                tv_onenumber.setText("一维码录入数：" + Integer.toString(scnaoneNumber));
                tv_number.setText("二维码录入数："+Integer.toString(scanNumber));
            }

            myAdapternew.notifyDataSetChanged();
            if(!thisok){
                Toast.makeText(MainNewActivity.this, "请选择单号", Toast.LENGTH_LONG).show();

            }
        }catch (Exception ex){
            Toast.makeText(MainNewActivity.this, "操作异常", Toast.LENGTH_LONG).show();
        }

    }


    private  boolean onechcode;
//一维持扫描的
    private int FindOneBarcode(String onecode){
        ContentValues values = new ContentValues();
        if(onecode.length()<30) {
            values.put("barcode", onecode);
            // 插入数据
            helper.insert(values);
            onechcode = false;
            refreshData(onecode);
            SoundVibratorManager.playSound(1, 1);//成功提示音
            return 1;
        }

        return -1;

    }




//    private  boolean onechcode;
    private int FindListBox(String barcode){
        try{
            ContentValues values = new ContentValues();
            String fu= spn_fwfs.getSelectedItem().toString();
            String mark=et_mark.getText().toString();
            String codeleght=edit_sex.getText().toString().trim();
            Boolean Infor=false;
            if (list_AdapterData.size() > 0) {
                String regEx2="[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
                Pattern pl   =   Pattern.compile(regEx2);
                Matcher m2   =   pl.matcher(barcode);
                String  barcodet= m2.replaceAll(",").trim();

                String[] codesized;
                boolean emcode=false;
                   if(et_mark.getText().toString().equals(" ")){
                       codesized=barcodet.split(" ");
                       emcode=true;

                   }else{
                       codesized= barcodet.split(",");

                   }
                   int chcode=0;//检查条码所有重复
                String code2="";
                String thbarcode="";
                boolean emok=false;
                for(int ii=0;ii<codesized.length;ii++){
                    code2=codesized[ii].toString();
                    for(int i2=0;i2<list_AdapterData.size();i2++){
                        String getcode = list_AdapterData.get(i2).getTxtBarcode();

                        if(getcode.equals(code2)){
                            chcode++;
                            emok=true;

                        }
                    }


                    if(emok==false) {
                            if (emcode) {

                                thbarcode = thbarcode + " " + code2;
                            } else {
                                emcode=false;
                                thbarcode = thbarcode + et_mark.getText().toString().trim() + code2;
                            }
                    }else {

                        emok=false;

                    }



                }


                if(chcode==codesized.length){//所有条码重复
                    SoundVibratorManager.playSound(2, 1);//失败提示音
                    chcode=0;
                    Comfind();
                    return -1;
                }else{
                     emcode=false;
                    if(codesized.length>1){
                        barcode= thbarcode;
                    }
                    chcode=0;
                    Infor=true;
                }

//                for (int i = 0; i < list_AdapterData.size(); i++) {
//
//                    String getcode = list_AdapterData.get(i).getTxtBarcode();
//                    if(getcode.equals(code2)){
//                        SoundVibratorManager.playSound(2, 1);//失败提示音
//
//                        Comfind();
//                        return -1;
//                    }else{
//                        Infor=true;
//                    }
//
//                }
                if(Infor){
                    String Marks=et_mark.getText().toString();
                    Infor=false;
                    if(Marks.equals(" ")) {
                        if (barcode.indexOf(" ") != -1) {
                            String[] codesize = barcode.split(" ");
                            String code = "";
                            for (int i = 0; i < codesize.length; i++) {
                                code = codesize[i].toString();
                                refreshData(code);
                            }
                            onechcode=true;
                            SoundVibratorManager.playSound(1, 1);//成功提示音
                            return 1;
                        }
                    }


                    if(!Marks.equals("")) {
                        if (barcode.indexOf(Marks) != -1) {
                            String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
                            Pattern p = Pattern.compile(regEx);
                            Matcher m = p.matcher(barcode);
                            barcode = m.replaceAll(",").trim();
                            String[] codesize = barcode.split(",");
                            String code = "";
                            for (int i = 0; i < codesize.length; i++) {
                                code = codesize[i].toString();
                                refreshData(code);
                            }
                            onechcode=true;
                            SoundVibratorManager.playSound(1, 1);//成功提示音
                            return 1;

                        } else {

                            if(barcode.length()<30){
                                values.put("barcode", barcode);
                                // 插入数据
                                helper.insert(values);
                                onechcode=false;
                                refreshData(barcode);
                                SoundVibratorManager.playSound(1, 1);//成功提示音
                                return 1;

                            }else{

                                if(!codeleght.equals("")) {
                                    int codele = Integer.parseInt(codeleght);//输入的取字符长度
                                    barcode=barcode.replace("\r","");
                                    int getnumbe = barcode.length() / codele;//取的条码数
                                    if (codele > 10) {
                                        boolean chlhcode=false;
                                        int begin = 0;
                                        int end = 0;
                                        String getbarcode = "";
                                        for (int i = 0; i <= getnumbe-1; i++) {
                                            begin++;
                                            for(int i2=0;i2<list_AdapterData.size();i2++){
                                                getbarcode = barcode.substring(i*codele, codele*begin);
                                                String getcode = list_AdapterData.get(i2).getTxtBarcode();
                                                if(getcode.equals(getbarcode)){
                                                    end++;
                                                    chlhcode=true;
                                                }
                                            }
                                            if(!chlhcode){//没重复
                                                chlhcode=false;
                                                refreshData(getbarcode);
                                            }

                                        }

                                        if(end==getnumbe){

                                            Comfind();
                                            SoundVibratorManager.playSound(2, 1);//失败提示音
                                            return -1;
                                        }
                                        onechcode=true;
                                        SoundVibratorManager.playSound(1, 1);//成功提示音
                                        begin=0;
                                        return 1;

                                    } else {
                                        SoundVibratorManager.playSound(2, 1);//失败提示音
                                        Toast.makeText(MainNewActivity.this, "请输入正确的分隔符", Toast.LENGTH_LONG).show();
                                        return -1;


                                    }
                                }else{
                                    SoundVibratorManager.playSound(2, 1);//失败提示音
                                    Toast.makeText(MainNewActivity.this, "请输入条码长度", Toast.LENGTH_LONG).show();
                                    return -1;
                                }


                            }

                        }
                    }

                }

            }else{

                String Marks=et_mark.getText().toString();
                if(Marks.equals(" ")) {
                    if (barcode.indexOf(" ") != -1) {
                        String[] codesize = barcode.split(" ");
                        String code = "";
                        for (int i = 0; i < codesize.length; i++) {
                            code = codesize[i].toString();
                            refreshData(code);
                        }
                        onechcode=true;
                        SoundVibratorManager.playSound(1, 1);//成功提示音
                        return 1;
                    }
                }
                if(!Marks.equals("")) {
                    if (barcode.indexOf(Marks) != -1) {
                        String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
                        Pattern p = Pattern.compile(regEx);
                        Matcher m = p.matcher(barcode);
                        barcode = m.replaceAll(",").trim();
                        String[] codesize = barcode.split(",");

                        String code = "";
                        for (int i = 0; i < codesize.length; i++) {
                            code = codesize[i].toString();
                            refreshData(code);
                        }
                        onechcode=true;
                        SoundVibratorManager.playSound(1, 1);//成功提示音
                        return 1;

                    } else {


                        if(barcode.length()<30){
                            values.put("barcode", barcode);
                            // 插入数据
                            helper.insert(values);
                            refreshData(barcode);
                            onechcode=false;
                            SoundVibratorManager.playSound(1, 1);//成功提示音
                            return 1;

                        }else{

                            if(!codeleght.equals("")) {
                                int codele = Integer.parseInt(codeleght);//输入的取字符长度
                                barcode=barcode.replace("\r","");
                                int getnumbe = barcode.length() / codele;//取的条码数
                                if (codele > 10) {
                                    int begin = 0;
                                    int end = 0;
                                    String getbarcode = "";
                                    for (int i = 0; i <= getnumbe-1; i++) {
                                        begin++;
                                        getbarcode = barcode.substring(i*codele, codele*begin);
                                        refreshData(getbarcode);
                                    }
                                    onechcode=true;
                                    SoundVibratorManager.playSound(1, 1);//成功提示音
                                    begin=0;
                                    return 1;

                                } else {
                                    SoundVibratorManager.playSound(2, 1);//失败提示音
                                    Toast.makeText(MainNewActivity.this, "请输入正确的分隔符", Toast.LENGTH_LONG).show();
                                    return -1;


                                }
                            }else{
                                SoundVibratorManager.playSound(2, 1);//失败提示音
                                Toast.makeText(MainNewActivity.this, "请输入条码长度", Toast.LENGTH_LONG).show();
                                return -1;
                            }
//                            SoundVibratorManager.playSound(2, 1);//失败提示音
//                            Toast.makeText(MainNewActivity.this, "请输入正确的分隔符", Toast.LENGTH_LONG).show();
//                            return -1;
                        }

                    }


                }
            }


        }catch (Exception ex){

            ex.printStackTrace();

        }
        return -1;


    }

    private void WriteDate(String filename) {

        for(int i=0;i<list_AdapterData.size();i++){
            String barcode=list_AdapterData.get(i).getTxtBarcode();
            writeLog(barcode,filename);

        }
        if(susseclu){
            Toast.makeText(MainNewActivity.this, "保存成功",
                    Toast.LENGTH_LONG).show();
            susseclu=false;
        }
    }
    /**
     * 输出到SD卡
     *
     * @param data 输出的信息
     */
    private void writeLog(String data,String filename) {
        // 没有SD卡则不写入日志
        if (sdExist) {
            try {
                if (getAvailableMemory() < 3) {
                    if(!filename.equals(""))
                    {
                        writeFile(LOGFILENAME + getDateToday() + ".txt", "SD卡空间不足");
                        return;
                    }else{
                        Toast.makeText(MainNewActivity.this, "请输入文件名字保存",
                                Toast.LENGTH_LONG).show();

                    }

                    return;
                }

                if(!filename.equals(""))
                {
                    writeFile(LOGFILENAME +filename+ ".txt", data);


                }else{
                    Toast.makeText(MainNewActivity.this, "请输入文件名字保存",
                            Toast.LENGTH_LONG).show();
                }


            } catch (Exception e) {
                e.printStackTrace();
                Log.v("Log", "导出不正常");
            }
        } else {
            Log.v("LoggUtils", "SD卡不存在");
        }

    }

    /**
     * 获取今天的日期字符
     *
     * @return
     */
    private static String getDateToday() {
//        SimpleDateFormat simDateFormat = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss");
        SimpleDateFormat simDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simDateFormat.format(new Date());
    }

    private static long getAvailableMemory() {
        try {
            File sdFile = Environment.getExternalStorageDirectory();
            StatFs statfs = new StatFs(sdFile.getPath());
            long size = statfs.getBlockSize();// 获得SD卡大小
            long available = statfs.getAvailableBlocks();// 可用大小
            return available * size / 1024 / 1024;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    boolean  susseclu=false;
    /**
     * 真正的写入SD卡
     *
     * @param filepath 文件路径
     * @param data     写入的信息
     * @throws IOException 抛出IO异常
     */
    private void writeFile(String filepath, String data)
            throws IOException {

        FileOutputStream fOut = null;
        OutputStreamWriter osw = null;
        File file = new File(filepath);
        try {
            if (!file.isFile()) { // 文件目录不存在则创建
                String pa = filepath.substring(0, filepath.lastIndexOf("/"));
                new File(pa).mkdirs();
                file.createNewFile();

            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        try { // 输出到SD卡文件中
            fOut = new FileOutputStream(file, true);
            osw = new OutputStreamWriter(fOut, "UTF-8");
            osw.write(data + "\r\n");
            osw.flush();
            susseclu=true;
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(MainNewActivity.this, "数据导出失败",
                    Toast.LENGTH_LONG).show();
        } finally {
            try {
                if (osw != null)
                    osw.close();
                if (fOut != null)
                    fOut.close();


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 获取数据
     */
    private void refreshData(String barcode) {
        try {

            if (list_AdapterData == null) {
                list_AdapterData = new ArrayList<MyTempModel>();
            }
            if (!TextUtils.isEmpty(barcode)) {



                list_AdapterData.add(new MyTempModel(barcode,true));
                myAdapternew = new ListCodeAdapter(this,list_AdapterData);

                    mListView.setAdapter(myAdapternew);

            }

        } catch (Exception ex) {
//            LogHelper.e(SetCarDutyFragment.class, ex, "refreshData");
        }

    }




}

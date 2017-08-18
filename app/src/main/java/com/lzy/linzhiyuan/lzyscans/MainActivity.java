package com.lzy.linzhiyuan.lzyscans;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.os.StatFs;
import android.provider.SyncStateContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.linzhiyuan.lzyscans.MyTempModel;
import com.lzy.linzhiyuan.lzyscans.R;
import com.lzy.linzhiyuan.lzyscans.adapters.SpinnerAdapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Stack;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.widget.Toast.LENGTH_SHORT;

public class MainActivity extends Activity implements View.OnClickListener {

    private String lid;
    private Button btnBack;
    private Button btnSave,btndele;
    private EditText editbarcode,editmark,editfile;
    private TextView  tv_number;
    private CheckBox ch_box;
    Spinner spn_jjd,spn_sjd,spn_fwfs;
    ListView mListView;
    private static Stack<Activity> activityStack;
    Receiver receiver;
    MyAdapter myAdapter = null;
    List<MyTempModel> list_AdapterData = new ArrayList<MyTempModel>();
    private boolean ch_boxno=false;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init(){
        btnBack=(Button)findViewById(R.id.btnBack);
        btnSave=(Button)findViewById(R.id.btnSave);
        btndele=(Button)findViewById(R.id.btndele);
        editbarcode=(EditText)findViewById(R.id.editbarcode);
        editmark=(EditText)findViewById(R.id.editmark);
        editfile=(EditText)findViewById(R.id.editfile);
//        tv_number=(TextView)findViewById(R.id.tv_number);
        mListView=(ListView)findViewById(R.id.list1);
        spn_fwfs = (Spinner)findViewById(R.id.spn_fwf);
        ch_box=(CheckBox)findViewById(R.id.ch_box) ;
        SpinnerAdapter<String> mPackAdapter_fwfs = new SpinnerAdapter<String>(this, R.layout.spinner_item, Constants.SERVICES);
        spn_fwfs.setAdapter(mPackAdapter_fwfs);
        btnBack.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        btndele.setOnClickListener(this);
        /************************列表初始化start*********************/
        mListView.setChoiceMode(mListView.CHOICE_MODE_SINGLE);
//        headerScroll.setOnTouchScroll(PanDianScanActivity.this);
        myAdapter = new MyAdapter(MainActivity.this);
        mListView.setAdapter(myAdapter);

//

        //给CheckBox设置事件监听
        ch_box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                // TODO Auto-generated method stub
                if(isChecked){
                    ch_boxno=true;
                }else{
                    ch_boxno=false;

                }
            }
        });
    }



    public void Savafile(){

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("是否确定保存文件名为"+editfile.getText().toString())
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {



                        WriteDate();

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



    public void Sevefile(){

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("数据导出成功在TEXT文件")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        new Thread(){
                            @Override
                            public void run() {
                                super.run();
                                try {


                                }catch (Exception ex){

                                }
                            }
                        }.start();


                    }
                })
                .create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }


    protected void dialog3() {
        LayoutInflater lf = (LayoutInflater) MainActivity.this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewGroup vg = (ViewGroup) lf.inflate(R.layout.filenamet,
                null);
        final EditText etShow = (EditText) vg
                .findViewById(R.id.et_show);
        new AlertDialog.Builder(MainActivity.this)
                .setView(vg)
                .setTitle("标题")
                .setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                String str = etShow.getText()
                                        .toString();

                            }
                        })
                .setNegativeButton("取消", null).show();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP){
            String scanText= editbarcode.getText().toString();
            FindListBox(scanText);

        }
        if (keyCode == 4) {


            return false;
        }
        //super.onKeyUp(keyCode, event);
        return super.onKeyUp(keyCode, event);
    }

    private void Clear(){

//        list_AdapterData.clear();
        list_AdapterData.removeAll(list_AdapterData);
        notifyDataSetChanged();
        mListView.setAdapter(myAdapter);

    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        if (keyCode!=0)
            finish();
        return super.onKeyLongPress(keyCode, event);

    }
    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     * @param activity
     */
    public void finishActivity(Activity activity) {
         activity = activityStack.lastElement();
        finishActivity(activity);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBack:

                dialog3();
//                Extfind();
                break;
            case R.id.btnSave:
                if(list_AdapterData.size()>0){
                    try {


                        String filedate=editfile.getText().toString();
                        if(!filedate.equals("")){
                            Savafile();
                        }else{
                            Toast.makeText(MainActivity.this, "请输入文件名",
                                    Toast.LENGTH_LONG).show();
                        }


                    }catch (Exception ex){
                        ex.printStackTrace();
                    }

                }else{
                    Toast.makeText(MainActivity.this, "数据不存在",
                            Toast.LENGTH_LONG).show();

                }


                break;
            case R.id.btndele:

                Deledata();

                break;


        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();

    }

    @Override
    protected void onStart() {
        super.onStart();
        receiver = new Receiver();
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

            FindListBox(barcode);


        }};




    private void SaveScan(String barcode){


        Pattern p = Pattern.compile("\\s*|\t|\r|\n");
        Matcher m = p.matcher(barcode);
        barcode = m.replaceAll("");


        String[] std = barcode.split(",");
        String slo="";
        for(int i=0;i<std.length;i++){
            slo=std[i].toString();

        }


    }


    private void WriteDate() {


        for(int i=0;i<list_AdapterData.size();i++){
            String barcode=list_AdapterData.get(i).getTxtBarcode();
            writeLog(barcode);

        }

        if(susseclu){
            Sevefile();
            susseclu=false;
        }
    }
    /**
     * 输出到SD卡
     *
     * @param data 输出的信息
     */
    private void writeLog(String data) {
        // 没有SD卡则不写入日志
        if (sdExist) {
            try {
                String filename=editfile.getText().toString();
                if (getAvailableMemory() < 3) {
                    if(!filename.equals(""))
                    {
                        writeFile(LOGFILENAME + getDateToday() + ".txt", "SD卡空间不足");
                        return;
                    }else{
                        Toast.makeText(MainActivity.this, "请输入文件名字保存",
                                Toast.LENGTH_LONG).show();

                    }


                    return;
                }


                if(!filename.equals(""))
                {
                    writeFile(LOGFILENAME +filename+ ".txt", data);


                }else{
                    Toast.makeText(MainActivity.this, "请输入文件名字保存",
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
//            Toast.makeText(MainActivity.this, "数据导出成功在TEXT文件",
//                        Toast.LENGTH_SHORT).show();
//            showMessage(MainActivity.this.getApplicationContext(),"数据导出成功在TEXT文件");
//            Sevefile();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "数据导出失败",
                    Toast.LENGTH_LONG).show();
        } finally {
            try {
                if (osw != null)
                    osw.close();
                if (fOut != null)
                    fOut.close();
//                Toast.makeText(MainActivity.this, "数据导出成功在TEXT文件",
//                        Toast.LENGTH_LONG).show();


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    AlertDialog dialogShowMessage;
    public void showMessage(Context context ,String msg){
        try {
//            _callBack = null;//因为是单例模式，所以有可能这个回调还是上次的
//            closeMessage();
            dialogShowMessage = new AlertDialog.Builder(context)
                    .setCancelable(true)//设置为false，按返回键不能退出。默认为true可以退出
                    .setPositiveButton(context.getString(R.string.utils_dialog_ok), (DialogInterface.OnClickListener) this)
                    .setMessage(msg)
                    .setTitle(context.getString(R.string.utils_dialog_title))
                    .create();

            dialogShowMessage.setCanceledOnTouchOutside(false);
            dialogShowMessage.show();
            //获取按钮对象
            Button PositiveButton=dialogShowMessage.getButton(AlertDialog.BUTTON_POSITIVE);
            PositiveButton.setFocusable(true);
            PositiveButton.setFocusableInTouchMode(true);
            PositiveButton.requestFocus();

        }catch (Exception e){
            e.printStackTrace();
        }

    }
    private int FindListBox(String barcode){
        try{
           String fu= spn_fwfs.getSelectedItem().toString();


            Boolean Infor=false;
            if (list_AdapterData.size() > 0) {


                String regEx2="[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
                Pattern   pl   =   Pattern.compile(regEx2);
                Matcher   m2   =   pl.matcher(barcode);
               String  barcodet= m2.replaceAll(",").trim();

                String[] codesized;

                if(ch_boxno){
                    codesized=barcodet.split(" ");
                }else{
                    codesized= barcodet.split(",");
                }


                String code2="";
                for(int ii=0;ii<codesized.length;ii++){
                    code2=codesized[ii].toString();


                }

        
                for (int i = 0; i < list_AdapterData.size(); i++) {

                    String getcode = list_AdapterData.get(i).getTxtBarcode();
                    if(getcode.equals(code2)){
                        Toast.makeText(MainActivity.this, "条码已扫描", Toast.LENGTH_LONG).show();
                        return 1;

                    }else{
                        Infor=true;
                    }

                }
                if(Infor){

                    Infor=false;
                    if(ch_boxno) {
                        if (barcode.indexOf(" ") != -1) {
                            String[] codesize = barcode.split(" ");
                            String code = "";
                            for (int i = 0; i < codesize.length; i++) {
                                code = codesize[i].toString();

                                MyTempModel temp = new MyTempModel();
                                temp.setTxtBarcode(code);
                                list_AdapterData.add(temp);
                            }
                            notifyDataSetChanged();
                            return 1;
                        }
                    }

                    String Marks=editmark.getText().toString();
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

                                MyTempModel temp = new MyTempModel();
                                temp.setTxtBarcode(code);
                                list_AdapterData.add(temp);
                            }
                            notifyDataSetChanged();
                            return 1;

                        } else {


                            Toast.makeText(MainActivity.this, "请输入正确的分隔符", Toast.LENGTH_LONG).show();


                            return 1;

                        }
                    }else{
                        Toast.makeText(MainActivity.this, "请输入分隔符符:", Toast.LENGTH_LONG).show();


                        return 1;
                    }

                }


            }else{

                String Marks=editmark.getText().toString();

                if(ch_boxno) {
                    if (barcode.indexOf(" ") != -1) {
                        String[] codesize = barcode.split(" ");
                        String code = "";
                        for (int i = 0; i < codesize.length; i++) {
                            code = codesize[i].toString();

                            MyTempModel temp = new MyTempModel();
                            temp.setTxtBarcode(code);
                            list_AdapterData.add(temp);
                        }
                        notifyDataSetChanged();
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

                            MyTempModel temp = new MyTempModel();
                            temp.setTxtBarcode(code);
                            list_AdapterData.add(temp);
                        }
                        notifyDataSetChanged();
                        return 1;

                    } else {


                        Toast.makeText(MainActivity.this, "请输入正确的分隔符", Toast.LENGTH_LONG).show();
                        return 1;
                    }

//                    if(barcode.indexOf(" ")!=-1){
//                    String[] codesize= barcode.split(" ");
//                    String code="";
//                    for(int i=0;i<codesize.length;i++){
//                        code=codesize[i].toString();
//
//                        MyTempModel temp = new MyTempModel();
//                        temp.setTxtBarcode(code);
//                        list_AdapterData.add(temp);
//                    }
//                    notifyDataSetChanged();
//                    return 1;
//                }
                }else{
                    Toast.makeText(MainActivity.this, "请输入分隔符符", Toast.LENGTH_LONG).show();
                    return 1;
                }
//

//                MyTempModel temp = new MyTempModel();
//                temp.setTxtBarcode(barcode);
//                list_AdapterData.add(temp);
//                mListView.setAdapter(myAdapter);
//
//                notifyDataSetChanged();
//
//            return 1;





            }


        }catch (Exception ex){

            ex.printStackTrace();

        }
        return -1;


    }

    /**
     * 更新列表
     */
    private void notifyDataSetChanged() {

        MyAdapter myAdapter1 = (MyAdapter) mListView.getAdapter();
        myAdapter1.notifyDataSetChanged();

    }

    class MyAdapter extends BaseAdapter {
        private Context context;

        public MyAdapter(Context context) {
            this.context = context;
        }

        /**
         * 定义所包含的控件
         */
        class ViewHolder {
            TextView txtBarcode;
            TextView txtCode;
            TextView txtNumber;
            TextView txtuser;
            TextView time;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub

            return list_AdapterData.size();
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub

            return list_AdapterData.get(arg0);

        }

        @Override
        public long getItemId(int i) {
            return i;
        }


        @Override
        public View getView(final int position, View convertView, ViewGroup viewGroup) {
            try {
                ViewHolder holder = null;
                if (convertView == null) {
                    convertView = LayoutInflater.from(context).inflate(R.layout.queritem, null);
                    holder = new ViewHolder();
                    holder.txtBarcode = (TextView) convertView.findViewById(R.id.list_code);

                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }

                MyTempModel info = list_AdapterData.get(position);
                holder.txtBarcode.setText(info.getTxtBarcode());

            } catch (Exception ex) {
                ex.printStackTrace();
            }

            return convertView;
        }


    }






}

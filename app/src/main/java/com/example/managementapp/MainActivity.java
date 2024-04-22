package com.example.managementapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.BlendMode;
import android.graphics.BlendModeColorFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    ArrayList<Map<String, String>> planList = new ArrayList<>();
    private DatabaseHelper _helper;
    private DatabaseHelper2 _helper2;
    private final String[] FROM= {"id","plan"};
    private final int[] TO = {R.id.lv_layout_id,R.id.lv_layout_plan};
    static public String desplayingClass = "すべて";
    private HorizontalScrollView hsvClass;
    private ListView lvPlan;
    private OperateDataByDatabase operator;
    private ClassEditDialog dialogFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



//        Button btJob = findViewById(R.id.btJob);
//        Button btSchool = findViewById(R.id.btSchool);
//        Button btPlay = findViewById(R.id.btPlay);
        Button btAdd = findViewById(R.id.btAdd);
        Button btAll = findViewById(R.id.btAll);
        Button btAddClass = findViewById(R.id.btAddClass);
        lvPlan = findViewById(R.id.lvPlan);
        hsvClass = findViewById(R.id.hsvClass);


//        予定管理用データベースヘルパーの生成
        _helper = new DatabaseHelper(MainActivity.this);
//        分類管理用データベースヘルパーの生成
        _helper2 = new DatabaseHelper2(MainActivity.this);
//        オペレーターの作成
        operator = new OperateDataByDatabase(_helper,_helper2);
//        リストビューにデータベース内のすべてのデータを登録
        SetLvAdapter(lvPlan,"すべて");

//        HorizontalScrollViewにデータベース内のすべてのデータを登録
        SetAdapter(hsvClass);

//        リストビューやボタンにリスナを設定
        lvPlan.setOnItemClickListener(new LvAction());
        ClickAction listener = new ClickAction();
        btAdd.setOnClickListener(listener);
        btAll.setOnClickListener(listener);
        btAddClass.setOnClickListener(listener);

        dialogFragment = new ClassEditDialog(hsvClass,_helper, _helper2);
//        operator.deleteAllClassData();

    }

    @Override
    protected void onDestroy(){
        _helper.close();
        _helper2.close();
        super.onDestroy();
    }


    private class ClickAction implements View.OnClickListener{

        private int _id;

        @Override
        public void onClick(View view){
            EditText input = findViewById(R.id.etInput);
            int buttonId = view.getId();

            switch (buttonId){
                case R.id.btAll:
                    SetLvAdapter(lvPlan,"すべて");
                    desplayingClass = "すべて";
                    Toast.makeText(MainActivity.this, "すべての予定", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.btAdd:
//                    追加データの保存先となるidを取得
                    Map<String,String> plan;
//                    一旦planListにすべてのデータを格納
                    planList = operator.GetAllData("すべて");
//                    planListの要素数が０の場合とそれ以外とで場合分け
                    if(planList.size() == 0) {
                        _id = 0;
                    } else {
                        plan = planList.get(planList.size() - 1);
//                    最後尾の要素のid(つまり最大のid)を取得し、それに1だけ加えたものを追加要素のidとする。これによりデータベース上での主キーの衝突を避けられる
                        _id = Integer.parseInt(plan.get("id"));
                        _id++;
                    }
//                    Toast.makeText(MainActivity.this, ""+_id, Toast.LENGTH_SHORT).show();

//                    追加データのクラスを取得
                    Spinner spClassification = findViewById(R.id.spClassification);
                    String selectedClass = spClassification.getSelectedItem().toString();

//                    表示の更新
                    String content = input.getText().toString();
//                    Toast.makeText(MainActivity.this, content, Toast.LENGTH_SHORT).show();
                    input.setText("");

//                    入力データをデータベースに追加
                    operator.AddData(_id,content,selectedClass);

//                    表示の更新
                    SetLvAdapter(lvPlan,desplayingClass);
                    Toast.makeText(getApplicationContext(),"追加しました",Toast.LENGTH_SHORT).show();
                    break;

                case R.id.btAddClass:
                    String newClass = input.getText().toString();
                    operator.AddData(newClass);
                    AddButtonToScrollView(hsvClass,newClass);
                    input.setText("");

            }
        }
    }




    private class LvAction implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id){
//            このビューがもっているMap型のデータを取得
            Map<String, String> item = (Map<String, String>) parent.getItemAtPosition(position);
//            IDを取得
            int ID = Integer.parseInt(item.get("id"));
//            ダイアログにIDとリスト、リストビューの情報を渡して起動
            PlanEditDialog dialogFragment = new PlanEditDialog(ID, planList, lvPlan, _helper);
            dialogFragment.show(getSupportFragmentManager(),"PlanEditDialog");
        }
    }

    public void SetLvAdapter(ListView lvPlan,String classification){
//        指定した種類のデータをデータベースから読みだし、planListに格納
        planList = operator.GetAllData(classification);
//        取得したデータをリストビューにセット
        SimpleAdapter adapter = new SimpleAdapter(MainActivity.this,planList,R.layout.listview_layout,FROM,TO);
        lvPlan.setAdapter(adapter);
    }

    public void SetAdapter(HorizontalScrollView hsvClass){
        ArrayList<String> classList = new ArrayList<>();
        classList = operator.GetAllDataInClass();
        for(String c:classList){
            AddButtonToScrollView(hsvClass,c);
        }
    }

    private void AddButtonToScrollView(HorizontalScrollView hsvClass, String classification){
        LinearLayout llScroll = findViewById(R.id.llScroll);
        Button btAll = findViewById(R.id.btAll);
        float scale = getResources().getDisplayMetrics().density;
        int margin = (int)(4*scale);
        ColorStateList csList = btAll.getBackgroundTintList();

        //ボタンの背景色や形、表示するテキストの設定
        Button bt = new Button(MainActivity.this);
        bt.setText(classification);
        bt.setBackgroundTintList(csList);
        bt.setTextColor(Color.WHITE);

        //LinerLayout上でのボタンの位置を設定
        LinearLayout.LayoutParams btParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        btParams.setMarginStart(margin);
        btParams.weight = 1;
        bt.setLayoutParams(btParams);

//        LinearLayoutにボタンを追加
        llScroll.addView(bt);
//        追加したボタンにクリックリスナーを設定
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetLvAdapter(lvPlan,classification);
                desplayingClass = classification;
                Toast.makeText(MainActivity.this, classification+"の予定", Toast.LENGTH_SHORT).show();
            }
        });

////        追加したボタンにロングクリックリスナーを設定
//        bt.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View view) {
//                // ダイアログにtxtを渡して起動
//                Button clickedBt = (Button) view;
//                String txt = clickedBt.getText().toString();
//                dialogFragment.setTxt(txt);
//                dialogFragment.show(getSupportFragmentManager(),"ClassEditDialog");
//                return true;
//            }
//        });

//        Spinnerに新たな分類を追加
        ArrayList<String> spEntries = operator.GetAllDataInClass();
        Spinner sp = findViewById(R.id.spClassification);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1,spEntries);
        sp.setAdapter(adapter);

    }
}



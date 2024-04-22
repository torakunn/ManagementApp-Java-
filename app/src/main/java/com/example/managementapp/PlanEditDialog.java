package com.example.managementapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.Map;

public class PlanEditDialog extends DialogFragment {
    private int _id;
    private ArrayList<Map<String, String>> planList;
    private ListView lvPlan;
    private DatabaseHelper helper;
    private OperateDataByDatabase operator;
    private String contents,classification;
    private final String[] FROM= {"id","plan"};
    private final int[] TO = {R.id.lv_layout_id,R.id.lv_layout_plan};

    public PlanEditDialog(int _id, ArrayList<Map<String, String>> planList, ListView lvPlan, DatabaseHelper helper){
        this._id = _id;
        this.planList = planList;
        this.lvPlan = lvPlan;
        this.helper = helper;
        this.operator = new OperateDataByDatabase(helper);
        this.contents = operator.GetDataById(_id,"memo");
        this.classification = operator.GetDataById(_id,"class");
    }
    
    @Override
    @NonNull
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.dialog_title);
        builder.setMessage(this.contents);
        builder.setNeutralButton(R.string.dialog_bt_cancel,new DlbtAction());
        builder.setNegativeButton(R.string.dialog_bt_edit,new DlbtAction());
        builder.setPositiveButton(R.string.dialog_bt_delete,new DlbtAction());
        AlertDialog dialog = builder.create();
        return dialog;
    }

    private class DlbtAction implements DialogInterface.OnClickListener{
        @Override
        public void onClick(DialogInterface dialog, int which){
            switch (which){
                case AlertDialog.BUTTON_NEUTRAL:    //キャンセル
                    break;
                case DialogInterface.BUTTON_NEGATIVE:   //編集
                    //一旦データベースから選択されたデータを削除
                    operator.DeleteDataById(_id);
//                    初期値を設定
                    EditText etInput = getActivity().findViewById(R.id.etInput);
                    etInput.setText(contents);
                    Spinner spClassification = getActivity().findViewById(R.id.spClassification);
                    spClassification.setSelection((((ArrayAdapter)spClassification.getAdapter()).getPosition(classification)));
                    break;
                case DialogInterface.BUTTON_POSITIVE:   //削除
//                    データベースから内容を削除して、削除後のデータベースから取得したデータをリストビューに表示
                    operator.DeleteDataById(_id);
                    planList = operator.GetAllData(MainActivity.desplayingClass);
//                   リストビューにアダプタオブジェクトを再設定
                    lvPlan.setAdapter(new SimpleAdapter(getActivity(),planList,R.layout.listview_layout,FROM,TO));
                    Toast.makeText(getActivity(), "予定：「"+contents+"」を削除しました", Toast.LENGTH_SHORT).show();
                    break;

            }
        }
    }

}

package com.example.managementapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.Map;

public class ClassEditDialog extends DialogFragment {
    private DatabaseHelper helper;
    private DatabaseHelper2 helper2;
    private OperateDataByDatabase operator;
    private String contents, classification, txt;
    private HorizontalScrollView hsvClass;
    public ClassEditDialog(HorizontalScrollView hsvClass, DatabaseHelper helper, DatabaseHelper2 helper2) {
        this.hsvClass = hsvClass;
        this.helper = helper;
        this.helper2 = helper2;
        this.operator = new OperateDataByDatabase(helper,helper2);
    }

//    public void setListener(Listener listener){ mlistener = listener;}
    public void setTxt(String txt) { this.txt = txt; }
    @Override
    @NonNull
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.dialog_title);
        builder.setMessage(this.contents);
        builder.setNeutralButton(R.string.dialog_bt_cancel, new DlbtAction());
//        builder.setNegativeButton(R.string.dialog_bt_edit,new DlbtAction());
        builder.setPositiveButton(R.string.dialog_bt_delete, new DlbtAction());
        AlertDialog dialog = builder.create();
        return dialog;
    }

    private class DlbtAction implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case AlertDialog.BUTTON_NEUTRAL:    //キャンセル
                    break;
                case DialogInterface.BUTTON_POSITIVE:   //削除
//                    データベースから内容を削除して、削除後のデータベースからデータを取得
                    operator.DeleteDataInClass(txt);
                    ArrayList<String> classList = operator.GetAllDataInClass();

                    //planテーブルからも不要なデータを削除
                    SQLiteDatabase db = helper.getWritableDatabase();
                    String sqlDelete = "DELETE FROM plans WHERE class = ?";
                    SQLiteStatement stmt = db.compileStatement(sqlDelete);
                    stmt.bindString(1,txt);
                    stmt.executeUpdateDelete();
                    break;

            }
        }
    }



}
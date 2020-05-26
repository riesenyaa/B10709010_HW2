package com.example.android.waitlist;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.android.waitlist.data.WaitlistContract;


public class Add extends AppCompatActivity {
    private static final String LOG_TAG = null;
    private EditText et_name,et_number;
    private Button btn_ok, btn_cancel;
    String guestName;
    int peopleNum;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add);
        et_name = (EditText) findViewById(R.id.edt_name);
        et_number = (EditText) findViewById(R.id.edt_people_number);
        btn_ok = (Button) findViewById(R.id.btn_ok);
        btn_cancel = (Button) findViewById(R.id.btn_cancel);

        //cancel
        btn_ok.setOnClickListener(new Button.OnClickListener() {



            @Override
            public void onClick(View v) {
                guestName = et_name.getText().toString();
                peopleNum = Integer.parseInt(et_number.getText().toString());
                Intent intent = new Intent(Add.this,MainActivity.class);
                intent.putExtra("name",guestName);
                intent.putExtra("size",peopleNum);
//                intent.putExtra("sizeAndName",bag);
                startActivity(intent);
            }
        });

        //cancel
        btn_cancel.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_name.setText("");
                et_number.setText("");
            }
        });
    }
}

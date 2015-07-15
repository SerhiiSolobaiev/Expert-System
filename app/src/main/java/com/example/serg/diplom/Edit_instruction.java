package com.example.serg.diplom;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class Edit_instruction extends EditBD{
    private DBHelper mDataBaseHelper;
    private SQLiteDatabase db;
    Spinner spinner;
    EditText n;
    EditText t;
    EditText c;
    EditText r;
    Button btn_add;
    Button btn_remove;
    ArrayAdapter<String> dataAdapter;
    private int pos_item = 0;
    private String name_before_edit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_instruction);

        edit_instruction();
        btn_add = (Button) findViewById(R.id.button_add);
        btn_remove = (Button) findViewById(R.id.button_remove);
        btn_add.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                update_instr();
            }
        });
        btn_remove.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                remove_instr();
            }
        });

    }
    private void edit_instruction(){
        try {
            mDataBaseHelper = new DBHelper(this);
            db = mDataBaseHelper.getReadableDatabase();
            Cursor cursor = db.query(mDataBaseHelper.table_name_instruction,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            );

            List<String> list = new ArrayList<>();
            spinner = (Spinner) findViewById(R.id.spinner);
            list.add("Додати нову інструкцію");

            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor
                        .getColumnIndex(mDataBaseHelper.name_instruction));
                list.add(name);
            }
            cursor.close();
            dataAdapter = new ArrayAdapter<String>(this,
                    R.layout.spinner_item, list);

            dataAdapter
                    .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            dataAdapter.notifyDataSetChanged();
            spinner.setAdapter(dataAdapter);

        }
        catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                name_before_edit = parentView.getItemAtPosition(position).toString();
                query_from_bd(position,name_before_edit);
                pos_item = position;

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
    }
    private void query_from_bd(int pos,String name_selected){
        n = (EditText)findViewById(R.id.editText_name);
        t = (EditText)findViewById(R.id.editText_tool_list);
        c = (EditText)findViewById(R.id.editText_complexity);
        r = (EditText)findViewById(R.id.editText2_remedy);
        if (pos == 0){
            n.setText("");
            t.setText("");
            c.setText("");
            r.setText("");
            btn_add.setText("Додати");
        }
        else{
            btn_add.setText("Оновити");
            mDataBaseHelper = new DBHelper(this);
            db = mDataBaseHelper.getReadableDatabase();
            String selection = "NAME = '" + name_selected + "'";
            Cursor cursor = db.query(mDataBaseHelper.table_name_instruction,
                    null,
                    selection,
                    null,
                    null,
                    null,
                    null
            );
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex(mDataBaseHelper.name_instruction));
                String tool_list = cursor.getString(cursor.getColumnIndex(mDataBaseHelper.tool_list));
                String complexity = cursor.getString(cursor.getColumnIndex(mDataBaseHelper.complexity));
                String remedy_procedure = cursor.getString(cursor.getColumnIndex(mDataBaseHelper.remedy_procedure));
                n.setText(name);
                t.setText(tool_list);
                c.setText(complexity);
                r.setText(remedy_procedure);
            }
            cursor.close();
        }

    }
    private void update_instr(){
        mDataBaseHelper = new DBHelper(this);
        db = mDataBaseHelper.getWritableDatabase();
        if (pos_item == 0){
            if(!n.getText().toString().equals("") && !t.getText().toString().equals("")&& !c.getText().toString().equals("")
                    && !r.getText().toString().equals("")){

                ContentValues cv = new ContentValues();
                cv.put(mDataBaseHelper.name_instruction,n.getText().toString());
                cv.put(mDataBaseHelper.tool_list,t.getText().toString());
                cv.put(mDataBaseHelper.complexity,c.getText().toString());
                cv.put(mDataBaseHelper.remedy_procedure, r.getText().toString());
                db.insert(mDataBaseHelper.table_name_instruction, null, cv);
                n.setText("");
                t.setText("");
                c.setText("");
                r.setText("");
                Toast.makeText(getApplicationContext(), "Інструкція " +n.getText()+" додана!", Toast.LENGTH_SHORT).show();
                edit_instruction();

                Intent i_i = new Intent();
                setResult(RESULT_OK,i_i);
            }
            else Toast.makeText(getApplicationContext(), "Заповніть, будь ласка, всі поля!", Toast.LENGTH_LONG).show();
        }
        else {
            ContentValues cv = new ContentValues();
            cv.put(mDataBaseHelper.name_instruction, n.getText().toString());
            cv.put(mDataBaseHelper.tool_list, t.getText().toString());
            cv.put(mDataBaseHelper.complexity,c.getText().toString());
            cv.put(mDataBaseHelper.remedy_procedure, r.getText().toString());
            db.update(mDataBaseHelper.table_name_instruction, cv, mDataBaseHelper.name_instruction +" = '" +
                    name_before_edit + "'", null);
            Toast.makeText(getApplicationContext(), "Інструкція " + n.getText()+ " оновлена!", Toast.LENGTH_SHORT).show();
            edit_instruction();
        }
    }
    private void remove_instr(){
        if (pos_item > 0){
            mDataBaseHelper = new DBHelper(this);
            db = mDataBaseHelper.getWritableDatabase();
            db.delete(mDataBaseHelper.table_name_instruction, mDataBaseHelper.name_instruction + " = '" + n.getText()+"'", null);
            Toast.makeText(getApplicationContext(), "Інструкція " + n.getText()+ " видалена! ", Toast.LENGTH_SHORT).show();
            edit_instruction();
        }
        else
            Toast.makeText(getApplicationContext(), "Оберіть яку інструкцію видалити!", Toast.LENGTH_SHORT).show();
    }

}

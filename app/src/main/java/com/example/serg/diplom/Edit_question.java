package com.example.serg.diplom;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class Edit_question extends EditBD{
    private DBHelper mDataBaseHelper;
    private SQLiteDatabase db;
    Spinner spinner;
    EditText n;
    Button btn_add;
    Button btn_remove;
    ArrayAdapter<String> dataAdapter;
    private int pos_item = 0;
    private String name_before_edit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_question);
        edit_question();

        btn_add = (Button) findViewById(R.id.button_add);
        btn_remove = (Button) findViewById(R.id.button_remove);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update_question();
            }
        });
        btn_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remove_question();
            }
        });
    }
    private void edit_question(){
        try {
            mDataBaseHelper = new DBHelper(this);
            db = mDataBaseHelper.getReadableDatabase();
            String selection = mDataBaseHelper.text_question + " LIKE '%?%'";
            Cursor cursor = db.query(mDataBaseHelper.table_name_tree,
                    null,
                    selection,
                    null,
                    null,
                    null,
                    null
            );

            List<String> list = new ArrayList<>();
            spinner = (Spinner) findViewById(R.id.spinner_question);
            list.add("Додати нове питання");

            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor
                        .getColumnIndex(mDataBaseHelper.text_question));
                list.add(name);
            }
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

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                name_before_edit = parent.getItemAtPosition(position).toString();
                query_from_bd(position,name_before_edit);
                pos_item = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    private void query_from_bd(int pos,String name_selected){
        n = (EditText)findViewById(R.id.editText_name);
        if (pos == 0){
            n.setText("");
            btn_add.setText("Додати");
        }
        else{
            btn_add.setText("Оновити");
            mDataBaseHelper = new DBHelper(this);
            db = mDataBaseHelper.getReadableDatabase();
            String selection = "TEXT = '" + name_selected + "'";
            Cursor cursor = db.query(mDataBaseHelper.table_name_tree,
                    null,
                    selection,
                    null,
                    null,
                    null,
                    null
            );
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex(mDataBaseHelper.text_question));
                n.setText(name);
            }
            cursor.close();
        }

    }
    private void update_question(){
        mDataBaseHelper = new DBHelper(this);
        db = mDataBaseHelper.getWritableDatabase();
        String name = n.getText().toString();
        if (pos_item == 0){
            if(!name.equals("")){
                if(!name.contains("?")) name += "?";
                ContentValues cv = new ContentValues();
                cv.put(mDataBaseHelper.text_question, name);
                db.insert(mDataBaseHelper.table_name_tree, null, cv);
                n.setText("");
                Toast.makeText(getApplicationContext(), "Питання " +n.getText()+" додане!", Toast.LENGTH_SHORT).show();
                edit_question();
            }
            else Toast.makeText(getApplicationContext(), "Заповніть, будь ласка, назву питання!", Toast.LENGTH_LONG).show();
        }
        else {
            ContentValues cv = new ContentValues();
            cv.put(mDataBaseHelper.text_question, n.getText().toString());
            db.update(mDataBaseHelper.table_name_tree, cv, mDataBaseHelper.text_question +" = '" +
                    name_before_edit + "'", null);
            Toast.makeText(getApplicationContext(), "Питання " + n.getText()+ " оновлене!", Toast.LENGTH_SHORT).show();
            edit_question();
        }
    }
    private void remove_question(){
        if (pos_item > 0){
            mDataBaseHelper = new DBHelper(this);
            db = mDataBaseHelper.getWritableDatabase();
            db.delete(mDataBaseHelper.table_name_tree, mDataBaseHelper.text_question + " = '" + n.getText()+"'", null);
            Toast.makeText(getApplicationContext(), "Питання " + n.getText()+ " видалене! ", Toast.LENGTH_SHORT).show();
            edit_question();
        }
        else
            Toast.makeText(getApplicationContext(), "Оберіть яке питання видалити!", Toast.LENGTH_SHORT).show();
    }
}

package com.example.serg.diplom;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class Edit_breakdown extends EditBD {
    private DBHelper mDataBaseHelper;
    private SQLiteDatabase db;
    EditText n;
    Button btn_add;
    Button btn_remove;
    Button btn_instruction;
    List<String> list;
    Spinner spinner;
    ArrayAdapter<String> dataAdapter;
    private int pos_item_breakdown = 0;
    private int pos_item_instruction = 0;
    private String name_before_edit;
    private String name_instr;

    MyCustomAdapter dataAdapter_q = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_breakdown);
        edit_breakdown();
        select_instruction();

        btn_add = (Button) findViewById(R.id.button_add);
        btn_remove = (Button) findViewById(R.id.button_remove);
        btn_instruction = (Button) findViewById(R.id.button_add_instr);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update_breakdown();
            }
        });
        btn_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remove_breakdown();
            }
        });
        btn_instruction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i_i = new Intent(Edit_breakdown.this, Edit_instruction.class);
                startActivityForResult(i_i, 1);
            }
        });

        displayListView();
        //checkButtonClick();
    }
    private void edit_breakdown(){
        try {
            mDataBaseHelper = new DBHelper(this);
            db = mDataBaseHelper.getReadableDatabase();
            String selection = mDataBaseHelper.text_question + " NOT LIKE '%?%'";
            Cursor cursor = db.query(mDataBaseHelper.table_name_tree,
                    null,
                    selection,
                    null,
                    null,
                    null,
                    null
            );

            List<String> list = new ArrayList<>();
            spinner = (Spinner) findViewById(R.id.spinner_breakdown);
            list.add("Додати нову несправність");

            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor
                        .getColumnIndex(mDataBaseHelper.text_question));
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
            public void onItemSelected(AdapterView<?> parentView, View view, int position, long id) {
                name_before_edit = parentView.getItemAtPosition(position).toString();
                query_from_bd(position, name_before_edit);
                pos_item_breakdown = position;
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
    private void update_breakdown(){
        mDataBaseHelper = new DBHelper(this);
        db = mDataBaseHelper.getWritableDatabase();
        String name = n.getText().toString();

        if (pos_item_instruction != 0) {
            if (pos_item_breakdown == 0) {
                if (!name.equals("")) {
                    ContentValues cv = new ContentValues();
                    cv.put(mDataBaseHelper.text_question, name);
                    cv.put(mDataBaseHelper.id_instr, find_id_by_name_instruction(name_instr));
                    db.insert(mDataBaseHelper.table_name_tree, null, cv);
                    n.setText("");
                    Toast.makeText(getApplicationContext(), "Несправність " + n.getText() + " додана!", Toast.LENGTH_SHORT).show();
                    edit_breakdown();
                } else
                    Toast.makeText(getApplicationContext(), "Заповніть, будь ласка, назву несправності!", Toast.LENGTH_LONG).show();
            } else {
                ContentValues cv = new ContentValues();
                cv.put(mDataBaseHelper.text_question, n.getText().toString());
                cv.put(mDataBaseHelper.id_instr, find_id_by_name_instruction(name_instr));
                db.update(mDataBaseHelper.table_name_tree, cv, mDataBaseHelper.text_question + " = '" +
                        name_before_edit + "'", null);
                Toast.makeText(getApplicationContext(), "Несправність " + n.getText() + " оновлена!", Toast.LENGTH_SHORT).show();
                edit_breakdown();
            }
        }
        else Toast.makeText(getApplicationContext(), "Оберіть, будь ласка, інстркуцію усунення цієї несправності!", Toast.LENGTH_SHORT).show();

    }
    private void remove_breakdown(){
        if (pos_item_breakdown > 0){
            mDataBaseHelper = new DBHelper(this);
            db = mDataBaseHelper.getWritableDatabase();
            db.delete(mDataBaseHelper.table_name_tree, mDataBaseHelper.text_question + " = '" + n.getText()+"'", null);
            Toast.makeText(getApplicationContext(), "Несправність " + n.getText()+ " видалена! ", Toast.LENGTH_SHORT).show();
            edit_breakdown();
        }
        else
            Toast.makeText(getApplicationContext(), "Оберіть яку несправність видалити!", Toast.LENGTH_SHORT).show();
    }

    private int find_id_by_name_instruction(String name){
        int id_instruction = 0;
        mDataBaseHelper = new DBHelper(this);
        db = mDataBaseHelper.getReadableDatabase();
        String selection = "NAME =  '" + name + "'";
        Cursor c = db.query(mDataBaseHelper.table_name_instruction,
                null,
                selection,
                null,
                null,
                null,
                null
        );
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    String idDB = c.getString(c.getColumnIndex(mDataBaseHelper.UID));
                    id_instruction = Integer.valueOf(idDB);
                } while (c.moveToNext());
            }
            c.close();
        }
        return id_instruction;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == 1) {
            select_instruction();
            spinner.setSelection(spinner.getAdapter().getCount() - 1);
        }
    }

    private int select_instruction(){
        spinner = (Spinner) findViewById(R.id.spinner_instruction);

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

            list = new ArrayList<>();
            list.add("Оберіть несправність");
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
                name_instr = parentView.getItemAtPosition(position).toString();
                pos_item_instruction = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
        return pos_item_instruction;
    }

    private void displayListView() {

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
        ArrayList<Question> questionList = new ArrayList<Question>();

        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor
                    .getColumnIndex(mDataBaseHelper.text_question));
            Question q = new Question(name,false);
            questionList.add(q);
        }
        cursor.close();

        //create an ArrayAdaptar from the String Array
        dataAdapter_q = new MyCustomAdapter(this,
                R.layout.list_with_checkbox, questionList);
        ListView listView = (ListView) findViewById(R.id.listView1);
        // Assign adapter to ListView
        listView.setAdapter(dataAdapter_q);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // When clicked, show a toast with the TextView text
                Question country = (Question) parent.getItemAtPosition(position);
                /*Toast.makeText(getApplicationContext(),
                        "Clicked on Row: " + country.getName(),
                        Toast.LENGTH_LONG).show();*/
            }
        });

    }

    private class MyCustomAdapter extends ArrayAdapter<Question> {

        private ArrayList<Question> questionList;

        public MyCustomAdapter(Context context, int textViewResourceId,
                               ArrayList<Question> questionList) {
            super(context, textViewResourceId, questionList);
            this.questionList = new ArrayList<Question>();
            this.questionList.addAll(questionList);
        }

        private class ViewHolder {
            CheckBox name;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;

            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater)getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.list_with_checkbox, null);

                holder = new ViewHolder();
                holder.name = (CheckBox) convertView.findViewById(R.id.checkBox1);
                convertView.setTag(holder);

                holder.name.setOnClickListener( new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v ;
                        Question country = (Question) cb.getTag();
                        /*Toast.makeText(getApplicationContext(),
                                "Clicked on Checkbox: " + cb.getText() +
                                        " is " + cb.isChecked(),
                                Toast.LENGTH_LONG).show();*/
                        country.setSelected(cb.isChecked());
                    }
                });
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }

            Question country = questionList.get(position);
            holder.name.setText(country.getName());
            holder.name.setChecked(country.isSelected());
            holder.name.setTag(country);

            return convertView;

        }

    }

    /*private void checkButtonClick() {
        Button myButton = (Button) findViewById(R.id.findSelected);
        myButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                StringBuffer responseText = new StringBuffer();
                responseText.append("The following were selected...\n");

                ArrayList<Question> questionList = dataAdapter_q.questionList;
                for (int i = 0; i < questionList.size(); i++) {
                    Question country = questionList.get(i);
                    if (country.isSelected()) {
                        responseText.append("\n" + country.getName());
                    }
                }

                Toast.makeText(getApplicationContext(),
                        responseText, Toast.LENGTH_LONG).show();

            }
        });

    }*/

}

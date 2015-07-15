package com.example.serg.diplom;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class ShowBreakdowns extends Prompt {

    private static final String TAG = "ShowASD";

    private ArrayList<String> results = new ArrayList<String>();

    private DBHelper mDataBaseHelper;
    private SQLiteDatabase db;
    ListView myList;
    //protected Node now;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_breakdown);

        Log.v(TAG, "oncreate");
        queryDatabase();
        listener();
    }

    private void queryDatabase() {
        try {
            Log.v(TAG,"try1");
            mDataBaseHelper = new DBHelper(this);
            db = mDataBaseHelper.getReadableDatabase();
            String selection = mDataBaseHelper.id_instr + " IS NOT '' AND " + mDataBaseHelper.text_question + " NOT LIKE '%?%'";
            Cursor c = db.query(mDataBaseHelper.table_name_tree,
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
                        Log.v(TAG, "try_do_while");
                        String Name = c.getString(c.getColumnIndex(mDataBaseHelper.text_question));
                        results.add(Name);
                    } while (c.moveToNext());
                }
                c.close();
            }
            myList = (ListView)findViewById(R.id.listView);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, results);
            myList.setAdapter(adapter);
        }
        catch (Exception e){
            Log.v(TAG,"catch");
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void listener(){
        mDataBaseHelper = new DBHelper(this);
        db = mDataBaseHelper.getReadableDatabase();
        myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.v(TAG, "onItemClick");

                String name_breakdown = ((TextView) view).getText().toString();

                Log.v(TAG,"namebreakdown  = "+ ((TextView) view).getText().toString());
                String selection = "TEXT =  '" + name_breakdown + "'";
                Cursor c = db.query(mDataBaseHelper.table_name_tree,
                        null,
                        selection,
                        null,
                        null,
                        null,
                        null
                );
                String id_ins = "";
                if (c != null) {
                    if (c.moveToFirst()) {
                        do {
                            String idDB = c.getString(c.getColumnIndex(mDataBaseHelper.id_instr));
                            id_ins = id_ins+idDB;
                        } while (c.moveToNext());
                    }
                    c.close();
                }
                Intent intent = new Intent(ShowBreakdowns.this,InstructionShow.class);
                intent.putExtra("out_id_breakdown", id_ins);
                startActivity(intent);

            }
        });
        myList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), "You long clicked on "+((TextView) view).getText().toString(), Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }
}

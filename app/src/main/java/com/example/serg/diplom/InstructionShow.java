package com.example.serg.diplom;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;


public class InstructionShow extends  Prompt{

    private static final String TAG = "INS";
    private DBHelper mDataBaseHelper;
    private SQLiteDatabase db;

    String in_id_breakdown;
    TextView tool_list_ins;
    TextView remedy_procedure_ins;
    TextView complexity_ins;
    TextView name_ins;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_instruction);
        Log.v(TAG, "oncreate instr");

        name_ins = (TextView) findViewById(R.id.name_ins_textView);
        tool_list_ins = (TextView) findViewById(R.id.toollist_textView);
        complexity_ins = (TextView) findViewById(R.id.complexity_textView);
        remedy_procedure_ins = (TextView) findViewById(R.id.instruction_textView);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                in_id_breakdown= null;
            } else {
                in_id_breakdown= extras.getString("out_id_breakdown");
            }
        } else {
            in_id_breakdown= (String) savedInstanceState.getSerializable("out_id_breakdown");
        }
        show(in_id_breakdown);

    }
    private void show(String in_id){
        if (in_id.isEmpty()) in_id = "100500";
        mDataBaseHelper = new DBHelper(this);
        db = mDataBaseHelper.getReadableDatabase();
        String selection = "_id = " + in_id;
        Cursor cursor = db.query(mDataBaseHelper.table_name_instruction,
                null,
                selection,
                null,
                null,
                null,
                null
        );
        while (cursor.moveToNext()) {
            String name_instruction = cursor.getString(cursor.getColumnIndex(mDataBaseHelper.name_instruction));
            String tool_list = cursor.getString(cursor.getColumnIndex(mDataBaseHelper.tool_list));
            String complexity = cursor.getString(cursor.getColumnIndex(mDataBaseHelper.complexity));
            String remedy_procedure = cursor.getString(cursor.getColumnIndex(mDataBaseHelper.remedy_procedure));
            name_ins.setText(name_instruction);
            tool_list_ins.setText(tool_list+"\n");
            complexity_ins.setText(complexity+"\n");
            remedy_procedure_ins.setText(remedy_procedure);
        }
        cursor.close();
    }
	
}

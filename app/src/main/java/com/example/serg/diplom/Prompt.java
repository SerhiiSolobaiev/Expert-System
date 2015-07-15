package com.example.serg.diplom;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class Prompt extends MainActivity implements OnClickListener, OnKeyListener {

	private QuestionGame game;
	private boolean gameover;
	public static int n = 0;
	public static int r = 0;
	TextView t;
	DBHelper mDataBaseHelper;
	SQLiteDatabase db;
	List<String> list;
	Spinner spinner;
	ArrayAdapter<String> dataAdapter;

	private int pos_item = 0;
	public static String found_name_breakdown;
	public static int get_n(){
		return n;
	}
	public static int get_r(){
		return r;
	}
	public static void set_found_name_breakdown(String name_breakdown){found_name_breakdown = name_breakdown;}
    @Override
    public void onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState);
        game = new QuestionGame("dog", this);
        setContentView(R.layout.find_breakdown);
        View yesButton = findViewById(R.id.yes);
		View noButton = findViewById(R.id.no);
		View xzButton = findViewById(R.id.xz);

        yesButton.setOnClickListener(this);
		noButton.setOnClickListener(this);
		xzButton.setOnClickListener(this);
        //Finds the more_textbox view
		
        EditText animalField = (EditText)findViewById(R.id.breakdown_textbox);
		animalField.setOnKeyListener(this);
		
        EditText questionField = (EditText)findViewById(R.id.question_textbox);
		questionField.setOnKeyListener(this);
		
		View submitButton = findViewById(R.id.entertext);
		View newgameButton = findViewById(R.id.newgame);
		View add_new_instruction= findViewById(R.id.button_add_instruction);

		submitButton.setOnClickListener(this);
		newgameButton.setOnClickListener(this);
		add_new_instruction.setOnClickListener(this);

        run();
		find_id_by_name_instruction_from_spinner();

    }
    
    private void run() {
		t = (TextView) findViewById(R.id.textprompt);

		if (gameover) {
			if (game.lost()) {
				t.setText(game.lose());
				LinearLayout lin = (LinearLayout) findViewById(R.id.linear_layout_add);
				lin.setVisibility(View.VISIBLE);
				t.invalidate();
				return;
			}

			t.setText(game.win());
			go_to_instruction(found_name_breakdown);
			t.invalidate();
			return;
    	}
		t.setText(game.getNext());
		t.invalidate();
    }

	private void go_to_instruction(String found_name_breakdown){
		DBHelper mDataBaseHelper = new DBHelper(this);
		SQLiteDatabase db = mDataBaseHelper.getReadableDatabase();
		String id_ins = "";
		String selection = "TEXT =  '" + found_name_breakdown + "'";
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
					String idDB = c.getString(c.getColumnIndex(mDataBaseHelper.id_instr));
					id_ins += idDB;
				} while (c.moveToNext());
			}
			c.close();
		}
		Intent intent = new Intent(Prompt.this,InstructionShow.class);
		intent.putExtra("out_id_breakdown", id_ins);
		startActivity(intent);
	}
    public void onClick(View v) {

		switch(v.getId()) {		
		case R.id.yes:	
			gameover = game.guessResult(true);
			run();
			break;
		case R.id.no:
			r++;
			gameover = game.guessResult(false);
			run();
			break;
		case R.id.xz:
			n++;
			gameover = game.guessResult(false);
			run();
			break;
		case R.id.entertext:
				String breakdown = ((TextView) findViewById(R.id.breakdown_textbox)).getText().toString();
				String question = ((TextView) findViewById(R.id.question_textbox)).getText().toString();
				//String instruction = ((TextView) findViewById(R.id.instruction_textbox)).getText().toString();
				String instruction = String.valueOf(find_id_by_name_instruction_from_spinner());
				if (!question.contains("?")) question += "?";
				if (!breakdown.equals("") && !question.equals("?") && !instruction.equals("") && pos_item != 0) {
					game.learn(breakdown, question, instruction, find_instr_by_name_breakdown(found_name_breakdown));
					onClick(findViewById(R.id.newgame));
					break;
				} else {
					Toast.makeText(getApplicationContext(), "Заповніть, будь ласка, всі поля!", Toast.LENGTH_SHORT).show();
					break;
				}
		case R.id.newgame:
			TextView a = ((TextView) findViewById(R.id.breakdown_textbox));
			TextView b = ((TextView) findViewById(R.id.question_textbox));
			LinearLayout lin = (LinearLayout)findViewById(R.id.linear_layout_add);
            lin.setVisibility(View.INVISIBLE);
            a.setText("");
			b.setText("");
			n=0;r=0;
			game.newGame();
			gameover = false;
			run();
			break;
		case R.id.button_add_instruction:
			Intent i_i = new Intent(Prompt.this,Edit_instruction.class);
			startActivityForResult(i_i,1);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && requestCode == 1) {
			find_id_by_name_instruction_from_spinner();
			spinner.setSelection(spinner.getAdapter().getCount()-1);
		}
	}

	private int find_id_by_name_instruction_from_spinner(){
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
				pos_item = position;
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
				// your code here
			}

		});

		return pos_item;
	}

	private String find_instr_by_name_breakdown(String found_name_breakdown){
		mDataBaseHelper = new DBHelper(this);
		db = mDataBaseHelper.getReadableDatabase();
		String id_ins = "";
		String selection = "TEXT =  '" + found_name_breakdown + "'";
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
					String idDB = c.getString(c.getColumnIndex(mDataBaseHelper.id_instr));
					id_ins += idDB;
				} while (c.moveToNext());
			}
			c.close();
		}
		return  id_ins;
	}
    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event){
    	if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
    		 	      (keyCode == KeyEvent.KEYCODE_ENTER)){
    		//EditText t = (EditText) v;
    		this.onClick(findViewById(R.id.entertext));
    	}
    	return false;
    }
}
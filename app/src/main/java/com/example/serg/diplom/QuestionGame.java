package com.example.serg.diplom;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import static android.provider.BaseColumns._ID;


public class QuestionGame extends Prompt{
	private static final String TAG = "questiongame";
	private Node now;
	private Node root;
	private boolean askLoseQuestion = false;
	private Context ctx;

	
	public QuestionGame(String msg, Context ctx){
		
		this.ctx = ctx;
		root = getRootNode(msg);
		now = root;
		
	}

	public String getNext(){
		Log.d(TAG, "entering getNext()");
		if (now.isLeaf()){
			Prompt.set_found_name_breakdown(now.text);
			if (Prompt.get_n()>=2 || Prompt.get_r() >= 3) {
				return ctx.getResources().getString(R.string.no_breakdown);
			}
			else return ctx.getString(R.string.founded_breakdown) + " " + now.text + "\n\n" +ctx.getString(R.string.show_instr);
		}
		else
			return now.text;
	}
	
	//return a true if the game is over, false if it continues
	public boolean guessResult(boolean r){
		if(r == true && now.getY() == null){
			Log.v(TAG, "now.y == null");
			return true; /*You win*/
		}
		else if(r == true && now.getY() != null){
			now = now.getY();
			return false;
		}
		else if (r == false && now.getN() == null){
			askLoseQuestion = true;
			return true; /*You lose*/
		}
		else if(r == false){
			now = now.getN();
			return false;
		}
		else{
			//System.out.println( "This should never happen.");
			return false;
			
		}
	}	
	
	public String win(){
        Log.v(TAG,"open show_instruction");
        return now.text;
	}
	
	public String lose(){

        return ctx.getResources().getString(R.string.add_break) +" "+ now.text + "?";
		//I lose. What were you thinking of? What question is true for it, but not a " + now.text + "?";
	}
	
	public void learn(String b, String q, String i,String i_before){
		//b = breakdown, q = question, i = instruction for breakdown.
		Log.v(TAG, "i_before ===== "+i_before);

		Node y = now.newY(b,i);

		Node n = now.newN(now.text,i_before);
		now.setText(q);
		//now.n = n;
		//now.y = y;
	}
	
	public boolean lost(){
		return askLoseQuestion?true:false;
	}
	
	public void newGame(){
		now = root;
		askLoseQuestion = false;
	}

	public Node getRootNode(String msg){
		
		Log.v(TAG, "get node from ID 0");
		
		DBHelper d = new DBHelper(ctx);
		SQLiteDatabase db = d.getWritableDatabase();
		Log.v(TAG, "successfully read from db");
		String[] args = {"0"};
		Cursor c = db.rawQuery("Select * from NODES where "+ _ID + " =?", args);

		if (c.getCount() == 0){ //if the db is empty
			boolean isdone = c.moveToNext();
			Log.d(TAG, "this should be false if cursor was truly empty" + isdone);
			c.close();
			ContentValues values = new ContentValues();
		    values.put("TEXT", msg);
		    values.put(_ID, 0);
		    db.beginTransaction();
		    db.execSQL("insert into nodes (" + _ID + ", text) values (0, '" + msg+"')");
		    //db.insertOrThrow("NODES", null, values);
		    db.setTransactionSuccessful();
		    db.endTransaction();
		    c = db.rawQuery("Select * from NODES where " + _ID + "=?", args);
		    
		}
		c.moveToNext();
		String retrievedmsg = c.getString(1);
		c.close();
		db.close();
		return new Node(retrievedmsg, "0", ctx);
	}
	private String printDB(Cursor c){
		String s = "";
		c.moveToFirst();
		s += c.getString(0);
		s += " | ";
		s += c.getString(1);
		s += " | ";
		s += c.getString(2);
		s += " | ";
		s += c.getString(3);
		s += "\n";
		while (c.moveToNext())
		{
			s += c.getString(0);
			s += " | ";
			s += c.getString(1);
			s += " | ";
			s += c.getString(2);
			s += " | ";
			s += c.getString(3);
			s += "\n";
		}
		return s;
	}
}

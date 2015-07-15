package com.example.serg.diplom;

import android.content.Context;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class DBHelper extends SQLiteAssetHelper {
    private static final String DATABASE_NAME = "events.db";
    private static final int DATABASE_VERSION = 4;

    public static final String table_name_tree = "NODES";
    public static final String UID = "_id";
    public static final String text_question = "TEXT";
    public static final String text_yes= "Y";
    public static final String text_no = "N";
    public static final String id_instr = "ID_INSTRUCTION";

    public static final String table_name_instruction = "INSTRUCTIONS";
    public static final String name_instruction = "NAME";
    public static final String complexity = "COMPLEXITY";
    public static final String remedy_procedure = "REMEDY_PROCEDURE";
    public static final String tool_list = "TOOL_LIST";

    public DBHelper(Context ctx) {
        super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
    }
}

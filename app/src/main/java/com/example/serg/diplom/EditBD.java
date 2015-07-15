package com.example.serg.diplom;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class EditBD extends MainActivity implements OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_bd);
        Button btn_breakdown = (Button)findViewById(R.id.button_breakdown);
        Button btn_ins = (Button)findViewById(R.id.button_instruction);
        Button btn_question = (Button)findViewById(R.id.button_question);
        btn_breakdown.setOnClickListener(this);
        btn_ins.setOnClickListener(this);
        btn_question.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())  {
            case  R.id.button_breakdown:
                Intent i_b = new Intent(EditBD.this,Edit_breakdown.class);
                startActivity(i_b);
                break;
            case  R.id.button_instruction:
                Intent i_i = new Intent(EditBD.this,Edit_instruction.class);
                startActivity(i_i);
                break;
            case  R.id.button_question:
                Intent i_q = new Intent(EditBD.this,Edit_question.class);
                startActivity(i_q);
                break;
            }
    }
}

package com.example.serg.diplom;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import java.util.Locale;


public class MainActivity extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        View btn_find = findViewById(R.id.button_find);
        View btn_show = findViewById(R.id.button_show);
        btn_find.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent int_find = new Intent(MainActivity.this,Prompt.class);
                startActivity(int_find);
            }
        });
        btn_show.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent int_show = new Intent(MainActivity.this,ShowBreakdowns.class);
                startActivity(int_show);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            AlertDialog levelDialog;
            final CharSequence[] items = {"Українська","Русский"};
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.select_language);
            builder.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    switch (item) {
                        case 0:
                            Locale locale = new Locale("eu-US");
                            Locale.setDefault(locale);
                            Configuration config = new Configuration();
                            config.locale = locale;
                            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
                            break;
                        case 1:
                            Locale locale_ru = new Locale("ru");
                            Locale.setDefault(locale_ru);
                            Configuration config_ru = new Configuration();
                            config_ru.locale = locale_ru;
                            getBaseContext().getResources().updateConfiguration(config_ru, getBaseContext().getResources().getDisplayMetrics());
                    }
                    dialog.dismiss();
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }
            });
            levelDialog = builder.create();
            levelDialog.show();
            return true;
        }
        if (id == R.id.about) {
            aboutAlertDialog();
            return true;
        }
        if (id == R.id.action_call) {
            createAlertDialog();
            return true;
        }
        if (id == R.id.expert) {
            Intent int_exp = new Intent(MainActivity.this,EditBD.class);
            startActivity(int_exp);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void createAlertDialog(){
        final String expert_phone = "0964699065";
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(getResources().getString(R.string.name_expert));
        alertDialog.setMessage(getResources().getString(R.string.name_expert));
        alertDialog.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                try {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + expert_phone));
                    startActivity(callIntent);
                } catch (ActivityNotFoundException activityException) {
                    Log.e("Calling a Phone Number", "Call failed", activityException);
                    Toast.makeText(getApplicationContext(),"BADDDDD(((", Toast.LENGTH_SHORT).show();
                }
            }
        });
        alertDialog.setNegativeButton(getResources().getString(R.string.not_now), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }
    private void aboutAlertDialog(){

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage(getResources().getString(R.string.about_name));
        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }
}


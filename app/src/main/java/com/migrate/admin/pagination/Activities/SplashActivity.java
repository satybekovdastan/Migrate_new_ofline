package com.migrate.admin.pagination.Activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.migrate.admin.pagination.Helpers.DataHelper;
import com.migrate.admin.pagination.R;

import java.util.Calendar;


public class SplashActivity extends AppCompatActivity {
    DataHelper dataHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        dataHelper=new DataHelper(this);
        Cursor cursor=dataHelper.getDataLanguage();
        if (cursor.getCount()==0)
            dataHelper.insertLanguage(1);
        Calendar calendar=Calendar.getInstance();
        int day=calendar.get(Calendar.DAY_OF_MONTH);
        int month=calendar.get(Calendar.MONTH);
        int year=calendar.get(Calendar.YEAR);
        String date=day+"."+month+"."+year;

        cursor=dataHelper.getDataDate1();
        if (cursor.getCount()>0){
            cursor.moveToFirst();
            String dateDB=cursor.getString(cursor.getColumnIndex(DataHelper.DATE_LAST_DATE_COLUMN));
            if (dateDB.equals(date)){
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent i = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(i);
                        finish();
                    }
                }, 2 * 1000);
            }
            else{
                dataHelper.deleteDate1();

                Intent intent=new Intent(SplashActivity.this,SyncActivity.class);
                intent.putExtra("date",date);
                startActivity(intent);
            finish();}
        }else {
            dataHelper.deleteDate1();

            Intent intent=new Intent(SplashActivity.this,SyncActivity.class);
            intent.putExtra("date",date);
            startActivity(intent);
            finish();
        }


    }
}

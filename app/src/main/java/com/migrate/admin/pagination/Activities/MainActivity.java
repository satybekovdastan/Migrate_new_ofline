package com.migrate.admin.pagination.Activities;

import android.accounts.Account;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.migrate.admin.pagination.*;
import com.migrate.admin.pagination.Adapters.RVNewsAdapter;
import com.migrate.admin.pagination.Adapters.RVProhibitionAdapter;
import com.migrate.admin.pagination.Helpers.DataHelper;
import com.squareup.picasso.Picasso;

import java.util.Locale;

import static android.R.id.message;
import static android.provider.ContactsContract.AUTHORITY;

public class MainActivity extends AppCompatActivity {
    DataHelper dataHelper;

    private Locale mNewLocale;
    MenuItem item;
    ActionBar actionBar;
    TextView news,eaeu,hotLine,embassy,diaspora,abroad,ht,prohibition,employ,faq, about;
    int lang;
    private Menu menu;
    AlertDialog.Builder ad;
    Account mAccount;
    public RVProhibitionAdapter mAdapter;
    public RVNewsAdapter newsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar;
        Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();
        Log.e("WERTYUIKOLKJHGFDFJK",width+"  "+height);
        news = (TextView) findViewById(R.id.news);
        eaeu = (TextView) findViewById(R.id.eaeu);
        hotLine = (TextView) findViewById(R.id.hot_line);
        embassy = (TextView) findViewById(R.id.embassy);
        diaspora = (TextView) findViewById(R.id.diaspora);
        abroad = (TextView) findViewById(R.id.abroad);
        ht = (TextView) findViewById(R.id.ht);
        prohibition = (TextView) findViewById(R.id.prohibition);
        employ = (TextView) findViewById(R.id.employ);
        faq = (TextView) findViewById(R.id.faq);
        about = (TextView) findViewById(R.id.o_proekt);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar=getSupportActionBar();
        actionBar.setTitle(R.string.app_name);
        dataHelper=new DataHelper(this);
        dataHelper.deleteSize();
        dataHelper.insertSize(width,height);


//        Bundle settingsBundle = new Bundle();
//        settingsBundle.putBoolean(
//                ContentResolver.SYNC_EXTRAS_MANUAL, true);
//        settingsBundle.putBoolean(
//                ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
//
//        ContentResolver.requestSync(mAccount, AUTHORITY, settingsBundle);

        Cursor cursor=dataHelper.getDataLanguage();
        if (cursor.getCount()==0)
        {
            dataHelper.insertLanguage(1);
        }

        if (dataHelper.getDataDate1().getCount()==0) {
            for (int i = 1; i < 25; i++) {
                dataHelper.insertDate("ss");
            }
        }



        ad = new AlertDialog.Builder(this);
        ad.setTitle("Внимание");  // заголовок
        ad.setMessage("Приложение будет перезапущено для применение настроек"); // сообщение
        ad.setPositiveButton("Да", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {

                Cursor cursor=dataHelper.getDataLanguage();
                if (cursor.getCount()>0)
                {
                    cursor.moveToFirst();
                    lang=cursor.getInt(cursor.getColumnIndex(DataHelper.LANGUAGE_COLUMN));
                }
                else {lang=0;
                }
                lang=(lang+1)%2;
                dataHelper.deleteLanguage();
                dataHelper.insertLanguage(lang);
                if (lang==0){ item.setTitle("kg");
                    setLocale("ru");}
                else{   item.setTitle("ru");
                    setLocale("ky");}
                Intent intent=new Intent(MainActivity.this,SyncActivity.class);
                intent.putExtra("kk",1);
                //intent.putExtra("kk",0);
                startActivity(intent);

                finish();
            }
        });
        ad.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
//                Toast.makeText(MainActivity.this, "Возможно вы правы", Toast.LENGTH_LONG)
//                        .show();
            }
        });
        ad.setCancelable(true);
        ad.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                Toast.makeText(MainActivity.this, "Загрузка",
                        Toast.LENGTH_LONG).show();
            }
        });


    }

    public void onClick(View view) {
        switch (view.getId()){
            case R.id.bt_faq:
                startActivity(new Intent(MainActivity.this, FAQActivity.class));
                break;
            case R.id.bt_diaspory:
                startActivity(new Intent(MainActivity.this, DiasporyActivity.class));
                break;
            case R.id.bt_zapret_v_RF:
                startActivity(new Intent(MainActivity.this,ProhibitionRFActivity.class));
                break;
            case  R.id.bt_news:
                startActivity(new Intent(MainActivity.this,NewsActivity.class));
                break;
            case R.id.bt_tel_doveriya:
                startActivity(new Intent(MainActivity.this,HotLineCountryActivity.class));
                break;
            case R.id.bt_konsulstva_diaspory:
                startActivity(new Intent(MainActivity.this, EmbassyActivity.class));
                break;
            case R.id.bt_instruction:
                startActivity(new Intent(MainActivity.this,EAEUActivity.class));
                break;
            case R.id.bt_rules_of_migration:
                startActivity(new Intent(MainActivity.this,AbroadActivity.class));
                break;
            case R.id.bt_torgovlya:
                startActivity(new Intent(MainActivity.this,HumanTraffickingActivity.class));
            break;
            case R.id.bt_trudoustiystvo:
                startActivity(new Intent(MainActivity.this,EmployementActivity.class));
                break;
            case R.id.bt_about:
                startActivity(new Intent(MainActivity.this,AboutProjectActivity.class));
                break;

        }
    }
    void setLocale(String mLang) {
        mNewLocale = new Locale(mLang);
        Locale.setDefault(mNewLocale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.locale = mNewLocale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
        updateTextView();
    }

    private void updateTextView() {
        embassy.setText(R.string.bt_embassy);
        employ.setText(R.string.bt_employment);
        eaeu.setText(R.string.bt_eaeu);
        abroad.setText(R.string.bt_abroad);
        diaspora.setText(R.string.bt_diaspora);
        ht.setText(R.string.bt_ht);
        hotLine.setText(R.string.bt_hot_line);
        prohibition.setText(R.string.bt_prohibition);
        faq.setText(R.string.bt_faq);
        news.setText(R.string.bt_news);
        actionBar.setTitle(R.string.app_name);
        about.setText(R.string.ac_about_project);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        item = menu.findItem(R.id.menu_item_language);
        Cursor cursor=dataHelper.getDataLanguage();
        if (cursor.getCount()>0)
        {
            cursor.moveToFirst();
            lang=cursor.getInt(cursor.getColumnIndex(DataHelper.LANGUAGE_COLUMN));
        }
        else lang=0;
        if (lang==0){
            item.setTitle("kg");
            setLocale("ru");
        }
        else{
            setLocale("ky");
            item.setTitle("ru");
        }
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // получим идентификатор выбранного пункта меню
        int id = item.getItemId();
        // Операции для выбранного пункта меню
        switch (id) {

//            case R.id.about_project:
//                startActivity(new Intent(MainActivity.this, AboutProjectActivity.class));

               // return true;
            case R.id.menu_item_language:


             // ifConnect();
                ad.show();

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void ifConnect(){
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED){
            ad.show();
        }
        else {
            Toast.makeText(this,R.string.no_internet,Toast.LENGTH_SHORT).show();
        }
    }

}
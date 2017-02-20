package com.migrate.admin.pagination.Activities;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.migrate.admin.pagination.R;
import com.migrate.admin.pagination.Serializables.Istories;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.Toast;

import static android.R.attr.name;

public class InspectionActivity extends AppCompatActivity {
    EditText editfio, editday, editmonth, edityear;
    TextView textView, tvSuka, text_text;
    Calendar calendar;
    String fio, day, month, year;
    int DIALOG_DATE = 1;
    int myYear = 2016;
    int myMonth = 11;
    int myDay = 22;
    ProgressBar progressBarOne;
    String na;
    DatePicker datePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspection);
        Toolbar toolbar;
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.ac_blacklist);

        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        tvSuka = (TextView) findViewById(R.id.suka);
        editfio = (EditText) findViewById(R.id.edit_fio);
        editday = (EditText) findViewById(R.id.edit_day);
        editmonth = (EditText) findViewById(R.id.edit_month);
        edityear = (EditText) findViewById(R.id.edit_year);
        textView = (TextView) findViewById(R.id.tv_result);
        text_text = (TextView) findViewById(R.id.tv_text);
        progressBarOne = (ProgressBar) findViewById(R.id.pbHeaderProgress);
        progressBarOne.setVisibility(View.GONE);

        ifConnect();
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(InspectionActivity.this);
        alertDialog.setTitle(R.string.error);
        alertDialog.setMessage(R.string.tv_text);
        alertDialog.setPositiveButton(R.string.yasno, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alertDialog.show();
      //  editfio.setError("Введите тоько фамилию");
        editfio.setError(getText(R.string.familya));
    }

    public void ifConnect() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            text_text.setText("");
        } else {
            Toast.makeText(this, R.string.no_internet, Toast.LENGTH_SHORT).show();
            text_text.setText(R.string.no_internet);
            progressBarOne.setVisibility(View.GONE);
        }
    }


    public static boolean hasConnection(final Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiInfo != null && wifiInfo.isConnected()) {
            return true;
        }
        wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifiInfo != null && wifiInfo.isConnected()) {
            return true;
        }
        wifiInfo = cm.getActiveNetworkInfo();
        if (wifiInfo != null && wifiInfo.isConnected()) {
            return true;
        }
        return false;
    }


    //    fio = request.POST.get('fio')
//    day = request.POST.get('day')
//    month = request.POST.get('month')
//    year = request.POST.get('year')
    public void onClickNewStory(View view) {

        ifConnect();
        boolean bool = true;
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editfio.getWindowToken(), 0);

        fio = editfio.getText().toString().trim();
        //fio.trim();
        if (edityear.getText().toString().length() == 1)
            year = edityear.getText().toString();
        else year = edityear.getText().toString();
        if (editday.getText().toString().length() == 1)
            day = editday.getText().toString();
        else day = editday.getText().toString();
        if (editmonth.getText().toString().length() == 0)
            month = editmonth.getText().toString();

        else month = editmonth.getText().toString();
        new ParseTask().execute();
        progressBarOne.setVisibility(View.VISIBLE);
        //  text_text.setText("Пожалуйста, проверьте Ваше интернет-соединение");
        na = editfio.getText().toString();
        if (!isValidName(na)) {
            editfio.setError("Заполните поле");
            bool = false;
        }

        ifConnect();

    }


    private boolean isValidName(String na) {
        if (na != null && na.length() > 1) {
            return true;
        }
        return false;
    }


    public void onclick(View view) {
        showDialog(DIALOG_DATE);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editfio.getWindowToken(), 0);
    }


    protected Dialog onCreateDialog(int id) {
        if (id == DIALOG_DATE) {
            DatePickerDialog tpd = new DatePickerDialog(InspectionActivity.this, myCallBack, myYear, myMonth, myDay);
            DatePickerDialog dpd3 = new DatePickerDialog(InspectionActivity.this,
                    AlertDialog.THEME_HOLO_LIGHT, myCallBack, myYear, myYear, myDay);
            dpd3.getDatePicker().setMaxDate(21);
            Calendar maxDate = Calendar.getInstance();
            maxDate.set(Calendar.DAY_OF_MONTH, 31);
            maxDate.set(Calendar.MONTH, 12);
            maxDate.set(Calendar.YEAR, 2016);

            dpd3.getDatePicker().setMaxDate(maxDate.getTimeInMillis());
            return dpd3;
        }
        return super.onCreateDialog(id);
    }

    DatePickerDialog.OnDateSetListener myCallBack = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            myYear = year;
            int a = 1;
            myMonth = monthOfYear;
            int b = myMonth + a;

            myDay = dayOfMonth;
            editday.setText(myDay + "");
            editmonth.setText("" + b);
            edityear.setText("" + myYear);
            tvSuka.setText(myDay + "." + b + "." + myYear);


        }

    };

    public void onClickCyl(View view) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://ps.fsb.ru/receiving.htm")));
    }


    public class ParseTask extends AsyncTask<Void, Void, String> {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String jsonResult = "";


        @Override
        protected String doInBackground(Void... params) {

            try {

                URL url = new URL("http://176.126.167.249/blacklist/?fio=" + fio + "&day=" + day + "&month=" + month + "&year=" + year + "&format=json");
                Log.e("DAY", day + " " + month + "" + year + "");
                Log.e("DAY", day);
                Log.e("MON", month + "");
                Log.e("DAY", year);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder builder = new StringBuilder();

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }

                jsonResult = builder.toString();

            } catch (Exception e) {
                e.printStackTrace();
            }

            return jsonResult;
        }

        @Override
        protected void onPostExecute(String json) {
            super.onPostExecute(json);

            JSONObject dataJsonObject;


            try {
                dataJsonObject = new JSONObject(json);


                progressBarOne.setVisibility(View.GONE);
                String s = dataJsonObject.getString("result");
                String a = "[]";
                if (s.equals(a)) {
                    textView.setText("Введите Фамилию в форму поиска для проверки наличия в «Чёрном списке»:");
                } else {
                    textView.setText(dataJsonObject.getString("result"));
                }
                Log.e("TAG", dataJsonObject.getString("result"));
                //  text_text.setText(R.string.tv_text);
//                text_text.setVisibility(View.VISIBLE);
//                text_text.setText(dataJsonObject.getString("name"));
                //  dataJsonObject = new JSONObject(json);
                JSONArray menus = dataJsonObject.getJSONArray("result");

                for (int i = 0; i < menus.length(); i++) {
                    JSONObject menu = menus.getJSONObject(i);
                    String name = menu.getString("name");
                    Log.e("LOG", name);
                    String nakaz = menu.getString("nakaz");
                    textView.setText(name + "\n\n" + nakaz);
                    //   text_text.setText(menu.getString("name"));
                }


            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("TAG", "JSON_PIZDEC");
            }


        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);

    }
}

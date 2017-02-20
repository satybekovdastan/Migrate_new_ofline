package com.migrate.admin.pagination.Activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.migrate.admin.pagination.Helpers.DataHelper;

import com.migrate.admin.pagination.R;
import com.migrate.admin.pagination.Serializables.Consulate;
import com.migrate.admin.pagination.Serializables.Diaspora;
import com.migrate.admin.pagination.Serializables.EAEU;
import com.migrate.admin.pagination.Serializables.Embassy;
import com.migrate.admin.pagination.Serializables.Employment;
import com.migrate.admin.pagination.Serializables.Hotline;
import com.migrate.admin.pagination.Serializables.Istories;
import com.migrate.admin.pagination.Serializables.NKO;
import com.migrate.admin.pagination.Serializables.RulesOfIncoming;
import com.pnikosis.materialishprogress.ProgressWheel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static com.migrate.admin.pagination.R.id.image;
import static com.migrate.admin.pagination.R.id.imageView;
import static com.migrate.admin.pagination.R.id.imagesss;


public class SyncActivity extends AppCompatActivity {
    DataHelper dataHelper;
    int kk=0;
    ProgressWheel progressBar;
   // ProgressBar progressBar;
    int lang;
    URL urlM;
    String date;
    LinearLayout linePovtor;
    TextView text;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync);
        dataHelper=new DataHelper(this);
        date=getIntent().getStringExtra("date");
        kk=getIntent().getIntExtra("kk",0);
        progressBar=(ProgressWheel) findViewById(R.id.progressBarOne);
        imageView = (ImageView) findViewById(R.id.imagesss);

        linePovtor = (LinearLayout) findViewById(R.id.linePovtor);
        text = (TextView) findViewById(R.id.textPovtor);
        text.setText("Пожалуйста, проверьте Ваше интернет - соединение");
        Cursor cursor=dataHelper.getDataLanguage();
        if (cursor.getCount()>0)
        {

            cursor.moveToFirst();
            lang=cursor.getInt(cursor.getColumnIndex(DataHelper.LANGUAGE_COLUMN));

        }
        else lang=0;

        ifConnect();

    }


    public void ifConnect(){


            ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED){
                new ParseTask().execute();
                Log.e("IFCONNECT","SSSSUUUUKKKAAA");
                progressBar.setVisibility(View.VISIBLE);
                linePovtor.setVisibility(View.GONE);
            }
            else {
                linePovtor.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                Toast.makeText(this,R.string.toast_no_internet,Toast.LENGTH_SHORT).show();

            }


    }

    public void onClickPovtor(View view) {
        ifConnect();

    }



    public class ParseTask extends AsyncTask<Void, Void, String> {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String jsonResult = "";

        @Override
        protected String doInBackground(Void... params) {
            Log.e("TAG_S",1+"");

            try {

                URL url = new URL("http://176.126.167.249/api/v1/country_eaes/?&limit=0&format=json");

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
                Log.e("TAG_S",3+"DELETE");

            } catch (Exception e) {
                e.printStackTrace();
                Log.e("TAG_S",1+"PIZDEC");
            }

            Log.e("TAG_S",4+"");
            return jsonResult;
        }

        @Override
        protected void onPostExecute(String json) {
            super.onPostExecute(json);

            JSONObject dataJsonObject;
            String secondName;

            try {
                dataJsonObject = new JSONObject(json);
                JSONArray menus = dataJsonObject.getJSONArray("objects");
                JSONObject meta=dataJsonObject.getJSONObject("meta");


                for (int i = 0; i < menus.length(); i++) {
                    JSONObject menu = menus.getJSONObject(i);
                    EAEU eaeu=new EAEU();
                    eaeu.setName(menu.getString("country"));
                    eaeu.setPicture(menu.getString("image"));
                    Log.e("HUMAN", eaeu.getPicture() );
                    eaeu.setId(menu.getString("id"));
                    if (i==0) dataHelper.deleteEAEU();
                    dataHelper.insertEAEU(eaeu);

                }




            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.e("TAG_1","NORM");

            new ParseTask1().execute();
            Log.e("TAGGGGG", 0+"");

        }
    }
    public class ParseTask1 extends AsyncTask<Void, Void, String> {



        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String jsonResult = "";


        @Override
        protected String doInBackground(Void... params) {


            try {
                if (lang==0) {
                    try {
                        urlM=new URL("http://176.126.167.249/api/v1/rules_of_incoming_eaes/?format=json&limit=0");
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }else {
                    try {
                        urlM=new URL("http://176.126.167.249/api/v1/rules_of_incoming_eaes_kg/?format=json&limit=0");
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }
                URL url = urlM;

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

            Log.e("TAG", json);
            JSONObject user;
            JSONObject dataJsonObject;
            String secondName;

            try {
                dataJsonObject = new JSONObject(json);
                JSONArray menus = dataJsonObject.getJSONArray("objects");
                JSONObject meta=dataJsonObject.getJSONObject("meta");



                for (int i = 0; i < menus.length(); i++) {
                    JSONObject menu = menus.getJSONObject(i);
                    RulesOfIncoming student = new RulesOfIncoming();


                    student.setText(menu.getString("text_ru"));
                    student.setTitle("   "+menu.getString("title_ru"));
                    JSONObject country=menu.getJSONObject("country");
                    String id=country.getString("id");
                    Log.e("TAG___ID",id);
                    if (i==0){dataHelper.deleteROI();
                    }
                    dataHelper.insertROI(student,id);






                }

            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("TAG","JSON_PIZDEC");
            }


            new ParseTask3().execute();
            Log.e("TAGGGGG", 1+"");
        }
    }

    public class ParseTask3 extends AsyncTask<Void, Void, String> {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String jsonResult = "";

        @Override
        protected String doInBackground(Void... params) {

            try {

                URL url = new URL("http://176.126.167.249/api/v1/country/?format=json");

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
            String secondName;

            try {
                dataJsonObject = new JSONObject(json);
                JSONArray menus = dataJsonObject.getJSONArray("objects");


                for (int i = 0; i < menus.length(); i++) {
                    JSONObject menu = menus.getJSONObject(i);
                    EAEU eaeu=new EAEU();
                    eaeu.setName(menu.getString("country"));
                    eaeu.setPicture(menu.getString("image"));
                    Log.e("HUMAN", eaeu.getPicture() );
                    eaeu.setId(menu.getString("id"));
                    if (i==0) dataHelper.deleteAbroad();
                    dataHelper.insertAbroad(eaeu);

                }




            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.e("TAG_1","NORM");

            new ParseTask4().execute();
            Log.e("TAGGGGG", 3+"");
        }
    }

    public class ParseTask4 extends AsyncTask<Void, Void, String> {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String jsonResult = "";

        @Override
        protected String doInBackground(Void... params) {

            try {
                if (lang==0) {
                    try {
                        urlM=new URL("http://176.126.167.249/api/v1/rules_of_incoming/?format=json&limit=0");
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }else {
                    try {
                        urlM=new URL("http://176.126.167.249/api/v1/rules_of_incoming_kg/?format=json&limit=0");
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }
                URL url = urlM;

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
                JSONArray menus = dataJsonObject.getJSONArray("objects");


                for (int i = 0; i < menus.length(); i++) {
                    JSONObject menu = menus.getJSONObject(i);
                    RulesOfIncoming student = new RulesOfIncoming();


                    student.setText(menu.getString("text_ru"));
                    student.setTitle(menu.getString("title_ru"));
                    if (i==0){dataHelper.deleteROM();
                        Log.e("TAG_NEWS","DELETE");
                    }
                    JSONObject country=menu.getJSONObject("country");
                    String id=country.getString("id");
                    dataHelper.insertROM(student,id);






                }



            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("TAG", "JSON_PIZDEC");
            }

new ParseTask5().execute();
            Log.e("TAGGGGG", 4+"");
        }
    }
    public class ParseTask5 extends AsyncTask<Void, Void, String> {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String jsonResult = "";

        @Override
        protected String doInBackground(Void... params) {
            Log.e("TAG_S",1+"");

            try {

                URL url = new URL("http://176.126.167.249/api/v1/country_diaspora/?format=json");

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
                Log.e("TAG_S",3+"DELETE");

            } catch (Exception e) {
                e.printStackTrace();
                Log.e("TAG_S",1+"PIZDEC");
            }

            Log.e("TAG_S",4+"");
            return jsonResult;
        }

        @Override
        protected void onPostExecute(String json) {
            super.onPostExecute(json);


            JSONObject dataJsonObject;
            String secondName;

            try {
                dataJsonObject = new JSONObject(json);
                JSONArray menus = dataJsonObject.getJSONArray("objects");
                JSONObject meta=dataJsonObject.getJSONObject("meta");


                for (int i = 0; i < menus.length(); i++) {
                    JSONObject menu = menus.getJSONObject(i);
                    EAEU eaeu=new EAEU();
                    eaeu.setName(menu.getString("country"));
                    eaeu.setPicture(menu.getString("image"));
                    Log.e("HUMAN", eaeu.getPicture() );
                    eaeu.setId(menu.getString("id"));
                    if (i==0) dataHelper.deleteDias();
                    dataHelper.insertDias(eaeu);

                }



            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.e("TAG_1","NORM");
            new ParseTask6().execute();
            Log.e("TAGGGGG", 5+"");


        }
    }
    public class ParseTask6 extends AsyncTask<Void, Void, String> {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String jsonResult = "";

        @Override
        protected String doInBackground(Void... params) {
            Log.e("TAG_S",1+"");

            try {

                URL url = new URL("http://176.126.167.249/api/v1/diaspora/?offset=0&limit=0&format=json");

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
            JSONObject country;
            try {
                dataJsonObject = new JSONObject(json);
                JSONArray menus = dataJsonObject.getJSONArray("objects");


                for (int i = 0; i < menus.length(); i++) {
                    JSONObject menu = menus.getJSONObject(i);
                    Diaspora student = new Diaspora();

                    if (i==0){dataHelper.deleteDiaspora();
                    }


                    student.setNumber(menu.getString("phone_number"));

                    student.setCity(menu.getString("city"));
                    student.setManager(menu.getString("manager"));
                    student.setAddress(menu.getString("address"));
                    student.setEmail(menu.getString("email"));
                    student.setPlace(menu.getString("place"));
                    student.setKarta(menu.getString("map_link"));

                    country=menu.getJSONObject("country");
                    String id=country.getString("id");
                    Log.e("TAG___ID",id);

                    dataHelper.insertDiaspora(student,id);






                }



            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("df", "JSON_PIZDEC");
            }
        new ParseTask7().execute();
            Log.e("TAGGGGG", 6+"");
        }
    }
    public class ParseTask7 extends AsyncTask<Void, Void, String> {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String jsonResult = "";

        @Override
        protected String doInBackground(Void... params) {
            Log.e("TAG_S",1+"");

            try {

                URL url = new URL("http://176.126.167.249/api/v1/embassy/?limit=0&format=json");

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
                JSONArray menus = dataJsonObject.getJSONArray("objects");
                JSONObject meta=dataJsonObject.getJSONObject("meta");



                for (int i = 0; i < menus.length(); i++) {
                    JSONObject menu = menus.getJSONObject(i);
                    Embassy student = new Embassy();
                    student.setPhoneNumber(menu.getString("phone_number"));
                    student.setCountry(menu.getString("country"));
                    student.setEmail(menu.getString("email"));
                    student.setFax(menu.getString("fax"));
                    student.setId(menu.getString("id"));
                    student.setRegion(menu.getString("address"));
                    student.setSite(menu.getString("site"));
                    student.setImage(menu.getString("image"));
                    Log.e("HUMAN", student.getImage() );
                    student.setKarta(menu.getString("map_link"));
                    Log.e("TAG_NEWSssss",student.getKarta());
                    if (i==0){dataHelper.deleteEmbassy();
                        Log.e("TAG_NEWS","DELETE");
                    }
                    dataHelper.insertEmbassy(student);





                }



            } catch (JSONException e) {
                e.printStackTrace();
            }


            new ParseTask8().execute();
            Log.e("TAGGGGG", 7+"");

        }
    }

    public class ParseTask8 extends AsyncTask<Void, Void, String> {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String jsonResult = "";

        @Override
        protected String doInBackground(Void... params) {

            try {

                URL url = new URL("http://176.126.167.249/api/v1/consulate/?limit=0&format=json");

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
                Log.e("TAG_S",3+"DELETE");

            } catch (Exception e) {
                e.printStackTrace();
                Log.e("TAG_S",1+"PIZDEC");
            }

            Log.e("TAG_S",4+"");
            return jsonResult;
        }

        @Override
        protected void onPostExecute(String json) {
            super.onPostExecute(json);


            JSONObject dataJsonObject;
            String secondName;

            try {
                dataJsonObject = new JSONObject(json);
                JSONArray menus = dataJsonObject.getJSONArray("objects");
                JSONObject meta=dataJsonObject.getJSONObject("meta");
                dataHelper.deleteConsulate();

                for (int i = 0; i < menus.length(); i++) {
                    JSONObject menu = menus.getJSONObject(i);
                    Consulate student = new Consulate();
                    student.setPhoneNumber(menu.getString("phone_number"));

                    student.setRegion(menu.getString("region"));

                    student.setAddress(menu.getString("address"));
                    student.setKarta(menu.getString("map_link"));


                    if (i==0){
                        Log.e("TAG_NEWS","DELETE");
                    }
                    JSONObject country=menu.getJSONObject("embassy");
                    String id=country.getString("id");
                    dataHelper.insertConsulate(student,id);






                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
          new ParseTask9().execute();
            Log.e("TAGGGGG", 8+"");
        }
    }
    public class ParseTask9 extends AsyncTask<Void, Void, String> {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String jsonResult = "";

        @Override
        protected String doInBackground(Void... params) {

            try {

                URL url = new URL("http://176.126.167.249/api/v1/country_employment/?limit=0&format=json");

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
            String secondName;

            try {
                dataJsonObject = new JSONObject(json);
                JSONArray menus = dataJsonObject.getJSONArray("objects");


                for (int i = 0; i < menus.length(); i++) {
                    Log.e("!!!!",i+"");
                    JSONObject menu = menus.getJSONObject(i);
                    EAEU eaeu=new EAEU();
                    eaeu.setName(menu.getString("country"));
                    eaeu.setPicture(menu.getString("image"));
                    Log.e("HUMAN", eaeu.getPicture() );
                    eaeu.setId(menu.getString("id"));
                    if (i==0) dataHelper.deleteEmploy();
                    dataHelper.insertEmploy(eaeu);

                }



            } catch (JSONException e) {
                e.printStackTrace();
            }

            new ParseTask10().execute();
            Log.e("TAGGGGG", 9+"");

        }
    }
    public class ParseTask10 extends AsyncTask<Void, Void, String> {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String jsonResult = "";

        @Override
        protected String doInBackground(Void... params) {

            try {

                URL url = new URL("http://176.126.167.249/api/v1/employment/?limit=0&format=json");

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


            JSONObject user;
            JSONObject dataJsonObject;
            String secondName;

            try {
                dataJsonObject = new JSONObject(json);
                JSONArray menus = dataJsonObject.getJSONArray("objects");
                JSONObject meta=dataJsonObject.getJSONObject("meta");

                for (int i = 0; i < menus.length(); i++) {
                    JSONObject menu = menus.getJSONObject(i);
                    Employment student = new Employment();

                    if (i==0){dataHelper.deleteEmployment();
                    }


                    student.setPhone_number(menu.getString("phone_number"));
                    student.setPhone_number1(menu.getString("phone_number_1"));
                    student.setPhone_number2(menu.getString("phone_number_2"));
                    student.setName(menu.getString("name"));
                    student.setManager(menu.getString("manager"));
                    student.setAdress(menu.getString("address"));
                    JSONObject country=menu.getJSONObject("country");
                    String id=country.getString("id");


                    dataHelper.insertEmployment(student,id);







                }



            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("TAG", "JSON_PIZDEC");
            }

new ParseTask11().execute();
            Log.e("TAGGGGG", 10+"");
        }
    }
    public class ParseTask11 extends AsyncTask<Void, Void, String> {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String jsonResult = "";

        @Override
        protected String doInBackground(Void... params) {

            try {
                if (lang==0) {
                    try {

                        urlM=new URL("http://176.126.167.249/api/v1/faq/?format=json&limit=0");
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }else {
                    try {

                        urlM=new URL("http://176.126.167.249/api/v1/faq_kg/?format=json&limit=0");
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }
                URL url = urlM;

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
            String secondName;

            try {
                dataJsonObject = new JSONObject(json);
                JSONArray menus = dataJsonObject.getJSONArray("objects");
                JSONObject meta=dataJsonObject.getJSONObject("meta");


                for (int i = 0; i < menus.length(); i++) {
                    JSONObject menu = menus.getJSONObject(i);
                    Istories student = new Istories();
                    student.setText(menu.getString("answer_ru"));
                    student.setNickName(menu.getString("question_ru"));

                    if (i==0){dataHelper.deleteFAQ();
                    }
                    dataHelper.insertFAQ(student);





                }



            } catch (JSONException e) {
                e.printStackTrace();
            }
    new ParseTask12().execute();
            Log.e("TAGGGGG", 11+"");
        }
    }

    public class ParseTask12 extends AsyncTask<Void, Void, String> {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String jsonResult = "";

        @Override
        protected String doInBackground(Void... params) {
            Log.e("TAG_S",1+"");

            try {

                URL url = new URL("http://176.126.167.249/api/v1/country_hotline/?&limit=0&format=json");

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
                Log.e("TAG_S",3+"DELETE");

            } catch (Exception e) {
                e.printStackTrace();
                Log.e("TAG_S",1+"PIZDEC");
            }

            Log.e("TAG_S",4+"");
            return jsonResult;
        }

        @Override
        protected void onPostExecute(String json) {
            super.onPostExecute(json);

            JSONObject dataJsonObject;
            String secondName;

            try {
                dataJsonObject = new JSONObject(json);
                JSONArray menus = dataJsonObject.getJSONArray("objects");
                JSONObject meta=dataJsonObject.getJSONObject("meta");


                for (int i = 0; i < menus.length(); i++) {
                    JSONObject menu = menus.getJSONObject(i);
                    EAEU eaeu=new EAEU();
                    eaeu.setName(menu.getString("country"));
                    eaeu.setPicture(menu.getString("image"));
                    Log.e("HUMAN", eaeu.getPicture() );
                    eaeu.setId(menu.getString("id"));
                    if (i==0) dataHelper.deleteHotCountry();
                    dataHelper.insertHotCountry(eaeu);

                }




            } catch (JSONException e) {
                e.printStackTrace();
            }


            new ParseTask13().execute();
            Log.e("TAGGGGG", 12+"");

        }
    }
    public class ParseTask13 extends AsyncTask<Void, Void, String> {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String jsonResult = "";

        @Override
        protected String doInBackground(Void... params) {

            try {

                URL url = new URL("http://176.126.167.249/api/v1/hotline/?limit=0&format=json");

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

            Log.e("TAG_S",4+"");
            return jsonResult;
        }

        @Override
        protected void onPostExecute(String json) {
            super.onPostExecute(json);


            JSONObject dataJsonObject;
            String secondName;

            try {
                dataJsonObject = new JSONObject(json);
                JSONArray menus = dataJsonObject.getJSONArray("objects");
                JSONObject meta=dataJsonObject.getJSONObject("meta");


                for (int i = 0; i < menus.length(); i++) {
                    JSONObject menu = menus.getJSONObject(i);
                    Hotline student = new Hotline();

                    student.setTitle(menu.getString("title_ru"));
                    student.setPhoneNumber(menu.getString("phone_number"));
                    JSONObject country=menu.getJSONObject("country");
                    String id=country.getString("id");
                    if (i==0){dataHelper.deleteHot();
                    }
                    dataHelper.insertHot(student,id);






                }



            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("TAG", "JSON_PIZDEC");
            }
           new ParseTask14().execute();
            Log.e("TAGGGGG", 13+"");
        }
    }
    public class ParseTask14 extends AsyncTask<Void, Void, String> {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String jsonResult = "";


        @Override
        protected String doInBackground(Void... params) {

            try {


                if (lang==0) {
                    try {
                        urlM=new URL("http://176.126.167.249/api/v1/human_traffic/?format=json&limit=0");
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }else {
                    try {
                        urlM=new URL("http://176.126.167.249/api/v1/human_traffic_kg/?format=json&limit=0");
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }

                URL url = urlM;

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
                JSONArray menus = dataJsonObject.getJSONArray("objects");
                JSONObject meta=dataJsonObject.getJSONObject("meta");


                for (int i = 0; i < menus.length(); i++) {
                    JSONObject menu = menus.getJSONObject(i);
                    RulesOfIncoming student = new RulesOfIncoming();

                    student.setImage("http://176.126.167.249/"+menu.getString("image"));
                    Log.e("HUMAN", student.getImage() );
                    student.setText(menu.getString("text_ru"));
                    student.setTitle(menu.getString("title_ru"));
                    if (i==0){dataHelper.deleteHT();
                    }
                    dataHelper.insertHT(student);





                }



            } catch (JSONException e) {
                e.printStackTrace();
            }
          new ParseTask15().execute();
            Log.e("TAGGGGG", 14+"");
        }
    }

    public class ParseTask15 extends AsyncTask<Void, Void, String> {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String jsonResult = "";

        @Override
        protected String doInBackground(Void... params) {

            try {
                if (lang==0) {
                    try {
                        urlM=new URL("http://176.126.167.249/api/v1/news/?format=json&limit=0");
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }else {
                    try {
                        urlM=new URL("http://176.126.167.249/api/v1/news_kg/?format=json&limit=0");
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }

                URL url = urlM;

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
                JSONArray menus = dataJsonObject.getJSONArray("objects");
                JSONObject meta=dataJsonObject.getJSONObject("meta");


                for (int i = 0; i < menus.length(); i++) {
                    JSONObject menu = menus.getJSONObject(i);
                    Istories student = new Istories();
                    student.setImage(menu.getString("image"));
                    Log.e("HUMAN", student.getImage() );
                    student.setText(menu.getString("text_ru"));
                    student.setNickName(menu.getString("title_ru"));

                    if (i==0){dataHelper.delete();
                    }

                    dataHelper.insertNews(student);



                }

            } catch (JSONException e) {
                e.printStackTrace();



            }
           new ParseTask16().execute();
            Log.e("TAGGGGG", 15+"");
        }
    }
    public class ParseTask16 extends AsyncTask<Void, Void, String> {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String jsonResult = "";

        @Override
        protected String doInBackground(Void... params) {
            Log.e("TAG_S",1+"");

            try {

                URL url = new URL("http://176.126.167.249/api/v1/region/?&limit=0&format=json");

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
                Log.e("TAG_S",3+"DELETE");

            } catch (Exception e) {
                e.printStackTrace();
                Log.e("TAG_S",1+"PIZDEC");
            }

            Log.e("TAG_S",4+"");
            return jsonResult;
        }

        @Override
        protected void onPostExecute(String json) {
            super.onPostExecute(json);

            JSONObject dataJsonObject;
            String secondName;

            try {
                dataJsonObject = new JSONObject(json);
                JSONArray menus = dataJsonObject.getJSONArray("objects");
                JSONObject meta=dataJsonObject.getJSONObject("meta");


                for (int i = 0; i < menus.length(); i++) {
                    JSONObject menu = menus.getJSONObject(i);
                    EAEU eaeu=new EAEU();
                    eaeu.setName(menu.getString("name"));

                    eaeu.setId(menu.getString("id"));
                    if (i==0) dataHelper.deleteNKOCountry();
                    dataHelper.insertNKOCountry(eaeu);

                }




            } catch (JSONException e) {
                e.printStackTrace();
            }


            new ParseTask17().execute();
            Log.e("TAGGGGG", 16+"");

        }
    }
    public class ParseTask17 extends AsyncTask<Void, Void, String> {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String jsonResult = "";

        @Override
        protected String doInBackground(Void... params) {

            try {

                if (lang==0) {
                    try {
                        urlM=new URL("http://176.126.167.249/api/v1/nko/?format=json&limit=0");
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }else {
                    try {
                        urlM=new URL("http://176.126.167.249/api/v1/nko_kg/?format=json&limit=0");
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }
                URL url = urlM;

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

            Log.e("TAG_S",4+"");
            return jsonResult;
        }

        @Override
        protected void onPostExecute(String json) {
            super.onPostExecute(json);


            JSONObject dataJsonObject;
            String secondName;

            try {
                dataJsonObject = new JSONObject(json);
                JSONArray menus = dataJsonObject.getJSONArray("objects");
                JSONObject meta=dataJsonObject.getJSONObject("meta");


                for (int i = 0; i < menus.length(); i++) {
                    JSONObject menu = menus.getJSONObject(i);
                    NKO student = new NKO();
                    student.setText(menu.getString("text_ru"));
                    student.setTitle(menu.getString("title_ru"));
                    student.setPhone(menu.getString("phone_number"));
                    student.setAddress(menu.getString("address"));
                    student.setMail(menu.getString("mail"));
                    student.setManager(menu.getString("manager"));
                    student.setPhone1(menu.getString("phone_number_1"));

                    JSONObject country=menu.getJSONObject("region");
                    String id=country.getString("id");
                    if (i==0){dataHelper.deleteNKO();
                    }
                    dataHelper.insertNKO(student,id);






                }



            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("TAG", "JSON_PIZDEC");
            }
            new ParseTask18().execute();
            Log.e("TAGGGGG", 17+"");
        }
    }

    public class ParseTask18 extends AsyncTask<Void, Void, String> {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String jsonResult = "";

        @Override
        protected String doInBackground(Void... params) {

            try {
                if (lang==0) {
                    try {
                        urlM=new URL("http://176.126.167.249/api/v1/rf/?format=json&limit=0");
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }else {
                    try {
                        urlM=new URL("http://176.126.167.249/api/v1/rf_kg/?format=json&limit=0");
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }
                URL url = urlM;

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

            JSONObject user;
            JSONObject dataJsonObject;
            String secondName;

            try {
                dataJsonObject = new JSONObject(json);
                JSONArray menus = dataJsonObject.getJSONArray("objects");
                JSONObject meta=dataJsonObject.getJSONObject("meta");


                for (int i = 0; i < menus.length(); i++) {
                    JSONObject menu = menus.getJSONObject(i);
                    RulesOfIncoming student = new RulesOfIncoming();

                    student.setImage("http://176.126.167.249/"+menu.getString("image"));
                    Log.e("HUMAN", student.getImage() );


                    student.setText(menu.getString("text_ru"));
                    student.setTitle(menu.getString("title_ru"));
                    if (i==0){dataHelper.deleteProhibition();
                    }
                    dataHelper.insertProhibition(student);



                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.e("TAGGGGG", 18+"");
            progressBar.setVisibility(View.GONE);
            if (kk==0)
            dataHelper.insertDate(date);
            startActivity(new Intent(SyncActivity.this,MainActivity.class));
            finish();



        }


    }

    final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            RulesOfIncoming student = new RulesOfIncoming();
          Log.e("asd", student.getImage());
        }
    };


    @Override
    public void onBackPressed() {
        moveTaskToBack(true);

        Toast.makeText(SyncActivity.this, "Не выходите пока все файлы не скачаются", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}

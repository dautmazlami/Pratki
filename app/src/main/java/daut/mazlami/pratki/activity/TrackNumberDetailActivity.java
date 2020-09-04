package daut.mazlami.pratki.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

import daut.mazlami.pratki.R;
import daut.mazlami.pratki.adapter.TrackNrDetailsAdapter;
import daut.mazlami.pratki.adapter.TrackingDataAdapter;
import daut.mazlami.pratki.fragment.MyDeliveriesFragment;
import daut.mazlami.pratki.fragment.TrackingFragment;
import daut.mazlami.pratki.model.TrackingData;
import fr.arnaudguyon.xmltojsonlib.XmlToJson;

import static daut.mazlami.pratki.fragment.TrackingFragment.UNSUCCESFUL;

public class TrackNumberDetailActivity extends AppCompatActivity {

    public static final String TRACKNUMBER = "track_number";
    public static final String UNSUCCESFUL = "Unsuccesful";

    private RecyclerView recyclerView;
    private TrackNrDetailsAdapter adapter;
    private ArrayList<TrackingData> list = new ArrayList<>();
    TrackingData trackingData;
    ProgressBar progressBar;

    TextView textView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trackingnumber_details);

        textView = findViewById(R.id.saved_tracking_number);
        recyclerView = findViewById(R.id.recyclerview_tracking_details);
        recyclerView.setLayoutManager(new LinearLayoutManager(TrackNumberDetailActivity.this));
        adapter = new TrackNrDetailsAdapter(TrackNumberDetailActivity.this,list);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        progressBar = findViewById(R.id.progressBar2);

        new xmlParsing().execute(textView.getText().toString());

    }

        public class xmlParsing extends AsyncTask<String, String, String> {

        HttpURLConnection httpURLConnection;
        URL url;
        JSONObject jsonObject;

        @Override
        protected String doInBackground(String... params) {
            try {
                url = new URL("https://www.posta.com.mk/tnt/api/query?id=" + getIntent().getStringExtra("tracking_number"));
                //getArguments().getString("Code") + "");

                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setReadTimeout(15000);
                httpURLConnection.setConnectTimeout(10000);
                httpURLConnection.setRequestMethod("GET");

            } catch (IOException e) {
                e.getMessage();
                e.printStackTrace();
            }
            try {
                int response_code = httpURLConnection.getResponseCode();
                if (response_code == HttpURLConnection.HTTP_OK) {

                    InputStream input = httpURLConnection.getInputStream();
                    String result = convertStreamToString(input);

                    XmlToJson xmlToJson = new XmlToJson.Builder(result).build();
                    jsonObject = xmlToJson.toJson();
                    Log.d("Track", result);
                    return result;

                } else {

                    return UNSUCCESFUL;
                }

            } catch (IOException e) {
                e.printStackTrace();

                return e.toString();

            } finally {
                httpURLConnection.disconnect();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if ((result == UNSUCCESFUL) || (result.startsWith("<ArrayOfTrackingData></ArrayOfTrackingData"))) {
                Toast.makeText(TrackNumberDetailActivity.this, R.string.no_information_found_for_this_track_number, Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);

            } else {
                ArrayList<TrackingData> dataList = new ArrayList<>();
                try {
                    JSONObject value = jsonObject.getJSONObject("ArrayOfTrackingData");
                    JSONArray jsonArray = new JSONArray();

                    JSONObject trackingObject = value.optJSONObject("TrackingData");

                    if (trackingObject == null) {
                        jsonArray = value.getJSONArray("TrackingData");

                    } else {
                        jsonArray.put(value.getJSONObject("TrackingData"));
                    }

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject json_data = jsonArray.getJSONObject(i);

                        dataList.add(new TrackingData(json_data.getString("Notice"),
                                json_data.getString("Begining"),
                                json_data.getString("End"),
                                json_data.getString("ID"),
                                json_data.getString("Date")));

                    }

                    adapter = new TrackNrDetailsAdapter(TrackNumberDetailActivity.this,list);
                    recyclerView.setAdapter(adapter);
                    Collections.reverse(dataList);
                    progressBar.setVisibility(View.GONE);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    public static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while((line = reader.readLine()) != null){
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}

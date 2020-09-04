package daut.mazlami.pratki.fragment;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

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
import java.util.Collection;
import java.util.Collections;

import daut.mazlami.pratki.R;
import daut.mazlami.pratki.adapter.TrackingDataAdapter;
import daut.mazlami.pratki.model.TrackingData;
import fr.arnaudguyon.xmltojsonlib.XmlToJson;
import okhttp3.OkHttpClient;

/**
 * A simple {@link Fragment} subclass.
 */
public class TrackingFragment extends Fragment {

    public static final String TAG = TrackingFragment.class.getSimpleName();
    public static final String UNSUCCESFUL = "Unsuccesful";

    private RecyclerView recyclerView;
    private TrackingDataAdapter adapter;
    private ArrayList<TrackingData> list = new ArrayList<>();
    private OkHttpClient client;

    EditText trackNumber;
    Button searchButton;
    ProgressBar progressBar;
    private View view;
    SharedPreferences prefs;
    TextView textViewConnectionFailed;

    public static TrackingFragment newInstance(Bundle bundle) {
        TrackingFragment trackingFragment = new TrackingFragment();
        trackingFragment.setArguments(bundle);
        return trackingFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tracking, container, false);

        recyclerView = view.findViewById(R.id.recyclerview_tracking);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TrackingDataAdapter(getActivity(),list);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);

        progressBar = view.findViewById(R.id.progressBar);
        trackNumber = view.findViewById(R.id.trackNumber_editText);
        searchButton = view.findViewById(R.id.searchButton);
        textViewConnectionFailed = view.findViewById(R.id.connection_failed_txt_view);

        prefs = getActivity().getSharedPreferences("prefs",Context.MODE_PRIVATE);
        trackNumber.setText(prefs.getString("tracking_number",null));

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new xmlParsing().execute(trackNumber.getText().toString());
                progressBar.setVisibility(View.VISIBLE);
                prefs = getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("tracking_number",trackNumber.getText().toString());
                editor.commit();

            }
        });

        client = new OkHttpClient();
        new xmlParsing();

        return view;
    }

    public class xmlParsing extends AsyncTask<String,String,String>{

        HttpURLConnection httpURLConnection;
        URL url;
        JSONObject jsonObject;

       @Override
       protected String doInBackground(String... params) {
           try {
               url = new URL("https://www.posta.com.mk/tnt/api/query?id="+ params[0]);
                       //getArguments().getString("Code") + "");

               httpURLConnection = (HttpURLConnection)  url.openConnection();
               httpURLConnection.setReadTimeout(15000);
               httpURLConnection.setConnectTimeout(10000);
               httpURLConnection.setRequestMethod("GET");

           } catch (IOException e) {
               e.getMessage();
               e.printStackTrace();
           }
           try {
               int response_code = httpURLConnection.getResponseCode();
               if (response_code == HttpURLConnection.HTTP_OK){

                   InputStream input = httpURLConnection.getInputStream();
                    String result = convertStreamToString(input);

                   XmlToJson xmlToJson = new XmlToJson.Builder(result).build();
                   jsonObject = xmlToJson.toJson();
                   Log.d("Track",result);
                   return result;

               }else{

                   return UNSUCCESFUL;
               }

           } catch (IOException e) {
               e.printStackTrace();

               return e.toString();

           }finally {
               httpURLConnection.disconnect();
           }
       }

       @Override
       protected void onPostExecute(String result) {
           super.onPostExecute(result);
           if ((result == UNSUCCESFUL) || (result.startsWith("<ArrayOfTrackingData></ArrayOfTrackingData"))){
               Toast.makeText(getContext(), R.string.no_information_found_for_this_track_number, Toast.LENGTH_SHORT).show();
               progressBar.setVisibility(View.GONE);

           } else{
               ArrayList<TrackingData> dataList = new ArrayList<>();
               try {
                   JSONObject value = jsonObject.getJSONObject("ArrayOfTrackingData");
                   JSONArray jsonArray = new JSONArray();

                   JSONObject trackingObject = value.optJSONObject("TrackingData");

                   if (trackingObject == null){
                       jsonArray = value.getJSONArray("TrackingData");

                   }else{
                       jsonArray.put(value.getJSONObject("TrackingData"));
                   }

                   for (int i = 0; i <jsonArray.length();i++){
                       JSONObject json_data = jsonArray.getJSONObject(i);

                       dataList.add(new TrackingData(json_data.getString("Notice"),
                               json_data.getString("Begining"),
                               json_data.getString("End"),
                               json_data.getString("ID"),
                               json_data.getString("Date")));

                   }

                   adapter = new TrackingDataAdapter(getContext(),dataList);
                   recyclerView.setAdapter(adapter);
                   Collections.reverse(dataList);
                   progressBar.setVisibility(View.GONE);

               } catch (JSONException e) {
                   e.printStackTrace();
               }

           }
       }

        @Override
        protected void onCancelled(String s) {
            super.onCancelled(s);
            if (s == UNSUCCESFUL){
                textViewConnectionFailed.setVisibility(View.VISIBLE);
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

package daut.mazlami.pratki.fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import daut.mazlami.pratki.R;
import daut.mazlami.pratki.activity.MainActivity;
import daut.mazlami.pratki.model.PostLocation;
import daut.mazlami.pratki.utils.Constans;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static androidx.core.content.ContextCompat.getSystemService;
import static daut.mazlami.pratki.fragment.PostLocationFragment.PERMISSIONS_REQUEST_ENABLE_GPS;
import static daut.mazlami.pratki.utils.Constans.ERROR_DIALOG_REQUEST;
import static daut.mazlami.pratki.utils.Constans.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;

public class PostLocationsFragment extends Fragment implements OnMapReadyCallback {

    public static final String TAG = PostLocationsFragment.class.getSimpleName();
    public static final String LOG_TAG = "PostLocations";
    private GoogleMap mMap;
    public static final String url = "https://posta.com.mk/locations_en/locations.php";
    private boolean mLocationPermissionsGranted = false;
    private FusedLocationProviderClient fusedLocationProviderClient;

    OkHttpClient okHttpClient;
    Gson gson;
    List<PostLocation> locationList = new ArrayList<>();

    public static final int TILT_LEVEL = 0;
    public static final int BEARING_LEVEL = 0;
    public static final float DEFAULT_ZOOM = 20f;

    public static PostLocationsFragment newInstance(Bundle bundle) {
        PostLocationsFragment postLocationsFragment = new PostLocationsFragment();
        postLocationsFragment.setArguments(bundle);
        return postLocationsFragment;
    }

    private final OnMapReadyCallback callback = new OnMapReadyCallback() {

        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;
            addMarkersFromApi();
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_post_locations_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
        okHttpClient = new OkHttpClient();
        gson = new Gson();
        addMarkersFromApi();

    }

    private String loadJsonFromAssets(){
        String json;
        try {
            InputStream is = getActivity().getAssets().open("postlocations.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer,"UTF-8");

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return json;
    }

    private void addMarkersFromJson() throws JSONException {
        String json = loadJsonFromAssets();
        JSONArray jsonArray = new JSONArray(json);
        for (int i = 0; i < jsonArray.length() ; i++){
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            mMap.addMarker(new MarkerOptions()
            .title(jsonObject.getString("name"))
            .snippet(jsonObject.getString("zip"))
            .position(new LatLng(
                    jsonObject.getJSONArray("latitude").getDouble(0),
                    jsonObject.getJSONArray("longitude").getDouble(1)
            )));
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        addMarkersFromApi();
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
    }



    //-------------------- Google Services, GPS, and Location Permissions---------------------------

    private boolean checkMapServices(){
        if(isServicesOK()){
            if(isMapsEnabled()){
                return true;
            }
        }
        return false;
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("This application requires GPS to work properly, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGpsIntent, Constans.PERMISSIONS_REQUEST_ENABLE_GPS);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public boolean isMapsEnabled(){
        final LocationManager manager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
            return false;
        }
        return true;
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionsGranted = true;

        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    public boolean isServicesOK(){
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getContext());

        if(available == ConnectionResult.SUCCESS){
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(getContext(), "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionsGranted= false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionsGranted = true;
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: called.");
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ENABLE_GPS: {
                if(mLocationPermissionsGranted){
                    try {
                        addMarkersFromJson();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    getLocationPermission();
                }
            }
        }

    }

    // add markers on map from api request

    public void addMarkersFromApi() {
        Request request = new Request.Builder().url(url).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                e.getMessage();

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String jsonString = response.body().string();
                 Type listType = new TypeToken<List<PostLocation>>(){}.getType();
                List<PostLocation> postLocations = gson.fromJson(jsonString,listType);

                if (postLocations != null){

                    for (int i = 0; i<postLocations.size(); i++){
                        locationList.add(postLocations.get(i));

                        final int finalI = i;
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                /*CameraPosition cameraPosition = new CameraPosition(new LatLng(locationList.get(finalI).getLatitude(),locationList.get(finalI).getLongitude()),DEFAULT_ZOOM,TILT_LEVEL,BEARING_LEVEL);
                                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition)); */

                                mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(locationList.get(finalI).getLatitude(),locationList.get(finalI).getLongitude()))
                                        .title(locationList.get(finalI).getCity())
                                        .snippet(locationList.get(finalI).getZip())
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                            }
                        });

                    }

                }else{
                    Toast.makeText(getContext(), "Failed to load nearby post offices", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
}


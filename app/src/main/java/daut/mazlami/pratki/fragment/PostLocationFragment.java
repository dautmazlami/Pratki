package daut.mazlami.pratki.fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import daut.mazlami.pratki.R;
import daut.mazlami.pratki.activity.MainActivity;
import daut.mazlami.pratki.model.PostLocation;
import daut.mazlami.pratki.model.PostLocationResponse;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PostLocationFragment extends Fragment implements OnMapReadyCallback {

    public static final String TAG = PostLocationFragment.class.getSimpleName();

    private GoogleMap mMap;
    private CameraPosition cameraPosition;
    private FusedLocationProviderClient fusedLocationProviderClient;

    public static final int PERMISSIONS_REQUEST_CODE = 1;
    public static final int PERMISSIONS_REQUEST_ENABLE_GPS = 2;
    private boolean mLocationPermissionGranted = false;

    private Location mLastKnownLocation;
    public static final float DEFAULT_ZOOM = 15f;
    public static final int TILT_LEVEL = 0;
    public static final int BEARING_LEVEL = 0;

    public static final String KEY_CAMERA_POSITION = "camera_position";
    public static final String KEY_LOCATION = "location";

    private EditText mSearchText;
    private ImageButton myLocationButton;

    List<PostLocation> locationList = new ArrayList<>();
    private PlacesClient placesClient;
    Gson gson;
    String url;
    OkHttpClient client;

    public static PostLocationFragment newInstance(Bundle bundle) {
        PostLocationFragment postLocationFragment = new PostLocationFragment();
        postLocationFragment.setArguments(bundle);
        return postLocationFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_location, container, false);

        if (savedInstanceState != null){
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        mSearchText = view.findViewById(R.id.google_map_search);
        myLocationButton = (ImageButton) view.findViewById(R.id.icon_my_location);
        gson = new Gson();
        url = "https://posta.com.mk/locations_en/locations.php";
        client = new OkHttpClient();

        requestPermissions();
        getDeviceLocation();
        addMarkersFromJson();
        initMap();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(PostLocationFragment.this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (mLocationPermissionGranted ){
             if (ActivityCompat.checkSelfPermission(getContext(),Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(),Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED){
                return;
            }

            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            addMarkersFromJson();
            init();
        }

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

    private void addMarkersFromJson(){
        String jsonString = loadJsonFromAssets();
        Type listType = new TypeToken<List<PostLocation>>(){}.getType();
        List<PostLocation> postLocation = gson.fromJson(jsonString,listType);

        for (int i = 0; i < postLocation.size(); i++){
            createMarker(postLocation.get(i).getLatitude(),postLocation.get(i).getLongitude()
            ,postLocation.get(i).getName(),postLocation.get(i).getZip());

            locationList.add(postLocation.get(i));

        }

           /* for (int i = 0 ; i < postLocation.size(); i++ ){
            locationList.add(postLocation.get(i));

            MarkerOptions marker = new MarkerOptions();

           if (i == 0){
                CameraPosition cameraPosition = new CameraPosition(new LatLng(Double.parseDouble(locationList.get(i).getLongitude())
                        ,Double.parseDouble(locationList.get(i).getLatitude())),DEFAULT_ZOOM,TILT_LEVEL,BEARING_LEVEL);
                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            } 
            mMap.addMarker(new MarkerOptions()
            .position(new LatLng(Double.parseDouble(locationList.get(i).getLatitude()),Double.parseDouble(locationList.get(i).getLongitude())))
            .title(locationList.get(i).getName())
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
            mMap.addMarker(marker);
        } */
    }

    protected Marker createMarker(double latitude, double longtitude, String title, String snnipet){
        return mMap.addMarker(new MarkerOptions()
        .position(new LatLng(latitude,longtitude))
        .title(title)
        .snippet(snnipet)
        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
    }

    private void requestPermissions() {
        String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};

        if (ContextCompat.checkSelfPermission(getContext().getApplicationContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(getContext().getApplicationContext()
                    , Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(getActivity(), permissions, PERMISSIONS_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(getActivity(), permissions, PERMISSIONS_REQUEST_CODE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (checkMapServices()){
            if (mLocationPermissionGranted){
                initMap();
            }
            else {
                requestPermissions();
            }
        }
    }

    private boolean checkMapServices(){
        if (isMapsEnabled()){
            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;

        switch(requestCode){
            case PERMISSIONS_REQUEST_CODE:{
                if (grantResults.length > 0){
                    for (int i = 0; i< grantResults.length; i++){
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionGranted = false;
                            return;
                        }
                    }
                    mLocationPermissionGranted = true;
                }
            }
        }
    }

    private void initMap(){
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(PostLocationFragment.this);

        }

    }

    private void getDeviceLocation(){
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        try {
            if (mLocationPermissionGranted){
                final Task location = fusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()){
                            Log.d(TAG,"OnComplete: found location");
                            Location currentLocation = (Location) task.getResult();
                            if (currentLocation != null){
                                moveCamera(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude())
                                        ,DEFAULT_ZOOM,"My Location");
                            }

                        }else {
                            Log.d(TAG,"OnComplete: current location is null");
                            Toast.makeText(getContext(), "could find location", Toast.LENGTH_SHORT).show();
                            buildAlertMessageNoGps();
                        }
                    }
                });
            }

        }catch (SecurityException e){
            Log.e(TAG,"getDeviceLocation: SecurityExeption " + e.getMessage());
        }
    }

    private void moveCamera(LatLng latLng,float zoom, String title){
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        if (!title.equals("My Location")){
            MarkerOptions options =  new MarkerOptions()
                    .position(latLng)
                    .title(title);
            mMap.addMarker(options);
        }

    }

    private void init(){

        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                || actionId == EditorInfo.IME_ACTION_DONE
                    || event.getAction() == KeyEvent.ACTION_DOWN
                        || event.getAction() == KeyEvent.KEYCODE_ENTER){
                    geoLocate();
                }
                return false;
            }
        });
        myLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDeviceLocation();
            }
        });
    }

    private void geoLocate() {

        String searchString = mSearchText.getText().toString();
        Geocoder geocoder = new Geocoder(getContext());
        List<Address> list = new ArrayList<>();

        try {
            list = geocoder.getFromLocationName(searchString,5);

        }catch (IOException e){
            Log.e(TAG,"geoLocate: IOexeption" + e.getMessage());

        }
        if (list.size() > 0){
            Address address = list.get(0);
            moveCamera(new LatLng(address.getLatitude(),address.getLongitude()),DEFAULT_ZOOM,address.getAddressLine(0));

        }

    }

    private void buildAlertMessageNoGps(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("this application requires GPS to work properly, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent enableGpsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGpsIntent,PERMISSIONS_REQUEST_ENABLE_GPS);
                    }
                });
        final AlertDialog alertDialog  = builder.create();
        alertDialog.show();
    }
    public boolean isMapsEnabled(){
        final LocationManager manager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
            return false;
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case PERMISSIONS_REQUEST_ENABLE_GPS:{
                if (mLocationPermissionGranted){
                    initMap();
                }else {
                    requestPermissions();
                }
            }
        }
    }

   /* private void addMarkersFromApi(){
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "Connection Failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final List<PostLocation> list = new ArrayList<>();
                String jsonString = response.body().string();
                Type listType = new TypeToken<List<PostLocation>>(){}.getType();
                List<PostLocation> postLocations = gson.fromJson(jsonString,listType);

                for (int i = 0; i < postLocations.size(); i++){
                    locationList.add(postLocations.get(i));

                    final int finalI = i;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MarkerOptions marker = new MarkerOptions();

                            if (finalI == 0){
                                CameraPosition cameraPosition = new CameraPosition(new LatLng(Double.parseDouble(locationList.get(finalI).getLongitude())
                                        ,Double.parseDouble(locationList.get(finalI).getLatitude())),DEFAULT_ZOOM,TILT_LEVEL,BEARING_LEVEL);
                                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                            }
                            mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(Double.parseDouble(locationList.get(finalI).getLatitude()),Double.parseDouble(locationList.get(finalI).getLongitude())))
                                    .title(locationList.get(finalI).getName())
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                            mMap.addMarker(marker);
                        }
                    });

                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });

            }
        });
    }*/

}

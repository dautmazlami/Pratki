package daut.mazlami.pratki.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import daut.mazlami.pratki.R;
import daut.mazlami.pratki.fragment.ProfileFragment;
import daut.mazlami.pratki.fragment.MyDeliveriesFragment;
import daut.mazlami.pratki.fragment.PostLocationFragment;
import daut.mazlami.pratki.fragment.TrackingFragment;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.menu_item_search);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        switch (menuItem.getItemId()){
            case R.id.menu_item_search:
                ft.replace(R.id.frame_layout, TrackingFragment.newInstance(new Bundle()),TrackingFragment.TAG);
                ft.commit();
                return true;
            case R.id.menu_item_archive:
                ft.replace(R.id.frame_layout, MyDeliveriesFragment.newInstance(new Bundle()),MyDeliveriesFragment.TAG);
                ft.commit();
                return true;
            case R.id.menu_item_post:
                ft.replace(R.id.frame_layout, PostLocationFragment.newInstance(new Bundle()), PostLocationFragment.TAG);
                ft.commit();
                return true;
            case R.id.menu_item_location:
                ft.replace(R.id.frame_layout, ProfileFragment.newInstance(new Bundle()), ProfileFragment.TAG);
                ft.commit();
                return true;
        }
        return false;
    }

}

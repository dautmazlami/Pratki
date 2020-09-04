package daut.mazlami.pratki.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import daut.mazlami.pratki.R;
import daut.mazlami.pratki.activity.AddNewTrackingNumber;
import daut.mazlami.pratki.activity.TrackNumberDetailActivity;
import daut.mazlami.pratki.adapter.AddAdapter;
import daut.mazlami.pratki.interfaces.OnTrackNumberClick;
import daut.mazlami.pratki.model.TrackNumber;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyDeliveriesFragment extends Fragment implements OnTrackNumberClick {

    public static final String TAG = MyDeliveriesFragment.class.getSimpleName();
    public static final int NEW_TN_REQUEST_CODE = 0;

    FloatingActionButton fab;
    EditText addTrackingNumber;
    EditText addTitle;
    TextView savedTrackingNumberID;
    TextView sasvedTitleTN;

    RecyclerView recyclerView;
    AddAdapter adapter;
    List<TrackNumber> list = new ArrayList<>();
    OnTrackNumberClick onTrackNumberClick;

    public static MyDeliveriesFragment newInstance(Bundle bundle) {
        MyDeliveriesFragment myDeliveriesFragment = new MyDeliveriesFragment();
        myDeliveriesFragment.setArguments(bundle);
        return myDeliveriesFragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_my_deliveries, container, false);

        recyclerView = view.findViewById(R.id.recyclerview_myshipments);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AddAdapter(getActivity(),list,onTrackNumberClick);
        recyclerView.setAdapter(adapter);

        savedTrackingNumberID = view.findViewById(R.id.saved_tracking_number);
        sasvedTitleTN = view.findViewById(R.id.saved_tracking_title);
        addTrackingNumber = view.findViewById(R.id.add_tracking_number);
        addTitle = view.findViewById(R.id.add_title);

        list = TrackNumber.listAll(TrackNumber.class);

        fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddNewTrackingNumber.class);
                startActivityForResult(intent,NEW_TN_REQUEST_CODE);
            }
        });

        displayTrackingNumbers();
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NEW_TN_REQUEST_CODE && resultCode == RESULT_OK){
            TrackNumber trackNumber = new TrackNumber(data.getStringExtra(AddNewTrackingNumber.REPLAY_TRACKING_NR)
                    ,data.getStringExtra(AddNewTrackingNumber.REPLAY_TITLE));
            trackNumber.save();
            list.add(trackNumber);
            adapter = new AddAdapter(getActivity(),list,onTrackNumberClick);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }else{
            Toast.makeText(getActivity(), "No tracking number added", Toast.LENGTH_SHORT).show();
        }
    }

    private void displayTrackingNumbers() {
        TrackNumber trackNumber = new TrackNumber();
        trackNumber.listAll(TrackNumber.class);
        adapter = new AddAdapter(getActivity(), list,onTrackNumberClick);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        Collections.reverse(list);
    }

    @Override
    public void onTrackNumberClick(TrackNumber trackNumber) {
        String trackingNumber = savedTrackingNumberID.getText().toString();
        Intent intent = new Intent(getActivity(),TrackNumberDetailActivity.class);
        intent.putExtra("track_number",trackingNumber);
        startActivity(intent);

    }
}

package daut.mazlami.pratki.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import daut.mazlami.pratki.R;
import daut.mazlami.pratki.fragment.MyDeliveriesFragment;
import daut.mazlami.pratki.interfaces.OnTrackNumberClick;
import daut.mazlami.pratki.model.TrackingData;

public class AddNewTrackingNumber extends AppCompatActivity{

    public static final String REPLAY_TRACKING_NR = "Replay Tracking Number";
    public static final String REPLAY_TITLE = "Replay Title";

    EditText trackingNumber;
    EditText title;
    Button saveButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        trackingNumber = findViewById(R.id.add_tracking_number);
        title = findViewById(R.id.add_title);
        saveButton = findViewById(R.id.save_trackingNUmber_button);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent replayIntent = new Intent(getApplicationContext(), MyDeliveriesFragment.class);
                if(TextUtils.isEmpty(trackingNumber.getText().toString()) || TextUtils.isEmpty(title.getText().toString())){
                    setResult(Activity.RESULT_CANCELED,replayIntent);
                }else{
                    String trackNr = trackingNumber.getText().toString();
                    String trackTitle = title.getText().toString();
                    replayIntent.putExtra(REPLAY_TRACKING_NR,trackNr);
                    replayIntent.putExtra(REPLAY_TITLE,trackTitle);
                    setResult(Activity.RESULT_OK,replayIntent);
                }
                finish();
            }
        });
    }

}

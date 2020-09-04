package daut.mazlami.pratki.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.orm.SugarRecord;

public class TrackNumber extends SugarRecord<TrackNumber> implements Parcelable {

    String trackNumber;
    String title;

    public TrackNumber() {
    }

    public TrackNumber(String trackNumber, String title) {
        this.trackNumber = trackNumber;
        this.title = title;
    }

    protected TrackNumber(Parcel in) {
        trackNumber = in.readString();
        title = in.readString();
    }

    public static final Creator<TrackNumber> CREATOR = new Creator<TrackNumber>() {
        @Override
        public TrackNumber createFromParcel(Parcel in) {
            return new TrackNumber(in);
        }

        @Override
        public TrackNumber[] newArray(int size) {
            return new TrackNumber[size];
        }
    };

    public String getTrackNumber() {
        return trackNumber;
    }

    public void setTrackNumber(String trackNumber) {
        this.trackNumber = trackNumber;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(trackNumber);
        dest.writeString(title);
    }
}

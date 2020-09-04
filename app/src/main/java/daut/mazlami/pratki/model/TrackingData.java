package daut.mazlami.pratki.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.orm.SugarRecord;

public class TrackingData implements Parcelable {

    private String Notice;

    private String Begining;

    private String End;

    private String ID;

    private String Date;

    public TrackingData(String notice, String begining, String end, String ID, String date) {
        Notice = notice;
        Begining = begining;
        End = end;
        this.ID = ID;
        Date = date;
    }

    public TrackingData() { }

    public TrackingData(Parcel in) {
        Notice = in.readString();
        Begining = in.readString();
        End = in.readString();
        ID = in.readString();
        Date = in.readString();
    }

    public static final Creator<TrackingData> CREATOR = new Creator<TrackingData>() {
        @Override
        public TrackingData createFromParcel(Parcel in) {
            return new TrackingData(in);
        }

        @Override
        public TrackingData[] newArray(int size) {
            return new TrackingData[size];
        }
    };

    public TrackingData(String stringExtra) {
    }

    public String getNotice() {
        return Notice;
    }

    public void setNotice(String notice) {
        Notice = notice;
    }

    public String getBegining() {
        return Begining;
    }

    public void setBegining(String begining) {
        Begining = begining;
    }

    public String getEnd() {
        return End;
    }

    public void setEnd(String end) {
        End = end;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Notice);
        dest.writeString(Begining);
        dest.writeString(End);
        dest.writeString(ID);
        dest.writeString(Date);
    }
}

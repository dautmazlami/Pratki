package daut.mazlami.pratki.model;

import android.os.Parcel;
import android.os.Parcelable;

public class PostLocation implements Parcelable {

    private String zip;

    private String workingperiod;

    private String city;

    private String phone;

    private String typepost;

    private double latitude;

    private String name;

    private String street1;

    private double longitude;

    public PostLocation() {
    }

    public PostLocation(String zip, String workingperiod, String city, String phone, String typepost, double latitude, String name, String street1, double longitude) {
        this.zip = zip;
        this.workingperiod = workingperiod;
        this.city = city;
        this.phone = phone;
        this.typepost = typepost;
        this.latitude = latitude;
        this.name = name;
        this.street1 = street1;
        this.longitude = longitude;
    }

    protected PostLocation(Parcel in) {
        zip = in.readString();
        workingperiod = in.readString();
        city = in.readString();
        phone = in.readString();
        typepost = in.readString();
        latitude = in.readDouble();
        name = in.readString();
        street1 = in.readString();
        longitude = in.readDouble();
    }

    public static final Creator<PostLocation> CREATOR = new Creator<PostLocation>() {
        @Override
        public PostLocation createFromParcel(Parcel in) {
            return new PostLocation(in);
        }

        @Override
        public PostLocation[] newArray(int size) {
            return new PostLocation[size];
        }
    };

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getWorkingperiod() {
        return workingperiod;
    }

    public void setWorkingperiod(String workingperiod) {
        this.workingperiod = workingperiod;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTypepost() {
        return typepost;
    }

    public void setTypepost(String typepost) {
        this.typepost = typepost;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStreet1() {
        return street1;
    }

    public void setStreet1(String street1) {
        this.street1 = street1;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(zip);
        dest.writeString(workingperiod);
        dest.writeString(city);
        dest.writeString(phone);
        dest.writeString(typepost);
        dest.writeDouble(latitude);
        dest.writeString(name);
        dest.writeString(street1);
        dest.writeDouble(longitude);
    }
}


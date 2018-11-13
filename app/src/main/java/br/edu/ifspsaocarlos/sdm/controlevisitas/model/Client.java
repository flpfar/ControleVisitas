package br.edu.ifspsaocarlos.sdm.controlevisitas.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

public class Client implements Parcelable {
    private String id;
    private String name;
    private String contact;
    private String email;
    private String phone;

    public Client (){}

    public Client(String name, String contact, String phone, String email) {
        this.name = name;
        this.contact = contact;
        this.email = email;
        this.phone = phone;
    }

    protected Client(Parcel in) {
        id = in.readString();
        name = in.readString();
        contact = in.readString();
        email = in.readString();
        phone = in.readString();
    }

    public static final Creator<Client> CREATOR = new Creator<Client>() {
        @Override
        public Client createFromParcel(Parcel in) {
            return new Client(in);
        }

        @Override
        public Client[] newArray(int size) {
            return new Client[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(contact);
        dest.writeString(email);
        dest.writeString(phone);
    }
}

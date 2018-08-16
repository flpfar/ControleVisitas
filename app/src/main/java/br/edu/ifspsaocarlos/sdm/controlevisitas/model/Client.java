package br.edu.ifspsaocarlos.sdm.controlevisitas.model;

import com.google.firebase.database.Exclude;

public class Client {
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

    @Exclude
    public String getId() {
        return id;
    }

    @Exclude
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
}

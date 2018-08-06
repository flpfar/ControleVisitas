package br.edu.ifspsaocarlos.sdm.controlevisitas.model;

public class Client {
    private String name;
    private String contact;
    private String email;
    private String phone;

    public Client (){}

    public Client(String name, String contact, String email, String phone) {
        this.name = name;
        this.contact = contact;
        this.email = email;
        this.phone = phone;
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

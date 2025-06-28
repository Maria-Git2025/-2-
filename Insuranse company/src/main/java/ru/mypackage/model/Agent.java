package ru.mypackage.model;

// Модель агента страховой компании
public class Agent {
    private int id;
    private int idUser;
    private int idBranch;
    private String surname;
    private String name;
    private String patronymic;
    private String address;
    private String phoneNumber;

    public Agent(int id, int idUser, int idBranch, String surname, String name, String patronymic, String address, String phoneNumber) {
        this.id = id;
        this.idUser = idUser;
        this.idBranch = idBranch;
        this.surname = surname;
        this.name = name;
        this.patronymic = patronymic;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getIdUser() { return idUser; }
    public void setIdUser(int idUser) { this.idUser = idUser; }
    public int getIdBranch() { return idBranch; }
    public void setIdBranch(int idBranch) { this.idBranch = idBranch; }
    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPatronymic() { return patronymic; }
    public void setPatronymic(String patronymic) { this.patronymic = patronymic; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
} 
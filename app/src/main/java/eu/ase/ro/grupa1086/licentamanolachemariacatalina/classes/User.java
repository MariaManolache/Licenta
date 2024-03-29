package eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class User implements Serializable {

    private String id;

    private String email;
    private String name;
    private String password;
    private String phoneNumber;
    private int isDriver;
    private int isRestaurant;

    public User() {
    }

    public User(String id, String email, String name, String password, String phoneNumber, int isDriver) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.isDriver = isDriver;
    }

    public User(String id, String email, String name, String password, String phoneNumber, int isDriver, int isRestaurant) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.isDriver = isDriver;
        this.isRestaurant = isRestaurant;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getIsDriver() {
        return isDriver;
    }

    public void setIsDriver(int isDriver) {
        this.isDriver = isDriver;
    }

    public int getIsRestaurant() {
        return isRestaurant;
    }

    public void setIsRestaurant(int isRestaurant) {
        this.isRestaurant = isRestaurant;
    }


    @NonNull
    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", isDriver=" + isDriver +
                '}';
    }
}

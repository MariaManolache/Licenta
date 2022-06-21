package eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes;

import java.io.Serializable;
import java.util.List;

import eu.ase.ro.grupa1086.licentamanolachemariacatalina.rating.Rating;

public class RestaurantAccount implements Serializable {

    private String id;
    private String name;
    private String email;
    private String phoneNumber;
    private String address;
    private String password;
    private int isRestaurant;

    public RestaurantAccount() {
    }


    public RestaurantAccount(String id, String name, String email, String phoneNumber, String address, String password, int isRestaurant) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.password = password;
        this.isRestaurant = isRestaurant;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getIsRestaurant() {
        return isRestaurant;
    }

    public void setIsRestaurant(int isRestaurant) {
        this.isRestaurant = isRestaurant;
    }

    @Override
    public String toString() {
        return "RestaurantAccount{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}

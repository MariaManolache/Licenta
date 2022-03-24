package eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes;

import java.io.Serializable;

public class Address implements Serializable {

    private String id;
    private String street;
    private String number;
    private String block;
    private String entrance;
    private String floor;
    private String apartment;
    private String city;
    private String region;
    private String idUser;
    private String mapsAddress;

    public Address() {

    }

    public Address(String id, String mapsAddress) {
        this.id = id;
        this.mapsAddress = mapsAddress;
    }

//    public Address(String idUser) {
//        this.idUser = idUser;
//    }

    public Address(String id, String street, String number, String block, String entrance, String floor, String apartment, String city, String region, String idUser) {
        this.id = id;
        this.street = street;
        this.number = number;
        this.block = block;
        this.entrance = entrance;
        this.floor = floor;
        this.apartment = apartment;
        this.city = city;
        this.region = region;
        this.idUser = idUser;
    }

    public Address(String id, String street, String number, String block, String entrance, String floor, String apartment, String city, String region, String idUser, String mapsAddress) {
        this.id = id;
        this.street = street;
        this.number = number;
        this.block = block;
        this.entrance = entrance;
        this.floor = floor;
        this.apartment = apartment;
        this.city = city;
        this.region = region;
        this.idUser = idUser;
        this.mapsAddress = mapsAddress;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public String getEntrance() {
        return entrance;
    }

    public void setEntrance(String entrance) {
        this.entrance = entrance;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getApartment() {
        return apartment;
    }

    public void setApartment(String apartment) {
        this.apartment = apartment;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getMapsAddress() {
        return mapsAddress;
    }

    public void setMapsAddress(String mapsAddress) {
        this.mapsAddress = mapsAddress;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    @Override
    public String toString() {
        return "Address{" +
                "street='" + street + '\'' +
                ", number='" + number + '\'' +
                ", block='" + block + '\'' +
                ", entrance='" + entrance + '\'' +
                ", floor='" + floor + '\'' +
                ", apartment='" + apartment + '\'' +
                ", city='" + city + '\'' +
                ", region='" + region + '\'' +
                '}';
    }
}

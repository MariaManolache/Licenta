package eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

import eu.ase.ro.grupa1086.licentamanolachemariacatalina.rating.Rating;

public class Order implements Serializable {
    private String id;
    private float total;
    private String userId;
    private PaymentMethod paymentMethod;
    private Address address;
    private List<Food> cart;
    private Status status;
    private List<Restaurant> restaurantAddress;
    private List<Rating> ratingsList;
    private List<Restaurant> restaurants;
    private int preparationTime;
    private String driverId;
    private String currentDateAndTime;

    public Order() {

    }

    public Order(String id, float total, String userId, PaymentMethod paymentMethod, Address address,List<Food> cart, Status status, List<Restaurant> restaurantAddress) {
        this.id = id;
        this.total = total;
        this.userId = userId;
        this.paymentMethod = paymentMethod;
        this.address = address;
        this.cart = cart;
        this.status = status;
        this.restaurantAddress = restaurantAddress;
    }

    public Order(String id, float total, String userId, PaymentMethod paymentMethod, Address address,List<Food> cart, Status status) {
        this.id = id;
        this.total = total;
        this.userId = userId;
        this.paymentMethod = paymentMethod;
        this.address = address;
        this.cart = cart;
        this.status = status;
    }

    public Order(String id, float total, String userId, PaymentMethod paymentMethod, Address address,List<Food> cart, Status status, int preparationTime) {
        this.id = id;
        this.total = total;
        this.userId = userId;
        this.paymentMethod = paymentMethod;
        this.address = address;
        this.cart = cart;
        this.status = status;
        this.preparationTime = preparationTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }


    public List<Food> getCart() {
        return cart;
    }

    public void setCart(List<Food> cart) {
        this.cart = cart;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public List<Restaurant> getRestaurantAddress() {
        return restaurantAddress;
    }

    public void setRestaurantAddress(List<Restaurant> restaurantAddress) {
        this.restaurantAddress = restaurantAddress;
    }

    public List<Rating> getRatingsList() {
        return ratingsList;
    }

    public void setRatingsList(List<Rating> ratingsList) {
        this.ratingsList = ratingsList;
    }

    public int getPreparationTime() {
        return preparationTime;
    }

    public void setPreparationTime(int preparationTime) {
        this.preparationTime = preparationTime;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getCurrentDateAndTime() {
        return currentDateAndTime;
    }

    public void setCurrentDateAndTime(String currentDateAndTime) {
        this.currentDateAndTime = currentDateAndTime;
    }

    @NonNull
    @Override
    public String toString() {
        return "Order{" +
                "id='" + id + '\'' +
                ", total=" + total +
                ", userId='" + userId + '\'' +
                ", paymentMethod=" + paymentMethod +
                ", address=" + address +
                ", cart=" + cart +
                ", status=" + status +
                '}';
    }
}

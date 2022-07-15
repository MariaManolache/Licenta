package eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

public class RestaurantOrder implements Serializable {
    private String restaurantId;
    private List<Food> foodList;
    private String orderId;
    private Status status;
    private double total;
    private PaymentMethod paymentMethod;
    private String userId;
    private Restaurant restaurant;
    private Address address;
    private int preparationTime;
    private String driverId;
    private String currentDateAndTime;

    public RestaurantOrder() {

    }

    public RestaurantOrder(String restaurantId, List<Food> foodList, String orderId, Status status, double total, PaymentMethod paymentMethod, String userId, Restaurant restaurant, Address address) {
        this.restaurantId = restaurantId;
        this.foodList = foodList;
        this.orderId = orderId;
        this.status = status;
        this.total = total;
        this.paymentMethod = paymentMethod;
        this.userId = userId;
        this.restaurant = restaurant;
        this.address = address;
    }

    public RestaurantOrder(String restaurantId, List<Food> foodList, String orderId, Status status, double total, PaymentMethod paymentMethod, String userId, Restaurant restaurant, Address address, int preparationTime) {
        this.restaurantId = restaurantId;
        this.foodList = foodList;
        this.orderId = orderId;
        this.status = status;
        this.total = total;
        this.paymentMethod = paymentMethod;
        this.userId = userId;
        this.restaurant = restaurant;
        this.address = address;
        this.preparationTime = preparationTime;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public List<Food> getFoodList() {
        return foodList;
    }

    public void setFoodList(List<Food> foodList) {
        this.foodList = foodList;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
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
        return "RestaurantOrder{" +
                "restaurantId='" + restaurantId + '\'' +
                ", foodList=" + foodList +
                ", orderId='" + orderId + '\'' +
                ", status=" + status +
                ", total=" + total +
                ", paymentMethod=" + paymentMethod +
                ", userId='" + userId + '\'' +
                ", restaurant=" + restaurant +
                ", address=" + address +
                '}';
    }
}

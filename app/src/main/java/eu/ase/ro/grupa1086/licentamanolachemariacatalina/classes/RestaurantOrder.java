package eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes;

import java.util.List;

public class RestaurantOrder {
    private String restaurantId;
    private List<Food> foodList;
    private String orderId;
    private Status status;

    public RestaurantOrder(String restaurantId, List<Food> foodList, String orderId, Status status) {
        this.restaurantId = restaurantId;
        this.foodList = foodList;
        this.orderId = orderId;
        this.status = status;
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

    @Override
    public String toString() {
        return "RestaurantOrder{" +
                "restaurantId='" + restaurantId + '\'' +
                ", foodList=" + foodList +
                ", orderId='" + orderId + '\'' +
                ", status=" + status +
                '}';
    }
}

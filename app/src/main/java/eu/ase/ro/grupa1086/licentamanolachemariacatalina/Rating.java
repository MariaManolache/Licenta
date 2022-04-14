package eu.ase.ro.grupa1086.licentamanolachemariacatalina;

public class Rating {
    private String orderId;
    private String restaurantId;
    private String rateValue;
    private String comment;

    public Rating() {

    }

    public Rating(String orderId, String restaurantId, String rateValue, String comment) {
        this.orderId = orderId;
        this.restaurantId = restaurantId;
        this.rateValue = rateValue;
        this.comment = comment;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getRateValue() {
        return rateValue;
    }

    public void setRateValue(String rateValue) {
        this.rateValue = rateValue;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "Rating{" +
                "orderId='" + orderId + '\'' +
                ", restaurantId='" + restaurantId + '\'' +
                ", rateValue='" + rateValue + '\'' +
                ", comment='" + comment + '\'' +
                '}';
    }
}

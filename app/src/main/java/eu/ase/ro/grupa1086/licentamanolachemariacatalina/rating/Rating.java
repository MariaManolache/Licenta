package eu.ase.ro.grupa1086.licentamanolachemariacatalina.rating;

import java.io.Serializable;
import java.util.Map;

public class Rating implements Serializable {
    private String name;
    private String orderId;
    private float rateValue;
    private String comment;
    private Map<String, Object> commentTimeStamp;

    public Rating() {

    }

    public Rating(String name, String orderId, float rateValue, String comment, Map<String, Object> commentTimeStamp) {
        this.name = name;
        this.orderId = orderId;
        this.rateValue = rateValue;
        this.comment = comment;
        this.commentTimeStamp = commentTimeStamp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }


    public float getRateValue() {
        return rateValue;
    }

    public void setRateValue(float rateValue) {
        this.rateValue = rateValue;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Map<String, Object> getCommentTimeStamp() {
        return commentTimeStamp;
    }

    public void setCommentTimeStamp(Map<String, Object> commentTimeStamp) {
        this.commentTimeStamp = commentTimeStamp;
    }

    @Override
    public String toString() {
        return "Rating{" +
                "name='" + name + '\'' +
                ", orderId='" + orderId + '\'' +
                ", rateValue=" + rateValue +
                ", comment='" + comment + '\'' +
                ", commentTimeStamp=" + commentTimeStamp +
                '}';
    }
}

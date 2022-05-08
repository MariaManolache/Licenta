package eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes;

import java.io.Serializable;
import java.util.List;

import eu.ase.ro.grupa1086.licentamanolachemariacatalina.rating.Rating;

public class Restaurant implements Serializable {

    private String id;
    private String name;
    private String image;
    private String categoryId;
    private String categoryIdName;
    private String address;
    private String deliveryTime;
    private List<Rating> ratingsList;

    public Restaurant() {
    }

    public Restaurant(String id, String name, String image, String categoryId, String categoryIdName, String address, String deliveryTime) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.categoryId = categoryId;
        this.categoryIdName = categoryIdName;
        this.address = address;
        this.deliveryTime = deliveryTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryIdName() {
        return categoryIdName;
    }

    public void setCategoryIdName(String categoryIdName) {
        this.categoryIdName = categoryIdName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public List<Rating> getRatingsList() {
        return ratingsList;
    }

    public void setRatingsList(List<Rating> ratingsList) {
        this.ratingsList = ratingsList;
    }

    @Override
    public String toString() {
        return "Restaurant{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", image='" + image + '\'' +
                ", categoryId='" + categoryId + '\'' +
                ", categoryIdName='" + categoryIdName + '\'' +
                ", address='" + address + '\'' +
                ", deliveryTime='" + deliveryTime + '\'' +
                '}';
    }
}

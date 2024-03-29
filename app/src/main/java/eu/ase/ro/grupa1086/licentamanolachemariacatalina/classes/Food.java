package eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.time.Duration;

import eu.ase.ro.grupa1086.licentamanolachemariacatalina.rating.Rating;

public class Food implements Serializable {

    private String id;
    private String name;
    private float price;
    private String description;
    private int quantity;
    private String image;
    private String restaurantId;
    private String restaurantIdName;
    private Rating ratings;
    private int preparationTime;

    public Food() {
    }

    public Food(String id, String name, float price, String description, int quantity,
                String image, String restaurantId) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.quantity = quantity;
        this.image = image;
        this.restaurantId = restaurantId;
        this.restaurantIdName = restaurantId + name;
    }

    public Food(String id, String name, float price, String description, int quantity,
                String image, String restaurantId, int preparationTime) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.quantity = quantity;
        this.image = image;
        this.restaurantId = restaurantId;
        this.restaurantIdName = restaurantId + name;
        this.preparationTime = preparationTime;
    }

    public Food(String id, String restaurantId) {
        this.id = id;
        this.restaurantId = restaurantId;
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
        this.restaurantIdName = this.restaurantId + name;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
        this.restaurantIdName = restaurantId + this.name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRestaurantIdName() {
        return restaurantIdName;
    }

    public void setRestaurantIdName(String restaurantIdName) {
        this.restaurantIdName = restaurantIdName;
    }

    public Rating getRatings() {
        return ratings;
    }

    public void setRatings(Rating ratings) {
        this.ratings = ratings;
    }

    public int getPreparationTime() {
        return preparationTime;
    }

    public void setPreparationTime(int preparationTime) {
        this.preparationTime = preparationTime;
    }

    @NonNull
    @Override
    public String toString() {
        return "Food{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", description='" + description + '\'' +
                ", quantity=" + quantity +
                ", image='" + image + '\'' +
                ", restaurantId='" + restaurantId + '\'' +
                ", restaurantIdName='" + restaurantIdName + '\'' +
                ", ratings=" + ratings +
                '}';
    }
}
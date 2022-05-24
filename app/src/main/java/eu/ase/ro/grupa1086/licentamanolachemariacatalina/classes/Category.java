package eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes;

import android.media.Image;

import java.io.Serializable;
import java.util.Arrays;

import eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes.Restaurant;

public class Category implements Serializable {

    private String id;
    private String name;
    private String image;
   // private Restaurant[] restaurants;

//    public Category(String id, String name, Image image, Restaurant[] restaurants) {
//        this.id = id;
//        this.name = name;
//        this.image = image;
//        this.restaurants = restaurants;
//    }

    public Category(String id, String name, String image) {
        this.id = id;
        this.name = name;
        this.image = image;
    }

    public Category() {

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

//    public Restaurant[] getRestaurants() {
//        return restaurants;
//    }
//
//    public void setRestaurants(Restaurant[] restaurants) {
//        this.restaurants = restaurants;
//    }


    @Override
    public String toString() {
        return "Category{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", image='" + image + '\'' +
                '}';
    }
}

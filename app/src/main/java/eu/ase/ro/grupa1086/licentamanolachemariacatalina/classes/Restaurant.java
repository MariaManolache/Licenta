package eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;

public class Restaurant implements Serializable {

    private String id;
    private String name;
    private String image;
    private String categoryId;
    private String categoryIdName;
    private String address;

    public Restaurant() {
    }

    public Restaurant(String id, String name, String image, String categoryId, String address) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.categoryId = categoryId;
        this.categoryIdName = categoryId + name;
        this.address = address;
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

    @Override
    public String toString() {
        return "Restaurant{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", image='" + image + '\'' +
                ", categoryId='" + categoryId + '\'' +
                ", categoryIdName='" + categoryIdName + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}

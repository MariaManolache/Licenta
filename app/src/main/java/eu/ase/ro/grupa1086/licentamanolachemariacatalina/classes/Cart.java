package eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class Cart implements Serializable {

    private String id;
    private List<Food> foodList;

    public Cart() {
    }

    public Cart(String id, List<Food> foodList) {
        this.id = id;
        this.foodList = foodList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Food> getFoodList() {
        return foodList;
    }

    public void setFoodList(List<Food> foodList) {
        this.foodList = foodList;
    }

    @NonNull
    @Override
    public String toString() {
        return "Cart{" +
                "id='" + id + '\'' +
                ", foodList=" + foodList +
                '}';
    }
}

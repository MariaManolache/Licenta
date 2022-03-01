package eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;

public class Restaurant implements Serializable {

    private String id;
    private String name;
    private ArrayList<Food> menu;

    public Restaurant() {
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

    public ArrayList<Food> getMenu() {
        return menu;
    }

    public void setMenu(ArrayList<Food> menu) {
        this.menu = menu;
    }

    @NonNull
    @Override
    public String toString() {
        return "Restaurant{" +
                ", name='" + name + '\'' +
                ", menu=" + menu +
                '}';
    }
}

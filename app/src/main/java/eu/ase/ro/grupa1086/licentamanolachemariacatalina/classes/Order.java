package eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes;

import java.io.Serializable;
import java.util.Arrays;

enum Status {plasata, confirmata, in_curs_de_livrare, finalizata}

public class Order implements Serializable {
    private String id;
    private String phone;
    private String address;
    private String total;
    private Cart cart;
    private Status status;

    public Order() {

    }

    public Order(String id, Cart cart, Status status) {
        this.id = id;
        this.cart = cart;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id='" + id + '\'' +
                ", cart=" + cart +
                ", status=" + status +
                '}';
    }
}

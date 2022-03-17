package eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes;

import java.io.Serializable;
import java.util.List;

public class AddressList implements Serializable {

    private String id;
    private List<Address> addresses;

    public AddressList() {

    }

    public AddressList(String id, List<Address> addresses) {
        this.id = id;
        this.addresses = addresses;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "AddressList{" +
                "id='" + id + '\'' +
                ", addresses=" + addresses +
                '}';
    }
}

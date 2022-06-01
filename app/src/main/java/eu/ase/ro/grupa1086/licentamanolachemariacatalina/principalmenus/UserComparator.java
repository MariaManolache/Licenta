package eu.ase.ro.grupa1086.licentamanolachemariacatalina.principalmenus;

import com.google.android.gms.maps.model.LatLng;

import java.util.Comparator;
import java.util.Map;

public class UserComparator implements Comparator<Object> {
    Map<LatLng, Double> map;

    public UserComparator(Map<LatLng, Double> map) {
        this.map = map;
    }

    public int compare(Object o1, Object o2) {
        if (map.get(o2) == map.get(o1))
            return 1;
        else
            return ((Double) map.get(o1)).compareTo((Double)
                    map.get(o2));
    }
}

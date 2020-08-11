package pl.bratosz.smartlockers.model;

import javax.persistence.CascadeType;
import javax.persistence.OneToMany;
import java.util.Set;

public class UserEmployee extends User {

    private Position position;

    @OneToMany(mappedBy = "acceptedBy", cascade = CascadeType.ALL)
    private ClothOrder acceptedOrders;

    @OneToMany(mappedBy = "realizedBy", cascade = CascadeType.ALL)
    private ClothOrder realizedOrders;

}

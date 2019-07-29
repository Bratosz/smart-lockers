package pl.bratosz.smartlockers.converters;

import org.springframework.stereotype.Component;
import pl.bratosz.smartlockers.model.Locker;

@Component
public class LocationConverter extends EnumConverter<Locker.Location> {
    public LocationConverter() {
        super(Locker.Location.class);
    }
}

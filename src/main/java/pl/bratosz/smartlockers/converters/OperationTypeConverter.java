package pl.bratosz.smartlockers.converters;

import org.springframework.stereotype.Component;
import pl.bratosz.smartlockers.service.exels.ClothOperationType;

@Component
public class OperationTypeConverter extends EnumConverter<ClothOperationType> {
    public OperationTypeConverter() {
        super(ClothOperationType.class);
    }
}

package store.sbin.postservice.config;

import jakarta.persistence.AttributeConverter;
import org.springframework.stereotype.Component;

@Component
public class BooleanToYNConverter implements AttributeConverter<Boolean, String> {

    @Override
    public String convertToDatabaseColumn(Boolean yn) {
        return yn ? "Y" : "N";
    }

    @Override
    public Boolean convertToEntityAttribute(String s) {
        return "Y".equalsIgnoreCase(s);
    }
}

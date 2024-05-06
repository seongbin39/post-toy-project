package store.sbin.postservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import store.sbin.postservice.domain.Role;

@Component
@Slf4j
public class StringToEnumConverter implements Converter<String, Role> {
    @Override
    public Role convert(String source) {
        try {
            return Role.valueOf(source.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Role.USER;
        }
    }
}

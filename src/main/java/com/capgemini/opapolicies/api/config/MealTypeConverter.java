package com.capgemini.opapolicies.api;

import com.capgemini.opapolicies.domain.MealType;
import org.springframework.core.convert.converter.Converter;

import java.util.Locale;

public class MealTypeConverter implements Converter<String, MealType> {

    @Override
    public MealType convert(String source) {
        if (source == null || source.isBlank()) {
            return null;
        }
        String normalized = source.trim()
                .toUpperCase(Locale.ROOT)
                .replace('-', '_')
                .replace(' ', '_');
        return MealType.valueOf(normalized);
    }
}

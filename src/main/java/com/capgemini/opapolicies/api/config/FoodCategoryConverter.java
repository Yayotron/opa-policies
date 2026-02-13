package com.capgemini.opapolicies.api.config;

import com.capgemini.opapolicies.domain.FoodCategory;
import org.springframework.core.convert.converter.Converter;

import java.util.Locale;

public class FoodCategoryConverter implements Converter<String, FoodCategory> {

    @Override
    public FoodCategory convert(String source) {
        if (source == null || source.isBlank()) {
            return null;
        }
        String normalized = source.trim()
                .toUpperCase(Locale.ROOT)
                .replace('-', '_')
                .replace(' ', '_');
        return FoodCategory.valueOf(normalized);
    }
}
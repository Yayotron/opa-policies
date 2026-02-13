package com.capgemini.opapolicies.api.config;

import com.capgemini.opapolicies.domain.Allergen;
import org.springframework.core.convert.converter.Converter;

import java.util.Locale;

public class AllergenConverter implements Converter<String, Allergen> {

    @Override
    public Allergen convert(String source) {
        if (source == null || source.isBlank()) {
            return null;
        }
        String normalized = source.trim()
                .toUpperCase(Locale.ROOT)
                .replace('-', '_')
                .replace(' ', '_');
        return Allergen.valueOf(normalized);
    }
}

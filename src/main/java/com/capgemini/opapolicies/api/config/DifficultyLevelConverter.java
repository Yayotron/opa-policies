package com.capgemini.opapolicies.api.config;

import com.capgemini.opapolicies.domain.DifficultyLevel;
import org.springframework.core.convert.converter.Converter;

import java.util.Locale;

public class DifficultyLevelConverter implements Converter<String, DifficultyLevel> {

    @Override
    public DifficultyLevel convert(String source) {
        if (source == null || source.isBlank()) {
            return null;
        }
        String normalized = source.trim()
                .toUpperCase(Locale.ROOT)
                .replace('-', '_')
                .replace(' ', '_');
        return DifficultyLevel.valueOf(normalized);
    }
}
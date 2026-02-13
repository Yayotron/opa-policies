package com.capgemini.opapolicies.api.config;

import com.capgemini.opapolicies.api.MealTypeConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new FoodCategoryConverter());
        registry.addConverter(new AllergenConverter());
        registry.addConverter(new DifficultyLevelConverter());
        registry.addConverter(new MealTypeConverter());
    }
}
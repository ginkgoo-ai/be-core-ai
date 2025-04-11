package com.ginkgooai.core.ai.converter;

import org.springframework.ai.converter.BeanOutputConverter;

public class Converter {

    public static <T> BeanOutputConverter<T> getConverter(Class<T> t) {
        return new BeanOutputConverter<>(t);
    }
}

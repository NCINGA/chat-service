package com.ncinga.chatservice.dto;

import lombok.Data;
import org.modelmapper.ModelMapper;

import java.io.Serializable;

@Data
public abstract class BaseDto<T, D> implements Serializable {
    private static final ModelMapper modelMapper = new ModelMapper();

    public D toEntity(Class<D> type) {
        D entity = modelMapper.map(this, type);
        return entity;
    }

    public static <T extends BaseDto<T, D>, D> T fromEntity(D entity, Class<T> dtoClass) {
        T dto = modelMapper.map(entity, dtoClass);
        return dto;
    }
}

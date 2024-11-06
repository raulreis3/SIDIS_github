package com.example.SIDIS_Reader.readermanagement.services;

import com.example.SIDIS_Reader.readermanagement.model.Reader;
import org.mapstruct.*;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public abstract class ReaderEditMapper {
    public abstract Reader create(CreateReaderRequest request);

    @BeanMapping(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void update(UpdateReaderRequest request, @MappingTarget Reader reader);
}

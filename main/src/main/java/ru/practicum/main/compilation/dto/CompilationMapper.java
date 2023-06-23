package ru.practicum.main.compilation.dto;


import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;
import ru.practicum.main.compilation.model.Compilation;
import ru.practicum.main.event.dto.events.EventMapper;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = "spring", uses = {EventMapper.class})
public interface CompilationMapper {

    EventMapper INSTANCE = Mappers.getMapper(EventMapper.class);

    CompilationDto toCompilationDto(Compilation compilation);

}

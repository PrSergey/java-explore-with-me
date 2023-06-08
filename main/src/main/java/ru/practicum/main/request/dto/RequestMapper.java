package ru.practicum.main.request.dto;


import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.Mapping;
import ru.practicum.main.request.model.ParticipationRequest;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = "spring")
public interface RequestMapper {

    ParticipationRequest fromParticipationRequestDto(ParticipationRequestDto request);
}

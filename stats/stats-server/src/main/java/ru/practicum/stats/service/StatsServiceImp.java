package ru.practicum.stats.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.stats.EndpointHitDto;
import ru.practicum.stats.ViewStatsDto;
import ru.practicum.stats.dto.EndpointMapper;
import ru.practicum.stats.dto.ViewStatsMapper;
import ru.practicum.stats.excepsion.ValidationException;
import ru.practicum.stats.model.EndpointHit;
import ru.practicum.stats.model.ViewStats;
import ru.practicum.stats.storage.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class StatsServiceImp implements StatsService {

    StatsRepository statsRepository;

    @Override
    @Transactional
    public EndpointHitDto save(EndpointHitDto endpointHitDto) {
        EndpointHit endpointHit = EndpointMapper.fromEndpointHitDto(endpointHitDto);
        return EndpointMapper.toEndpointHitDto(statsRepository.save(endpointHit));
    }

    @Override
    @Transactional
    public List<ViewStatsDto> get(List<String> uris, Boolean unique, LocalDateTime start, LocalDateTime end) {
        List<ViewStats> allViewStats;
        if (start.isAfter(end)) {
            throw new ValidationException("The beginning of the range cannot be later than the end of the range.");
        }
        if (unique)
            allViewStats = statsRepository.getWithUniqueIp(start, end, uris);
        else
            allViewStats = statsRepository.getAllEndpointHit(start, end, uris);
        return allViewStats.stream().map(ViewStatsMapper::toViewStatsDto).collect(Collectors.toList());
    }

}

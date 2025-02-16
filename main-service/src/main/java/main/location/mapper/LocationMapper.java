package main.location.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import main.location.dto.LocationDto;
import main.location.model.Location;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LocationMapper {
    public static Location toLocation(LocationDto locationDto) {
        return Location.builder()
                .lat(locationDto.getLat())
                .lon(locationDto.getLon())
                .build();
    }


    public static LocationDto toLocationDto(Location location) {
        return LocationDto.builder()
                .lat(location.getLat())
                .lon(location.getLon())
                .build();
    }
}
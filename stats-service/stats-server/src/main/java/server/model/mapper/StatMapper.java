
        package server.model.mapper;

import dto.StatDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import server.model.Stats;

        @Mapper
public interface StatMapper {
    StatMapper INSTANCE = Mappers.getMapper(StatMapper.class);

    Stats toStat(StatDto statDto);

    StatDto toStatDto(Stats stat);
}

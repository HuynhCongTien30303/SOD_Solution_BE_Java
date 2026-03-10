package web.internship.SODSolutions.mapper;

import org.mapstruct.*;
import web.internship.SODSolutions.dto.response.ResFieldDTO;
import web.internship.SODSolutions.model.Field;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface FieldMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "projects", ignore = true)
    @Mapping(target = "forms", ignore = true)
    Field toField(ResFieldDTO resFieldDTO);

    ResFieldDTO toResFieldDTO(Field field);

    List<ResFieldDTO> toResFieldDTO(List<Field> fields);

}
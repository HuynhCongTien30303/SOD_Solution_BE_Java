package web.internship.SODSolutions.mapper;

import org.mapstruct.*;
import web.internship.SODSolutions.dto.request.ReqProjectPhaseDTO;
import web.internship.SODSolutions.dto.response.ResProjectPhaseDTO;
import web.internship.SODSolutions.model.ProjectPhase;
import web.internship.SODSolutions.model.common.PhaseStatus;
import web.internship.SODSolutions.util.error.AppException;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProjectPhaseMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "project", ignore = true)
    @Mapping(target = "status", source = "status", qualifiedByName = "stringToPhaseStatus")
    @Mapping(target = "startDate", source = "startDate")
    @Mapping(target = "endDate", source = "endDate")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "payment", ignore = true)
    ProjectPhase toProjectPhase(ReqProjectPhaseDTO reqProjectPhaseDTO);

    @Mapping(target = "status", source = "status", qualifiedByName = "phaseStatusToString")
    @Mapping(target = "projectId", source = "project.id")
    ResProjectPhaseDTO toResProjectPhaseDTO(ProjectPhase projectPhase);

    List<ResProjectPhaseDTO> toResProjectPhaseDTO(List<ProjectPhase> projectPhaseList);

    @Named("stringToPhaseStatus")
    default PhaseStatus stringToPhaseStatus(String status) {
        if (status == null) {
            return null;
        }
        try {
            return PhaseStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new AppException("Invalid phase status: " + status);
        }
    }

    @Named("phaseStatusToString")
    default String phaseStatusToString(PhaseStatus status) {
        return status != null ? status.name() : null;
    }
}
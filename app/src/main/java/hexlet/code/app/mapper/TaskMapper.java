package hexlet.code.app.mapper;

import hexlet.code.app.dto.task.TaskCreateDTO;
import hexlet.code.app.dto.task.TaskDTO;
import hexlet.code.app.dto.task.TaskUpdateDTO;
import hexlet.code.app.model.entity.Label;
import hexlet.code.app.model.entity.Task;
import hexlet.code.app.model.entity.TaskStatus;
import hexlet.code.app.repository.LabelRepository;
import hexlet.code.app.repository.TaskStatusRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(
        uses = {JsonNullableMapper.class, ReferenceMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class TaskMapper {

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private LabelRepository labelRepository;

    @Mapping(source = "title", target = "name")
    @Mapping(source = "status", target = "taskStatus", qualifiedByName = "statusToTaskStatus")
    @Mapping(source = "assigneeId", target = "assignee")
    @Mapping(source = "taskLabelIds", target = "labels", qualifiedByName = "mapIdsToLabels")
    @Mapping(source = "content", target = "description")
    public abstract Task map(TaskCreateDTO dto);

    @Mapping(source = "name", target = "title")
    @Mapping(source = "description", target = "content")
    @Mapping(source = "taskStatus.slug", target = "status")
    @Mapping(source = "assignee.id", target = "assigneeId")
    @Mapping(source = "labels", target = "taskLabelIds", qualifiedByName = "mapLabelsToIds")
    public abstract TaskDTO map(Task model);

    @Mapping(source = "title", target = "name")
    @Mapping(source = "content", target = "description")
    @Mapping(source = "status", target = "taskStatus.slug")
    @Mapping(source = "assigneeId", target = "assignee")
    @Mapping(source = "taskLabelIds", target = "labels", qualifiedByName = "mapIdsToLabels")
    public abstract void update(TaskUpdateDTO dto, @MappingTarget Task model);

    @Named("statusToTaskStatus")
    public TaskStatus statusToTaskStatus(String status) {

        return taskStatusRepository.findBySlug(status).orElseThrow();
    }

    @Named("mapLabelsToIds")
    public Set<Long> mapLabelsToIds(Set<Label> labels) {

        return labels == null ? new HashSet<>() : labels.stream()
                .map(Label::getId)
                .collect(Collectors.toSet());
    }

    @Named("mapIdsToLabels")
    protected Set<Label> mapIdsToLabels(Set<Long> labelIds) {

        return labelIds == null ? new HashSet<>() : labelRepository.findByIdIn(labelIds);
    }

}

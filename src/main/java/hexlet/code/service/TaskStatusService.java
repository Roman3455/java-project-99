package hexlet.code.service;

import hexlet.code.dto.taskstatus.TaskStatusCreateDTO;
import hexlet.code.dto.taskstatus.TaskStatusDTO;
import hexlet.code.dto.taskstatus.TaskStatusUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskStatusMapper;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskStatusService {

    private final TaskStatusRepository taskStatusRepository;
    private final TaskStatusMapper taskStatusMapper;

    public List<TaskStatusDTO> getAllTaskStatuses() {
        var taskStatuses = taskStatusRepository.findAll();

        return taskStatuses.stream()
                .map(taskStatusMapper::map)
                .toList();
    }

    public TaskStatusDTO getTaskStatusById(Long id) {

        return taskStatusMapper.map(getById(id));
    }

    public TaskStatusDTO createTaskStatus(TaskStatusCreateDTO taskStatusData) {
        var taskStatus = taskStatusMapper.map(taskStatusData);
        taskStatusRepository.save(taskStatus);

        return taskStatusMapper.map(taskStatus);
    }

    public TaskStatusDTO updateTaskStatus(Long id, TaskStatusUpdateDTO taskStatusData) {
        var taskStatus = getById(id);
        taskStatusMapper.update(taskStatusData, taskStatus);
        taskStatusRepository.save(taskStatus);

        return taskStatusMapper.map(taskStatus);
    }

    public void deleteTaskStatus(Long id) {
        if (!taskStatusRepository.existsById(id)) {
            throw new ResourceNotFoundException(String.format("Task status with id '%s' not found", id));
        }

        taskStatusRepository.deleteById(id);
    }

    private TaskStatus getById(Long id) {
        return taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String
                        .format("TaskStatus with id '%d' not found", id)));
    }
}

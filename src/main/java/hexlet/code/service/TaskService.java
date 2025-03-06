package hexlet.code.service;

import hexlet.code.dto.task.TaskCreateDTO;
import hexlet.code.dto.task.TaskDTO;
import hexlet.code.dto.task.TaskParamsDTO;
import hexlet.code.dto.task.TaskUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.model.Task;
import hexlet.code.repository.TaskRepository;
import hexlet.code.specification.TaskSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    private final TaskMapper taskMapper;

    private final TaskSpecification taskSpecification;

    public List<TaskDTO> getAllTasks(TaskParamsDTO params) {
        var specification = taskSpecification.build(params);
        var tasks = taskRepository.findAll(specification);

        return tasks.stream()
                .map(taskMapper::map)
                .toList();
    }

    public TaskDTO getTaskById(Long id) {

        return taskMapper.map(getById(id));
    }

    public TaskDTO createTask(TaskCreateDTO taskCreateDTO) {
        var task = taskMapper.map(taskCreateDTO);
        taskRepository.save(task);

        return taskMapper.map(task);
    }

    public TaskDTO updateTask(Long id, TaskUpdateDTO taskUpdateDTO) {
        var task = getById(id);
        taskMapper.update(taskUpdateDTO, task);
        taskRepository.save(task);

        return taskMapper.map(task);
    }

    public void deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new ResourceNotFoundException(String.format("Task with id '%s' not found", id));
        }

        taskRepository.deleteById(id);
    }

    private Task getById(Long id) {

        return taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Task with id '%s' not found", id)));
    }
}

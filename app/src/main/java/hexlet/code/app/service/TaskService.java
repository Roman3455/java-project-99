package hexlet.code.app.service;

import hexlet.code.app.dto.task.TaskCreateDTO;
import hexlet.code.app.dto.task.TaskDTO;
import hexlet.code.app.dto.task.TaskParamsDTO;
import hexlet.code.app.dto.task.TaskUpdateDTO;
import hexlet.code.app.exception.ResourceNotFoundException;
import hexlet.code.app.mapper.TaskMapper;
import hexlet.code.app.model.entity.Task;
import hexlet.code.app.repository.TaskRepository;
import hexlet.code.app.specification.TaskSpecification;
import jakarta.transaction.Transactional;
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

    @Transactional
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

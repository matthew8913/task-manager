package ru.effective_mobile.task_manager.service;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.effective_mobile.task_manager.dto.PartialUpdateTaskRequest;
import ru.effective_mobile.task_manager.dto.TaskRequest;
import ru.effective_mobile.task_manager.dto.TaskResponse;
import ru.effective_mobile.task_manager.dto.UpdateTaskStatusRequest;
import ru.effective_mobile.task_manager.entities.Task;
import ru.effective_mobile.task_manager.entities.User;
import ru.effective_mobile.task_manager.repository.TaskRepository;
import ru.effective_mobile.task_manager.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    /**
     * Создает новую задачу.
     *
     * @param taskRequest Запрос на создание задачи.
     * @return Ответ с созданной задачей.
     */
    public TaskResponse createTask(TaskRequest taskRequest) {
        Task newTask = new Task();
        newTask.setTitle(taskRequest.getTitle());
        newTask.setDescription(taskRequest.getDescription());
        newTask.setStatus(taskRequest.getStatus());
        newTask.setPriority(taskRequest.getPriority());
        newTask.setCreatedAt(LocalDateTime.now());
        newTask.setUpdatedAt(LocalDateTime.now());

        User author = userRepository.findById(taskRequest.getAuthorId())
                .orElseThrow(() -> new IllegalArgumentException("Author not found"));
        newTask.setAuthor(author);

        if (taskRequest.getAssigneeId() != null) {
            User assignee = userRepository.findById(taskRequest.getAssigneeId())
                    .orElseThrow(() -> new IllegalArgumentException("Assignee not found"));
            newTask.setAssignee(assignee);
        }
        Task savedTask = taskRepository.save(newTask);
        return mapToTaskResponse(savedTask);
    }

    /**
     * Частично обновляет задачу.
     *
     * @param id          Идентификатор задачи.
     * @param taskRequest Запрос на обновление задачи.
     * @return Ответ с обновленной задачей.
     */
    public TaskResponse partialUpdateTask(Long id, PartialUpdateTaskRequest taskRequest) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        if (taskRequest.getTitle() != null) {
            task.setTitle(taskRequest.getTitle());
        }
        if (taskRequest.getDescription() != null) {
            task.setDescription(taskRequest.getDescription());
        }
        if (taskRequest.getStatus() != null) {
            task.setStatus(taskRequest.getStatus());
        }
        if (taskRequest.getPriority() != null) {
            task.setPriority(taskRequest.getPriority());
        }
        if (taskRequest.getAssigneeId() != null) {
            User assignee = userRepository.findById(taskRequest.getAssigneeId())
                    .orElseThrow(() -> new IllegalArgumentException("Assignee not found"));
            task.setAssignee(assignee);
        }

        task.setUpdatedAt(LocalDateTime.now());

        Task updatedTask = taskRepository.save(task);
        return mapToTaskResponse(updatedTask);
    }

    /**
     * Полностью обновляет задачу.
     *
     * @param id          Идентификатор задачи.
     * @param taskRequest Запрос на обновление задачи.
     * @return Ответ с обновленной задачей.
     */
    public TaskResponse fullUpdateTask(Long id, TaskRequest taskRequest) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        task.setTitle(taskRequest.getTitle());
        task.setDescription(taskRequest.getDescription());
        task.setStatus(taskRequest.getStatus());
        task.setPriority(taskRequest.getPriority());

        User author = userRepository.findById(taskRequest.getAuthorId())
                .orElseThrow(() -> new IllegalArgumentException("Author not found"));
        task.setAuthor(author);

        if (taskRequest.getAssigneeId() != null) {
            User assignee = userRepository.findById(taskRequest.getAssigneeId())
                    .orElseThrow(() -> new IllegalArgumentException("Assignee not found"));
            task.setAssignee(assignee);
        } else {
            task.setAssignee(null);
        }

        task.setUpdatedAt(LocalDateTime.now());

        Task updatedTask = taskRepository.save(task);
        return mapToTaskResponse(updatedTask);
    }

    /**
     * Удаляет задачу по идентификатору.
     *
     * @param id Идентификатор задачи.
     */
    public void deleteTask(Long id) {
        if(taskRepository.existsById(id)) {
            taskRepository.deleteById(id);
        }else{
            throw new IllegalArgumentException("Task not found");
        }
    }

    /**
     * Получает задачу по идентификатору.
     *
     * @param id Идентификатор задачи.
     * @return Ответ с задачей.
     */
    public TaskResponse getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));
        return mapToTaskResponse(task);
    }

    /**
     * Получает список задач по идентификатору автора с пагинацией.
     *
     * @param authorId Идентификатор автора.
     * @param pageable Объект пагинации.
     * @return Страница с задачами.
     */
    public Page<TaskResponse> getTasksByAuthor(Long authorId, Pageable pageable) {
        Page<Task> tasks = taskRepository.findByAuthorId(authorId, pageable);
        return tasks.map(this::mapToTaskResponse);
    }

    /**
     * Получает список задач по идентификатору исполнителя с пагинацией.
     *
     * @param assigneeId Идентификатор исполнителя.
     * @param pageable   Объект пагинации.
     * @return Страница с задачами.
     */
    public Page<TaskResponse> getTasksByAssignee(Long assigneeId, Pageable pageable) {
        Page<Task> tasks = taskRepository.findByAssigneeId(assigneeId, pageable);
        return tasks.map(this::mapToTaskResponse);
    }

    /**
     * Получает все задачи с пагинацией.
     *
     * @param pageable Объект пагинации.
     * @return Страница с задачами.
     */
    public Page<TaskResponse> getAllTasks(Pageable pageable) {
        Page<Task> tasks = taskRepository.findAll(pageable);
        return tasks.map(this::mapToTaskResponse);
    }

    /**
     * Преобразует сущность Task в DTO TaskResponse.
     *
     * @param task Сущность Task.
     * @return DTO TaskResponse.
     */
    private TaskResponse mapToTaskResponse(Task task) {
        TaskResponse response = new TaskResponse();
        response.setId(task.getId());
        response.setTitle(task.getTitle());
        response.setDescription(task.getDescription());
        response.setStatus(task.getStatus());
        response.setPriority(task.getPriority());
        response.setAuthorId(task.getAuthor().getId());
        if (task.getAssignee() != null) {
            response.setAssigneeId(task.getAssignee().getId());
        }
        response.setCreatedAt(task.getCreatedAt());
        response.setUpdatedAt(task.getUpdatedAt());
        return response;
    }

    public boolean isAssignee(String username, Long taskId) {
         Task task = taskRepository.findById(taskId).orElseThrow(() -> new IllegalArgumentException("Task not found"));
         return task.getAssignee().getEmail().equals(username);}

    public TaskResponse updateTaskStatus(Long id, UpdateTaskStatusRequest updateTaskStatusRequest) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Task not found"));
        task.setStatus(updateTaskStatusRequest.getStatus());
        task.setUpdatedAt(LocalDateTime.now());
        Task updatedTask = taskRepository.save(task);
        return mapToTaskResponse(updatedTask);
    }
}
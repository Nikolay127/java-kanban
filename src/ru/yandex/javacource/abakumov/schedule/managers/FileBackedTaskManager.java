package ru.yandex.javacource.abakumov.schedule.managers;

import ru.yandex.javacource.abakumov.schedule.exceptions.ManagerSaveException;
import ru.yandex.javacource.abakumov.schedule.tasks.*;

import java.io.*;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {

    private static final String FILE_HEADER = "id,type,name,status,description,epic,startTime,endTime,duration";
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;

    }

    protected void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(FILE_HEADER);
            writer.newLine();

            // Stream API для задач
            getTasksForFile().values().stream()
                    .map(this::taskOrEpicToString)
                    .forEach(taskString -> {
                        try {
                            writer.write(taskString);
                            writer.newLine();
                        } catch (IOException e) {
                            throw new ManagerSaveException("Can't save task to file: " + file.getName(), e);
                        }
                    });

            // Stream API для подзадач
            getSubtasksForFile().values().stream()
                    .map(this::subtaskToString)
                    .forEach(subtaskString -> {
                        try {
                            writer.write(subtaskString);
                            writer.newLine();
                        } catch (IOException e) {
                            throw new ManagerSaveException("Can't save subtask to file: " + file.getName(), e);
                        }
                    });

            // Stream API для эпиков
            getEpicsForFile().values().stream()
                    .map(this::taskOrEpicToString)
                    .forEach(epicString -> {
                        try {
                            writer.write(epicString);
                            writer.newLine();
                        } catch (IOException e) {
                            throw new ManagerSaveException("Can't save epic to file: " + file.getName(), e);
                        }
                    });
        } catch (IOException e) {
            throw new ManagerSaveException("Can't save to file: " + file.getName(), e);
        }
    }

    private String taskOrEpicToString(Task task) {
        return task.getId() + "," +
                task.getType() + "," +
                task.getName() + "," +
                task.getStatus() + "," +
                task.getDescription() + "," +
                " - " + "," +
                task.getStartTime() + "," +
                task.getEndTime() + "," +
                task.getDuration();
    }

    private String subtaskToString(Subtask task) {
        return task.getId() + "," +
                task.getType() + "," +
                task.getName() + "," +
                task.getStatus() + "," +
                task.getDescription() + "," +
                task.getEpicID() + "," +
                task.getStartTime() + "," +
                task.getEndTime() + "," +
                task.getDuration();
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        final FileBackedTaskManager taskManager = new FileBackedTaskManager(file);
        try {
            final String csv = Files.readString(file.toPath());
            final String[] lines = csv.split(System.lineSeparator());

            // Обработка строк с задачами, подзадачами и эпиками
            int generatorId = Arrays.stream(lines, 1, lines.length)
                    .filter(line -> line != null && !line.trim().isEmpty())
                    .map(line -> {
                        Task task = taskFromString(line);
                        taskManager.addAnyTask(task);
                        return task.getId();
                    })
                    .max(Integer::compareTo)
                    .orElse(0);

            // Установление связи между эпиками и подзадачами
            taskManager.subtasks.values().stream()
                    .forEach(subtask -> {
                        Epic epic = taskManager.epics.get(subtask.getEpicID());
                        if (epic != null) {
                            epic.addSubtaskId(subtask.getId());
                        }
                    });

            taskManager.generatorId = generatorId;
        } catch (IOException e) {
            throw new ManagerSaveException("Can't read from file: " + file.getName(), e);
        }
        return taskManager;
    }

    protected void addAnyTask(Task task) {
        final int id = task.getId();
        switch (task.getType()) {
            case TASK:
                tasks.put(id, task);
                break;
            case SUBTASK:
                subtasks.put(id, (Subtask) task);
                break;
            case EPIC:
                epics.put(id, (Epic) task);
                break;
        }
    }

    private static Task taskFromString(String value) {
        final String[] values = value.split(",");
        final int id = Integer.parseInt(values[0]);
        final TaskType type = TaskType.valueOf(values[1]);
        final String name = values[2];
        final Status status = Status.valueOf(values[3]);
        final String description = values[4];
        final LocalDateTime startTime = LocalDateTime.parse(values[6]);
        final Duration duration = Duration.parse(String.valueOf(values[8]));
        if (type == TaskType.TASK) {
            return new Task(id, name, description, status, startTime, duration);
        }
        if (type == TaskType.SUBTASK) {
            final int epicId = Integer.parseInt(values[5]);
            return new Subtask(id, name, description, status, epicId, startTime, duration);
        }
        return new Epic(id, name, description);
    }

    @Override
    public int addTask(Task task) {
        if (super.addTask(task) == -1) {
            return -1;
        }
        save();
        return task.getId();
    }

    @Override
    public int addEpic(Epic epic) {
        if (super.addEpic(epic) == -1) {
            return -1;
        }
        save();
        return epic.getId();
    }

    @Override
    public Integer addSubtask(Subtask subtask) {
        if (super.addSubtask(subtask) == -1) {
            return -1;
        }
        save();
        return subtask.getId();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void deleteSubtask(int id) {
        super.deleteSubtask(id);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }
}

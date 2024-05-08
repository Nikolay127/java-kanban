package ru.yandex.javacource.abakumov.schedule.managers;

import ru.yandex.javacource.abakumov.schedule.tasks.Epic;
import ru.yandex.javacource.abakumov.schedule.tasks.Status;
import ru.yandex.javacource.abakumov.schedule.tasks.Subtask;
import ru.yandex.javacource.abakumov.schedule.tasks.Task;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {

    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;

    }

    private void save() {
        Map<Integer, Task> tasksForFile = getTasksForFile();
        Map<Integer, Epic> epicsForFile = getEpicsForFile();
        Map<Integer, Subtask> subtasksForFile = getSubtasksForFile();
        Map<Integer, Task> allTasks = new HashMap<>(); //мапа, содержащая все задачи
        allTasks.putAll(tasksForFile);
        allTasks.putAll(epicsForFile);
        allTasks.putAll(subtasksForFile);
        int idsQuantityForFile = getIdsQuantity();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(String.valueOf(file)))) {
            writer.write("id,type,name,status,description,epic \n");
            for (var i = 1; i <= idsQuantityForFile; i++) {
                Task task = allTasks.get(i);
                writer.write(task.getId() + ",");
                writer.write(task.getClass().getSimpleName() + ",");
                writer.write(task.getName() + ",");
                writer.write(task.getStatus() + ",");
                writer.write(task.getDescription());
                if (task instanceof Subtask) {
                    writer.write("," + ((Subtask) task).getEpicID());
                }
                writer.write("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
        try (BufferedReader reader = new BufferedReader(new FileReader(file))){
            String columnNamesLine = reader.readLine();//игнорируем первую строку с названием столбцов
            while (reader.ready()) {
                //Сначала считаем строку(одну задачу) и поделим на детали задачи
                String line = reader.readLine();
                String[] taskDetails = line.split(",");
                //записываем детали задачи в нужные переменные
                int id, epicId = 0;
                String type = null, name = null, description = null;
                Status status = null;
                for (var i = 0; i < taskDetails.length; i++) {
                    switch (i) {
                        case 0:
                            id = Integer.parseInt(taskDetails[i]);
                            break;
                        case 1:
                            type = taskDetails[i];
                            break;
                        case 2:
                            name = taskDetails[i];
                            break;
                        case 3:
                            status = Status.valueOf(taskDetails[i]);
                            break;
                        case 4:
                            description = taskDetails[i];
                            break;
                    }
                    if (i == 5) {
                        epicId = Integer.parseInt(taskDetails[i]);
                    }
                }
                //вносим задачу в нужную мапу
                switch (type) {
                    case "Task":
                        fileBackedTaskManager.addTask(new Task(name, description, status));
                        break;
                    case "Epic":
                        fileBackedTaskManager.addEpic(new Epic(name, description));
                        break;
                    case "Subtask":
                        fileBackedTaskManager.addSubtask(new Subtask(epicId, name, description, status));
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileBackedTaskManager;
    }

    @Override
    public int addTask(Task task) {
        super.addTask(task);
        save();
        return task.getId();
    }

    @Override
    public int addEpic(Epic epic) {
        super.addEpic(epic);
        save();
        return epic.getId();
    }

    @Override
    public Integer addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
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

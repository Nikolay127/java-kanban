package ru.yandex.javacource.abakumov.schedule;

import ru.yandex.javacource.abakumov.schedule.managers.*;
import ru.yandex.javacource.abakumov.schedule.tasks.*;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new InMemoryTaskManager(new InMemoryHistoryManager());
        taskManager.addTask(new Task("Трекер задач", "Написать программу трекер-задач для четвертого спринта", Status.IN_PROGRESS));
        taskManager.addTask(new Task("Купить продукты", "1.Молоко, 2.Хлеб, 3. Печенье", Status.NEW));
        taskManager.addEpic(new Epic("Закончить курс практикума", "Пройти все разделы"));
        taskManager.addSubtask(new Subtask(3,"Закончить 4й спринт", "Сдать финальное задание 4го спринта", Status.NEW));
        taskManager.addSubtask(new Subtask(3, "Закончить 5й спринт", "Сделать финальное задание 5го спринта", Status.NEW));
        taskManager.addEpic(new Epic("Электронная подпись", "Контроль электронных подписей"));
        taskManager.addSubtask(new Subtask(6,"Установка ЭЦП", "Установить эцп Пудову", Status.NEW));
        taskManager.getTask(1);
        taskManager.getTask(2);
        taskManager.getEpic(6);
        taskManager.getSubtask(7);
        printAllTasks(taskManager);
    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Task epic : manager.getAllEpics()) {
            System.out.println(epic);
        }
        System.out.println("Подзадачи:");
        for (Task subtask : manager.getAllSubtasks()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        for (Task task : manager.getInMemoryHistoryManager().getHistory()) {
            System.out.println(task);
        }
    }
}

package ru.yandex.javacource.abakumov.schedule;

import ru.yandex.javacource.abakumov.schedule.managers.*;
import ru.yandex.javacource.abakumov.schedule.tasks.*;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new InMemoryTaskManager(new InMemoryHistoryManager());
        taskManager.addTask(new Task("Трекер задач", "Написать программу трекер-задач для четвертого спринта", Status.IN_PROGRESS)); //id-1
        taskManager.addTask(new Task("Купить продукты", "1.Молоко, 2.Хлеб, 3. Печенье", Status.NEW)); //id-2
        taskManager.addEpic(new Epic("Закончить курс практикума", "Пройти все разделы")); //id-3
        taskManager.addSubtask(new Subtask(3,"Закончить 4й спринт", "Сдать финальное задание 4го спринта", Status.NEW)); //id-4
        taskManager.addSubtask(new Subtask(3, "Закончить 5й спринт", "Сделать финальное задание 5го спринта", Status.NEW)); //id-5
        taskManager.addSubtask(new Subtask(3, "Закончить 6й спринт", "Сделать финальное задание 6го спринта", Status.NEW)); //id-6
        taskManager.addEpic(new Epic("Электронная подпись", "Контроль электронных подписей")); //id-7
        taskManager.getTask(1);
        taskManager.getTask(2);
        taskManager.getEpic(7);
        taskManager.getSubtask(6);
        taskManager.getEpic(7);
        taskManager.getTask(1);
        taskManager.getEpic(3);

        //После каждого запроса выведите историю и убедитесь, что в ней нет повторов.
        List<Task> tasks = taskManager.getHistory();

            for (Task task : tasks) {
                System.out.println(task);
            }
        System.out.println();
        System.out.println("Удалите задачу, которая есть в истории, и проверьте, что при печати она не будет выводиться");
        System.out.println();
        //Удалите задачу, которая есть в истории, и проверьте, что при печати она не будет выводиться.
        taskManager.deleteTask(1);
        tasks = taskManager.getHistory();
        for (Task task : tasks) {
            System.out.println(task);
        }
        System.out.println();
        System.out.println("Удалите эпик с тремя подзадачами и убедитесь, что из истории удалился как сам эпик, так и все его подзадачи.");
        System.out.println();
        //Удалите эпик с тремя подзадачами и убедитесь, что из истории удалился как сам эпик, так и все его подзадачи.
        taskManager.deleteEpic(3);
        tasks = taskManager.getHistory();
        for (Task task : tasks) {
            System.out.println(task);
        }
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
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }
}

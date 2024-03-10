package ru.yandex.javacource.abakumov.schedule.managers;

import ru.yandex.javacource.abakumov.schedule.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{

    private List<Task> tasksGetHistory = new ArrayList<>(); //список последних десяти полученных задач

    @Override
    public void add(Task task) { //помечает задачи как просмотренные
        checkSpaceForTheTasksGetHistoryList(); //очищаем место, если необходимо перед добавлением
        tasksGetHistory.add(task);
    }

    @Override
    public void checkSpaceForTheTasksGetHistoryList() { //очищаем место под новую задачу, если необходимо
        if (tasksGetHistory.size() == 10) { //если уже есть 10 просмотров, то удаляем самый первый(самый старый)
            tasksGetHistory.remove(0);
        }
    }
    @Override
    public List<Task> getHistory() { //возвращает список просмотренных задач
        return tasksGetHistory;
    }

}

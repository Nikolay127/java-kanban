package ru.yandex.javacource.abakumov.schedule.managers;

import ru.yandex.javacource.abakumov.schedule.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final List<Task> history = new ArrayList<>(); //список последних десяти полученных задач
    private static final int HISTORY_MAX_SIZE = 10;

    @Override
    public void add(Task task) { //помечает задачи как просмотренные
        if (task == null) {
            return;
        }
        checkSpaceForTheTasksGetHistoryList(); //очищаем место, если необходимо перед добавлением
        history.add(task);
    }

    private void checkSpaceForTheTasksGetHistoryList() { //очищаем место под новую задачу, если необходимо
        if (history.size() == HISTORY_MAX_SIZE) { //если уже есть 10 просмотров, то удаляем самый первый(самый старый)
            history.removeFirst();
        }
    }
    @Override
    public List<Task> getHistory() { //возвращает список просмотренных задач
        return history;
    }

}

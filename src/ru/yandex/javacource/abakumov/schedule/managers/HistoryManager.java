package ru.yandex.javacource.abakumov.schedule.managers;

import ru.yandex.javacource.abakumov.schedule.tasks.Task;
import java.util.List;

public interface HistoryManager {

    void add(Task task);

    List<Task> getHistory();

    void checkSpaceForTheTasksGetHistoryList();

}

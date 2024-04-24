package ru.yandex.javacource.abakumov.schedule.managers;

import ru.yandex.javacource.abakumov.schedule.tasks.Task;
import java.util.List;

public interface HistoryManager {

    void addToHistory(Task task);

    void rewriteNode(Node<Task> node);

    void removeTaskFromHistory(int id);

    List<Task> getHistory();

    void deleteHistory();
}

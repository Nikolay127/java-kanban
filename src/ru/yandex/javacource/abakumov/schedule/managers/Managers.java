package ru.yandex.javacource.abakumov.schedule.managers;

public class Managers {

    static TaskManager getDefault() {
        return new InMemoryTaskManager(getDefaultHistory());
    }

    static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

}

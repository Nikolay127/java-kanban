package ru.yandex.javacource.abakumov.schedule.tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class Task implements Comparable<Task> {

    protected String name;
    protected String description;
    protected int id;
    protected Status status;
    protected LocalDateTime startTime = LocalDateTime.MIN;
    protected Duration duration = Duration.ZERO;

    public Task(String name, String description, Status status, LocalDateTime startTime, Duration duration) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(int id, String name, String description, Status status, LocalDateTime startTime, Duration duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
    }

    //Конструктор для эпика
    public Task(String name, String description, Status status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }

    //Конструктор для эпика
    public Task(int id, String name, String description, Status status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime getEndTime() {
        return startTime.plus(duration.toMinutes(), ChronoUnit.MINUTES);
    }

    public TaskType getType() {
        return TaskType.TASK;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public int compareTo(Task other) {
        return this.startTime.compareTo(other.startTime);
    }
    /*
    Если удалить реализацию Comparable, и оставить только компаратор в TreeSet, то idea ругается при добавлении
    любой второй задачи: задача, эпик или субтаска
    "ClassCastException: class Task cannot be cast to class java.lang.Comparable"
    Как решить данную проблему не понял, потому пока оставил так. Буду признателен, если подскажете, как это исправить
     */

    public void setStatus(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "ru.yandex.javacource.abakumov.schedule.tasks.Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

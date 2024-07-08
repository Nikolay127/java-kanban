package ru.yandex.javacource.abakumov.schedule.tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task implements Comparable<Task> {

    protected String name;
    protected String description;
    protected int id;
    protected Status status;
    protected LocalDateTime startTime = LocalDateTime.MIN;
    protected LocalDateTime endTime = LocalDateTime.MAX;
    protected Duration duration = Duration.ZERO;

    public Task(String name, String description, Status status, LocalDateTime startTime, Duration duration) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
        setEndTime(duration);
    }

    public Task(int id, String name, String description, Status status, LocalDateTime startTime, Duration duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
        setEndTime(duration);
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

    public void setEndTime(Duration duration) { //Написать реализацию, возможно нужен иной перевод
        endTime = startTime.plus(duration);
    }

    public LocalDateTime getEndTime() {
        return endTime;
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

    public void setStatus(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    @Override
    public int compareTo(Task other) {
        return this.startTime.compareTo(other.startTime);
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

package ru.yandex.javacource.abakumov.schedule.exceptions;

public class TaskValidationException extends IllegalArgumentException {

    public TaskValidationException(String message) {
        super(message);
    }

}

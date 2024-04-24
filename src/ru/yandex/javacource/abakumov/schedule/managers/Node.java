package ru.yandex.javacource.abakumov.schedule.managers;

public class Node<T> {

    public T data;
    public Node<T> next;
    public Node<T> prev;

    public Node(T data) {
        this.data = data;
        this.next = null;
        this.prev = null;
    }

}

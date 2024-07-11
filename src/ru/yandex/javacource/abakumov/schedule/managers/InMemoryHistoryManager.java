package ru.yandex.javacource.abakumov.schedule.managers;

import ru.yandex.javacource.abakumov.schedule.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private Map<Integer, Node<Task>> history = new HashMap<>();
    private Node<Task> head;
    private Node<Task> tail;

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        final int id = task.getId();
        remove(id);
        linkLast(task); //добавляем ноду в качестве хвоста
        history.put(id, tail);
    }

    @Override
    public void remove(int id) { //удаляем ноду из истории
        final Node<Task> node = history.remove(id);
        if (node == null) {
            return;
        }
        removeNode(node);
    }

    @Override
    public List<Task> getHistory() { //формируем лист истории всех типов задач
        List<Task> publicHistory = new ArrayList<>();
        if (!history.isEmpty()) {
            Node<Task> node = head;
            while (node.next != null) {
                publicHistory.add(node.task);
                node = node.next;
            }
            publicHistory.add(node.task);
        }
        return publicHistory;
    }

    private void linkLast(Task task) { //актуализируем ссылки при добавлении новой ноды
        final Node<Task> node = new Node<>(task, tail, null); //сразу указываем ссылку на старый хвост
        if (head == null) {
            head = node;
        } else {
            tail.next = node;
        }
        tail = node;
    }

    private void removeNode(Node<Task> node) { //удаляем ссылки у удаленной из истории ноды
        if (node.prev != null) {
            node.prev.next = node.next;
            if (node.next == null) {
                tail = node.prev;
            } else {
                node.next.prev = node.prev;
            }
        } else {
            head = node.next;
            if (head == null) {
                tail = null;
            } else {
                head.prev = null;
            }
        }
    }
}

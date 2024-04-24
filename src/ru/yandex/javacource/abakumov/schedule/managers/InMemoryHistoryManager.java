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
    public List<Task> getHistory() {
        List<Task> publicHistory = new ArrayList<>();
        if (!history.isEmpty()) {
            publicHistory.add(head.data);
            Node<Task> node = head;
            while (node.next != null) {
                publicHistory.add(node.next.data);
                node = node.next;
            }
        }
        return publicHistory;
    }


    @Override
    public void addToHistory(Task task) {
        if (history.isEmpty() || (history.size() == 1 && history.containsKey(task.getId()))) {
            /*
            Если мапа пуста или состоит из одного такого же элемента, который передаём
            */
            head = new Node<>(task); //преобразуем задачу в ноду и записываем как голову
            history.put(task.getId(), head);
        } else if ((history.size() == 1) && !(history.containsKey(task.getId()))) { //в мапе один элемент и не тот, что передаём
            tail = new Node<>(task);
            head.next = tail;
            tail.prev = head;
            history.put(task.getId(), tail);
        } else if (!(history.containsKey(task.getId()))) { //в мапе нет такого элемента
            Node<Task> newTail = new Node<>(task); //создаём новую ноду для таски, которая будет новым хвостом
            Node<Task> duplicateNode = tail; //запоминаем текущий хвост
            tail = newTail; //записываем новую ноду как новый хвост
            duplicateNode.next = tail; //даём ссылку на новый хвост старому хвосту
            tail.prev = duplicateNode; //записываем ссылку на старый хвост в новый
            history.put(task.getId(), tail); //помещаем в историю просмотра
        } else { //в мапе уже есть такая задача
            rewriteNode(history.get(task.getId())); //удаляем все ссылки у требуемой ноды
            Node<Task> node = history.get(task.getId()); //получаем ноду без ссылок на другие ноды
            Node<Task> duplicateNode = tail; //получаем текущий хвост
            duplicateNode.next = node; //прописываем в текущем хвосте ссылку на новый хвост(нашу ноду)
            node.prev = duplicateNode; //прописываем ноде ссылку на старый хвост
            tail = node; //записываем ноду в качестве хвоста
            /*
            Кладём ноду в то же место(в тот же id), в котором она и была. Только теперь с новыми указателями
             */
            history.put(task.getId(), node);
        }
    }

    @Override
    public void rewriteNode(Node<Task> node) {
        if (node == head) {
            node.next.prev = null;
            head = node.next;
            node.next = null;
            history.put(head.data.getId(), head);
        } else if (node == tail) {
            node.prev.next = null;
            tail = node.prev;
            node.prev = null;
            history.put(node.data.getId(), tail);
        } else {
            node.next.prev = node.prev;
            node.prev.next = node.next;
            node.next = null;
            node.prev = null;
        }
    }

    @Override
    public void removeTaskFromHistory(int id) {
        Node<Task> node = history.get(id);
        if (history.size() == 1) {
            head = null;
        } else if (node == head) {
            node.next.prev = null;
            head = node.next;
            node.next = null;
        } else if (node == tail) {
            node.prev.next = null;
            tail = node.prev;
            node.prev = null;
        } else {
            node.next.prev = node.prev;
            node.prev.next = node.next;
            node.next = null;
            node.prev = null;
        }
        history.remove(id);
    }

    @Override
    public void deleteHistory() {
        head = null;
        tail = null;
        history.clear();
    }
}

import java.util.ArrayList;
import java.util.HashMap;

public class TasksManager { //класс для хранения задач и операций над ними

    public static HashMap<Integer, Task> tasks = new HashMap<>();
    public static HashMap<Integer, Epic> epics = new HashMap<>();
    public static HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private static int tasksCounter = 1;


    public static void addTask(Task task){ //добавляем обычную задачу
        task.setId(tasksCounter++);
        tasks.put(task.getId(), task);
    }

    public static void addEpic(Epic epic){ //добавляем обычную задачу
        epic.setId(tasksCounter++);
        epics.put(epic.getId(), epic);
    }

    public static void addSubtask(Subtask subtask){ //добавляем обычную задачу
        subtask.setId(tasksCounter++);
        subtasks.put(subtask.getId(), subtask);
    }

    public static void updateTask(int id, Task task) { //обновляем уже имеющуюся задачу
        task.setId(id);
        tasks.put(id, task);
    }

    public static void updateEpic(int id, Epic epic) { //обновляем уже имеющийся эпик
        epic.setId(id);
        epics.put(epic.getId(), epic);
    }

    public static void updateSubtask(int id, Subtask subtask) { //обновляем уже имеющуюся задачу путём замены
        subtask.setId(id);
        subtasks.put(subtask.getId(), subtask);
        updateEpicStatus(subtask.getEpicID()); //проверяем статус эпика после обновления задачи
    }

    static void updateEpicStatus(int epicID){ //устанавливаем новый статус эпика по его идентификатору
        ArrayList<Subtask> relatedSubtasks = new ArrayList<>(); //список подзадач, подходящих под id нужного эпика
        for (Subtask subtask : subtasks.values()) {
            if (subtask.getEpicID() == epicID) {
                relatedSubtasks.add(subtask); //заполняем список подзадачами
            }
        }
        if (relatedSubtasks.isEmpty()) { //проверяем, есть ли хоть одна подзадача
            epics.get(epicID).setStatus(Status.NEW);
            return;
        }
        boolean isEpicDone = true;
        for (Subtask subtask : relatedSubtasks) { //проверка на статус DONE(все подзадачи done)
            if (subtask.status != Status.DONE){
                isEpicDone = false;
                break;
            }
        }
        boolean isEpicNew = true;
        for (Subtask subtask : relatedSubtasks) { //проверка на статус NEW(все подзадачи new)
            if (subtask.status != Status.NEW){
                isEpicNew = false;
                break;
            }
        }
        if (isEpicDone) {
            epics.get(epicID).setStatus(Status.DONE);
        } else if (isEpicNew) {
            epics.get(epicID).setStatus(Status.NEW);
        } else { //если не все подзадачи NEW и не все подзадачи DONE, то эпик в статусе IN_PROGRESS
            epics.get(epicID).status = Status.IN_PROGRESS;
        }
    }

    public static HashMap<Integer, Task> getAllTasks(){ //получаем список обычных задач
        if (tasks.isEmpty()){
            System.out.println("В списке нет ни одной задачи");
            return null;
        }
        System.out.println("Список всех обычных задач:\n");
        tasks.forEach((key, value) -> {
            System.out.println("Уникальный номер задачи: " + key);
            System.out.println("Подробное описание задачи:\n" + value + "\n");
        });
        System.out.println("На этом всё!\n");
        return tasks;
    }

    public static HashMap<Integer, Epic> getAllEpics(){ //получаем список обычных задач
        if (epics.isEmpty()){
            System.out.println("В списке нет ни одного эпика");
            return null;
        }
        System.out.println("Список всех эпиков:\n");
        epics.forEach((key, value) -> {
            System.out.println("Уникальный номер задачи: " + key);
            System.out.println("Подробное описание задачи:\n" + value + "\n");
        });
        System.out.println("На этом всё!\n");
        return epics;
    }

    public static HashMap<Integer, Subtask> getAllSubtasks(){ //получаем список обычных задач
        if (subtasks.isEmpty()){
            System.out.println("В списке нет ни одной подзадачи");
            return null;
        }
        System.out.println("Список всех подзадач:\n");
        subtasks.forEach((key, value) -> {
            System.out.println("Уникальный номер задачи: " + key);
            System.out.println("Подробное описание задачи:\n" + value + "\n");
        });
        System.out.println("На этом всё!\n");
        return subtasks;
    }

    public static Task getTask(Integer id){ //получаем конкретную обычную задачу
        if (!tasks.containsKey(id)) {
            System.out.println("Нет задачи с таким идентификатором");
            return null;
        }
        System.out.println("Задача с индентификатором \"" + id + "\":\n" + tasks.get(id));
        return tasks.get(id);
    }

    public static Epic getEpic(Integer id){ //получаем конкретную обычную задачу
        if (!epics.containsKey(id)) {
            System.out.println("Нет эпика с таким идентификатором");
            return null;
        }
        System.out.println("Эпик с индентификатором \"" + id + "\":\n" + epics.get(id));
        return epics.get(id);
    }

    public static Subtask getSubtask(Integer id){ //получаем конкретную обычную задачу
        if (!subtasks.containsKey(id)) {
            System.out.println("Нет задачи с таким идентификатором");
            return null;
        }
        System.out.println("Задача с индентификатором \"" + id + "\":\n" + subtasks.get(id));
        return subtasks.get(id);
    }

    public static void deleteTask(Integer id) { //удаляем конкретную простую задачу
        if (!tasks.containsKey(id)){
            System.out.println("Нет задачи с таким идентификатором");
            return;
        }
        tasks.remove(id);
        System.out.println("Задача с номером \"" + id + "\" успешно удалена.\n");
    }

    //вместе с эпиком удаляем все его подзадачи
    public static void deleteEpic(Integer id) { //удаляем конкретный эпик
        if (!epics.containsKey(id)){
            System.out.println("Нет эпика с таким идентификатором");
            return;
        }
        for (Subtask subtask : subtasks.values()) { //удаляем подзадачи, которые принадлежат эпику
            if(subtask.getEpicID() == id)
                subtasks.remove(subtask.getId());
        }
        epics.remove(id);
        System.out.println("Эпик с номером \"" + id + "\" успешно удален.\n");
    }

    public static void deleteSubtask(Integer id) { //удаляем конкретную подзадачу
        if (!subtasks.containsKey(id)){
            System.out.println("Нет подзадачи с таким идентификатором");
            return;
        }
        Subtask removingSubtask = subtasks.get(id);
        subtasks.remove(id);
        System.out.println("Подзадача с номером \"" + id + "\" успешно удалена.\n");
        updateEpicStatus(removingSubtask.getEpicID());//обновляем статус эпика после удаления задачи
    }

    public static void deleteAllTasks() { //удаляем все обычные задачи
        tasks.clear();
        System.out.println("Все обычные задачи удалены\n");
    }

    //Вместе со всеми эпиками удаляем и все подзадачи
    public static void deleteAllEpics() { //удаляем все эпики
        subtasks.clear();
        epics.clear();
        System.out.println("Все эпики удалены\n");
    }

    public static void deleteAllSubtasks() { //удаляем все подзадачи
        subtasks.clear();
        System.out.println("Все подзадачи удалены\n");
    }

}


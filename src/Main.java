public class Main {

    public static void main(String[] args) {
        TasksManager.addTask(new Task("Трекер задач", "Написать программу трекер-задач для четвертого спринта", Status.IN_PROGRESS)); //#1
        TasksManager.addTask(new Task("Купить продукты", "1.Молоко, 2.Хлеб, 3. Печенье", Status.NEW)); //#2
        TasksManager.addEpic(new Epic("Закончить курс практикума", "Пройти все разделы"));//#3
        TasksManager.addSubtask(new Subtask(3,"Закончить 4й спринт", "Сдать финальное задание 4го спринта", Status.NEW));//#4
        TasksManager.addSubtask(new Subtask(3, "Закончить 5й спринт", "Сделать финальное задание 5го спринта", Status.NEW));//#5
        TasksManager.addEpic(new Epic("Электронная подпись", "Контроль электронных подписей"));//#6
        TasksManager.addSubtask(new Subtask(6,"Установка ЭЦП", "Установить эцп Пудову", Status.NEW));//#7
        //проверяем вывод всех задач, эпиков и подзадач
        TasksManager.getAllTasks();
        TasksManager.getAllEpics();
        TasksManager.getAllEpics();
        //проверяем смену статуса эпика
        TasksManager.getEpic(6);
        TasksManager.getSubtask(7);
        TasksManager.updateSubtask(7, new Subtask(6,"Установка ЭЦП", "Установить эцп Пудову", Status.DONE));
        TasksManager.getEpic(6);
        TasksManager.getSubtask(7);
        //проверяем удаление задачи
        TasksManager.deleteTask(1);
        TasksManager.getAllTasks();
        //проверяем смену статуса эпика, если удалить единственную подзадачу
        TasksManager.deleteSubtask(7);
        TasksManager.getEpic(6);
        //проверяем удалится ли подзадача, если удалить эпик
        TasksManager.deleteEpic(6);
        TasksManager.getAllSubtasks();
    }
}

public class Subtask extends Task{

    private final int epicID; //принадлежность к эпику

    public Subtask(int epicID, String name, String description, Status status) {
        super(name, description, status);
        this.epicID = epicID;
    }

    public int getEpicID() {
        return epicID;
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + getId() +
                ", epicId=" + getEpicID() +
                ", status=" + status +
                '}';
    }
}

public class Player {
    String number,name,position,height,college;
    int weight,salary;

    public Player(String number, String name, String position, String height, String college, int weight, int salary) {
        this.number = number;
        this.name = name;
        this.position = position;
        this.height = height;
        this.college = college;
        this.weight = weight;
        this.salary = salary;
    }

    public String getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }

    public String getPosition() {
        return position;
    }

    public String getHeight() {
        return height;
    }

    public String getCollege() {
        return college;
    }

    public int getWeight() {
        return weight;
    }

    public int getSalary() {
        return salary;
    }
}

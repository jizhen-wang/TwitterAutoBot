public class Player implements Comparable<Player> {
    String number, name, position, height, college;
    int age, weight, tradeDays = 0;
    long salary;

    public int getTradeDays() {
        return tradeDays;
    }

    Player(String number, String name, String position, int age, String height, int weight, String college, long salary) {
        this.number = number;
        this.name = name;
        this.position = position;
        this.age = age;
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

    public long getSalary() {
        return salary;
    }

    public int getAge() {
        return age;
    }

    @Override
    public String toString() {
        return "Player{" +
                "number='" + number + '\'' +
                ", name='" + name + '\'' +
                ", position='" + position + '\'' +
                ", height='" + height + '\'' +
                ", college='" + college + '\'' +
                ", age=" + age +
                ", weight=" + weight +
                ", salary=" + salary +
                '}';
    }

    @Override
    public int compareTo(Player o) {
        return (int) (this.salary - o.salary);
    }
}

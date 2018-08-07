import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class Team {

    String name;
    List<Player> players;
    int totalSalary = 0, numOfPlayers;

    Team(String name, List<Player> players) {
        this.name = name;
        this.players = players;
        for (Player player : players) {
            totalSalary += player.salary;
        }
        numOfPlayers = players.size();
    }

    void addPlayer(String s) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.getDefault());
        String[] strings = s.split("@");
        String number = strings[0], name = strings[1], position = strings[2], height = strings[4], college = strings[6];
        int age = Integer.parseInt(strings[3]), weight = Integer.parseInt(strings[5]);
        long salary = 0;
        try {
            if (strings.length >= 8) {
                salary = (Long) formatter.parse(strings[7]);
            }
            players.add(new Player(number, name, position, age, height, weight, college, salary));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String toString() {
        String result = "";
        result += this.name + "\n";
        result += players.toString() + "\n";
        return result;
    }

    String getName() {
        return name;
    }

    List<Player> getPlayers() {
        return players;
    }

    int getTotalSalary() {
        return totalSalary;
    }

    int getNumOfPlayers() {
        return numOfPlayers;
    }

    public void sortPlayers() {
        players.sort((o1, o2) -> (int) (o2.salary - o1.salary));
    }
}

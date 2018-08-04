import java.util.List;

public class Team {
    String name;
    List<Player> players;
    int totalSalary = 0, numOfPlayers;

    public Team(String name, List<Player> players) {
        this.name = name;
        this.players = players;
        for (Player player:players){
            totalSalary+=player.salary;
        }
        numOfPlayers=players.size();
    }
}

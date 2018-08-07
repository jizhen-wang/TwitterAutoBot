import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

import java.util.Comparator;
import java.util.concurrent.ThreadLocalRandom;
import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static java.lang.Math.abs;

public class TwitterAutoBot {

    public static void main(String[] args) {
        while (true) {
            tweetLines();
        }
    }

    private static void tweetLines() {
        String line;
        createTweet();
        try {
            try (
                    InputStream fis = new FileInputStream("tweets.txt");
                    InputStreamReader isr = new InputStreamReader(fis, Charset.forName("Cp1252"));
                    BufferedReader br = new BufferedReader(isr)
            ) {
                while ((line = br.readLine()) != null) {
                    // Deal with the line
                    sendTweet(line);
                    System.out.println("Tweeting: " + line + "...");
                    try {
                        System.out.println("Sleeping for 10 seconds...");
                        Thread.sleep(10000); // every 30 minutes
                        // Thread.sleep(10000); // every 10 seconds
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void createTweet() {
        List<Team> teams = createTeams("roster.txt");
        List<String> templates = new ArrayList<>();
        templates.add("#team1 will send #player1 to #team2 for #player2 and cash, league sources tell ESPN.");
        templates.add("#team1 agreed to trade w/ #team2 to acquire #player2 for #player1 and cash, league sources tell ESPN.");
        templates.add("#team1 has traded #position1 #player1 to the #team2 for #player2 and cash, league source tells ESPN.");
        int teamIndex1 = randomInt(teams.size()), teamIndex2 = randomInt(teams.size());
        while (teamIndex1 == teamIndex2) {
            teamIndex2 = ThreadLocalRandom.current().nextInt(0, teams.size());
        }
        Team team1 = teams.get(teamIndex1 % teams.size());
        Team team2 = teams.get(teamIndex2 % teams.size());

        int playerIndex1 = 0, playerIndex2 = 0;
        boolean flag = false;

        for (playerIndex1 = 0; playerIndex1 < team1.players.size(); playerIndex1++) {
            for (playerIndex2 = 0; playerIndex2 < team2.players.size(); playerIndex2++) {
                Player player1 = team1.players.get(playerIndex1), player2 = team2.players.get(playerIndex2);
                if (checkTrade(player1, player2)) {
                    flag = true;
                    break;
                }
            }
            if (flag) {
                break;
            }
        }

        try {
            PrintWriter writer = new PrintWriter("tweets.txt");
            String tweet = templates.get(randomInt(templates.size()));
            tweet = tweet.replaceFirst("#team1", team1.name);
            tweet = tweet.replaceFirst("#team2", team2.name);
            tweet = tweet.replaceFirst("#player1", team1.players.get(playerIndex1).name);
            tweet = tweet.replaceFirst("#position1", team1.players.get(playerIndex1).position);
            tweet = tweet.replaceFirst("#position2", team1.players.get(playerIndex2).position);
            if (randomInt(100) % 2 == 0) {
                tweet = tweet.replaceFirst(" and cash", "");
            }
            writer.println(tweet.replaceFirst("#player2", team2.players.get(playerIndex2).name));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* Generate all possible trades between two teams*/
    private static List<Trade> generateTrades(Team team1, Team team2) {
        List<Trade> trades = new ArrayList<>();
        return trades;
    }

    private static int randomInt(int size) {
        return ThreadLocalRandom.current().nextInt(0, size);
    }

    private static boolean checkTrade(Player player1, Player player2) {

        System.out.println(abs(player1.salary - player2.salary));
        return abs(player1.salary - player2.salary) <= 100000;
    }

    private static List<Trade> genTrades(Team team1, Team team2) {
        List<Trade> result = new ArrayList<>();
        for (int i = 0; i < team1.players.size(); i++) {
        }
        return result;
    }

    private static List<Team> createTeams(String s) {
        List<Team> teams = new ArrayList<>();
        try {
            Scanner scanner = new Scanner(new File(s));
            Team newTeam = null;
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.startsWith("#")) {
                    if (newTeam != null) {
                        teams.add(newTeam);
                    }
                    newTeam = new Team(line.substring(1), new ArrayList<>());
                } else {
                    newTeam.addPlayer(line);
                    newTeam.sortPlayers();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        teams.sort((Comparator.comparing(o -> o.name)));
        for (Team team : teams) {
            System.out.println(team);
        }
        return teams;
    }

    private static void sendTweet(String line) {
        Twitter twitter = TwitterFactory.getSingleton();
        Status status;
        try {
            status = twitter.updateStatus(line);
            System.out.println(status);
        } catch (TwitterException e) {
            e.printStackTrace();
        }
    }

}
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

import java.util.concurrent.ThreadLocalRandom;
import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static java.lang.Math.abs;
import static java.lang.Math.random;

public class TwitterAutoBot {

    public static void main(String[] args) {
        while(true){
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
                        System.out.println("Sleeping for 30 seconds...");
                        Thread.sleep(30000); // every 30 minutes
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
        templates.add("#team1 will send #player1 to #team2 for #player2, league sources tell ESPN.");
        templates.add("#team1 agreed to trade w/ #team2 to acquire #player2 for #player1, league sources tell ESPN.");
        try {
            int teamIndex1 = randomInt(teams.size()), teamIndex2 = randomInt(teams.size());
            while (teamIndex1 == teamIndex2) {
                teamIndex2 = ThreadLocalRandom.current().nextInt(0, teams.size());
            }
            Team team1 = teams.get(teamIndex1 % teams.size());
            Team team2 = teams.get(teamIndex2 % teams.size());

            int playerIndex1 = ThreadLocalRandom.current().nextInt(0, team1.getPlayers().size() ),
                    playerIndex2 = ThreadLocalRandom.current().nextInt(0, team2.getPlayers().size() );
            Player player1 = team1.getPlayers().get(playerIndex1), player2= team2.getPlayers().get(playerIndex2);
            int count1 = 1,count2 = 1;
            while (playerIndex1 == playerIndex2 || !checkTrade(player1, player2)) {
                if (count1<team1.getPlayers().size()) {
                    if (count2 < team2.getPlayers().size()) {
                        playerIndex2 = ThreadLocalRandom.current().nextInt(0, team2.getPlayers().size());
                        player2 = team2.getPlayers().get(playerIndex2);
                        count2++;
                    } else {
                        playerIndex1 = ThreadLocalRandom.current().nextInt(0, team1.getPlayers().size());
                        player1 = team1.getPlayers().get(playerIndex1);
                        count2 = 1;
                    }
                }
            }


            PrintWriter writer = new PrintWriter("tweets.txt");
            String tweet=templates.get(randomInt(templates.size()));
            tweet = tweet.replaceFirst("#team1", team1.getName());
            tweet = tweet.replaceFirst("#team2", team2.getName());
            tweet = tweet.replaceFirst("#player1", team1.getPlayers().get(playerIndex1).getName());
            writer.println(tweet.replaceFirst("#player2", team2.getPlayers().get(playerIndex2).getName()));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int randomInt(int size) {
        return ThreadLocalRandom.current().nextInt(0, size);
    }

    private static boolean checkTrade(Player player1, Player player2) {

        System.out.println(abs(player1.salary-player2.salary));
        if (abs(player1.salary-player2.salary)>100000){
            return false;
        }
        return true;
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
                    newTeam = new Team(line.substring(1), new ArrayList<Player>());
                } else {
                    newTeam.addPlayer(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
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
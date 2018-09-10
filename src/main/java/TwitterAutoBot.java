
import com.ibm.icu.lang.UCharacter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.io.*;
import java.nio.charset.Charset;

import static java.lang.Math.abs;


public class TwitterAutoBot {

    public static void main(String[] args) throws IOException {
        updateRoster();
        tweetLines();
    }

    private static void updateRoster() throws IOException {
        String url = "http://www.espn.com/nba/teams";
        Document doc = Jsoup.connect(url)
                .userAgent("Mozilla")
                .get();
        Elements links = doc.select("a");
        List<String> linksHref = links.eachAttr("href");
        Set<String> teamLinks = new TreeSet<>();
        for (String s : linksHref) {
            if (s.matches("http://www.espn.com/nba/team/_/name/[a-zA-z0-9]+/[a-zA-z0-9-]+")) {
                teamLinks.add(s.substring(0, 29) + "roster/_" + s.substring(30));
            }
        }
        PrintWriter writer = new PrintWriter(new File("roster.txt"));
        for (String link : teamLinks) {
            writer.println('#' + UCharacter.toTitleCase(Locale.US, link.split("/")[9].replace('-', ' '), null, 0));
            doc = Jsoup.connect(link)
                    .userAgent("Mozilla")
                    .get();
            for (Element table : doc.select("table")) {
                for (Element row : table.select("tr")) {
                    Elements tds = row.select("td");
                    if (tds.size() >= 7 && !tds.get(0).text().equals("NO.")) {
                        StringBuilder line = new StringBuilder();
                        for (Element td : tds) {
                            line.append("@").append(td.text());
                        }
                        writer.println(line.substring(1));
                    }
                }
            }
        }
        writer.close();
    }

    /* create tweets then send tweets */
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
                        int t = randomInt(600000);
                        System.out.println("Sleeping for " + t + " seconds...");
                        Thread.sleep(t); // every 60 seconds
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
        /* templates here */
        templates.add("#team1 will send #players1 to #team2 for #players2, league sources tell ESPN.");
        templates.add("#team1 agreed to trade w/ #team2 to acquire #players2 for #players1, league sources tell ESPN.");
        templates.add("#team1 has traded #players1 to the #team2 for #players2, league source tells ESPN.");

        int teamIndex1 = randomInt(teams.size()), teamIndex2 = randomInt(teams.size());
        while (teamIndex1 == teamIndex2) {
            teamIndex2 = ThreadLocalRandom.current().nextInt(0, teams.size());
        }
        Team team1 = teams.get(teamIndex1 % teams.size());
        Team team2 = teams.get(teamIndex2 % teams.size());

        Trade trade = genTrades(team1, team2);

        /*
            int playerIndex1 = 0, playerIndex2 = 0;
            boolean flag = false;for (playerIndex1 = 0; playerIndex1 < team1.players.size(); playerIndex1++) {
            for (playerIndex2 = 0; playerIndex2 < team2.players.size(); playerIndex2++) {
                Player player1 = team1.players.get(playerIndex1), player2 = team2.players.get(playerIndex2);
                if (checkTrade(new ArrayList<>(Collections.singletonList(player1)),
                        new ArrayList<>(Collections.singletonList(player2)))) {
                    flag = true;
                    break;
                }
            }
            if (flag) {
                break;
            }
        }*/

        StringBuilder players1 = new StringBuilder(), players2 = new StringBuilder();
        int count = 0;
        for (Player p : trade.players1) {
            int size = trade.players1.size();
            players1.append(p.name);
            if (count != size - 1) {
                if (count == size - 2) {
                    players1.append(" and ");
                } else {
                    players1.append(", ");
                }
            }
            count++;
        }
        count = 0;
        for (Player p : trade.players2) {
            int size = trade.players2.size();
            players2.append(p.name);
            if (count != size - 1) {
                if (count == size - 2) {
                    players2.append(" and ");
                } else {
                    players2.append(", ");
                }
            }
            count++;
        }
        try {
            PrintWriter writer = new PrintWriter("tweets.txt");
            String tweet = templates.get(randomInt(templates.size()));
            tweet = tweet.replaceFirst("#team1", team1.name);
            tweet = tweet.replaceFirst("#team2", team2.name);
            tweet = tweet.replaceFirst("#players1", players1.toString());
            /*if (randomInt(100) % 2 == 0) {
                tweet = tweet.replaceFirst(" and cash", "");
            }*/
            tweet = tweet.replaceFirst("#players2", players2.toString());
            writer.println(tweet);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /*Check if a trade is valid, working on adding more rules and update the roster */
    private static boolean checkTrade(List<Player> players1, List<Player> players2) {

        /* System.out.println(abs(player1.salary - player2.salary)); */
        long sum = 0;
        for (Player player : players1) {
            sum += player.salary;
        }
        for (Player player : players2) {
            sum -= player.salary;
        }
        return abs(sum) <= 100000;
    }

    /* Generate all possible trades between two teams*/
    private static Trade genTrades(Team team1, Team team2) {
        while (true) {
            int numOfPlayers1 = randomInt(3) + 1, numOfPlayers2 = randomInt(3) + 1;
            List<Player> copy1 = new ArrayList<>(team1.players);
            List<Player> copy2 = new ArrayList<>(team2.players);
            Collections.shuffle(copy1);
            Collections.shuffle(copy2);
            copy1 = copy1.subList(0, numOfPlayers1);
            copy2 = copy2.subList(0, numOfPlayers2);
            if (checkTrade(copy1, copy2)) {
                System.out.println(copy1 + " " + copy2);
                return new Trade(copy1, copy2);
            }
        }
    }

    /* Reading from a file to create all NBA Teams */
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
                    if (newTeam != null) {
                        newTeam.addPlayer(line);
                        newTeam.players.sort((o1, o2) -> (int) (o2.salary - o1.salary));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        teams.sort((Comparator.comparing(o -> o.name)));
        /* for (Team team : teams) {
            System.out.println(team);
        } */
        return teams;
    }

    /* post a tweet */
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

    /* A shorter version of randomInt*/
    private static int randomInt(int size) {
        return ThreadLocalRandom.current().nextInt(0, size);
    }

}
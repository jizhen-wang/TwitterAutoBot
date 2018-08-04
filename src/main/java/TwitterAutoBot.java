import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static java.lang.Math.random;

public class TwitterAutoBot {

    public static void main(String[] args) {
        tweetLines();
    }

    private static void tweetLines() {
        String line;
        createTweet();
        try {
            try (
                    InputStream fis = new FileInputStream("tweets.txt");
                    InputStreamReader isr = new InputStreamReader(fis, Charset.forName("Cp1252"));
                    BufferedReader br = new BufferedReader(isr);
            ) {
                while ((line = br.readLine()) != null) {
                    // Deal with the line
                    sendTweet(line);
                    System.out.println("Tweeting: " + line + "...");
                    try {
                        System.out.println("Sleeping for 30 minutes...");
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
        List<Team> teams=createTeams("roster.txt");
        String template1="#team will send #player to #team for #player league sources tell ESPN.";
        try{
            PrintWriter writer=new PrintWriter("tweets.txt");
            template1=template1.replaceFirst("#team","Dallas");
            template1=template1.replaceFirst("#team","Houston");
            template1=template1.replaceFirst("#player","Chris Paul");
            writer.println(template1.replaceFirst("#player","Harrison Barnes")+random());
            writer.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private static List<Team> createTeams(String s) {
        List<Team> teams=new ArrayList<Team>();

        try{
            Scanner scanner=new Scanner(new File(s));
            while(scanner.hasNextLine()){
                String line=scanner.nextLine();
                if (line.startsWith("#")){
                    Team newTeam=new Team(line.substring(1),new ArrayList<Player>());
                }
            }
        }catch(IOException e){
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
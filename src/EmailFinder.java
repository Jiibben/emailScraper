import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.regex.*;
import java.util.ArrayDeque;
import java.util.ArrayList;


public class EmailFinder {

    private final HashSet<String> emails = new HashSet<>();
    private final ArrayList<String> searchedLinks = new ArrayList<>();
    private final ArrayDeque<String> links = new ArrayDeque<>();
    public static final Pattern EMAIL_VERIFICATION = Pattern.compile("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])");
    private final int threshold;

    public EmailFinder(int threshold) {
        this.threshold = threshold;
    }

    private void findEmail(String page) {
        Matcher emailMatcher = EMAIL_VERIFICATION.matcher(page);
        while (emailMatcher.find()) {
            emails.add(emailMatcher.group());
        }
    }

    private void findSubLinks(Document page) {
        Elements anchors = page.select("a[href]");
        for (Element a : anchors) {
            String link = a.attr("abs:href");
            if (link.startsWith("http") && !searchedLinks.contains(link)) {
                links.add(link);
            }
        }
    }

    private void executeOnce() {
        String link = links.pollFirst();

        try {

            System.out.println("[+] visiting : " + link);
            Document page = Requester.getHtmlDocument(link);
            findSubLinks(page);
            findEmail(page.html());
            searchedLinks.add(link);
        } catch (IOException ignored) {
            System.out.println("[-] couldn't visit " + link);
        }

    }

    private void execute() {
        while (!links.isEmpty() && searchedLinks.size() <= threshold) {
            executeOnce();
        }
    }

    public HashSet<String> run(String url) {
        links.add(url);
        execute();
        return emails;
    }

    public void saveData(String name) {
        try {
            FileWriter myWriter = new FileWriter(name + ".txt");
            myWriter.write("EMAILS : ");
            for (String i : emails) {
                System.out.println(i);
                myWriter.write(i + "\n");
            }
            myWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}



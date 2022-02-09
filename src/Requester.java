import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class Requester {


    public static Document getHtmlDocument(String url) throws IOException {
        return Jsoup.connect(url).get();
    }
}

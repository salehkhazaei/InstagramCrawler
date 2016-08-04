package instacrawler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author Saleh
 */
public class InstaCrawler {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        HashSet<String> crawled_links = new HashSet<>();
        HashSet<String> noncrwaled_links = new HashSet<>();
        ArrayList<Profile> profiles = new ArrayList<>();

        noncrwaled_links.add("جشن_فارغ_التحصيلى_91_كامپيوتر_اميركبير");

        CrawlerThread threads[] = new CrawlerThread[1];
        for ( int i = 0 ; i < 1 ; i ++ )
        {
            threads[i] = new CrawlerThread(crawled_links, noncrwaled_links, profiles);
            threads[i].start();
        }
    }

}

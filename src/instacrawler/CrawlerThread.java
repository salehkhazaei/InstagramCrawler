package instacrawler;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.acl.Owner;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.net.ssl.HttpsURLConnection;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author Saleh Khazaei
 * @email saleh.khazaei@gmail.com
 */
public class CrawlerThread extends Thread {

    HashSet<String> crawled_links;
    HashSet<String> noncrwaled_links;
    HashSet<String> usernames;
    HashSet<String> posts;
    HashSet<String> posts_pics;
    ArrayList<Profile> profiles;

    public boolean stop = false;

    public CrawlerThread(HashSet<String> crawled_links, HashSet<String> noncrwaled_links, ArrayList<Profile> profiles) {
        this.crawled_links = crawled_links;
        this.noncrwaled_links = noncrwaled_links;
        this.profiles = profiles;
        this.posts = new HashSet<>();
        this.usernames = new HashSet<>();
        this.posts_pics = new HashSet<>();
    }

    @Override
    public void run() {
        while (!stop) {
            if (noncrwaled_links.size() > 0) {
                try {
                    String str;
                    synchronized (noncrwaled_links) {
                        if (noncrwaled_links.size() == 0) {
                            continue;
                        }
                        str = noncrwaled_links.iterator().next();
                        noncrwaled_links.remove(str);
                    }
                    boolean has_next_page = true;
                    String media_after = "AQCpj3rcOKp1460iLruGuHhnfnHF_OH1Jij6f-L55KikSzJpNIWyEop4LiRmM2iwgNj4rzcDbz9tcl4O77aKZvygvDKZ4EawBHVJMgXZiJh2Te0nkOZoHDZRzY2nmjUGTHo";
                    while (has_next_page) {
                        URL url = new URL("https://www.instagram.com/query");
                        HttpsURLConnection uc = (HttpsURLConnection) url.openConnection();

                        uc.setRequestMethod("POST");
                        uc.setRequestProperty("Cookie", "csrftoken=d6eec7184d739f700d16552050ab0a10; s_network=; sessionid=IGSCe85c7e22114b0eaae5b02a3db152bbf5f395cdf3183a7e42ffd06c8463ead0c5%3APs5FUsKVJ5xxrtjkEVloCcj33QSY0AX2%3A%7B%22asns%22%3A%7B%22209.95.51.182%22%3A32780%2C%22time%22%3A1464856143%7D%7D; mid=V0_uTwAEAAGPURpjx6zlgHSr7vME; ig_pr=1; ig_vw=1366");
                        uc.setRequestProperty("Referer", "https://www.instagram.com/explore/tags/" + URLEncoder.encode(str) + "/");
                        uc.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0");
                        uc.setRequestProperty("Accept", "application/json, text/javascript, */*; q=0.01");
                        uc.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
                        uc.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
                        uc.setRequestProperty("X-Instagram-AJAX", "1");
                        uc.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
                        uc.setRequestProperty("X-CSRFToken", "d6eec7184d739f700d16552050ab0a10");
                        uc.setRequestProperty("X-Requested-With", "XMLHttpRequest");
                        uc.setDoOutput(true);
                        DataOutputStream wr = new DataOutputStream(uc.getOutputStream());
                        wr.writeBytes("q=ig_hashtag(" + URLEncoder.encode(str) + ")+%7B+media.after(" + media_after + "%2C+32)+%7B%0A++count%2C%0A++nodes+%7B%0A++++caption%2C%0A++++code%2C%0A++++comments+%7B%0A++++++count%0A++++%7D%2C%0A++++date%2C%0A++++dimensions+%7B%0A++++++height%2C%0A++++++width%0A++++%7D%2C%0A++++display_src%2C%0A++++id%2C%0A++++is_video%2C%0A++++likes+%7B%0A++++++count%0A++++%7D%2C%0A++++owner+%7B%0A++++++id%0A++++%7D%2C%0A++++thumbnail_src%2C%0A++++video_views%0A++%7D%2C%0A++page_info%0A%7D%0A+%7D&ref=tags%3A%3Ashow");
                        wr.flush();
                        wr.close();
                        uc.connect();

                        int responseCode = uc.getResponseCode();
                        System.out.println("\nSending 'POST' request to URL : " + url);
                        System.out.println("Response Code : " + responseCode);

                        BufferedReader in = new BufferedReader(
                                new InputStreamReader(uc.getInputStream()));
                        String inputLine;
                        StringBuffer response = new StringBuffer();

                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine + "\n");
                        }
                        in.close();

                        System.out.println("Parsing...");
                        //print result
                        final JSONObject obj = new JSONObject(response.toString());
                        final String status = obj.getString("status");
                        final JSONObject media = obj.getJSONObject("media");
                        final int m_count = media.getInt("count");
                        final JSONObject m_pageinfo = media.getJSONObject("page_info");
                        final JSONArray m_nodes = media.getJSONArray("nodes");
                        final int n = m_nodes.length();
                        for (int i = 0; i < n; ++i) {
                            final JSONObject node = m_nodes.getJSONObject(i);
                            String code = node.getString("code");
                            posts.add(code);
                        }
                        has_next_page = m_pageinfo.getBoolean("has_next_page");
                        media_after = m_pageinfo.getString("end_cursor");
                        System.out.println("Number of crawled hashtags: " + crawled_links.size() + ", Non-crawled ones: " + noncrwaled_links.size() + ", No of posts:" + posts.size());
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(CrawlerThread.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    System.out.println("**** POSTS ****");
                    while (posts.size() > 0) {
                        String code = posts.iterator().next();
                        posts.remove(code);
                        URL url = new URL("https://www.instagram.com/p/" + code + "/?tagged=" + str + "&__a=1");
                        HttpsURLConnection uc = (HttpsURLConnection) url.openConnection();

                        uc.setRequestMethod("GET");
                        uc.setRequestProperty("Cookie", "csrftoken=d6eec7184d739f700d16552050ab0a10; s_network=; sessionid=IGSCe85c7e22114b0eaae5b02a3db152bbf5f395cdf3183a7e42ffd06c8463ead0c5%3APs5FUsKVJ5xxrtjkEVloCcj33QSY0AX2%3A%7B%22asns%22%3A%7B%22209.95.51.182%22%3A32780%2C%22time%22%3A1464856143%7D%7D; mid=V0_uTwAEAAGPURpjx6zlgHSr7vME; ig_pr=1; ig_vw=1366");
                        uc.setRequestProperty("Referer", "https://www.instagram.com/explore/tags/" + str + "/");
                        uc.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0");
                        uc.setRequestProperty("Accept", "application/json, text/javascript, */*; q=0.01");
                        uc.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
                        uc.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
                        uc.setRequestProperty("X-Instagram-AJAX", "1");
                        uc.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
                        uc.setRequestProperty("X-CSRFToken", "d6eec7184d739f700d16552050ab0a10");
                        uc.setRequestProperty("X-Requested-With", "XMLHttpRequest");

                        int responseCode = uc.getResponseCode();
                        System.out.println("\nSending 'POST' request to URL : " + url);
                        System.out.println("Response Code : " + responseCode);

                        BufferedReader in = new BufferedReader(
                                new InputStreamReader(uc.getInputStream()));
                        String inputLine;
                        StringBuffer response = new StringBuffer();

                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine + "\n");
                        }
                        in.close();

                        System.out.println("Parsing post [" + code + "]...");
                        //print result
                        final JSONObject obj = new JSONObject(response.toString());
                        final JSONObject media = obj.getJSONObject("media");

                        final JSONObject m_usertags = media.getJSONObject("usertags");
                        final JSONObject m_owner = media.getJSONObject("owner");
                        final JSONObject m_comments = media.getJSONObject("comments");
                        final String m_caption = media.getString("caption");
                        // parse caption

                        final JSONObject m_likes = media.getJSONObject("likes");
                        final String m_display_src = media.getString("display_src");
                        posts_pics.add(m_display_src);

                        final JSONArray u_nodes = m_usertags.getJSONArray("nodes");
                        for (int i = 0; i < u_nodes.length(); i++) {
                            final JSONObject node = u_nodes.getJSONObject(i);
                            final JSONObject user = node.getJSONObject("user");
                            usernames.add(user.getString("username"));
                        }

                        final String o_username = m_owner.getString("username");

                        String o_fullname = "";
                        if (m_owner.has("full_name") && !m_owner.isNull("full_name")) {
                            o_fullname = m_owner.getString("full_name");
                        }

                        final String o_profilepic = m_owner.getString("profile_pic_url");
                        profiles.add(new Profile(o_fullname, o_username, o_profilepic));

                        final JSONArray c_nodes = m_comments.getJSONArray("nodes");
                        for (int i = 0; i < c_nodes.length(); i++) {
                            final JSONObject node = c_nodes.getJSONObject(i);
                            final JSONObject user = node.getJSONObject("user");
                            usernames.add(user.getString("username"));

                            String text = node.getString("text");
                            // parse text
                        }

                        final JSONArray l_nodes = m_likes.getJSONArray("nodes");
                        for (int i = 0; i < l_nodes.length(); i++) {
                            final JSONObject node = l_nodes.getJSONObject(i);
                            final JSONObject user = node.getJSONObject("user");
                            usernames.add(user.getString("username"));
                        }
                    }
                    System.out.println("**** USERNAMES ****");
                    while (usernames.size() > 0) {
                        String code = usernames.iterator().next();
                        usernames.remove(code);

                        URL url = new URL("https://www.instagram.com/" + code);
                        HttpsURLConnection uc = (HttpsURLConnection) url.openConnection();

                        uc.setRequestMethod("GET");
                        uc.setRequestProperty("Cookie", "csrftoken=d6eec7184d739f700d16552050ab0a10; s_network=; sessionid=IGSCe85c7e22114b0eaae5b02a3db152bbf5f395cdf3183a7e42ffd06c8463ead0c5%3APs5FUsKVJ5xxrtjkEVloCcj33QSY0AX2%3A%7B%22asns%22%3A%7B%22209.95.51.182%22%3A32780%2C%22time%22%3A1464856143%7D%7D; mid=V0_uTwAEAAGPURpjx6zlgHSr7vME; ig_pr=1; ig_vw=1366");
                        uc.setRequestProperty("Referer", "https://www.instagram.com/explore/tags/" + str + "/");
                        uc.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0");
                        uc.setRequestProperty("Accept", "application/json, text/javascript, */*; q=0.01");
                        uc.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
                        uc.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
                        uc.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");

                        int responseCode = uc.getResponseCode();
                        System.out.println("\nSending 'POST' request to URL : " + url);
                        System.out.println("Response Code : " + responseCode);

                        BufferedReader in = new BufferedReader(
                                new InputStreamReader(uc.getInputStream()));
                        String inputLine;
                        StringBuffer response = new StringBuffer();

                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine + "\n");
                        }
                        in.close();

                        String res = response.toString();
                        String prof_pic = "";
                        String name = "" ;
                        if (res.contains("profile_pic_url_hd")) {
                            res = res.substring(res.indexOf("\"profile_pic_url_hd\": \"") + "\"profile_pic_url_hd\": \"".length());
                            prof_pic = res.substring(0, res.indexOf("\""));
                        }
                        else if (res.contains("profile_pic_url")) {
                            res = res.substring(res.indexOf("\"profile_pic_url\": \"") + "\"profile_pic_url\": \"".length());
                            prof_pic = res.substring(0, res.indexOf("\""));
                        }
                        else
                        {
                            continue;
                        }
                        if (res.contains("full_name")) {
                            res = res.substring(res.indexOf("\"full_name\": \"") + "\"full_name\": \"".length());
                            name = res.substring(0, res.indexOf("\""));
                        }
                        profiles.add(new Profile(name, code, prof_pic));
                    }

                    FileOutputStream fos = new FileOutputStream(new File("posts_pic.txt"));
                    while (posts_pics.size() > 0) {
                        String code = posts_pics.iterator().next();
                        posts_pics.remove(code);

                        fos.write((code + "\n").getBytes());
                    }
                    fos.close();

                    for (Profile p : profiles) {
                        if ( p.img != null )
                        {
                            ImageIO.write(p.img, "JPG", new File(p.username + ".jpg"));
                        }
                    }
                    System.out.println("Saved");
                } catch (IOException ex) {
                    Logger.getLogger(InstaCrawler.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    Logger.getLogger(CrawlerThread.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException ex) {
                Logger.getLogger(CrawlerThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}


package instacrawler;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 *
 * @author Saleh Khazaei
 * @email saleh.khazaei@gmail.com
 */
public class Profile {
    public String name;
    public String username;
    public BufferedImage img;

    public Profile(String name, String username, BufferedImage img) {
        this.name = name;
        this.username = username;
        this.img = img;
    }

    public Profile(String name, String username, String url) {
        this.name = name;
        this.username = username;
        try {
            this.img = ImageIO.read(new URL(url));
        } catch (IOException ex) {
            Logger.getLogger(Profile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

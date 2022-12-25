/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package linkintegrity;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Document;
import userinterface.FrontPage;
import org.jsoup.safety.Whitelist;
import static userinterface.FrontPage.future;
import static userinterface.FrontPage.min;
import static userinterface.FrontPage.pool;
import static userinterface.FrontPage.startTime;
import static userinterface.FrontPage.timeCompare;

/**
 *
 */
public class ValidationMethods {

    private String link;
    private int currentDepth;
    private int totalDepth;
    private static int countValid;
    private static int countInvalid;

    /**
     * @param link
     * @param currentDepth
     * @param totalDepth
     */
    public ValidationMethods(String link, int currentDepth, int totalDepth) {
        this.link = link;
        this.currentDepth = currentDepth;
        this.totalDepth = totalDepth;
    }

    ValidationMethods() {
    }

    public String getLink() {
        return link;
    }

    /**
     * @param link the link to set
     */
    public void setLink(String link) {
        this.link = link;
    }

    /**
     * @return the currentDepth
     */
    public int getCurrentDepth() {
        return currentDepth;
    }

    /**
     * @param currentDepth the currentDepth to set
     */
    public void setCurrentDepth(int currentDepth) {
        this.currentDepth = currentDepth;
    }

    /**
     * @return the totalDepth
     */
    public int getTotalDepth() {
        return totalDepth;
    }

    /**
     * @param totalDepth the totalDepth to set
     */
    public void setTotalDepth(int totalDepth) {
        this.totalDepth = totalDepth;
    }

    public static int getCountValid() {
        return countValid;
    }

    /**
     * @param countValid the countValid to set
     */
    public static void setCountValid(int countValid) {
        ValidationMethods.countValid = 0;
    }

    /**
     * @return the countInvalid
     */
    public static int getCountInvalid() {
        return countInvalid;
    }

    /**
     * @param countInvalid the countInvalid to set
     */
    public static void setCountInvalid(int countInvalid) {
        ValidationMethods.countInvalid = 0;
    }

    public static boolean validateURL(String link) {
        boolean valid = true;
        String linkAndStatus = null;
        try {
            URL u = null;
            u = new URL(link);
            URLConnection x = null;
            x = u.openConnection();
            InputStream input = x.getInputStream();
            linkAndStatus = "Valid link: " + link;
            FrontPage.linkList.add(linkAndStatus);
          //  System.out.println(linkAndStatus);
            countValid++;
        } catch (IOException | IllegalArgumentException ex) {
            valid = false;
            linkAndStatus = "Invalid link: " + link;
            FrontPage.linkList.add(linkAndStatus);
           // System.err.println(linkAndStatus);
            countInvalid++;
        }

        return valid;
    }

    public void extractLinkAndValidate(String link, int currentDepth, int totalDepth) {
        if (validateURL(link)) {
            if (currentDepth == totalDepth && currentDepth == 0) {
                countValid = 1;
            }
            if (currentDepth == totalDepth) {
                return;
            }
            try {
                Document doc = Jsoup.connect(link).get();
                Elements e = doc.select("a[href]");
                URL u = new URL(link);
                for (int i = 0; i < e.size(); i++) {
                    String x = e.get(i).attr("abs:href");
                    String baseLink = u.getProtocol() + "://" + u.getHost();
                    if (!x.startsWith("http")) {
                        x = baseLink + x;
                    }
                    LinkServices t1 = new LinkServices(x, currentDepth + 1, totalDepth);
                    future = pool.submit(t1);
                }
            } catch (IOException ex) {
            }
        }
    }

    public static void startThreadsAndCounters(String l, int depth, int threadNumber, int i) {
        LinkServices ls = new LinkServices(l, 0, depth);
        future = pool.submit(ls);
        startTime = System.currentTimeMillis();
        if (depth != 0 && threadNumber != 0) {
            while (!future.isDone()) {
            }
        }
        pool.shutdown();
        while (!pool.isTerminated()) {
        }
        long endTime = System.currentTimeMillis();
        FrontPage.timeElapsed = (endTime - startTime) / 1000;
        if (i == 1) {
            min = FrontPage.timeElapsed;
        }
        timeCompare.add(FrontPage.timeElapsed);

        if (min == 0 || min > FrontPage.timeElapsed) {
            min = FrontPage.timeElapsed;
        }
        try {
            pool.awaitTermination(1, TimeUnit.HOURS);
        } catch (InterruptedException ex) {
            Logger.getLogger(FrontPage.class.getName()).log(Level.SEVERE, null, ex);
        }
        while (!pool.isTerminated()) {

        }
    }

    public static void WriteToFile() throws IOException {
        File f = new File("List of links.txt");
        try (PrintWriter p = new PrintWriter(f)) {
            for (int i = 0; i < FrontPage.linkList.size(); i++) {
                p.println(FrontPage.linkList.get(i));
            }
        }
    }

}

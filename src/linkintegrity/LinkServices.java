/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package linkintegrity;

/**
 *
 */
public class LinkServices extends Thread {
    private String link;
    private int currentDepth;
    private int maxDepth;

    public LinkServices(String link, int currentDepth, int maxDepth) {
        this.link = link;
        this.currentDepth = currentDepth;
        this.maxDepth = maxDepth;

    }

    @Override
    public void run() {
        ValidationMethods v = new ValidationMethods();
        v.extractLinkAndValidate(link, currentDepth, maxDepth);
    }
}

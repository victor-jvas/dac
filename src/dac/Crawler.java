/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dac;

import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author João
 */
public class Crawler {

    public JSONObject getPage(String url) {
        JSONObject autor = new JSONObject();
        try {
            Document doc = Jsoup.connect(url).get();
            getAuthorInfo(doc, autor, url);
        } catch (IOException ex) {
            Logger.getLogger(Crawler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return autor;
    }

    private void getAuthorInfo(Document doc, JSONObject autor, String url) {

        String nome = doc.getElementById("gsc_prf_in").text();
        String afiliacao = "afiliação desconhecida";
        Elements els = doc.getElementsByClass("gsc_prf_il");

        Elements yo = els.select("a");

        if (yo.first() != null) {
            afiliacao = yo.first().text();
            yo.remove(0);
        }
        String area = " ";
        if (!yo.isEmpty()) {
            for (Element yo1 : yo) {
                if (" ".equals(area)) {
                    area = yo1.text() + ", ";
                } else {
                    area += yo1.text() + ", ";
                }
            }
            area = area.substring(0, area.length() - 2);
        }
        autor.put("Nome", nome);
        autor.put("Afiliacao", afiliacao);
        autor.put("Area", area);

        try {
            getArticles(autor, url);
        } catch (IOException ex) {
            Logger.getLogger(Crawler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void getArticles(JSONObject autor, String url) throws IOException {

        JSONArray articles = new JSONArray();
        int count = 0;

        //System.out.println("\n" + url);
        do {
            url += "&cstart=" + count + "&pagesize=100";
            Document doc = Jsoup.connect(url).get();
            Element table;
            table = doc.getElementById("gsc_a_b");

            Elements titles = table.getElementsByClass("gsc_a_t");
            for (Element title : titles) {
                JSONObject article = new JSONObject();
                String name = title.getElementsByTag("a").text();
                Elements el = title.getElementsByClass("gs_gray");
                String authors = el.first().text();
                String local = el.last().text();

                article.put("Titulo", name);
                article.put("Autores", authors);
                article.put("Local", local);

                //System.out.println("Titulo: " + name + "\nAutores: " + authors + "\nLocal: " + local + "\n");
                articles.add(article);
                //System.out.println(articles + "\n");
                ++count;
            }
        } while (count % 100 == 0);
        // }while(count%100 == 0);
        // System.out.println("\n" + count);
        autor.put("Artigos", articles);
        //System.out.println(autor + "\n");
    }

    public void writeJSON(JSONObject authors) {
        try (FileWriter file = new FileWriter("autores_google.json")) {
            file.write(authors.toJSONString());
            System.out.println("Successfully Copied JSON Object to File...");
            System.out.println("\nJSON Object: " + authors);
        } catch (IOException ex) {
            Logger.getLogger(Crawler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

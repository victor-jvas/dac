/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dac;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


/**
 *
 * @author João
 */
public class DAC {

    /**
     *
     * @param input
     * @return
     */
    public static boolean isInteger(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (NumberFormatException | NullPointerException e) {
            return false;
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {

        String url;
        JSONArray autores = new JSONArray();
        JSONObject list = new JSONObject();
        Crawler c = new Crawler();
        String file = null;

        // Open the file
        FileInputStream fstream = new FileInputStream("url_list.txt");
        //Read File Line By Line
        try (BufferedReader br = new BufferedReader(new InputStreamReader(fstream))) {
            //Read File Line By Line
            while ((url = br.readLine()) != null) {
                // Chama método para processar URL
                autores.add(c.getPage(url));
            }
        }
        //Insere os autores no objeto final
        list.put("Autores", autores);
        //Cria arquivo JSON
        c.writeJSON(list);
    }
}

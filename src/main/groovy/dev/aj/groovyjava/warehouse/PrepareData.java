package dev.aj.groovyjava.warehouse;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;

public class PrepareData {

    public List<LinkedHashMap<String, String>> getProductData() {
        List<LinkedHashMap<String, String>> maps;
        try (BufferedReader br = new BufferedReader(new FileReader("./productsData.txt"))) {
            maps = br.lines()
                    .map(line -> line.substring(1, line.length() - 2))
                    .map(line -> line.split(", "))
                    .map(this::getProductFromString)
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return maps;
    }

    private LinkedHashMap<String, String> getProductFromString(String[] listItem) {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("name", removeInvertedCommas(listItem[0]));
        map.put("department", removeInvertedCommas(listItem[1]));
        map.put("price", listItem[2]);
        map.put("weight", listItem[3]);
        return map;
    }

    //Sanitise texts if encapsulated in inverted commas
    private String removeInvertedCommas(String text) {
       return text.startsWith("'") && text.endsWith("'") ? text.substring(text.indexOf("'") + 1, text.lastIndexOf("'")) : text;
    }
}

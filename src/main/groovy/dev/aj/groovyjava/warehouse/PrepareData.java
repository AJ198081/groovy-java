package dev.aj.groovyjava.warehouse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class PrepareData {

    public List<LinkedHashMap<String, String>> getProductData() {
        List<LinkedHashMap<String, String>> maps = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(new File("./productsData.txt")))) {


            maps = br.lines()
                    .map(line -> line.substring(1, line.length() - 2))
                    .map(line -> line.split(", "))
                    .map(this::getProductFromString)
                    .toList();

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        return maps;
    }


    private LinkedHashMap<String, String> getProductFromString(String[] listItem) {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("name", listItem[0]);
        map.put("department", listItem[1]);
        map.put("price", listItem[2]);
        map.put("weight", listItem[3]);
        return map;
    }

}

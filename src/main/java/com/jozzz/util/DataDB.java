package com.jozzz.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jozzz.models.Cow;
import com.jozzz.models.CowBreedDairy;

public class DataDB {

    // Connect to spring framwork or backend dpo-api
    static String url = "http://localhost:8083";
    static ArrayList<String[]> cowList = new ArrayList<>();
    static ArrayList<String[]> breedList = new ArrayList<>();

    public static ArrayList<String[]> getAllDairyCowBreedForPattern() throws Exception {
        System.out.println("connection to dpo_api");
        String endpoint = url + "/cow/all_list";
        HttpGet request = new HttpGet(endpoint);
        System.out.println("Get URL: "+endpoint);

        try (CloseableHttpClient httpClient = HttpClients.createDefault();
                CloseableHttpResponse response = httpClient.execute(request);) {
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                String result = EntityUtils.toString(entity);
                System.out.println(result);

                Gson gson = new Gson();
                Type listType = new TypeToken<List<CowBreedDairy>>() {
                }.getType();
                List<CowBreedDairy> cows = gson.fromJson(result, listType);

                for (CowBreedDairy cow : cows) {
                    String[] cowArray = {
                            String.valueOf(cow.getCow_id()),
                            cow.getCow_name(),
                            cow.getCow_fa_zyan_code(),
                            cow.getCow_ma_zyan_code(),
                            String.valueOf(cow.getFarm_id()),
                            String.valueOf(cow.getBreed_id()),
                            cow.getBreed_code(),
                            cow.getBreed_name(),
                            cow.getBreed_id_string(),
                            "",
                            "",
                            ""
                    };
                    cowList.add(cowArray);
                }
            }
        } catch (Exception e) {
            System.out.println("Error at http Entity!!!");
            System.out.println(e);
        }
        return cowList;
    }
    public static ArrayList<String[]> getAllDairBreedCode() throws Exception {
        System.out.println("connection to dpo_api");
        String endpoint = url + "/breed/breed_code";
        System.out.println("Get URL: "+endpoint);
        HttpGet request = new HttpGet(endpoint);

        try (CloseableHttpClient httpClient = HttpClients.createDefault();
                CloseableHttpResponse response = httpClient.execute(request);) {
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                String result = EntityUtils.toString(entity);
                Gson gson = new Gson();
                Type listType = new TypeToken<List<String>>() {
                }.getType();
                List<String> breedCodes = gson.fromJson(result, listType);

                for (String breedCode : breedCodes) {
                    String[] breed = {
                            breedCode
                    };
                    breedList.add(breed);
                }
            }
        } catch (Exception e) {
            System.out.println("Error at http Entity!!!");
            System.out.println(e);
        }
        return breedList;
    }
    public static ArrayList<String[]> getCowById() throws IOException {
        System.out.println("connection to dpo_api");
        String endpoint = url + "/cow/135678";

        return new ArrayList<>();
    }

    public static void printArrayList(ArrayList<String[]> arrayList) {
        for (String[] array : arrayList) {
            for (String element : array) {
                System.out.print(element + " ");
            }
            System.out.println(); // Move to the next line after printing each array
        }
    }

    public static void main(String[] args) throws Exception {
        ArrayList<String[]>cowList =  DataDB.getAllDairyCowBreedForPattern();
        printArrayList(cowList);
    }
}

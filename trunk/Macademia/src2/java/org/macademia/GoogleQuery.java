package org.macademia;

/**
 * Created by IntelliJ IDEA.
 * User: shilad
 * Date: Nov 17, 2009
 * Time: 8:26:03 PM
 * To change this template use File | Settings | File Templates.
 */

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.net.URLDecoder;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedHashMap;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

public class GoogleQuery {

    // Put your website here
    private final String HTTP_REFERER = "http://macademia.macalester.edu/";

    public GoogleQuery() {
    }

    /**
     * Returns an ordered set of url -> name pairs that match the query
     * @param query
     * @param maxResults
     * @return
     * @throws IOException
     * @throws JSONException
     */
    public Map<String, String> makeQuery(String query, int maxResults) throws IOException, JSONException {

        // Convert spaces to +, etc. to make a valid URL
        query = URLEncoder.encode(query, "UTF-8");

        URL url = new URL("http://ajax.googleapis.com/ajax/services/search/web?start=0&rsz=large&v=1.0&q=" + query + "+site:en.wikipedia.org");
        URLConnection connection = url.openConnection();
        connection.addRequestProperty("Referer", HTTP_REFERER);

        // Get the JSON response
        String line;
        StringBuilder builder = new StringBuilder();
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream()));
        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }

        String response = builder.toString();
        JSONObject json = new JSONObject(response);

        JSONArray ja = json.getJSONObject("responseData")
                .getJSONArray("results");

        Map<String, String> results = new LinkedHashMap<String, String>();
        for (int i = 0; i < ja.length() && i < maxResults; i++) {
            JSONObject j = ja.getJSONObject(i);
            String url2 = j.getString("url");
            if (url2.indexOf("/") > 0) {
                results.put(url2, decodeWikiUrl(url2));
            }
        }
        return results;
    }

    private static URLDecoder decoder = new URLDecoder();
    public static String decodeWikiUrl(String url) {
        String ending = url.substring(url.lastIndexOf("/")+1);
        String decoded = decoder.decode(ending);
        return decoded.replace('_', ' ');
    }

}


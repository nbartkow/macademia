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

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException
import grails.util.Environment;

public class  Google {

    // Put your website here
    private final String DEFAULT_REFERRER = "http://macademia.macalester.edu/"
    private final String DEFAULT_SITE = "en.wikipedia.org"
    private final String API_PREFIX = "http://ajax.googleapis.com/ajax/services/search/web?start=0&rsz=large&v=1.0&q="

    private DiskMap cache = null
    
    private String referrer = DEFAULT_REFERRER
    private String site = DEFAULT_SITE

    public Google() {

    }

    public Google (File file) {
         cache = new DiskMap(file)
    }

    /**
     * Returns a list of urls matching the specified query.
     * @param query The textual query.
     * @param maxResults the maximum number of results to return.
     */
    public List<String> query(String query, int maxResults) throws IOException, JSONException {
        // Convert spaces to +, etc. to make a valid URL
        query = URLEncoder.encode(query, "UTF-8");
        if (cache != null && cache.contains(query)) {
            return cache.get(query)
        }


        URL url = new URL(API_PREFIX + query + "+site:" + site);
        URLConnection connection = url.openConnection();
        connection.addRequestProperty("Referer", referrer);

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

        JSONArray ja = json.getJSONObject("responseData").getJSONArray("results");

        List<String> results = new ArrayList<String>();
        for (int i = 0; i < ja.length() && results.size() < maxResults; i++) {
            JSONObject j = ja.getJSONObject(i);
            String url2 = j.getString("unescapedUrl");
            if (Wikipedia.isUrlForNormalPage(url2)) {
                results.add(url2);
            }
        }
        if (cache != null) {
            cache.put(query, results)
        }
        return results;
    }

}


package org.macademia

import org.json.JSONException

class GoogleService {

    static transactional = false
    static scope = "request"
    static proxy = true
    Google google = new Google()

    public List<String> query (String query, int maxResults) {
      return google.query(query, maxResults)
    }

}

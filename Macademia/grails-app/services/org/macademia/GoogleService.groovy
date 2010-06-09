package org.macademia

import org.json.JSONException

class GoogleService {

    static transactional = false
    ThreadLocal<Google> holder = new ThreadLocal<Google>()

    public List<String> query (String query, int maxResults) {
        if (holder.get() == null) {
            holder.set(new Google())
        }
        return holder.get().query(query, maxResults)
    }

}

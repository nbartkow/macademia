package org.macademia

import org.json.JSONException

class GoogleService {

    static File googleCache = null
    static transactional = false
    ThreadLocal<Google> holder = new ThreadLocal<Google>()

    public List<String> query (String query, int maxResults) {
        try {
            return getGoogle().query(query, maxResults)
        } catch (Exception e) {
            holder.set(null)
            log.error("google query for " + query + " failed (${e.getMessage()}... retrying");
            return getGoogle().query(query, maxResults)
        }
    }

    public Google getGoogle() {
        if (holder.get() == null) {
            if (googleCache != null) {
                holder.set(new Google(googleCache))
            } else {
                holder.set(new Google())
            }
        }
        return holder.get()
    }

    public void setCache(File cache){
        googleCache=cache
        Google google = new Google(cache)
        holder.set(google)
    }

}

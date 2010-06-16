package org.macademia

import org.json.JSONException

class GoogleService {

    static File googleCache = null
    static transactional = false
    ThreadLocal<Google> holder = new ThreadLocal<Google>()

    public List<String> query (String query, int maxResults) {
        if (holder.get() == null) {
            if (googleCache != null) {
                holder.set(new Google(googleCache))
            }   else {
                holder.set(new Google())
            }
        }
        return holder.get().query(query, maxResults)
    }

    public void setCache(File cache){
        googleCache=cache
        Google google = new Google(cache)
        holder.set(google)
    }

}

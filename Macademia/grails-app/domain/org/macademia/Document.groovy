package org.macademia

class Document {
    String url;
    String name;
    String text;

    static constraints = {
        url unique : true
    }

    public String toString() {
        return "<document '$url', '$name', '${(text.length() < 50) ? text : text.substring(0, 10)}...'>"
    }
}

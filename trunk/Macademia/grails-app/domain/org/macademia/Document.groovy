package org.macademia

class Document {
    String url;
    String text;

    static constraints = {
        url unique : true
    }
}

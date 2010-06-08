package org.macademia

class InterestDocument {
    Document document;
    double weight;

    static searchable = false;
    static mapping = {
        document column : 'document_id'
    }
    static belongsTo = [ interest : Interest ]
}

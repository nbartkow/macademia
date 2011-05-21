package org.macademia;

public class IdAndScore<K extends Comparable> implements Comparable<IdAndScore<K>> {
    public final K id;
    public final K id2;
    public Double score;

    public IdAndScore(K id, K id2, Double score) {
        this.id = id;
        this.id2 = id2;
        this.score = score;
    }

    public IdAndScore(K id, Double score) {
        this.id = id;
        this.id2 = null;
        this.score = score;
    }

    public int compareTo(IdAndScore<K> that) {
        int r = -1 * this.score.compareTo(that.score);
        if (r == 0) {
            r = this.id.compareTo(that.id);
        }
        return r;
    }

    public String toString() {
        return ("<id=" + id + ", score=" + score + ">");
    }
}

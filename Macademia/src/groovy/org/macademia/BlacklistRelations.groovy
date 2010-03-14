package org.macademia
/**
 * Created by IntelliJ IDEA.
 * User: shilad
 * Date: Oct 5, 2009
 * Time: 10:40:20 PM
 * To change this template use File | Settings | File Templates.
 */

public class BlacklistRelations {
    Set<String> blacklisted
    String path

    public BlacklistRelations(String path) {
        this.path = path
        blacklisted = new HashSet<String>()
        new File(path).eachLine { line ->
            if (line[0] == "-") {
                def tokens = line.substring(1).tokenize("\t")
                if (tokens.size() == 2) {
                    blacklisted.add(makeKey(tokens[0], tokens[1]))
                } else {
                    error("invalid line in ${path} : ${line}")
                }
            }
        }
        error("blacklisted ${blacklisted.size()} interest relations")
    }

    static def makeKey(String i1, String i2) {
        i1 = Interest.normalize(i1)
        i2 = Interest.normalize(i2)
        if (i1.compareTo(i2) <= 0) {
            return i1 + "-" + i2
        } else {
            return i2 + "-" + i1
        }
    }

    def isRetained(InterestRelation ir) {
        def key = makeKey(ir.first.text,  ir.second.text)
        return !blacklisted.contains(key)
    }

    def append(InterestRelation ir) {
        def key = makeKey(ir.first.text, ir.second.text)
        if (!blacklisted.contains(key)) {
            def adjustFile = new FileWriter(new File(MacademiaConstants.PATH_SIM_ADJUSTEMENTS), true)
            adjustFile.write("${ir.first.text}\t${ir.second.text}\n")
            adjustFile.close()
        }
    }

    def error(String msg) {
        System.err.println(msg)
    }
}

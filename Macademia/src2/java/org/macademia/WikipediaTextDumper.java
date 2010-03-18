package org.macademia;

import net.sourceforge.jwbf.bots.MediaWikiBot;
import net.sourceforge.jwbf.contentRep.mw.Article;
import net.sourceforge.jwbf.actions.mw.util.ActionException;
import net.sourceforge.jwbf.actions.mw.util.ProcessException;

import java.net.MalformedURLException;
import java.io.IOException;
import java.util.Map;
import java.util.LinkedHashMap;

import de.tudarmstadt.ukp.wikipedia.parser.mediawiki.MediaWikiParserFactory;
import de.tudarmstadt.ukp.wikipedia.parser.mediawiki.MediaWikiParser;
import de.tudarmstadt.ukp.wikipedia.parser.ParsedPage;
import de.tudarmstadt.ukp.wikipedia.parser.Section;
import de.tudarmstadt.ukp.wikipedia.parser.Paragraph;
import org.json.JSONException;

/**
 * Created by IntelliJ IDEA.
 * User: shilad
 * Date: Nov 17, 2009
 * Time: 8:18:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class WikipediaTextDumper {
    private MediaWikiBot bot;
    private GoogleQuery gq;

    public WikipediaTextDumper() throws MalformedURLException, ActionException {
        bot  = new MediaWikiBot("http://en.wikipedia.org/w/");
        bot.login("shilad", "coltrane");
        gq = new GoogleQuery();
    }

    /**
     * Returns a set of url -> text key, value
     * pairs for wiki pages matching the topic.
     * @param topic
     * @param numDocs
     * @return
     * @throws ActionException
     * @throws ProcessException
     * @throws IOException
     * @throws JSONException
     */
    public Map<String, String> getText(String topic, int numDocs) throws ActionException, ProcessException, IOException, JSONException {
        Map<String, String> docs = new LinkedHashMap<String, String>();
        Map<String, String> urls = gq.makeQuery(topic, numDocs);
        for (String url : urls.keySet()) {
            String page = urls.get(url);
            StringBuffer text = new StringBuffer();
            Article article = bot.readContent(page);
            if (article == null) {
                System.err.println("unknown article: " + page);
                continue;
            };
            String contents = article.getText();
            MediaWikiParserFactory pf = new MediaWikiParserFactory();
            MediaWikiParser parser = pf.createParser();
            ParsedPage pp = parser.parse(contents);
            if (pp == null) {
                System.err.println("parsing failed for " + page);
                continue;
            }

            for(Section section : pp.getSections()) {
                if (!keepSection(section)) {
                    continue;
                }
                for (Paragraph p : section.getParagraphs()) {
                    if (keepParagraph(p)) {
                        text.append(p.getText());
                    }
                }
            }
            if (text.length() > 0) {
                docs.put(url, text.toString());
            }
        }
        return docs;
    }

    public boolean keepSection(Section section) {
        if (section.getTitle() == null) {
            return true;
        }
        String title = section.getTitle().toLowerCase();
        return ((!title.equals("sources"))
        &&      (!title.equals("references"))
        &&      (!title.equals("external links")));
    }

    public boolean keepParagraph(Paragraph par) {
        if (par.getText().toLowerCase().startsWith("template")) {
            return false;
        }
        return true;
    }

    public static void main(String args[]) throws IOException, ActionException, ProcessException, JSONException {
        WikipediaTextDumper dumper = new WikipediaTextDumper();
    }

}

package org.macademia;

import net.sourceforge.jwbf.bots.MediaWikiBot;
import net.sourceforge.jwbf.contentRep.mw.Article;
import net.sourceforge.jwbf.actions.mw.util.ActionException;
import net.sourceforge.jwbf.actions.mw.util.ProcessException;

import java.net.MalformedURLException;
import java.io.IOException;

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

    public String getText(String topic) throws ActionException, ProcessException, IOException, JSONException {
        StringBuffer text = new StringBuffer();
        System.err.println("retrieving content for " + topic);
        for (String page : gq.makeQuery(topic, 1)) {
            System.err.println("reading contents of article " + page);
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
        }
        if (text.length() == 0) {
            return null;
        } else {
            return text.toString();
        }
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
        dumper.getText("John Coltrane");
    }

}

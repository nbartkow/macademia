package org.macademia;

import net.sourceforge.jwbf.bots.MediaWikiBot;
import net.sourceforge.jwbf.contentRep.mw.Article;
import net.sourceforge.jwbf.actions.mw.util.ActionException;
import net.sourceforge.jwbf.actions.mw.util.ProcessException;

import java.net.MalformedURLException;

import de.tudarmstadt.ukp.wikipedia.parser.ParsedPage;
import de.tudarmstadt.ukp.wikipedia.parser.Section;
import de.tudarmstadt.ukp.wikipedia.parser.Paragraph;
import de.tudarmstadt.ukp.wikipedia.parser.mediawiki.MediaWikiParser;
import de.tudarmstadt.ukp.wikipedia.parser.mediawiki.MediaWikiParserFactory;

/**
 * Created by IntelliJ IDEA.
 * User: shilad
 * Date: Nov 17, 2009
 * Time: 6:41:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class WikipediaComparator {
    public static void main(String args[]) throws MalformedURLException, ActionException, ProcessException {
        MediaWikiBot bot  = new MediaWikiBot("http://en.wikipedia.org/w/");
        Article article = bot.readContent("John Coltrane");
        String contents = article.getText();
        MediaWikiParserFactory pf = new MediaWikiParserFactory();
        MediaWikiParser parser = pf.createParser();
        ParsedPage pp = parser.parse(contents);

        for(Section section : pp.getSections()) {
            System.out.println("section : " + section.getTitle());
            for (Paragraph p : section.getParagraphs()) {
                System.out.println("contents is " + p.getText());
            }
            System.out.println(" nr of paragraphs      : " + section.nrOfParagraphs());
            System.out.println(" nr of tables          : " + section.nrOfTables());
            System.out.println(" nr of nested lists    : " + section.nrOfNestedLists());
            System.out.println(" nr of definition lists: " + section.nrOfDefinitionLists());
        }
    }
}

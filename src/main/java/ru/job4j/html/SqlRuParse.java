package ru.job4j.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.utils.SqlRuDateTimeParser;

public class SqlRuParse {
    private final static String URL = "https://www.sql.ru/forum/job-offers/";
    private final static int START_PAGE = 1;
    private final static int COUNT = 5;

    public static void main(String[] args) throws Exception {
        int page = START_PAGE;
        Document doc;
        do {
            doc = parsePage(page);
        } while (hasNextPage(doc, ++page));
    }

    private static boolean hasNextPage(Document doc, int nextPage) {
        if (nextPage >= START_PAGE + COUNT) {
            return false;
        }

        Elements nextLink = doc.select("a[href='" + URL + nextPage + "']");

        return nextLink.size() > 0;
    }

    private static Document parsePage(int page) throws Exception {
        Document doc = Jsoup.connect(URL + page).get();

        Elements linkRows = doc.select(".postslisttopic");
        SqlRuDateTimeParser dateParser = new SqlRuDateTimeParser();
        System.out.println("page = " + URL + page);
        for (Element tdLink : linkRows) {
            Element href = tdLink.child(0);
            System.out.println(href.attr("href"));
            System.out.println(href.text());

            Element tdDate = tdLink.parent().child(5);
            System.out.println(tdDate.text());

            System.out.println("parsed date = " + dateParser.parse(tdDate.text()));
        }

        return doc;
    }
}
package ru.job4j.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.utils.SqlRuDateTimeParser;

import java.time.LocalDateTime;

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
            Post post = parsePost(href.attr("href"));
            System.out.println("post = " + post);
        }

        return doc;
    }

    private static Post parsePost(String href) throws Exception {
        Document doc = Jsoup.connect(href).get();

        String name = doc.selectFirst(".messageHeader")
                .textNodes().get(0).text().trim();

        String text = doc.select(".msgBody").eq(1).html();

        String date = doc.selectFirst(".msgFooter").textNodes()
                .get(0).text().replace("[", "").trim();

        LocalDateTime dateTime = new SqlRuDateTimeParser().parse(date);

        return new Post(name, text, href, dateTime);
    }
}
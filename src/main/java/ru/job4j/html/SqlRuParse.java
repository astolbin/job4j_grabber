package ru.job4j.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.utils.SqlRuDateTimeParser;

public class SqlRuParse {
    public static void main(String[] args) throws Exception {
        Document doc = Jsoup.connect("https://www.sql.ru/forum/job-offers").get();
        Elements linkRows = doc.select(".postslisttopic");
        SqlRuDateTimeParser dateParser = new SqlRuDateTimeParser();
        for (Element tdLink : linkRows) {
            Element href = tdLink.child(0);
            System.out.println(href.attr("href"));
            System.out.println(href.text());

            Element tdDate = tdLink.parent().child(5);
            System.out.println(tdDate.text());

            System.out.println("parsed date = " + dateParser.parse(tdDate.text()));
        }
    }
}
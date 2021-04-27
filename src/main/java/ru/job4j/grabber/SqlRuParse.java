package ru.job4j.grabber;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.utils.SqlRuDateTimeParser;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SqlRuParse implements Parse {
    public static void main(String[] args) throws Exception {
        List<Post> posts = new SqlRuParse().list("https://www.sql.ru/forum/job-offers/1");

        for (Post post : posts) {
            System.out.println("post = " + post);
        }
    }

    @Override
    public List<Post> list(String link) throws Exception {
        List<Post> rsl = new ArrayList<>();

        Document doc = Jsoup.connect(link).get();
        Elements linkRows = doc.select(".postslisttopic");

        for (Element tdLink : linkRows) {
            Element href = tdLink.child(0);
            rsl.add(detail(href.attr("href")));
        }

        return rsl;
    }

    @Override
    public Post detail(String link) throws Exception {
        Document doc = Jsoup.connect(link).get();

        String name = doc.selectFirst(".messageHeader")
                .textNodes().get(0).text().trim();

        String text = doc.select(".msgBody").eq(1).html();

        String date = doc.selectFirst(".msgFooter").textNodes()
                .get(0).text().replace("[", "").trim();

        LocalDateTime dateTime = new SqlRuDateTimeParser().parse(date);

        return new Post(name, text, link, dateTime);
    }
}
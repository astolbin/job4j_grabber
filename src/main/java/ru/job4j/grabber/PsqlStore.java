package ru.job4j.grabber;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PsqlStore implements Store, AutoCloseable {
    private final Connection cnn;
    private final static int TEXT_MAX_LENGTH = 2000;

    public static void main(String[] args) throws Exception {
        Properties cfg = getConfig("app.properties");

        try (PsqlStore store = new PsqlStore(cfg)) {
            for (int i = 1; i < 4; i++) {
                Post post = new Post(
                        "Test " + i,
                        "Text text text",
                        "Link " + i,
                        LocalDateTime.now()
                );
                store.save(post);
                System.out.println("post = " + store.findById(String.valueOf(post.getId())));
            }

            System.out.println("posts = " + store.getAll());
        }
    }

    public PsqlStore(Properties cfg) throws Exception {
        try {
            Class.forName(cfg.getProperty("db.driver"));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

        cnn = DriverManager.getConnection(
                cfg.getProperty("db.url"),
                cfg.getProperty("db.username"),
                cfg.getProperty("db.password")
        );
    }

    @Override
    public void save(Post post) {
        String sql = "insert into post (name, text, link, created) values (?, ?, ?, ?)";

        try (PreparedStatement statement =
                     cnn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            String text = post.getText();
            if (text.length() >= TEXT_MAX_LENGTH) {
                text = text.substring(0, TEXT_MAX_LENGTH);
            }
            statement.setString(1, post.getName());
            statement.setString(2, text);
            statement.setString(3, post.getLink());
            statement.setTimestamp(4, Timestamp.valueOf(post.getCreate()));
            statement.executeUpdate();
            ResultSet rsl = statement.getGeneratedKeys();
            if (rsl.next()) {
                post.setId(rsl.getInt("id"));
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public List<Post> getAll() {
        List<Post> posts = new ArrayList<>();
        try (PreparedStatement statement = cnn.prepareStatement("select * from post")) {
            ResultSet rsl = statement.executeQuery();
            while (rsl.next()) {
                Post post = new Post(
                        rsl.getString("name"),
                        rsl.getString("text"),
                        rsl.getString("link"),
                        rsl.getTimestamp("created").toLocalDateTime()
                );
                post.setId(rsl.getInt("id"));
                posts.add(post);
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

        return posts;
    }

    @Override
    public Post findById(String id) {
        Post post = null;
        String sql = "select * from post where id = ?";

        try (PreparedStatement statement = cnn.prepareStatement(sql)) {
            statement.setInt(1, Integer.parseInt(id));
            ResultSet rsl = statement.executeQuery();
            if (rsl.next()) {
                post = new Post(
                        rsl.getString("name"),
                        rsl.getString("text"),
                        rsl.getString("link"),
                        rsl.getTimestamp("created").toLocalDateTime()
                );
                post.setId(rsl.getInt("id"));
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

        return post;
    }

    @Override
    public void close() throws Exception {
        if (cnn != null) {
            cnn.close();
        }
    }

    private static Properties getConfig(String name) {
        Properties cfg = new Properties();

        try (InputStream in = PsqlStore.class
                .getClassLoader()
                .getResourceAsStream(name)
        ) {
            cfg.load(in);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        return cfg;
    }
}
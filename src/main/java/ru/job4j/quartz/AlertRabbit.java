package ru.job4j.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;

public class AlertRabbit {
    public static void main(String[] args) {
        Properties config = getScheduleProps("app.properties");

        try (Connection connection = getConnection(config)) {
            Scheduler scheduler = initScheduler(config, connection);
            Thread.sleep(10000);
            scheduler.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Scheduler initScheduler(Properties config, Connection connection)
            throws SchedulerException {
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        scheduler.start();

        JobDataMap data = new JobDataMap();
        data.put("connection", connection);

        JobDetail job = newJob(Rabbit.class)
                .usingJobData(data)
                .build();

        int interval = Integer.parseInt(config.getProperty("grab.interval"));
        SimpleScheduleBuilder times = simpleSchedule()
                .withIntervalInSeconds(interval)
                .repeatForever();
        Trigger trigger = newTrigger()
                .startNow()
                .withSchedule(times)
                .build();
        scheduler.scheduleJob(job, trigger);

        return scheduler;
    }

    public static Properties getScheduleProps(String propsPath) {
        try (InputStream in = AlertRabbit.class
                .getClassLoader()
                .getResourceAsStream(propsPath)
        ) {
            Properties config = new Properties();
            config.load(in);
            return config;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public static Connection getConnection(Properties config) throws Exception {
        Class.forName(config.getProperty("db.driver"));
        return DriverManager.getConnection(
                config.getProperty("db.url"),
                config.getProperty("db.username"),
                config.getProperty("db.password")
        );
    }

    public static class Rabbit implements Job {
        @Override
        public void execute(JobExecutionContext context) {
            System.out.println("Rabbit runs here ...");
            Connection connection = (Connection) context
                    .getJobDetail()
                    .getJobDataMap()
                    .get("connection");

            var sql = "insert into rabbit (created_date) values (current_timestamp)";
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(sql);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
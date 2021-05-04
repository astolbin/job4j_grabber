package ru.job4j.grabber;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.*;
import java.util.Properties;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

public class Grabber implements Grab {
    private final static int MAX_PAGE = 3;
    private final static String LIST_URL = "https://www.sql.ru/forum/job-offers/";

    private static int currentPage = 1;

    private final Properties cfg = new Properties();

    public Store store() throws Exception {
        return new PsqlStore(cfg);
    }

    public Scheduler scheduler() throws SchedulerException {
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        scheduler.start();
        return scheduler;
    }

    public void cfg(String name) {
        try (InputStream in = getClass()
                .getClassLoader()
                .getResourceAsStream(name)
        ) {
            cfg.load(in);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void init(Parse parse, Store store, Scheduler scheduler) throws SchedulerException {
        JobDataMap data = new JobDataMap();
        data.put("store", store);
        data.put("parse", parse);
        JobDetail job = newJob(GrabJob.class)
                .usingJobData(data)
                .build();
        SimpleScheduleBuilder times = simpleSchedule()
                .withIntervalInSeconds(Integer.parseInt(cfg.getProperty("grab.interval")))
                .withRepeatCount(MAX_PAGE - 1);
        Trigger trigger = newTrigger()
                .startNow()
                .withSchedule(times)
                .build();
        scheduler.scheduleJob(job, trigger);
    }

    public static class GrabJob implements Job {
        @Override
        public void execute(JobExecutionContext context) {
            JobDataMap map = context.getJobDetail().getJobDataMap();
            Store store = (Store) map.get("store");
            Parse parse = (Parse) map.get("parse");

            try {
                String url = LIST_URL + currentPage++;
                parse.list(url)
                        .stream()
                        .filter(this::hasJava)
                        .forEach(store::save);
                System.out.println("url = " + url);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private boolean hasJava(Post post) {
            return post.getName().contains("Java");
        }
    }

    public static void main(String[] args) throws Exception {
        Grabber grab = new Grabber();
        grab.cfg("app.properties");
        Scheduler scheduler = grab.scheduler();
        Store store = grab.store();
        grab.init(new SqlRuParse(), store, scheduler);
    }
}

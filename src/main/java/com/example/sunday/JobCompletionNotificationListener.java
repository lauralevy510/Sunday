package com.example.sunday;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {

    private static final Logger log = LoggerFactory.getLogger(JobCompletionNotificationListener.class);

    private final JdbcTemplate jdbcTemplate;

    public Date date;

    @Autowired
    public JobCompletionNotificationListener(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        if(jobExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info("!!! JOB FINISHED! Time to verify the results");

                jdbcTemplate.query("SELECT first_name, last_name FROM people",
                        (rs, row) -> {
                            return new Person(
                                        rs.getString(1),
                                        rs.getString(2),
                                        rs.getDouble(3),
                                        rs.getDate(4),
                                        rs.getString(5),
                                        rs.getDouble(6)
                            );
                        }
                ).forEach(person -> log.info("Found <" + person + "> in the database."));

            /*jdbcTemplate.query("SELECT first_name, last_name FROM people",
                    (rs, row) -> {
                        return new Person(
                                rs.getString(1),
                                rs.getString(2),
                                new SimpleDateFormat("dd/MMyyy").parse(rs.getString(3)),
                                rs.getString(4),
                                rs.getDouble(5)
                        );
                    }
            ).forEach(person -> log.info("Found <" + person + "> in the database."));

             */
        }
    }
}
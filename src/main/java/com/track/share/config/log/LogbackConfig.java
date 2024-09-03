package com.track.share.config.log;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import ch.qos.logback.core.util.FileSize;

import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LogbackConfig {

    @Bean
    RollingFileAppender<ch.qos.logback.classic.spi.ILoggingEvent> rollingFileAppender() {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        RollingFileAppender<ch.qos.logback.classic.spi.ILoggingEvent> appender = new RollingFileAppender<>();
        appender.setContext(context);
        appender.setName("FILE");
        appender.setFile("logs/springboot-app.log");

        // Set up the encoder
        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(context);
        encoder.setPattern("%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n");
        encoder.start();
        appender.setEncoder(encoder);

        // Set up the rolling policy
        TimeBasedRollingPolicy<ch.qos.logback.classic.spi.ILoggingEvent> rollingPolicy = new TimeBasedRollingPolicy<>();
        rollingPolicy.setContext(context);
        rollingPolicy.setParent(appender);
        rollingPolicy.setFileNamePattern("logs/springboot-app-%d{yyyy-MM-dd}.%i.log");
        rollingPolicy.setMaxHistory(30); // Keep 30 days of logs
        rollingPolicy.setTotalSizeCap(FileSize.valueOf("10MB"));
        rollingPolicy.start();
        appender.setRollingPolicy(rollingPolicy);

        appender.start();
        return appender;
    }

    @Bean
    ch.qos.logback.classic.Logger rootLogger() {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        ch.qos.logback.classic.Logger logger = context.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
        logger.addAppender(rollingFileAppender());
        logger.setLevel(ch.qos.logback.classic.Level.INFO);
        return logger;
    }
}

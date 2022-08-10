/*
 * Copyright 2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"). You may not use this file except in compliance
 * with the License. A copy of the License is located at
 *
 * http://aws.amazon.com/apache2.0/
 *
 * or in the "license" file accompanying this file. This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */
package net.softel.ai.classify;

import net.softel.ai.classify.common.AmazonClientConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Import;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;

import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.EnableAsync;


@EnableAsync
@EnableScheduling
@SpringBootApplication
@Import(AmazonClientConfiguration.class)
public class ClassifySpringBootApplication implements ApplicationListener<ApplicationReadyEvent> {

    static final Logger log = LoggerFactory.getLogger(ClassifySpringBootApplication.class);

    @Value("${git.build.version}")
    String buildVersion;

    @Value("${git.commit.time}")
    String buildTime;

    @Value("${git.commit.id}")
    String gitCommitIdFull;

    @Value("${git.branch}")
    String gitBranch;

    @Value("${git.commit.user.name}")
    String commitAuthor;

    @Value("${git.commit.message.full}")
    String commitMessage;

    @Value("${git.total.commit.count}")
    String gitTotalCommitCount;

    public static void main(String[] args) {
        init();
    }

    private static void init(){
        Environment env = SpringApplication.run(ClassifySpringBootApplication.class).getEnvironment();
        String protocol = "http";
        try {
            log.info(
                    "\n----------------------------------------------------------\n\t"
                            + "Application {} (v{}) is running! Access URLs:\n\t"
                            + "Local: \t\t{}://localhost:{}{}\n\t"
                            + "External: \t{}://{}:{}\n\t"
                            + "Profile(s): \t{}\n----------------------------------------------------------",
                    env.getProperty("info.app.description"),
                    env.getProperty("info.app.version"),
                    protocol,
                    env.getProperty("local.server.port"),
                    env.getProperty("server.servlet.context-path"),
                    protocol,
                    InetAddress.getLocalHost().getHostAddress(),
                    env.getProperty("local.server.port"),
                    env.getActiveProfiles());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * Handle an application event.
     *
     * @param event the event to respond to
     */
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        log.info(
                "\n------------------GIT INFORMATION------------------------\n\t"
                        + "Build Git Version: {}\n\t"
                        + "Build Time : {} \n\t"
                        + "Commit Ref :  {}\n\t"
                        + "Branch : {}\n\t"
                        + "Commit Message: {}\n\t"
                        + "Author: {}\n\t"
                        + "Git Total Commit Count: {}\n"
                        + "----------------------------------------------------------",
                buildVersion,
                buildTime,
                gitCommitIdFull,
                gitBranch,
                commitMessage,
                commitAuthor,
                gitTotalCommitCount
        );
    }
}

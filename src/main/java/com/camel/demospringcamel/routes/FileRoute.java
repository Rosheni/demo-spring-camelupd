package com.camel.demospringcamel.routes;

import com.camel.demospringcamel.processor.MyProcessor;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RouteDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FileRoute extends RouteBuilder {

    @Value("${app.source}")
    private String sourcelocation;

    @Value("${app.destination}")
    private String destination;

    @Autowired
    private MyProcessor processor;

    @Override
    public void configure() throws Exception {

//        fileCopier();
        renameFile();
    }

    private void renameFile() {
        from("file:" + sourcelocation)
                .routeId("Rename-File-Route")
                .log(LoggingLevel.INFO, "Incoming File :${file:name}")
//                .setHeader(Exchange.FILE_NAME, simple("${file:size}"))
                 .setHeader(Exchange.FILE_NAME,simple("${file:name.noext}_Modified.${file:name.ext}"))
//                 .setHeader(Exchange.FILE_NAME,simple("${file:name.noext}_${date:now:yyyyMMdd}.${file:name.ext}"))
                .log(LoggingLevel.INFO, "Rename File: ${file:name}")
                .to("file:" + destination);
    }

    private void fileCopier() {
        from("file:" + sourcelocation).routeId("Demo-File-Route")
//               .setBody(simple(" ${body}.I have updated the file content"))
                .process(processor)
                .to("file:" + destination)
                .log(LoggingLevel.INFO, "File is moved to destination \n ${body}");
    }
}

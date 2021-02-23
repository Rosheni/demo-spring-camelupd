package com.camel.demospringcamel.routes;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JmsRoute extends RouteBuilder {

    @Value("${app.messageSource}")
    private String messageSource;

    @Value("${app.messageDestination}")
    private String messageDestination;

    @Override
    public void configure() throws Exception{
        consumeMessageFromAmq();
        produceMessage2AmqUsingTimer();
        postFileToAmq();
        consumeFileToMove();
    }

    private void consumeFileToMove() {
        from("activemq:queue:fileMessage")
                .routeId(("Consume-to-move"))
                .log(LoggingLevel.INFO,"Message consumed from ActiveMQ Queue: ${body}")
                .setHeader(Exchange.FILE_NAME,
                        simple("${file:name.noext}_Received_on_${date:now:yyyyMMdd}.${file:name.ext}"))
                .to("file:" + messageDestination)
                .log("File Created at the Destination");
    }

    private void postFileToAmq() {
        from("file:" + messageSource)
                .routeId(("Message-From_File-Route"))
                .log(LoggingLevel.INFO,"Incoming Message ${body}")
                .to("activemq:queue:fileMessage")
                .log("Message posted to ActiveMQ Queue");
    }

    private void consumeMessageFromAmq() {
        from("activemq:queue:myqueue")
                .routeId(("JMS-Message-Route"))
                .log(LoggingLevel.INFO,"Incoming Message ${body}");
    }

    private void produceMessage2AmqUsingTimer() {
        from("timer:mytimer?period=5000")
                .routeId("Timer-Produce-Message-Route")
                .setBody(constant("Hello from spring boot Camel !!!"))
                .to("activemq:queue:myqueue");

    }



}

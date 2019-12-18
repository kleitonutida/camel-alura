package br.com.caelum.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

public class Velocity {

    public static void main(String[] args) throws Exception {

        CamelContext context = new DefaultCamelContext();

        RouteBuilder builder = new RouteBuilder() {
            @Override
            public void configure() throws Exception {

                from("direct:entrada").
                    setHeader("data", constant("8/12/2015")).
                    to("velocity:template.vm").
                log("${body}");
            }
        };

        context.addRoutes(builder);

        context.start();
        Thread.sleep(20000);

        ProducerTemplate producer = context.createProducerTemplate();
        producer.sendBody("direct:entrada", "Apache Camel Rocks!!!");

    }
}

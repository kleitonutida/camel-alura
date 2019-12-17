package br.com.caelum.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

public class NegociacoesToFile {

    public static void main(String[] args) throws Exception {

        CamelContext context = new DefaultCamelContext();

        RouteBuilder builder = new RouteBuilder() {
            @Override
            public void configure() throws Exception {
            from("timer://negociacoes?fixedRate=true&delay=1s&period=360s").
                to("https4://argentumws-spring.herokuapp.com/negociacoes").
                    convertBodyTo(String.class).
                    log("${body}").
                    setHeader(Exchange.FILE_NAME, constant("negociacoes.xml")).
            to("file:negociacoes");
            }
        };

        context.addRoutes(builder);

        context.start();
        Thread.sleep(10000);
    }
}

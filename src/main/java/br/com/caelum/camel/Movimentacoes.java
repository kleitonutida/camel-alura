package br.com.caelum.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.http4.HttpMethods;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.http.entity.ContentType;

public class Movimentacoes {

    public static void main(String[] args) throws Exception {

        CamelContext context = new DefaultCamelContext();

        RouteBuilder builder = new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("file:movimentacoes?delay=5s&noop=true").
                    routeId("rota-movimentacoes").
                to("direct:entrada");

                from("direct:entrada").
                    routeId("rota-entrada").
                        log("1 - ${body}").
                    transform(body().regexReplaceAll("tipo","tipoEntrada")). // realizar transformacao
                        log("2 - ${body}").
                    to("xslt:movimentacoes.xslt").
                        setHeader(Exchange.FILE_NAME, constant("movimentacoes.html")).
                to("file:saida");
            }
        };

        context.addRoutes(builder);

        context.start();
        Thread.sleep(20000);
    }
}

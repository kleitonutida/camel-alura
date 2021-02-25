package br.com.caelum.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Route;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.spi.RoutePolicyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class RoutePolicy {

    @Autowired
    @Qualifier("NewContext")
    private static CamelContext context;

    public static void main(String[] args) throws Exception {
        RouteBuilder builder = new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("file:movimentacoes?delay=5s&noop=true").
                        routeId("rota-movimentacoes").
                        log("Executando rota-movimentacoes").
                        to("direct:entrada");

                from("direct:entrada").
                        routeId("rota-entrada").
                        log("Executando rota-entrada").
//                        log("1 - ${body}").
                        transform(body().regexReplaceAll("tipo","tipoEntrada")). // realizar transformacao
//                        log("2 - ${body}").
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

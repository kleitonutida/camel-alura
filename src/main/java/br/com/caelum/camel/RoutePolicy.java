package br.com.caelum.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Route;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.spi.RoutePolicyFactory;

public class RoutePolicy {

    public static void main(String[] args) throws Exception {
        CamelContext context = new DefaultCamelContext();
        context.addRoutePolicyFactory(new MyRoutePolicyFactory());

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

    private static class MyRoutePolicyFactory implements RoutePolicyFactory {
        @Override
        public org.apache.camel.spi.RoutePolicy createRoutePolicy(CamelContext camelContext, String routeId, RouteDefinition route) {
            System.out.println("Criando Route Policy - Route ID: " + routeId);
            org.apache.camel.spi.RoutePolicy routePolicy = new org.apache.camel.spi.RoutePolicy() {
                @Override
                public void onInit(Route route) {
                    System.out.println("onInit - Route ID: " + routeId);
                }

                @Override
                public void onRemove(Route route) {
                    System.out.println("onRemove - Route ID: " + routeId);
                }

                @Override
                public void onStart(Route route) {
                    System.out.println("onStart - Route ID: " + routeId);
                }

                @Override
                public void onStop(Route route) {
                    System.out.println("onStop - Route ID: " + routeId);
                }

                @Override
                public void onSuspend(Route route) {
                    System.out.println("onSuspend - Route ID: " + routeId);
                }

                @Override
                public void onResume(Route route) {
                    System.out.println("onResume - Route ID: " + routeId);
                }

                @Override
                public void onExchangeBegin(Route route, Exchange exchange) {
                    System.out.println("onExchangeBegin - Route ID: " + routeId);
                }

                @Override
                public void onExchangeDone(Route route, Exchange exchange) {
                    System.out.println("onExchangeDone - Route ID: " + routeId);
                }
            };
            return routePolicy;
        }
    }
}

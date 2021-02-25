package br.com.caelum.camel.config;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Route;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.spi.RoutePolicyFactory;
import org.springframework.stereotype.Component;

@Component(value = "NewContext")
public class MyCamelContext {

    private CamelContext context;

    public MyCamelContext(CamelContext context) {
        this.context.addRoutePolicyFactory(new MyRoutePolicyFactory());
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

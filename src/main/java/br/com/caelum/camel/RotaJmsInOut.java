package br.com.caelum.camel;

import br.com.caelum.camel.jms.TratadorMensagemJms;
import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

public class RotaJmsInOut {

    public static void main(String[] args) throws Exception {

        CamelContext context = new DefaultCamelContext();
        context.addComponent("activemq", ActiveMQComponent.activeMQComponent("tcp://localhost:61616"));

        RouteBuilder builder = new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("activemq:queue:pedidos.req").
                    bean(TratadorMensagemJms.class).
                    log("Exchange - ${exchange.pattern}").
                    setBody(constant("Camel Rocks!")).
                    setHeader(Exchange.FILE_NAME, constant("message.txt")).
                to("file:saida");
            }
        };

        context.addRoutes(builder);

        context.start();
        Thread.sleep(10000);
        context.stop();
    }
}

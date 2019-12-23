package br.com.caelum.camel;

import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.http4.HttpMethods;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.http.entity.ContentType;
import org.xml.sax.SAXParseException;

public class RotaPedidos {

    public static void main(String[] args) throws Exception {

        CamelContext context = new DefaultCamelContext();
        context.addComponent("activemq", ActiveMQComponent.activeMQComponent("tcp://localhost:61616"));

        RouteBuilder builder = new RouteBuilder() {
            @Override
            public void configure() throws Exception {

//                onException(Exception.class).
//                        handled(false).
//                        to("file:error-parsing");

//                errorHandler(deadLetterChannel("file:erro"). // enviar o erro para um arquivo, o tratamento do deadLetterChannel funciona como uma rota
                errorHandler(deadLetterChannel("activemq:queue:pedidos.DLQ"). // enviar o erro para uma fila
                        logExhaustedMessageHistory(true). // exibir a stacktrace com erro
                        maximumRedeliveries(3). // quantidade maxima de tentativas
                        redeliveryDelay(2000). // tempo de delay para a re-tentativa
                        onRedelivery(exchange -> { // implementacao do processor para tratativa em cada tentativa
                            int counter = (int) exchange.getIn().getHeader(Exchange.REDELIVERY_COUNTER);
                            int max = (int) exchange.getIn().getHeader(Exchange.REDELIVERY_MAX_COUNTER);
                            System.out.println("Redelivery " + counter + "/" + max);
                        }));

                // lendo arquivos e delegando para outras rotas
//                from("file:pedidos?delay=5s&noop=true").
                // recebendo mensagem de uma fila
                from("activemq:queue:pedidos").
                routeId("rota-pedidos").
                to("validator:pedido.xsd").
                    multicast().
                        to("direct:soap"). // delegando
                        to("direct:http"); // delegando

                from("direct:http").
                    routeId("rota-http").
                    setProperty("pedidoId", xpath("/pedido/id/text()")).
                        log("Pedido ID: ${property.pedidoId}").
                    setProperty("clientId", xpath("/pedido/pagamento/email-titular/text()")).
                        log("Client ID: ${property.clientId}").
                    split().
                        xpath("/pedido/itens/item").
                    filter().
                        xpath("/item/formato[text()='EBOOK']").
                    setProperty("ebookId", xpath("/item/livro/codigo/text()")).
                        log("Ebook ID: ${property.ebookId}").
                    marshal().
                        xmljson().
                    log("${id} - ${body}").
                    setHeader(Exchange.HTTP_METHOD, HttpMethods.GET).
                    setHeader(Exchange.HTTP_QUERY, simple("clienteId=${property.clientId}&pedidoId=${property.pedidoId}&ebookId=${property.ebookId}")).
                to("http4://localhost:8080/webservices/ebook/item");

                from("direct:soap").
                    routeId("rota-soap").
                    to("xslt:pedido-para-soap.xslt").
                    log("Resultado do Template: ${body}").
                    setHeader(Exchange.CONTENT_TYPE, constant(ContentType.TEXT_XML.getMimeType())).
                to("http4://localhost:8080/webservices/financeiro");
            }
        };

        context.addRoutes(builder);

        context.start();
        Thread.sleep(20000);
        context.stop();
    }
}

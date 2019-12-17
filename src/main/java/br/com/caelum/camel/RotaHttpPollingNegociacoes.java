package br.com.caelum.camel;

import br.com.caelum.camel.domain.Negociacao;
import com.thoughtworks.xstream.XStream;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.xstream.XStreamDataFormat;
import org.apache.camel.impl.DefaultCamelContext;

public class RotaHttpPollingNegociacoes {

    public static void main(String[] args) throws Exception {

        CamelContext context = new DefaultCamelContext();

        final XStream xstream = new XStream();
        xstream.alias("negociacao", Negociacao.class);

        RouteBuilder builder = new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("timer://negociacoes?fixedRate=true&delay=1s&period=360s").
                    to("https4://argentumws-spring.herokuapp.com/negociacoes").
                        convertBodyTo(String.class).
                    unmarshal(new XStreamDataFormat(xstream)).
                        split(body()).
                        log("${body}").
                end();
            }
        };

        context.addRoutes(builder);

        context.start();
        Thread.sleep(10000);
    }
}

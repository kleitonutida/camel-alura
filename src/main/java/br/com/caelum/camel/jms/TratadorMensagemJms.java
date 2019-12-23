package br.com.caelum.camel.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

public class TratadorMensagemJms implements MessageListener {

    @Override
    public void onMessage(Message message) {
        try {
            System.out.println("JMSMessageID : " + message.getJMSMessageID());
            System.out.println("JMSCorrelationID : " + message.getJMSCorrelationID());
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}

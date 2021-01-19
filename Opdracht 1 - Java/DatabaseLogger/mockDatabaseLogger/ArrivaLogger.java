package mockDatabaseLogger;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import com.thoughtworks.xstream.XStream;

public class ArrivaLogger implements Runnable {

	private Connection connection;
	private Session session;
	private MessageConsumer consumer;

	@Override
	public void run() {
		try {
			setupConsumer();
			boolean newMessage = true;
			int aantalBerichten = 0;
			int aantalETAs = 0;
			while (newMessage) {
				Message message = consumer.receive(2000);
				newMessage = false;
				if (message instanceof TextMessage) {
					newMessage = true;
					Bericht bericht = parseMessageToBericht(message);
					aantalBerichten++;
					aantalETAs += bericht.ETAs.size();
				} else {
					System.out.println("Received: " + message);
				}
			}
			closeConsumer();
			System.out.println(aantalBerichten + " berichten met " + aantalETAs + " ETAs verwerkt.");
		} catch (Exception e) {
			System.out.println("Caught: " + e);
			e.printStackTrace();
		}
	}

	private void setupConsumer() throws JMSException {
		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
				ActiveMQConnection.DEFAULT_BROKER_URL);
		connection = connectionFactory.createConnection();
		connection.start();
		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		Destination destination = session.createQueue("ARRIVALOGGER");
		consumer = session.createConsumer(destination);
	}

	private void closeConsumer() throws JMSException {
		consumer.close();
		session.close();
		connection.close();
	}

	private Bericht parseMessageToBericht(Message message) throws JMSException {
		TextMessage textMessage = (TextMessage) message;
		String text = textMessage.getText();
		XStream xstream = new XStream();
		xstream.alias("Bericht", Bericht.class);
		xstream.alias("ETA", ETA.class);
		return (Bericht) xstream.fromXML(text);
	}

}


import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import java.sql.Timestamp;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

public class JMSChatSender {

	private static String user = ActiveMQConnection.DEFAULT_USER;
	private static String password = ActiveMQConnection.DEFAULT_PASSWORD;
	private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;
	private static String subject = "VSDBChat";

	public static void main(String[] args) {

		// Create the connection.
		Session session = null;
		Connection connection = null;
		MessageProducer producer = null;
		Destination destination = null;

		try {

			ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(user, password, url);
			connection = connectionFactory.createConnection();
			connection.start();

			// Create the session
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			destination = session.createQueue(subject);

			// Create the producer.
			producer = session.createProducer(destination);
			producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

			// Create the message
			int randomParkID = (int)(Math.random()*10+1);
			int randomWindRaeder = (int)(Math.random()*7+1);
			TextMessage message = session.createTextMessage(randomXML(randomParkID, randomWindRaeder));
			producer.send(message);
			System.out.println(message.getText());
			connection.stop();

		} catch (Exception e) {

			System.out.println("[MessageProducer] Caught: " + e);
			e.printStackTrace();

		} finally {

			try {
				producer.close();
			} catch (Exception e) {
			}
			try {
				session.close();
			} catch (Exception e) {
			}
			try {
				connection.close();
			} catch (Exception e) {
			}

		}

	} // end main

	public static double randomGenerator(double low, double high) {
		double r = Math.random() * (high - low) + low;
		double roundOff = (double) Math.round(r * 100) / 100;

		return roundOff;
	}

	public static String randomXML(int windparkId, int anzahlWindRaeder) {
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		//Gibt die Zeit dazu, damit man später besser nachvollziehen kann von wann die Nachricht gekommen ist.
		String xml = "\n<!-- Data sent at "+timestamp+" -->\n";
		xml += "<windpark id=\"NOE" + windparkId + "\">";
		for (int i = 1; i <= anzahlWindRaeder; i++) {
			xml += "\n\t<windrad id=\"" + i + "\">\n\t\t<power unit=\"kWh\">" + randomGenerator(100, 500)
					+ "</power>\n\t\t<blindpower unit=\"kWh\">" + randomGenerator(100, 500) + "</blindpower>"
					+ "\n\t\t<windspeed unit=\"km/h\">" + randomGenerator(0, 100)
					+ "</windspeed>\n\t\t<amk unit=\"m/s\">" + randomGenerator(0.1, 0.7)
					+ "</amk>\n\t\t<temperature unit=\"C\">" + randomGenerator(10, 35) + "</temperature>"
					+ "\n\t\t<bladeposition unit=\"deg\">" + randomGenerator(0, 360)
					+ "</bladeposition>\n\t\t<transfertime unit=\"ms\">" + randomGenerator(1, 3000)
					+ "</transfertime>\n\t</windrad>\n";
		}
		xml += "</windpark>\n";

		return xml;
	}
}

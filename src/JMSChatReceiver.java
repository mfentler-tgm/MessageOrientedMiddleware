
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import java.io.FileOutputStream;
import java.io.IOException;

//
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.File;

import java.sql.Timestamp;
//

public class JMSChatReceiver {

	private static String user = ActiveMQConnection.DEFAULT_USER;
	private static String password = ActiveMQConnection.DEFAULT_PASSWORD;
	private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;
	private static String subject = "VSDBChat";

	public static void main(String[] args) {

		// Create the connection.
		Session session = null;
		Connection connection = null;
		MessageConsumer consumer = null;
		Destination destination = null;

		try {

			ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(user, password, url);
			connection = connectionFactory.createConnection();
			connection.start();

			// Create the session
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			destination = session.createQueue(subject);

			// Create the consumer
			consumer = session.createConsumer(destination);

			// Start receiving
			// Läuft solange, bis manuell in der Console gekillt wird mit STRG+C
			while (true) {
				TextMessage message = (TextMessage) consumer.receive();
				Timestamp timestamp = new Timestamp(System.currentTimeMillis());
				writeToXML(message.getText());
				
				if (message != null) {
					message.acknowledge();
				}
				
			}
			//

		} catch (Exception e) {

			System.out.println("[MessageConsumer] Caught: " + e);
			e.printStackTrace();

		} finally {
			try {
				consumer.close();
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

	public static String getCharacterDataFromElement(Element e) {
		Node child = e.getFirstChild();
		if (child instanceof CharacterData) {
			CharacterData cd = (CharacterData) child;
			return cd.getData();
		}
		return "";
	}

	/**
	 * Die Methode schreibt mir die Nachricht in das Output.xml File und schreibt sie zusätzlich in die Console.
	 * @param str Ist ein ein XML-String
	 * @throws IOException
	 */
	public static void writeToXML(String str) throws IOException {
		System.out.println(str);

		File yourFile = new File("output.xml");
		yourFile.createNewFile();
		FileOutputStream outputStream = new FileOutputStream(yourFile, true);
		byte[] strToBytes = str.getBytes();
		outputStream.write(strToBytes);

		outputStream.close();
	}

}

package bussimulator.parser;

import com.thoughtworks.xstream.XStream;

import bussimulator.Bericht;
import bussimulator.ETA;
import bussimulator.Producer;

public class XMLParser implements Parser {	
	public void sendBericht(Bericht bericht) {
		XStream xstream = new XStream();
		xstream.alias("Bericht", Bericht.class);
		xstream.alias("ETA", ETA.class);
		String xml = xstream.toXML(bericht);
		Producer producer = new Producer();
		producer.sendBericht(xml);
	}
}

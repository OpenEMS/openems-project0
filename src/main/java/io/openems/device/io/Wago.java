package io.openems.device.io;

import io.openems.device.protocol.BitElement;
import io.openems.device.protocol.BitsElement;
import io.openems.device.protocol.ElementBuilder;
import io.openems.device.protocol.ElementRange;
import io.openems.device.protocol.ModbusProtocol;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class Wago extends IO {

	private InetAddress ip;

	private List<String> writeElements;
	private List<String> mainElements;
	private HashMap<String, String> bitElementMapping;

	public Wago(String name, String channel, int unitid, InetAddress ip) throws IOException,
			ParserConfigurationException, SAXException {
		super(name, channel, unitid);
		this.ip = ip;
	}

	@Override
	public Set<String> getWriteElements() {
		return new HashSet<String>(writeElements);
	}

	public InetAddress getIp() {
		return ip;
	}

	@Override
	protected ModbusProtocol getProtocol() throws IOException, ParserConfigurationException, SAXException {
		writeElements = new ArrayList<String>();
		mainElements = new ArrayList<String>();
		bitElementMapping = new HashMap<String, String>();
		HashMap<String, List<String>> channels = new HashMap<>();
		ModbusProtocol protocol = new ModbusProtocol(name);
		String username = "admin";
		String password = "wago";
		int ftpPort = 21;
		URL url = new URL("ftp://" + username + ":" + password + "@" + ip.getHostAddress() + ":" + ftpPort
				+ "/etc/EA-config.xml;type=i");
		URLConnection urlc = url.openConnection();
		InputStream is = urlc.getInputStream();
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(is);
		doc.getDocumentElement().normalize();

		Node wagoNode = doc.getElementsByTagName("WAGO").item(0);
		if (wagoNode != null) {
			HashMap<String, Integer> moduleCounter = new HashMap<String, Integer>();
			Node moduleNode = wagoNode.getFirstChild();
			while (moduleNode != null) {
				if (moduleNode.getNodeType() == Node.ELEMENT_NODE) {
					NamedNodeMap moduleAttrs = moduleNode.getAttributes();
					String article = moduleAttrs.getNamedItem("ARTIKELNR").getNodeValue();
					String moduletype = moduleAttrs.getNamedItem("MODULETYPE").getNodeValue();
					if (!moduleCounter.containsKey(moduletype)) {
						moduleCounter.put(moduletype, 0);
					}
					moduleCounter.replace(moduletype, moduleCounter.get(moduletype) + 1);
					int index = 1;
					Node channelNode = moduleNode.getFirstChild();
					while (channelNode != null) {
						if (channelNode.getNodeType() == Node.ELEMENT_NODE) {
							NamedNodeMap channelAttrs = channelNode.getAttributes();
							String channelType = channelAttrs.getNamedItem("CHANNELTYPE").getNodeValue();
							if (!channels.containsKey(channelType)) {
								channels.put(channelType, new ArrayList<String>());
							}
							String channelName = "";
							switch (channelType) {
							case "DO":
								channelName = "DigitalOutput_" + moduleCounter.get(channelType) + "_" + index;
								break;
							case "DI":
								channelName = "DigitalInput_" + moduleCounter.get(channelType) + "_" + index;
								break;
							}
							channels.get(channelType).add(channelName);
							index++;
						}
						channelNode = channelNode.getNextSibling();
					}
				}
				moduleNode = moduleNode.getNextSibling();
			}
		}
		for (String key : channels.keySet()) {
			switch (key) {
			case "DO": {
				List<BitsElement> elements = new ArrayList<>();
				int count = 0;
				ElementBuilder currentElementBuilder = null;
				String name = "";
				for (String channel : channels.get(key)) {
					if (count % 16 == 0) {
						if (currentElementBuilder != null) {
							elements.add((BitsElement) (currentElementBuilder.build()));
						}
						name = "outputs" + (count / 16 + 1);
						currentElementBuilder = new ElementBuilder(512 + count / 16).device(name).name(name);
						mainElements.add(name);
						writeElements.add(name);
					}
					bitElementMapping.put(channel, name);
					currentElementBuilder.bit(new BitElement(count % 16, channel));
					count++;
				}
				elements.add((BitsElement) (currentElementBuilder.build()));
				protocol.addElementRange(new ElementRange(512, elements.toArray(new BitsElement[elements.size()])));
			}
				break;
			case "DI": {
				List<BitsElement> elements = new ArrayList<>();
				int count = 0;
				ElementBuilder currentElementBuilder = null;
				String name = "";
				for (String channel : channels.get(key)) {
					if (count % 16 == 0) {
						if (currentElementBuilder != null) {
							elements.add((BitsElement) (currentElementBuilder.build()));
						}
						name = "inputs" + (count / 16 + 1);
						currentElementBuilder = new ElementBuilder(512 + count / 16).device(name).name(name);
						mainElements.add(name);
					}
					bitElementMapping.put(channel, name);
					currentElementBuilder.bit(new BitElement(count % 16, channel));
					count++;
				}
				elements.add((BitsElement) (currentElementBuilder.build()));
				protocol.addElementRange(new ElementRange(0, elements.toArray(new BitsElement[elements.size()])));
			}
				break;
			}
		}
		// protocol.addElementRange(new ElementRange(512, new
		// ElementBuilder(512).device(name).name("output") //
		// .bit(new BitElement(0, "DOUT_1_1")) //
		// .bit(new BitElement(1, "DOUT_1_2")).build()));
		return protocol;
	}

	@Override
	public Set<String> getInitElements() {
		return new HashSet<String>();
	}

	@Override
	public Set<String> getMainElements() {
		return new HashSet<String>(mainElements);
	}

	public BitElement getBitElement(String name) {
		return ((BitsElement) getElement(bitElementMapping.get(name))).getBit(name);
	}

}

package org.event.listener;


	import java.io.OutputStream;
import java.io.PrintStream;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

	public class XmppCommandLineParser {
		private static final String ALL_TOPICS = "final";
		private static final String DEFAULT_BROKER_LIST = "localhost:9092";
		private static final String DEFAULT_SERVER_URI_XMPP = "localhost:5222";
		
	
		
		@Option(name="--uri", usage="xmpp Server URI")
		private String serverURIXmpp = DEFAULT_SERVER_URI_XMPP;

		@Option(name="--brokerlist", aliases="-b", usage="Broker list (comma-separated)")
		private String brokerList = DEFAULT_BROKER_LIST;
		
		@Option(name="--resource", usage="xmpp topic filters (comma-separated)")
		private String TopicFilters = ALL_TOPICS;
		
		@Option(name="--help", aliases="-h", usage="Show help")
		private boolean showHelp = false;
		
		private CmdLineParser parser = new CmdLineParser(this);
		
		

		public String getServerURIXmpp() {
			return serverURIXmpp;
		}
		
		public String getBrokerList() {
			return brokerList;
		}

		public String[] getMqttTopicFilters() {
			return TopicFilters.split(",");
		}

		public void parse(String[] args) throws CmdLineException {
			parser.parseArgument(args);
			if (showHelp) {
				printUsage(System.out);
				System.exit(0);
			}
		}

		public void printUsage(OutputStream out) {
			PrintStream stream = new PrintStream(out);
			//stream.println("java " + KXmppProducer.class.getName() + " [options...]");
			parser.printUsage(out);
		}
	}


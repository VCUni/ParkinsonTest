package it.VCUni.parkinsonTestServer.handler;

import static io.moquette.BrokerConstants.PERSISTENT_STORE_PROPERTY_NAME;
import java.io.IOException;
import java.util.Properties;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import io.moquette.BrokerConstants;
import io.moquette.broker.Server;
import io.moquette.broker.config.IConfig;
import io.moquette.broker.config.MemoryConfig;
import it.VCUni.parkinsonTestServer.service.WhiteBoardPublisher;
import it.VCUni.parkinsonTestServer.settings.IDbConnection;

/**
 * Handler used to inizialize and send messages with broker
*/
@Component
public class WhiteBoardHandler {
	
	@Autowired
	Logger log;
	
	WhiteBoardPublisher pub;
	
	public void sendInfo(int testid) {
		try {
			pub.publish(Integer.toString(testid));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@Autowired
	IDbConnection conn;
	
    public void initBroker() throws IOException {
        Properties props = new Properties();
        props.setProperty(BrokerConstants.PORT_PROPERTY_NAME, "1883");
        props.setProperty(BrokerConstants.HOST_PROPERTY_NAME, "192.168.1.81");
        props.setProperty(PERSISTENT_STORE_PROPERTY_NAME, conn.getFilePath()+"moquette_store.mapdb");
        final IConfig configs = new MemoryConfig(props);
        final Server mqttBroker = new Server();
        mqttBroker.startServer(configs, null);
        log.info("moquette mqtt broker started");
       
        pub = new WhiteBoardPublisher();
       }	


}

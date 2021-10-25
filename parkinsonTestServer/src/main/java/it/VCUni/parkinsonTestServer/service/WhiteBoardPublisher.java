package it.VCUni.parkinsonTestServer.service;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

/**
 *  Service that allows to publish message on the broker
*/
public class WhiteBoardPublisher {
	
    private static final String broker = "tcp://192.168.1.81:1883";
    private static final String clientIdPrefix = "publisher";
    private MqttClient sampleClient;

    public WhiteBoardPublisher() {
    	sampleClient = null;
    	try {
    		final String clientId = clientIdPrefix;
            sampleClient = new MqttClient(broker, clientId, new MemoryPersistence());
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(false); // for publisher - this is not needed I think
            sampleClient.connect(connOpts);
            System.out.println("Connected to "+broker);
    	 } catch (MqttException me) {
             me.printStackTrace();
         }
    }
    
    /**
     * @param str
     * @throws Exception
     */
    public void publish(String str) throws Exception{
        sendDataWithQOSOne(str);
    }

    /**
     * @param content
     * @throws MqttException
     */
    private void sendDataWithQOSOne(String content) throws MqttException{
        MqttMessage message = new MqttMessage(content.getBytes());
        message.setQos(1);
        message.setRetained(true);
        final String topic = "topic/tests";
        try {
			sampleClient.publish(topic, message);
		} catch (MqttException e) {
			e.printStackTrace();
			sampleClient.disconnect();
		}
        System.out.println("Message published from : " + clientIdPrefix + " with payload of : " + content);
    }

}

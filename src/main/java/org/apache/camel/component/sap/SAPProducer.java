package org.apache.camel.component.sap;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;
import org.jboss.jca.adapters.sap.cci.Interaction;
import org.jboss.jca.adapters.sap.cci.InteractionSpec;
import org.jboss.jca.adapters.sap.cci.MappedRecord;

/**
 * The SAP producer.
 */
public class SAPProducer extends DefaultProducer {
    private SAPEndpoint endpoint;

    public SAPProducer(SAPEndpoint endpoint) {
        super(endpoint);
        this.endpoint = endpoint;
    }

    public void process(Exchange exchange) throws Exception {
    	Interaction interaction = endpoint.createInteraction();
    	InteractionSpec interactionSpec = endpoint.createInteractionSpec();
		InputStream inputStream = exchange.getIn().getBody(InputStream.class);
    	MappedRecord input = endpoint.loadMappedRecord(inputStream);
    	MappedRecord output = endpoint.createOutputRecord();
    	if (interaction.execute(interactionSpec, input, output)) {
    		exchange.setOut(exchange.getIn().copy());
    		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    		endpoint.storeMappedRecord(output, outputStream);
    		exchange.getOut().setBody(outputStream);
    	}	else {
    		exchange.getOut().setFault(true);
    		exchange.getOut().setBody(interaction.getWarnings());
    	}
    }

}

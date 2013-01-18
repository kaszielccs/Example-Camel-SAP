package org.apache.camel.component.sap;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class SAPComponentTest extends CamelTestSupport {

    @Test
    public void testSAP() throws Exception {
    	Thread.sleep(5000);
//        MockEndpoint mock = getMockEndpoint("mock:result");
//        mock.expectedMinimumMessageCount(1);       
//        
//        assertMockEndpointsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() {
                from("file:src/data?noop=true")
                  .to("sap:BAPI_FLCUST_GETLIST")
                  .to("file:target/messages");
            }
        };
    }
    
    @Override
    protected CamelContext createCamelContext() throws Exception {
        CamelContext context = new DefaultCamelContext();
        SAPComponent sapComponent = new SAPComponent();
        sapComponent.setAshost("nplhost");
        sapComponent.setSysnr("42");
        sapComponent.setClient("001");
        sapComponent.setUser("developer");
        sapComponent.setPasswd("ch4ngeme");
        sapComponent.setLang("en");
        context.addComponent("sap", sapComponent);
    	return context;
    }
    
    @Override
    protected int getShutdownTimeout() {
    	return 0;
    }
}

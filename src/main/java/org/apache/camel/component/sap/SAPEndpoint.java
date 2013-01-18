package org.apache.camel.component.sap;

import static org.apache.camel.component.sap.SAPConfig.ALIAS_USER;
import static org.apache.camel.component.sap.SAPConfig.ASHOST;
import static org.apache.camel.component.sap.SAPConfig.AUTH_TYPE;
import static org.apache.camel.component.sap.SAPConfig.CLIENT;
import static org.apache.camel.component.sap.SAPConfig.CODEPAGE;
import static org.apache.camel.component.sap.SAPConfig.CPIC_TRACE;
import static org.apache.camel.component.sap.SAPConfig.DENY_INITIAL_PASSWORD;
import static org.apache.camel.component.sap.SAPConfig.EXPIRATION_PERIOD;
import static org.apache.camel.component.sap.SAPConfig.EXPIRATION_TIME;
import static org.apache.camel.component.sap.SAPConfig.GETSSO2;
import static org.apache.camel.component.sap.SAPConfig.GROUP;
import static org.apache.camel.component.sap.SAPConfig.GWHOST;
import static org.apache.camel.component.sap.SAPConfig.GWSERV;
import static org.apache.camel.component.sap.SAPConfig.LANG;
import static org.apache.camel.component.sap.SAPConfig.LCHECK;
import static org.apache.camel.component.sap.SAPConfig.MAX_GET_TIME;
import static org.apache.camel.component.sap.SAPConfig.MSHOST;
import static org.apache.camel.component.sap.SAPConfig.MSSERV;
import static org.apache.camel.component.sap.SAPConfig.MYSAPSSO2;
import static org.apache.camel.component.sap.SAPConfig.PASSWD;
import static org.apache.camel.component.sap.SAPConfig.PCS;
import static org.apache.camel.component.sap.SAPConfig.PEAK_LIMIT;
import static org.apache.camel.component.sap.SAPConfig.PING_ON_CREATE;
import static org.apache.camel.component.sap.SAPConfig.POOL_CAPACITY;
import static org.apache.camel.component.sap.SAPConfig.R3NAME;
import static org.apache.camel.component.sap.SAPConfig.REPOSITORY_DEST;
import static org.apache.camel.component.sap.SAPConfig.REPOSITORY_PASSWD;
import static org.apache.camel.component.sap.SAPConfig.REPOSITORY_ROUNDTRIP_OPTIMIZATION;
import static org.apache.camel.component.sap.SAPConfig.REPOSITORY_SNC;
import static org.apache.camel.component.sap.SAPConfig.REPOSITORY_USER;
import static org.apache.camel.component.sap.SAPConfig.SAPROUTER;
import static org.apache.camel.component.sap.SAPConfig.SNC_LIBRARY;
import static org.apache.camel.component.sap.SAPConfig.SNC_MODE;
import static org.apache.camel.component.sap.SAPConfig.SNC_MYNAME;
import static org.apache.camel.component.sap.SAPConfig.SNC_PARTNERNAME;
import static org.apache.camel.component.sap.SAPConfig.SNC_QOP;
import static org.apache.camel.component.sap.SAPConfig.SYSNR;
import static org.apache.camel.component.sap.SAPConfig.TPHOST;
import static org.apache.camel.component.sap.SAPConfig.TPNAME;
import static org.apache.camel.component.sap.SAPConfig.TRACE;
import static org.apache.camel.component.sap.SAPConfig.TYPE;
import static org.apache.camel.component.sap.SAPConfig.USER;
import static org.apache.camel.component.sap.SAPConfig.USER_ID;
import static org.apache.camel.component.sap.SAPConfig.USE_SAPGUI;
import static org.apache.camel.component.sap.SAPConfig.X509CERT;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceImpl;
import org.jboss.jca.adapters.sap.cci.CciFactory;
import org.jboss.jca.adapters.sap.cci.Connection;
import org.jboss.jca.adapters.sap.cci.ConnectionFactory;
import org.jboss.jca.adapters.sap.cci.Interaction;
import org.jboss.jca.adapters.sap.cci.InteractionSpec;
import org.jboss.jca.adapters.sap.cci.MappedRecord;
import org.jboss.jca.adapters.sap.spi.ManagedConnectionFactory;

/**
 * Represents a HelloWorld endpoint.
 */
public class SAPEndpoint extends DefaultEndpoint {
	
	private SAPComponent component;
	
	private Map<String,String> config = new HashMap<String,String>();
	
	private ManagedConnectionFactory managedConnectionFactory;
	
	private String rfmName;
	private Connection connection;
	private ConnectionFactory connectionFactory;
	
    public SAPEndpoint() {
    }

    public SAPEndpoint(String uri, String rfmName, SAPComponent component) {
        super(uri, component);
        this.component = component;
        this.rfmName = rfmName;
    }

    public Producer createProducer() throws Exception {
        return new SAPProducer(this);
    }

    public Consumer createConsumer(Processor processor) throws Exception {
        return new SAPConsumer(this, processor);
    }

    public boolean isSingleton() {
        return true;
    }
    
    public Interaction createInteraction() throws Exception {
    	checkState();
    	return (Interaction) connection.createInteraction();
    }
    
    public InteractionSpec createInteractionSpec() throws Exception {
    	checkState();
    	InteractionSpec interactionSpec = CciFactory.INSTANCE.createInteractionSpec();
    	interactionSpec.setFunctionName(rfmName);
    	return interactionSpec;
    }
    
    public MappedRecord loadMappedRecord(InputStream inputStream) throws Exception {
    	checkState();
    	XMLResource resource = new XMLResourceImpl();
    	resource.load(inputStream, null);
    	return (MappedRecord) resource.getContents().get(0);
    }
    
    public void storeMappedRecord(MappedRecord record, OutputStream outputStream) throws Exception {
    	checkState();
    	XMLResource resource = new XMLResourceImpl();
    	resource.getContents().add(record);
    	resource.save(outputStream, null);
    }
    
    public MappedRecord createInputRecord() throws Exception {
    	checkState();
    	return (MappedRecord) connectionFactory.getRecordFactory().createMappedRecord(rfmName + ".INPUT_RECORD");
    }
    
    public MappedRecord createOutputRecord() throws Exception {
    	checkState();
    	return (MappedRecord) connectionFactory.getRecordFactory().createMappedRecord(rfmName + ".OUTPUT_RECORD");
    }
    
	/**
	 * Authentication type used by the destination. Known types are
	 * 
	 * CONFIGURED_USER - the destination configured for the specified user only. All connections opened via this
	 * destination belongs to the same user. This value is used in default case, if this property is missing.
	 * 
	 * CURRENT_USER - the connection created using this destination belongs to the current user. Before the connection
	 * is opened the runtime check the property "jco.client.current_user" in order to get the current user name.
	 * Note:This type is supported in SAP NetWeaver AS only
	 * 
	 * Note:This property is optional, default value is CONFIGURED_USER
	 * 
	 * @return Authentication type used by the destination. Known types are
	 */
	public String getAuthType() {
		return config.get(AUTH_TYPE);
	}

	/**
	 * Sets authentication type used by the destination.
	 * 
	 * @param authType
	 *            - authentication type used by the destination.
	 */
	public void setAuthType(String authType) {
		config.put(AUTH_TYPE, authType);
	}

	/**
	 * User identity which is used for logon to the ABAP AS.
	 * 
	 * Used by the JCo runtime, if the destination configuration uses SSO/assertion ticket, certificate, "current" user
	 * or SNC environment for authentication. The user id is mandatory, if neither user not user alias is set. This id
	 * will never be sent to SAP backend, it will be used by the JCo runtime locally.
	 * 
	 * @return User identity which is used for logon to the ABAP AS.
	 */
	public String getUserId() {
		return config.get(USER_ID);
	}

	/**
	 * Set user identity which is used for logon to the ABAP AS.
	 * 
	 * @param userId
	 *            - User identity which is used for logon to the ABAP AS.
	 */
	public void setUserId(String userId) {
		config.put(USER_ID, userId);
	}

	/*
	 * User Logon Properties
	 */

	/**
	 * SAP client, mandatory logon parameter.
	 * 
	 * @return SAP client.
	 */
	public String getClient() {
		return config.get(CLIENT);
	}

	/**
	 * Set SAP client.
	 * 
	 * @param client
	 *            - SAP client.
	 */
	public void setClient(String client) {
		config.put(CLIENT, client);
	}

	/**
	 * Logon user, logon parameter for password based authentication.
	 * 
	 * @return Logon user, logon parameter for password based authentication.
	 */
	public String getUser() {
		return config.get(USER);
	}

	/**
	 * Set logon user, logon parameter for password based authentication.
	 * 
	 * @param user
	 */
	public void setUser(String user) {
		config.put(USER, user);
	}

	/**
	 * Logon user alias, can be used instead of logon user.
	 * 
	 * @return Logon user alias.
	 */
	public String getAliasUser() {
		return config.get(ALIAS_USER);
	}

	/**
	 * Set logon user alias, can be used instead of logon user.
	 * 
	 * @param user
	 *            - logon user alias.
	 */
	public void setAliasUser(String user) {
		config.put(ALIAS_USER, user);
	}

	/**
	 * Logon password, logon parameter for password based authentication.
	 * 
	 * @return Logon password, logon parameter for password based authentication.
	 */
	public String getPasswd() {
		return config.get(PASSWD);
	}

	/**
	 * Set logon password, logon parameter for password based authentication.
	 * 
	 * @param passwd
	 *            - Logon password, logon parameter for password based authentication.
	 */
	public void setPasswd(String passwd) {
		config.put(PASSWD, passwd);
	}

	/**
	 * Logon language, if not defined the default user language is used.
	 * 
	 * @return Logon language, if not defined the default user language is used.
	 */
	public String getLang() {
		return config.get(LANG);
	}

	/**
	 * Sets logon language.
	 * 
	 * @param lang
	 *            - Logon language.
	 */
	public void setLang(String lang) {
		config.put(LANG, lang);
	}

	/**
	 * The SAP Cookie Version 2 used as logon ticket for SSO based authentication.
	 * 
	 * @return The SAP Cookie Version 2 used as logon ticket for SSO based authentication.
	 */
	public String getMysapsso2() {
		return config.get(MYSAPSSO2);
	}

	/**
	 * Set the SAP Cookie Version 2 used as logon ticket for SSO based authentication.
	 * 
	 * @param mysapsso2
	 *            - The SAP Cookie Version 2 used as logon ticket for SSO based authentication.
	 */
	public void setMysapsso2(String mysapsso2) {
		config.put(MYSAPSSO2, mysapsso2);
	}

	/**
	 * The specified X509 certificate used for certificate based authentication
	 * 
	 * @return The specified X509 certificate used for certificate based authentication
	 */
	public String getX509cert() {
		return config.get(X509CERT);
	}

	/**
	 * Set the specified X509 certificate used for certificate based authentication
	 * 
	 * @param x509cert
	 *            - The specified X509 certificate used for certificate based authentication
	 */
	public void setX509cert(String x509cert) {
		config.put(X509CERT, x509cert);
	}

	/**
	 * Additional logon parameter to define the codepage type of the SAP System,
	 * 
	 * 1 - non unicode, 2 - unicode enabled.
	 * 
	 * Used in special cases only
	 * 
	 * @return Additional logon parameter to define the codepage type of the SAP System.
	 */
	public String getPcs() {
		return config.get(PCS);
	}

	/**
	 * Set additional logon parameter to define the codepage type of the SAP System
	 * 
	 * @param pcs
	 *            - Additional logon parameter to define the codepage type of the SAP System
	 */
	public void setPcs(String pcs) {
		config.put(PCS, pcs);
	}

	/**
	 * Type of remote host.
	 * 
	 * The type will be recognized automatically and should not be set manually.
	 * 
	 * @return Type of remote host.
	 */
	public String getType() {
		return config.get(TYPE);
	}

	/**
	 * Set type of remote host.
	 * 
	 * @param type
	 *            - Type of remote host.
	 */
	public void setType(String type) {
		config.put(TYPE, type);
	}

	/*
	 * Connection Configuration
	 */

	/**
	 * SAP Router string for connection to systems behind a SAP Router.
	 * 
	 * SAP Router string contains the chain of SAP Routers and its port numbers and has the form:
	 * 
	 * (/H/<host>[/S/<port>])+
	 * 
	 * @return SAP Router string for connection to systems behind a SAP Router.
	 */
	public String getSaprouter() {
		return config.get(SAPROUTER);
	}

	/**
	 * Set SAP Router string for connection to systems behind a SAP Router.
	 * 
	 * @param saprouter
	 *            - SAP Router string for connection to systems behind a SAP Router.
	 */
	public void setSaprouter(String saprouter) {
		config.put(SAPROUTER, saprouter);
	}

	/**
	 * System number of the SAP ABAP application server, mandatory for a direct connection.
	 * 
	 * @return - System number of the SAP ABAP application server, mandatory for a direct connection.
	 */
	public String getSysnr() {
		return config.get(SYSNR);
	}

	/**
	 * Set system number of the SAP ABAP application server.
	 * 
	 * @param sysnr
	 *            - System number of the SAP ABAP application server.
	 */
	public void setSysnr(String sysnr) {
		config.put(SYSNR, sysnr);
	}

	/**
	 * SAP ABAP application server, mandatory for a direct connection.
	 * 
	 * @return SAP ABAP application server, mandatory for a direct connection.
	 */
	public String getAshost() {
		return config.get(ASHOST);
	}

	/**
	 * Set ABAP application server.
	 * 
	 * @param ashost
	 *            - SAP ABAP application server.
	 */
	public void setAshost(String ashost) {
		config.put(ASHOST, ashost);
	}

	/**
	 * SAP message server port, optional property for a load balancing connection.
	 * 
	 * In order to resolve the service names sapmsXXX a lookup in etc/services is performed by the network layer of the
	 * operating system. If using port numbers instead of symbolic service names, no lookups are performed and no
	 * additional entries are needed.
	 * 
	 * @return SAP message server port.
	 */
	public String getMshost() {
		return config.get(MSHOST);
	}

	/**
	 * Set SAP message server port, optional property for a load balancing connection.
	 * 
	 * @param mshost
	 *            - SAP message server port.
	 */
	public void setMshost(String mshost) {
		config.put(MSHOST, mshost);
	}

	/**
	 * SAP message server port, optional property for a load balancing connection.
	 * 
	 * In order to resolve the service names sapmsXXX a lookup in etc/services is performed by the network layer of the
	 * operating system. If using port numbers instead of symbolic service names, no lookups are performed and no
	 * additional entries are needed.
	 * 
	 * @return SAP message server port.
	 */
	public String getMsserv() {
		return config.get(MSSERV);
	}

	/**
	 * Set SAP message server port
	 * 
	 * @param msserv
	 *            - SAP message server port -
	 */
	public void setMsserv(String msserv) {
		config.put(MSSERV, msserv);
	}

	/**
	 * Allows specifying a concrete gateway, which should be used for establishing the connection to an application
	 * server.
	 * 
	 * If not specified the gateway on the application server is used.
	 * 
	 * @return Gateway used for establishing the connection to an application server.
	 */
	public String getGwhost() {
		return config.get(GWHOST);
	}

	/**
	 * Set Gateway used for establishing the connection to an application server.
	 * 
	 * @param gwhost
	 *            - Gateway used for establishing the connection to an application server.
	 */
	public void setGwhost(String gwhost) {
		config.put(GWHOST, gwhost);
	}

	/**
	 * Gateway server port.
	 * 
	 * Should be set, when setting GWhost.
	 * 
	 * Allows specifying the port used on that gateway. If not specified the port of the gateway on the application
	 * server is used.
	 * 
	 * In order to resolve the service names sapgwXXX a lookup in etc/services is performed by the network layer of the
	 * operating system. If using port numbers instead of symbolic service names, no lookups are performed and no
	 * additional entries are needed.
	 * 
	 * @return Gateway server port.
	 */
	public String getGwserv() {
		return config.get(GWSERV);
	}

	/**
	 * Set Gateway server port.
	 * 
	 * @param gwserv
	 *            - Gateway server port. -
	 */
	public void setGwserv(String gwserv) {
		config.put(GWSERV, gwserv);
	}

	/**
	 * Host of external server.
	 * 
	 * Not supported in all runtime environments.
	 * 
	 * @return Host of external server.
	 */
	public String getTphost() {
		return config.get(TPHOST);
	}

	/**
	 * Set host of external server.
	 * 
	 * @param tphost
	 *            - Host of external server.
	 */
	public void setTphost(String tphost) {
		config.put(TPHOST, tphost);
	}

	/**
	 * Program ID of external server.
	 * 
	 * Not supported in all runtime environments.
	 * 
	 * @return Program ID of external server.
	 */
	public String getTpname() {
		return config.get(TPNAME);
	}

	/**
	 * Set program ID of external server.
	 * 
	 * @param tpname
	 *            - Program ID of external server.
	 */
	public void setTpname(String tpname) {
		config.put(TPNAME, tpname);
	}

	/**
	 * System ID of the SAP system, mandatory property for a load balancing connection.
	 * 
	 * @return System ID of the SAP system.
	 */
	public String getR3name() {
		return config.get(R3NAME);
	}

	/**
	 * Set System ID of the SAP system.
	 * 
	 * @param r3name
	 *            - System ID of the SAP system.
	 */
	public void setR3name(String r3name) {
		config.put(R3NAME, r3name);
	}

	/**
	 * Group of SAP application servers, mandatory property for a load balancing connection.
	 * 
	 * @return Group of SAP application servers.
	 */
	public String getGroup() {
		return config.get(GROUP);
	}

	/**
	 * Set group of SAP application servers.
	 * 
	 * @param group
	 *            - Group of SAP application servers.
	 */
	public void setGroup(String group) {
		config.put(GROUP, group);
	}

	/*
	 * Trace Configuration
	 */

	/**
	 * Enable/disable RFC trace (0 or 1).
	 * 
	 * @return trace level of RFC trace (0 or 1).
	 */
	public String getTrace() {
		return config.get(TRACE);
	}

	/**
	 * Trace level of RFC trace
	 * 
	 * @param trace
	 *            - Trace level of RFC trace: Enable/disable RFC trace (0 or 1).
	 */
	public void setTrace(String trace) {
		config.put(TRACE, trace);
	}

	/**
	 * Enable/disable CPIC trace [0..3].
	 * 
	 * @return Trace level of CPIC trace [0..3].
	 */
	public String getCpicTrace() {
		return config.get(CPIC_TRACE);
	}

	/**
	 * Set trace level of CPIC trace [0..3].
	 * 
	 * @param cpicTrace
	 *            - Trace level of CPIC trace: [0..3].
	 */
	public void setCpicTrace(String cpicTrace) {
		config.put(CPIC_TRACE, cpicTrace);
	}

	/*
	 * Special Parameters
	 */

	/**
	 * Enable/Disable logon check at open time, 1 (enable) or 0 (disable).
	 * 
	 * Postpones the authentication until the first call - 1 (enable).
	 * 
	 * Used in special cases only.
	 * 
	 * @return 1 (enabled) or 0 (disabled).
	 */
	public String getLcheck() {
		return config.get(LCHECK);
	}

	/**
	 * Set whether to Enable/Disable logon check at open time, 1 (enable) or 0 (disable).
	 * 
	 * @param lcheck
	 *            - 1 (enabled) or 0 (disabled).
	 */
	public void setLcheck(String lcheck) {
		config.put(LCHECK, lcheck);
	}

	/**
	 * Start an SAP GUI and associate with the connection. (0 - do not start [default], 1 start GUI, 2 start GUI and
	 * hide if not used)
	 * 
	 * @return 0 - do not start [default], 1 start GUI, 2 start GUI and hide if not used
	 */
	public String getUseSapgui() {
		return config.get(USE_SAPGUI);
	}

	/**
	 * Set whether to start an SAP GUI and associate with the connection. (0 - do not start [default], 1 start GUI, 2
	 * start GUI and hide if not used)
	 * 
	 * @param useSapgui
	 *            - do not start [default], 1 start GUI, 2 start GUI and hide if not used.
	 */
	public void setUseSapgui(String useSapgui) {
		config.put(USE_SAPGUI, useSapgui);
	}

	/**
	 * Initial codepage in SAP notation.
	 * 
	 * Additional logon parameter to define the codepage that will used to convert the logon parameters.
	 * 
	 * Used in special cases only.
	 * 
	 * @return Initial codepage in SAP notation.
	 */
	public String getCodepage() {
		return config.get(CODEPAGE);
	}

	/**
	 * Set initial codepage in SAP notation.
	 * 
	 * @param codepage
	 *            - Initial codepage in SAP notation.
	 */
	public void setCodepage(String codepage) {
		config.put(CODEPAGE, codepage);
	}

	/**
	 * Get/Don't get a SSO ticket after logon (1 or 0)
	 * 
	 * Order a SSO ticket after logon, the obtained ticket is available in the destination attributes.
	 * 
	 * @return Get/Don't get a SSO ticket after logon (1 or 0)
	 */
	public String getGetsso2() {
		return config.get(GETSSO2);
	}

	/**
	 * Set whether to Get/Don't get a SSO ticket after logon (1 or 0).
	 * 
	 * @param getsso2
	 *            - Get/Don't get a SSO ticket after logon (1 or 0).
	 */
	public void setGetsso2(String getsso2) {
		config.put(GETSSO2, getsso2);
	}

	/**
	 * Deny usage of initial passwords (0[default] or 1).
	 * 
	 * If set to 1, using initial passwords will lead to an exception (default=0).
	 * 
	 * @return Deny usage of initial passwords (0[default] or 1)
	 */
	public String getDenyInitialPassword() {
		return config.get(DENY_INITIAL_PASSWORD);
	}

	/**
	 * Set whether to deny usage of initial passwords (0[default] or 1).
	 * 
	 * @param denyInitialPassword
	 *            - whether to deny usage of initial passwords (0[default] or 1).
	 */
	public void setDenyInitialPassword(String denyInitialPassword) {
		config.put(DENY_INITIAL_PASSWORD, denyInitialPassword);
	}

	/*
	 * Destination Pool Configuration
	 */

	/**
	 * Maximum number of active connections that can be created for a destination simultaneously.
	 * 
	 * A value of 0 allows an unlimited number of active connections, otherwise if the value is less than the value of
	 * jco.destination.pool_capacity, it will be automatically increased to this value.
	 * 
	 * Default setting is the value of jco.destination.pool_capacity, or in case of jco.destination.pool_capacity not
	 * being specified as well, the default is 0 (unlimited).
	 * 
	 * @return Maximum number of active connections that can be created for a destination simultaneously
	 */
	public String getPeakLimit() {
		return config.get(PEAK_LIMIT);
	}

	/**
	 * Set maximum number of active connections that can be created for a destination simultaneously
	 * 
	 * A value of 0 allows an unlimited number of active connections, otherwise if the value is less than the value of
	 * jco.destination.pool_capacity, it will be automatically increased to this value.
	 * 
	 * @param peakLimit
	 *            - Maximum number of active connections that can be created for a destination simultaneously
	 */
	public void setPeakLimit(String peakLimit) {
		config.put(PEAK_LIMIT, peakLimit);
	}

	/**
	 * Maximum number of idle connections kept open by the destination. A value of 0 has the effect that there is no
	 * connection pooling, i.e. connections will be closed after each request.
	 * 
	 * A value of 0 has the effect that there is no connection pooling (default=1)
	 * 
	 * @return Maximum number of idle connections kept open by the destination.
	 */
	public String getPoolCapacity() {
		return config.get(POOL_CAPACITY);
	}

	/**
	 * Set maximum number of idle connections kept open by the destination.
	 * 
	 * A value of 0 has the effect that there is no connection pooling (default=1)
	 * 
	 * @param poolCapacity
	 *            - Maximum number of idle connections kept open by the destination.
	 */
	public void setPoolCapacity(String poolCapacity) {
		config.put(POOL_CAPACITY, poolCapacity);
	}

	/**
	 * Time in ms after that a free connections hold internally by the destination can be closed.
	 * 
	 * @return Time in ms after that a free connections hold internally by the destination can be closed
	 */
	public String getExpirationTime() {
		return config.get(EXPIRATION_TIME);
	}

	/**
	 * Set the time in ms after that a free connections hold internally by the destination can be closed
	 * 
	 * @param expirationTime
	 *            - Time in ms after that a free connections hold internally by the destination can be closed
	 */
	public void setExpirationTime(String expirationTime) {
		config.put(EXPIRATION_TIME, expirationTime);
	}

	/**
	 * Interval in ms with which the timeout checker thread checks the connections in the pool for expiration.
	 * 
	 * @return Interval in ms with which the timeout checker thread checks the connections in the pool for expiration.
	 */
	public String getExpirationPeriod() {
		return config.get(EXPIRATION_PERIOD);
	}

	/**
	 * Set interval in ms with which the timeout checker thread checks the connections in the pool for expiration.
	 * 
	 * @param expirationPeriod
	 *            - Interval in ms with which the timeout checker thread checks the connections in the pool for
	 *            expiration.
	 */
	public void setExpirationPeriod(String expirationPeriod) {
		config.put(EXPIRATION_PERIOD, expirationPeriod);
	}

	/**
	 * Max time in ms to wait for a connection, if the max allowed number of connections is allocated by the application
	 * SNC configuration
	 * 
	 * @return Max time in ms to wait for a connection.
	 */
	public String getMaxGetTime() {
		return config.get(MAX_GET_TIME);
	}

	/**
	 * Set the max time in ms to wait for a connection
	 * 
	 * @param maxGetTime
	 *            - Max time in ms to wait for a connection
	 */
	public void setMaxGetTime(String maxGetTime) {
		config.put(MAX_GET_TIME, maxGetTime);
	}

	/*
	 * SNC Configuration
	 */

	/**
	 * Secure network connection (SNC) mode, 0 (off) or 1 (on).
	 * 
	 * @return Secure network connection (SNC) mode, 0 (off) or 1 (on).
	 */
	public String getSncMode() {
		return config.get(SNC_MODE);
	}

	/**
	 * Set the secure network connection (SNC) mode, 0 (off) or 1 (on).
	 * 
	 * @param sncMode
	 *            - Secure network connection (SNC) mode, 0 (off) or 1 (on).
	 */
	public void setSncMode(String sncMode) {
		config.put(SNC_MODE, sncMode);
	}

	/**
	 * SNC partner, e.g. p:CN=R3, O=XYZ-INC, C=EN
	 * 
	 * @return SNC partner.
	 */
	public String getSncPartnername() {
		return config.get(SNC_PARTNERNAME);
	}

	/**
	 * Set SNC partner.
	 * 
	 * e.g. p:CN=R3, O=XYZ-INC, C=EN
	 * 
	 * @param sncPartnername
	 *            - SNC partner.
	 */
	public void setSncPartnername(String sncPartnername) {
		config.put(SNC_PARTNERNAME, sncPartnername);
	}

	/**
	 * SNC level of security, 1 to 9.
	 * 
	 * @return SNC level of security, 1 to 9.
	 */
	public String getSncQop() {
		return config.get(SNC_QOP);
	}

	/**
	 * Set SNC level of security, 1 to 9.
	 * 
	 * @param sncQop
	 *            - SNC level of security, 1 to 9.
	 */
	public void setSncQop(String sncQop) {
		config.put(SNC_QOP, sncQop);
	}

	/**
	 * SNC name.
	 * 
	 * Overrides default SNC partner.
	 * 
	 * @return SNC name.
	 */
	public String getSncMyname() {
		return config.get(SNC_MYNAME);
	}

	/**
	 * Set SNC name.
	 * 
	 * @param sncMyname
	 *            - SNC name.
	 */
	public void setSncMyname(String sncMyname) {
		config.put(SNC_MYNAME, sncMyname);
	}

	/**
	 * Path to library which provides SNC service.
	 * 
	 * @return Path to library which provides SNC service
	 */
	public String getSncLibrary() {
		return config.get(SNC_LIBRARY);
	}

	/**
	 * Set path to library which provides SNC service
	 * 
	 * @param sncLibrary
	 *            - Path to library which provides SNC service
	 */
	public void setSncLibrary(String sncLibrary) {
		config.put(SNC_LIBRARY, sncLibrary);
	}

	/*
	 * Repository Configuration
	 */

	/**
	 * Specifies which destination should be used as repository, i.e. use this destination's repository.
	 * 
	 * @return Destination that should be used as repository
	 */
	public String getRepositoryDest() {
		return config.get(REPOSITORY_DEST);
	}

	/**
	 * Set destination that should be used as repository.
	 * 
	 * @param repositoryDest
	 *            - Destination that should be used as repository
	 */
	public void setRepositoryDest(String repositoryDest) {
		config.put(REPOSITORY_DEST, repositoryDest);
	}

	/**
	 * User to use for repository calls.
	 * 
	 * Optional: If repository destination is not set, and this property is set, it will be used as user for repository
	 * queries. This allows using a different user for repository lookups and restrict the permissions accordingly.
	 * 
	 * @return User used for repository calls.
	 */
	public String getRepositoryUser() {
		return config.get(REPOSITORY_USER);
	}

	/**
	 * Set user to use for repository calls.
	 * 
	 * Optional: If repository destination is not set, and this property is set, it will be used as user for repository
	 * queries. This allows using a different user for repository lookups and restrict the permissions accordingly.
	 * 
	 * @param repositoryUser
	 *            - User used for repository calls.
	 */
	public void setRepositoryUser(String repositoryUser) {
		config.put(REPOSITORY_USER, repositoryUser);
	}

	/**
	 * The password for a repository user. Mandatory, if a repository user should be used.
	 * 
	 * @return The password for a repository user.
	 */
	public String getRepositoryPasswd() {
		return config.get(REPOSITORY_PASSWD);
	}

	/**
	 * Set the password for a repository user.
	 * 
	 * @param repositoryPasswd
	 *            - The password for a repository user.
	 */
	public void setRepositoryPasswd(String repositoryPasswd) {
		config.put(REPOSITORY_PASSWD, repositoryPasswd);
	}

	/**
	 * If SNC is used for this destination.
	 * 
	 * Optional: It is possible to turn it off for repository connections, if this property is set to 0.
	 * 
	 * Default setting is the value of jco.client.snc_mode. For special cases only.
	 * 
	 * @return If SNC is used for this destination.
	 */
	public String getRepositorySnc() {
		return config.get(REPOSITORY_SNC);
	}

	/**
	 * Set if SNC is used for this destination.
	 * 
	 * Optional: It is possible to turn it off for repository connections, if this property is set to 0.
p	 * 
	 * @param repositorySnc
	 *            - if SNC is used for this destination.
	 */
	public void setRepositorySnc(String repositorySnc) {
		config.put(REPOSITORY_SNC, repositorySnc);
	}

	/**
	 * Is the usage of RFC_METADATA_GET API enabled, which is providing repository data in one single roundtrip.
	 * 
	 * 1 indicates the usage of RFC_METADATA_GET in ABAP System is forced, 0 indicated it is deactivated.
	 * 
	 * If the property is not set, the destination will initially do a remote call to check whether RFC_METADATA_GET is
	 * available. In case it is available, it will use it.
	 * 
	 * Note: If the repository is already initialized, for example because it is used by some other destination, this
	 * property does not have any effect. Generally, this property is related to the ABAP System, and should have the
	 * same value on all destinations pointing to the same ABAP System.
	 * 
	 * @return Is the usage of RFC_METADATA_GET API forced (1) or deactivated (0).
	 */
	public String getRepositoryRoundtripOptimization() {
		return config.get(REPOSITORY_ROUNDTRIP_OPTIMIZATION);
	}

	/**
	 * Enable the usage of RFC_METADATA_GET API, which is providing repository data in one single roundtrip.
	 * 
	 * 1 forces the usage of RFC_METADATA_GET in ABAP System, 0 deactivates it.
	 * 
	 * If the property is not set, the destination will initially do a remote call to check whether RFC_METADATA_GET is
	 * available. In case it is available, it will use it.
	 * 
	 * Note: If the repository is already initializated, for example because it is used by some other destination, this
	 * property does not have any effect. Generally, this property is related to the ABAP System, and should have the
	 * same value on all destinations pointing to the same ABAP System.
	 * 
	 * @param repositoryRoundtripOptimization
	 *            - Force(1)/Deactivate(0) the usage of RFC_METADATA_GET API.
	 */
	public void setRepositoryRoundtripOptimization(String repositoryRoundtripOptimization) {
		config.put(REPOSITORY_ROUNDTRIP_OPTIMIZATION, repositoryRoundtripOptimization);
	}
	
	/**
	 * Indicates whether the Managed Connection will ping the connected SAP instance when created, <code>true</code>, or not, <code>false</code>.
	 * Default is <code>false</code>.
	 * 
	 * @return Whether the Managed Connection will ping the connected SAP instance when created, <code>true</code>, or not, <code>false</code>.
	 */
	public String getPingOnCreate() {
		return config.get(PING_ON_CREATE);
	}

	/**
	 * Sets whether the Managed Connection will ping the connected SAP instance when created, <code>true</code>, or not, <code>false</code>.
	 * 
	 * @param pingOnCreate - whether the Managed Connection will ping the connected SAP instance when created, <code>true</code>, or not, <code>false</code>.
	 */
	public void setPingOnCreate(String pingOnCreate) {
		config.put(PING_ON_CREATE, pingOnCreate);
	}

    @Override
    protected void doStart() throws Exception {
    	super.doStart();
    	managedConnectionFactory = component.getAdmin().createManagedConnectionFactory(config);
		connectionFactory = (ConnectionFactory) managedConnectionFactory.createConnectionFactory();
		connection = connectionFactory.getConnection();
		connection.ping();
		connectionFactory.getRecordFactory().getPackage(rfmName);
    }
    
    @Override
    protected void doStop() throws Exception {
    	super.doStop();
    	connection.close();
    	connection = null;
    	connectionFactory = null;
    	managedConnectionFactory.destroy();
    	managedConnectionFactory = null;
    }
    
    private void checkState() {
    	if (managedConnectionFactory == null)
    		throw new IllegalStateException("SAP Endpoint is not started");
    }
}

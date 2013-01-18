/*************************************************************************************
 * Copyright (c) 2013 Red Hat, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     JBoss by Red Hat - Initial implementation.
 ************************************************************************************/
package org.apache.camel.component.sap;

import static org.apache.camel.component.sap.SAPConfig.*;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;
import org.jboss.jca.adapters.sap.spi.UnmanagedEnvironmentAdmin;

/**
 * Represents the component that manages {@link SAPEndpoint}.
 */
public class SAPComponent extends DefaultComponent {
	
	private UnmanagedEnvironmentAdmin admin;

	private Map<String,Object> defaultConfig = new HashMap<String,Object>();

	protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
        Endpoint endpoint = new SAPEndpoint(uri, remaining, this);
        setProperties(endpoint, defaultConfig);
        setProperties(endpoint, parameters);
        return endpoint;
    }
    
    public UnmanagedEnvironmentAdmin getAdmin() {
		return admin;
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
		return (String) defaultConfig.get(AUTH_TYPE);
	}

	/**
	 * Sets authentication type used by the destination.
	 * 
	 * @param authType
	 *            - authentication type used by the destination.
	 */
	public void setAuthType(String authType) {
		defaultConfig.put(AUTH_TYPE, authType);
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
		return (String) defaultConfig.get(USER_ID);
	}

	/**
	 * Set user identity which is used for logon to the ABAP AS.
	 * 
	 * @param userId
	 *            - User identity which is used for logon to the ABAP AS.
	 */
	public void setUserId(String userId) {
		defaultConfig.put(USER_ID, userId);
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
		return (String) defaultConfig.get(CLIENT);
	}

	/**
	 * Set SAP client.
	 * 
	 * @param client
	 *            - SAP client.
	 */
	public void setClient(String client) {
		defaultConfig.put(CLIENT, client);
	}

	/**
	 * Logon user, logon parameter for password based authentication.
	 * 
	 * @return Logon user, logon parameter for password based authentication.
	 */
	public String getUser() {
		return (String) defaultConfig.get(USER);
	}

	/**
	 * Set logon user, logon parameter for password based authentication.
	 * 
	 * @param user
	 */
	public void setUser(String user) {
		defaultConfig.put(USER, user);
	}

	/**
	 * Logon user alias, can be used instead of logon user.
	 * 
	 * @return Logon user alias.
	 */
	public String getAliasUser() {
		return (String) defaultConfig.get(ALIAS_USER);
	}

	/**
	 * Set logon user alias, can be used instead of logon user.
	 * 
	 * @param user
	 *            - logon user alias.
	 */
	public void setAliasUser(String user) {
		defaultConfig.put(ALIAS_USER, user);
	}

	/**
	 * Logon password, logon parameter for password based authentication.
	 * 
	 * @return Logon password, logon parameter for password based authentication.
	 */
	public String getPasswd() {
		return (String) defaultConfig.get(PASSWD);
	}

	/**
	 * Set logon password, logon parameter for password based authentication.
	 * 
	 * @param passwd
	 *            - Logon password, logon parameter for password based authentication.
	 */
	public void setPasswd(String passwd) {
		defaultConfig.put(PASSWD, passwd);
	}

	/**
	 * Logon language, if not defined the default user language is used.
	 * 
	 * @return Logon language, if not defined the default user language is used.
	 */
	public String getLang() {
		return (String) defaultConfig.get(LANG);
	}

	/**
	 * Sets logon language.
	 * 
	 * @param lang
	 *            - Logon language.
	 */
	public void setLang(String lang) {
		defaultConfig.put(LANG, lang);
	}

	/**
	 * The SAP Cookie Version 2 used as logon ticket for SSO based authentication.
	 * 
	 * @return The SAP Cookie Version 2 used as logon ticket for SSO based authentication.
	 */
	public String getMysapsso2() {
		return (String) defaultConfig.get(MYSAPSSO2);
	}

	/**
	 * Set the SAP Cookie Version 2 used as logon ticket for SSO based authentication.
	 * 
	 * @param mysapsso2
	 *            - The SAP Cookie Version 2 used as logon ticket for SSO based authentication.
	 */
	public void setMysapsso2(String mysapsso2) {
		defaultConfig.put(MYSAPSSO2, mysapsso2);
	}

	/**
	 * The specified X509 certificate used for certificate based authentication
	 * 
	 * @return The specified X509 certificate used for certificate based authentication
	 */
	public String getX509cert() {
		return (String) defaultConfig.get(X509CERT);
	}

	/**
	 * Set the specified X509 certificate used for certificate based authentication
	 * 
	 * @param x509cert
	 *            - The specified X509 certificate used for certificate based authentication
	 */
	public void setX509cert(String x509cert) {
		defaultConfig.put(X509CERT, x509cert);
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
		return (String) defaultConfig.get(PCS);
	}

	/**
	 * Set additional logon parameter to define the codepage type of the SAP System
	 * 
	 * @param pcs
	 *            - Additional logon parameter to define the codepage type of the SAP System
	 */
	public void setPcs(String pcs) {
		defaultConfig.put(PCS, pcs);
	}

	/**
	 * Type of remote host.
	 * 
	 * The type will be recognized automatically and should not be set manually.
	 * 
	 * @return Type of remote host.
	 */
	public String getType() {
		return (String) defaultConfig.get(TYPE);
	}

	/**
	 * Set type of remote host.
	 * 
	 * @param type
	 *            - Type of remote host.
	 */
	public void setType(String type) {
		defaultConfig.put(TYPE, type);
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
		return (String) defaultConfig.get(SAPROUTER);
	}

	/**
	 * Set SAP Router string for connection to systems behind a SAP Router.
	 * 
	 * @param saprouter
	 *            - SAP Router string for connection to systems behind a SAP Router.
	 */
	public void setSaprouter(String saprouter) {
		defaultConfig.put(SAPROUTER, saprouter);
	}

	/**
	 * System number of the SAP ABAP application server, mandatory for a direct connection.
	 * 
	 * @return - System number of the SAP ABAP application server, mandatory for a direct connection.
	 */
	public String getSysnr() {
		return (String) defaultConfig.get(SYSNR);
	}

	/**
	 * Set system number of the SAP ABAP application server.
	 * 
	 * @param sysnr
	 *            - System number of the SAP ABAP application server.
	 */
	public void setSysnr(String sysnr) {
		defaultConfig.put(SYSNR, sysnr);
	}

	/**
	 * SAP ABAP application server, mandatory for a direct connection.
	 * 
	 * @return SAP ABAP application server, mandatory for a direct connection.
	 */
	public String getAshost() {
		return (String) defaultConfig.get(ASHOST);
	}

	/**
	 * Set ABAP application server.
	 * 
	 * @param ashost
	 *            - SAP ABAP application server.
	 */
	public void setAshost(String ashost) {
		defaultConfig.put(ASHOST, ashost);
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
		return (String) defaultConfig.get(MSHOST);
	}

	/**
	 * Set SAP message server port, optional property for a load balancing connection.
	 * 
	 * @param mshost
	 *            - SAP message server port.
	 */
	public void setMshost(String mshost) {
		defaultConfig.put(MSHOST, mshost);
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
		return (String) defaultConfig.get(MSSERV);
	}

	/**
	 * Set SAP message server port
	 * 
	 * @param msserv
	 *            - SAP message server port -
	 */
	public void setMsserv(String msserv) {
		defaultConfig.put(MSSERV, msserv);
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
		return (String) defaultConfig.get(GWHOST);
	}

	/**
	 * Set Gateway used for establishing the connection to an application server.
	 * 
	 * @param gwhost
	 *            - Gateway used for establishing the connection to an application server.
	 */
	public void setGwhost(String gwhost) {
		defaultConfig.put(GWHOST, gwhost);
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
		return (String) defaultConfig.get(GWSERV);
	}

	/**
	 * Set Gateway server port.
	 * 
	 * @param gwserv
	 *            - Gateway server port. -
	 */
	public void setGwserv(String gwserv) {
		defaultConfig.put(GWSERV, gwserv);
	}

	/**
	 * Host of external server.
	 * 
	 * Not supported in all runtime environments.
	 * 
	 * @return Host of external server.
	 */
	public String getTphost() {
		return (String) defaultConfig.get(TPHOST);
	}

	/**
	 * Set host of external server.
	 * 
	 * @param tphost
	 *            - Host of external server.
	 */
	public void setTphost(String tphost) {
		defaultConfig.put(TPHOST, tphost);
	}

	/**
	 * Program ID of external server.
	 * 
	 * Not supported in all runtime environments.
	 * 
	 * @return Program ID of external server.
	 */
	public String getTpname() {
		return (String) defaultConfig.get(TPNAME);
	}

	/**
	 * Set program ID of external server.
	 * 
	 * @param tpname
	 *            - Program ID of external server.
	 */
	public void setTpname(String tpname) {
		defaultConfig.put(TPNAME, tpname);
	}

	/**
	 * System ID of the SAP system, mandatory property for a load balancing connection.
	 * 
	 * @return System ID of the SAP system.
	 */
	public String getR3name() {
		return (String) defaultConfig.get(R3NAME);
	}

	/**
	 * Set System ID of the SAP system.
	 * 
	 * @param r3name
	 *            - System ID of the SAP system.
	 */
	public void setR3name(String r3name) {
		defaultConfig.put(R3NAME, r3name);
	}

	/**
	 * Group of SAP application servers, mandatory property for a load balancing connection.
	 * 
	 * @return Group of SAP application servers.
	 */
	public String getGroup() {
		return (String) defaultConfig.get(GROUP);
	}

	/**
	 * Set group of SAP application servers.
	 * 
	 * @param group
	 *            - Group of SAP application servers.
	 */
	public void setGroup(String group) {
		defaultConfig.put(GROUP, group);
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
		return (String) defaultConfig.get(TRACE);
	}

	/**
	 * Trace level of RFC trace
	 * 
	 * @param trace
	 *            - Trace level of RFC trace: Enable/disable RFC trace (0 or 1).
	 */
	public void setTrace(String trace) {
		defaultConfig.put(TRACE, trace);
	}

	/**
	 * Enable/disable CPIC trace [0..3].
	 * 
	 * @return Trace level of CPIC trace [0..3].
	 */
	public String getCpicTrace() {
		return (String) defaultConfig.get(CPIC_TRACE);
	}

	/**
	 * Set trace level of CPIC trace [0..3].
	 * 
	 * @param cpicTrace
	 *            - Trace level of CPIC trace: [0..3].
	 */
	public void setCpicTrace(String cpicTrace) {
		defaultConfig.put(CPIC_TRACE, cpicTrace);
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
		return (String) defaultConfig.get(LCHECK);
	}

	/**
	 * Set whether to Enable/Disable logon check at open time, 1 (enable) or 0 (disable).
	 * 
	 * @param lcheck
	 *            - 1 (enabled) or 0 (disabled).
	 */
	public void setLcheck(String lcheck) {
		defaultConfig.put(LCHECK, lcheck);
	}

	/**
	 * Start an SAP GUI and associate with the connection. (0 - do not start [default], 1 start GUI, 2 start GUI and
	 * hide if not used)
	 * 
	 * @return 0 - do not start [default], 1 start GUI, 2 start GUI and hide if not used
	 */
	public String getUseSapgui() {
		return (String) defaultConfig.get(USE_SAPGUI);
	}

	/**
	 * Set whether to start an SAP GUI and associate with the connection. (0 - do not start [default], 1 start GUI, 2
	 * start GUI and hide if not used)
	 * 
	 * @param useSapgui
	 *            - do not start [default], 1 start GUI, 2 start GUI and hide if not used.
	 */
	public void setUseSapgui(String useSapgui) {
		defaultConfig.put(USE_SAPGUI, useSapgui);
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
		return (String) defaultConfig.get(CODEPAGE);
	}

	/**
	 * Set initial codepage in SAP notation.
	 * 
	 * @param codepage
	 *            - Initial codepage in SAP notation.
	 */
	public void setCodepage(String codepage) {
		defaultConfig.put(CODEPAGE, codepage);
	}

	/**
	 * Get/Don't get a SSO ticket after logon (1 or 0)
	 * 
	 * Order a SSO ticket after logon, the obtained ticket is available in the destination attributes.
	 * 
	 * @return Get/Don't get a SSO ticket after logon (1 or 0)
	 */
	public String getGetsso2() {
		return (String) defaultConfig.get(GETSSO2);
	}

	/**
	 * Set whether to Get/Don't get a SSO ticket after logon (1 or 0).
	 * 
	 * @param getsso2
	 *            - Get/Don't get a SSO ticket after logon (1 or 0).
	 */
	public void setGetsso2(String getsso2) {
		defaultConfig.put(GETSSO2, getsso2);
	}

	/**
	 * Deny usage of initial passwords (0[default] or 1).
	 * 
	 * If set to 1, using initial passwords will lead to an exception (default=0).
	 * 
	 * @return Deny usage of initial passwords (0[default] or 1)
	 */
	public String getDenyInitialPassword() {
		return (String) defaultConfig.get(DENY_INITIAL_PASSWORD);
	}

	/**
	 * Set whether to deny usage of initial passwords (0[default] or 1).
	 * 
	 * @param denyInitialPassword
	 *            - whether to deny usage of initial passwords (0[default] or 1).
	 */
	public void setDenyInitialPassword(String denyInitialPassword) {
		defaultConfig.put(DENY_INITIAL_PASSWORD, denyInitialPassword);
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
		return (String) defaultConfig.get(PEAK_LIMIT);
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
		defaultConfig.put(PEAK_LIMIT, peakLimit);
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
		return (String) defaultConfig.get(POOL_CAPACITY);
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
		defaultConfig.put(POOL_CAPACITY, poolCapacity);
	}

	/**
	 * Time in ms after that a free connections hold internally by the destination can be closed.
	 * 
	 * @return Time in ms after that a free connections hold internally by the destination can be closed
	 */
	public String getExpirationTime() {
		return (String) defaultConfig.get(EXPIRATION_TIME);
	}

	/**
	 * Set the time in ms after that a free connections hold internally by the destination can be closed
	 * 
	 * @param expirationTime
	 *            - Time in ms after that a free connections hold internally by the destination can be closed
	 */
	public void setExpirationTime(String expirationTime) {
		defaultConfig.put(EXPIRATION_TIME, expirationTime);
	}

	/**
	 * Interval in ms with which the timeout checker thread checks the connections in the pool for expiration.
	 * 
	 * @return Interval in ms with which the timeout checker thread checks the connections in the pool for expiration.
	 */
	public String getExpirationPeriod() {
		return (String) defaultConfig.get(EXPIRATION_PERIOD);
	}

	/**
	 * Set interval in ms with which the timeout checker thread checks the connections in the pool for expiration.
	 * 
	 * @param expirationPeriod
	 *            - Interval in ms with which the timeout checker thread checks the connections in the pool for
	 *            expiration.
	 */
	public void setExpirationPeriod(String expirationPeriod) {
		defaultConfig.put(EXPIRATION_PERIOD, expirationPeriod);
	}

	/**
	 * Max time in ms to wait for a connection, if the max allowed number of connections is allocated by the application
	 * SNC configuration
	 * 
	 * @return Max time in ms to wait for a connection.
	 */
	public String getMaxGetTime() {
		return (String) defaultConfig.get(MAX_GET_TIME);
	}

	/**
	 * Set the max time in ms to wait for a connection
	 * 
	 * @param maxGetTime
	 *            - Max time in ms to wait for a connection
	 */
	public void setMaxGetTime(String maxGetTime) {
		defaultConfig.put(MAX_GET_TIME, maxGetTime);
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
		return (String) defaultConfig.get(SNC_MODE);
	}

	/**
	 * Set the secure network connection (SNC) mode, 0 (off) or 1 (on).
	 * 
	 * @param sncMode
	 *            - Secure network connection (SNC) mode, 0 (off) or 1 (on).
	 */
	public void setSncMode(String sncMode) {
		defaultConfig.put(SNC_MODE, sncMode);
	}

	/**
	 * SNC partner, e.g. p:CN=R3, O=XYZ-INC, C=EN
	 * 
	 * @return SNC partner.
	 */
	public String getSncPartnername() {
		return (String) defaultConfig.get(SNC_PARTNERNAME);
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
		defaultConfig.put(SNC_PARTNERNAME, sncPartnername);
	}

	/**
	 * SNC level of security, 1 to 9.
	 * 
	 * @return SNC level of security, 1 to 9.
	 */
	public String getSncQop() {
		return (String) defaultConfig.get(SNC_QOP);
	}

	/**
	 * Set SNC level of security, 1 to 9.
	 * 
	 * @param sncQop
	 *            - SNC level of security, 1 to 9.
	 */
	public void setSncQop(String sncQop) {
		defaultConfig.put(SNC_QOP, sncQop);
	}

	/**
	 * SNC name.
	 * 
	 * Overrides default SNC partner.
	 * 
	 * @return SNC name.
	 */
	public String getSncMyname() {
		return (String) defaultConfig.get(SNC_MYNAME);
	}

	/**
	 * Set SNC name.
	 * 
	 * @param sncMyname
	 *            - SNC name.
	 */
	public void setSncMyname(String sncMyname) {
		defaultConfig.put(SNC_MYNAME, sncMyname);
	}

	/**
	 * Path to library which provides SNC service.
	 * 
	 * @return Path to library which provides SNC service
	 */
	public String getSncLibrary() {
		return (String) defaultConfig.get(SNC_LIBRARY);
	}

	/**
	 * Set path to library which provides SNC service
	 * 
	 * @param sncLibrary
	 *            - Path to library which provides SNC service
	 */
	public void setSncLibrary(String sncLibrary) {
		defaultConfig.put(SNC_LIBRARY, sncLibrary);
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
		return (String) defaultConfig.get(REPOSITORY_DEST);
	}

	/**
	 * Set destination that should be used as repository.
	 * 
	 * @param repositoryDest
	 *            - Destination that should be used as repository
	 */
	public void setRepositoryDest(String repositoryDest) {
		defaultConfig.put(REPOSITORY_DEST, repositoryDest);
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
		return (String) defaultConfig.get(REPOSITORY_USER);
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
		defaultConfig.put(REPOSITORY_USER, repositoryUser);
	}

	/**
	 * The password for a repository user. Mandatory, if a repository user should be used.
	 * 
	 * @return The password for a repository user.
	 */
	public String getRepositoryPasswd() {
		return (String) defaultConfig.get(REPOSITORY_PASSWD);
	}

	/**
	 * Set the password for a repository user.
	 * 
	 * @param repositoryPasswd
	 *            - The password for a repository user.
	 */
	public void setRepositoryPasswd(String repositoryPasswd) {
		defaultConfig.put(REPOSITORY_PASSWD, repositoryPasswd);
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
		return (String) defaultConfig.get(REPOSITORY_SNC);
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
		defaultConfig.put(REPOSITORY_SNC, repositorySnc);
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
		return (String) defaultConfig.get(REPOSITORY_ROUNDTRIP_OPTIMIZATION);
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
		defaultConfig.put(REPOSITORY_ROUNDTRIP_OPTIMIZATION, repositoryRoundtripOptimization);
	}
	
	/**
	 * Indicates whether the Managed Connection will ping the connected SAP instance when created, <code>true</code>, or not, <code>false</code>.
	 * Default is <code>false</code>.
	 * 
	 * @return Whether the Managed Connection will ping the connected SAP instance when created, <code>true</code>, or not, <code>false</code>.
	 */
	public String getPingOnCreate() {
		return (String) defaultConfig.get(PING_ON_CREATE);
	}

	/**
	 * Sets whether the Managed Connection will ping the connected SAP instance when created, <code>true</code>, or not, <code>false</code>.
	 * 
	 * @param pingOnCreate - whether the Managed Connection will ping the connected SAP instance when created, <code>true</code>, or not, <code>false</code>.
	 */
	public void setPingOnCreate(String pingOnCreate) {
		defaultConfig.put(PING_ON_CREATE, pingOnCreate);
	}

	@Override
    protected void doStart() throws Exception {
    	super.doStart();
    	admin = UnmanagedEnvironmentAdmin.INSTANCE;
    	admin.deployResourceAdapter(null);
    }
    
    @Override
    protected void doStop() throws Exception {
    	super.doStop();
    	admin.undeployResourceAdapter();
    	admin = null;
    }
    
}

/*
 * Copyright (c) 2012 - Batoo Software ve Consultancy Ltd.
 * 
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.batoo.jpa.jdbc.extension;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.channels.SocketChannel;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import com.mysql.jdbc.Messages;
import com.mysql.jdbc.SocketFactory;
import com.mysql.jdbc.SocketMetadata;

/**
 * Socket factory for vanilla TCP/IP sockets (the standard)
 * 
 * @author hceylan
 * @since $version
 */
public class MySqlNioSocketFactory implements SocketFactory, SocketMetadata {

	private static final String TCP_NO_DELAY_PROPERTY_NAME = "tcpNoDelay";
	private static final String TCP_KEEP_ALIVE_DEFAULT_VALUE = "true";
	private static final String TCP_KEEP_ALIVE_PROPERTY_NAME = "tcpKeepAlive";
	private static final String TCP_RCV_BUF_PROPERTY_NAME = "tcpRcvBuf";
	private static final String TCP_SND_BUF_PROPERTY_NAME = "tcpSndBuf";
	private static final String TCP_TRAFFIC_CLASS_PROPERTY_NAME = "tcpTrafficClass";
	private static final String TCP_RCV_BUF_DEFAULT_VALUE = "0";
	private static final String TCP_SND_BUF_DEFAULT_VALUE = "0";
	private static final String TCP_TRAFFIC_CLASS_DEFAULT_VALUE = "0";
	private static final String TCP_NO_DELAY_DEFAULT_VALUE = "true";

	private static Method setTraficClassMethod;

	static {
		try {
			MySqlNioSocketFactory.setTraficClassMethod = Socket.class.getMethod("setTrafficClass", new Class[] { Integer.TYPE });
		}
		catch (final SecurityException e) {
			MySqlNioSocketFactory.setTraficClassMethod = null;
		}
		catch (final NoSuchMethodException e) {
			MySqlNioSocketFactory.setTraficClassMethod = null;
		}
	}

	/**
	 * The hostname to connect to
	 * 
	 */
	protected String host = null;

	/**
	 * The port number to connect to
	 * 
	 */
	protected int port = 3306;

	/**
	 * The underlying TCP/IP socket to use
	 * 
	 */
	protected Socket rawSocket = null;

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Socket afterHandshake() throws SocketException, IOException {
		return this.rawSocket;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Socket beforeHandshake() throws SocketException, IOException {
		return this.rawSocket;
	}

	/**
	 * Configures socket properties based on properties from the connection (tcpNoDelay, snd/rcv buf, traffic class, etc).
	 * 
	 * @param props
	 * @throws SocketException
	 * @throws IOException
	 */
	private void configureSocket(Properties props) throws SocketException, IOException {
		try {
			this.rawSocket.setTcpNoDelay(Boolean.valueOf(
				props.getProperty(MySqlNioSocketFactory.TCP_NO_DELAY_PROPERTY_NAME, MySqlNioSocketFactory.TCP_NO_DELAY_DEFAULT_VALUE)).booleanValue());

			final String keepAlive = props.getProperty(MySqlNioSocketFactory.TCP_KEEP_ALIVE_PROPERTY_NAME, MySqlNioSocketFactory.TCP_KEEP_ALIVE_DEFAULT_VALUE);

			if ((keepAlive != null) && (keepAlive.length() > 0)) {
				this.rawSocket.setKeepAlive(Boolean.valueOf(keepAlive).booleanValue());
			}

			final int receiveBufferSize = Integer.parseInt(props.getProperty(MySqlNioSocketFactory.TCP_RCV_BUF_PROPERTY_NAME,
				MySqlNioSocketFactory.TCP_RCV_BUF_DEFAULT_VALUE));

			if (receiveBufferSize > 0) {
				this.rawSocket.setReceiveBufferSize(receiveBufferSize);
			}

			final int sendBufferSize = Integer.parseInt(props.getProperty(MySqlNioSocketFactory.TCP_SND_BUF_PROPERTY_NAME,
				MySqlNioSocketFactory.TCP_SND_BUF_DEFAULT_VALUE));

			if (sendBufferSize > 0) {
				this.rawSocket.setSendBufferSize(sendBufferSize);
			}

			final int trafficClass = Integer.parseInt(props.getProperty(MySqlNioSocketFactory.TCP_TRAFFIC_CLASS_PROPERTY_NAME,
				MySqlNioSocketFactory.TCP_TRAFFIC_CLASS_DEFAULT_VALUE));

			if ((trafficClass > 0) && (MySqlNioSocketFactory.setTraficClassMethod != null)) {
				MySqlNioSocketFactory.setTraficClassMethod.invoke(this.rawSocket, new Object[] { Integer.valueOf(trafficClass) });
			}
		}
		catch (final Throwable t) {
			this.unwrapExceptionToProperClassAndThrowIt(t);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Socket connect(String hostname, int portNumber, Properties properties) throws SocketException, IOException {
		if (properties != null) {
			this.host = hostname;
			this.port = portNumber;

			final Method connectWithTimeoutMethod = null;
			final Method socketBindMethod = null;

			final String localSocketHostname = properties.getProperty("localSocketAddress");
			final String connectTimeoutStr = properties.getProperty("connectTimeout");

			final boolean wantsTimeout = ((connectTimeoutStr != null) && (connectTimeoutStr.length() > 0) && !connectTimeoutStr.equals("0"));
			final boolean wantsLocalBind = ((localSocketHostname != null) && (localSocketHostname.length() > 0));
			final boolean needsConfigurationBeforeConnect = this.socketNeedsConfigurationBeforeConnect(properties);

			if (wantsTimeout || wantsLocalBind || needsConfigurationBeforeConnect) {

				if (connectTimeoutStr != null) {
					try {
						Integer.parseInt(connectTimeoutStr);
					}
					catch (final NumberFormatException nfe) {
						throw new SocketException("Illegal value '" + connectTimeoutStr + "' for connectTimeout");
					}
				}

				if (wantsLocalBind && (socketBindMethod == null)) {
					throw new SocketException("Can't specify \"localSocketAddress\" on JVMs older than 1.4");
				}

				if (wantsTimeout && (connectWithTimeoutMethod == null)) {
					throw new SocketException("Can't specify \"connectTimeout\" on JVMs older than 1.4");
				}
			}

			if (!(wantsLocalBind || wantsTimeout || needsConfigurationBeforeConnect)) {
				final InetAddress[] possibleAddresses = InetAddress.getAllByName(this.host);

				Throwable caughtWhileConnecting = null;

				// Need to loop through all possible addresses, in case someone has IPV6 configured (SuSE, for example...)
				for (final InetAddress possibleAddress : possibleAddresses) {
					try {
						final SocketChannel socketChannel = SocketChannel.open();
						socketChannel.connect(new InetSocketAddress(possibleAddress, this.port));

						this.rawSocket = socketChannel.socket();

						this.configureSocket(properties);

						break;
					}
					catch (final Exception ex) {
						caughtWhileConnecting = ex;
					}
				}

				if (this.rawSocket == null) {
					this.unwrapExceptionToProperClassAndThrowIt(caughtWhileConnecting);
				}

				return this.rawSocket;
			}
		}

		throw new SocketException("Unable to create socket");
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isLocallyConnected(com.mysql.jdbc.ConnectionImpl conn) throws SQLException {
		final long threadId = conn.getId();
		final java.sql.Statement processListStmt = conn.getMetadataSafeStatement();
		ResultSet rs = null;

		try {
			String processHost = null;

			rs = processListStmt.executeQuery("SHOW PROCESSLIST");

			while (rs.next()) {
				final long id = rs.getLong(1);

				if (threadId == id) {
					processHost = rs.getString(3);

					break;
				}
			}

			if (processHost != null) {
				if (processHost.indexOf(":") != -1) {
					processHost = processHost.split(":")[0];

					try {
						boolean isLocal;

						isLocal = InetAddress.getByName(processHost).equals(this.rawSocket.getLocalAddress());

						return isLocal;
					}
					catch (final UnknownHostException e) {
						conn.getLog().logWarn(Messages.getString("Connection.CantDetectLocalConnect", new Object[] { this.host }), e);

						return false;
					}
				}
			}

			return false;
		}
		finally {
			processListStmt.close();
		}
	}

	/**
	 * Does the configureSocket() need to be called before the socket is connect() based on the properties supplied?
	 * 
	 */
	private boolean socketNeedsConfigurationBeforeConnect(Properties props) {
		final int receiveBufferSize = Integer.parseInt(props.getProperty(MySqlNioSocketFactory.TCP_RCV_BUF_PROPERTY_NAME,
			MySqlNioSocketFactory.TCP_RCV_BUF_DEFAULT_VALUE));

		if (receiveBufferSize > 0) {
			return true;
		}

		final int sendBufferSize = Integer.parseInt(props.getProperty(MySqlNioSocketFactory.TCP_SND_BUF_PROPERTY_NAME,
			MySqlNioSocketFactory.TCP_SND_BUF_DEFAULT_VALUE));

		if (sendBufferSize > 0) {
			return true;
		}

		final int trafficClass = Integer.parseInt(props.getProperty(MySqlNioSocketFactory.TCP_TRAFFIC_CLASS_PROPERTY_NAME,
			MySqlNioSocketFactory.TCP_TRAFFIC_CLASS_DEFAULT_VALUE));

		if ((trafficClass > 0) && (MySqlNioSocketFactory.setTraficClassMethod != null)) {
			return true;
		}

		return false;
	}

	private void unwrapExceptionToProperClassAndThrowIt(Throwable error) throws SocketException, IOException {
		if (error instanceof InvocationTargetException) {
			error = ((InvocationTargetException) error).getTargetException();
		}

		if (error instanceof SocketException) {
			throw (SocketException) error;
		}

		if (error instanceof IOException) {
			throw (IOException) error;
		}

		throw new SocketException(error.toString());
	}
}

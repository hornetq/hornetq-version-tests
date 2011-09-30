package org.hornetq.integration;

import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.api.core.client.ClientSession;
import org.hornetq.api.core.client.ClientSessionFactory;
import org.hornetq.api.core.client.HornetQClient;
import org.hornetq.api.core.client.ServerLocator;
import org.hornetq.core.remoting.impl.netty.NettyConnectorFactory;
import org.hornetq.utils.VersionLoader;
import org.junit.Test;

/**
 * @author <a href="mailto:andy.taylor@jboss.com">Andy Taylor</a>
 *         Date: 9/29/11
 *         Time: 10:56 AM
 */
public class SimpleConnectionIT
{
    @Test
    public void testSingleConnection() throws Exception
    {
        TransportConfiguration transportConfiguration = new TransportConfiguration(NettyConnectorFactory.class.getName());
        ServerLocator locator = HornetQClient.createServerLocatorWithoutHA(transportConfiguration);
        ClientSessionFactory factory = locator.createSessionFactory();
        ClientSession clientSession = factory.createSession();
        System.out.println("version=" + VersionLoader.getVersion().getFullVersion());
        clientSession.close();
    }
}

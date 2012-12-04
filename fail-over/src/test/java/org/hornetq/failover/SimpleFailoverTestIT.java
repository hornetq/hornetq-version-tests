/*
* JBoss, Home of Professional Open Source.
* Copyright 2010, Red Hat, Inc., and individual contributors
* as indicated by the @author tags. See the copyright.txt file in the
* distribution for a full listing of individual contributors.
*
* This is free software; you can redistribute it and/or modify it
* under the terms of the GNU Lesser General Public License as
* published by the Free Software Foundation; either version 2.1 of
* the License, or (at your option) any later version.
*
* This software is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
* Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public
* License along with this software; if not, write to the Free
* Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
* 02110-1301 USA, or see the FSF site: http://www.fsf.org.
*/
package org.hornetq.failover;

import org.hornetq.api.core.management.HornetQServerControl;
import org.hornetq.jms.client.HornetQJMSConnectionFactory;
import org.hornetq.jms.client.HornetQQueue;
import org.junit.Test;

import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.naming.InitialContext;
import javax.jms.Connection;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * @author <a href="mailto:andy.taylor@jboss.org">Andy Taylor</a>
 */
public class SimpleFailoverTestIT
{

    private final String JMX_URL = "service:jmx:rmi:///jndi/rmi://localhost:3000/jmxrmi";

    @Test
    public void simpleFailover() throws Exception
    {
        Thread.sleep(5000);
        Connection c;
        Hashtable<String, String> env = new Hashtable<String, String>();
        env.put("java.naming.factory.initial", "org.jnp.interfaces.NamingContextFactory");
        env.put("java.naming.provider.url", "jnp://localhost:1099");
        env.put("java.naming.factory.url.pkgs", "org.jboss.naming:org.jnp.interfaces");
        InitialContext context = new InitialContext(env);
        HornetQJMSConnectionFactory cf = (HornetQJMSConnectionFactory) context.lookup("/ConnectionFactory");
        c = cf.createConnection();
        Session session  = c.createSession(false, Session.AUTO_ACKNOWLEDGE);
        HornetQQueue q = (HornetQQueue) context.lookup("/queue/testQueue");
        MessageProducer producer = session.createProducer(q);
        kill("liveA");
        Thread.sleep(5000);
        producer.send(session.createTextMessage("got there!"));
        c.close();
    }

    private void kill(String nodeName) throws Exception
    {

         ObjectName on = ObjectName.getInstance("org.hornetq." + nodeName + ":module=Core,type=Server");
         JMXConnector connector = JMXConnectorFactory.connect(new JMXServiceURL(JMX_URL), new HashMap<String, String>());
         MBeanServerConnection mbsc = connector.getMBeanServerConnection();
         HornetQServerControl serverControl = MBeanServerInvocationHandler.newProxyInstance(mbsc,
                                                                                            on,
                                                                                            HornetQServerControl.class,
                                                                                            false);
        try
        {
            serverControl.forceFailover();
        }
        catch (Exception e)
        {
            //we expect this dont we
        }
    }


}

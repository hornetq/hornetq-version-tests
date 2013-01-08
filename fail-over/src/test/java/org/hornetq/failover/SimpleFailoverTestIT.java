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

import static org.junit.Assert.*;

import org.hornetq.api.core.DiscoveryGroupConfiguration;
import org.hornetq.api.core.management.HornetQServerControl;
import org.hornetq.jms.client.HornetQConnectionFactory;
import org.hornetq.jms.client.HornetQJMSConnectionFactory;
import org.hornetq.jms.client.HornetQQueue;
import org.junit.Test;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.jms.Connection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Properties;

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
        c.start();
        MessageConsumer consumer = session.createConsumer(q);
        assertNotNull(consumer.receive(5000));
        c.close();
    }

    @Test
    public void CFJndiLookupBackwardCompatibilityTest() throws Exception
    {
       Connection connection1 = null;
       Connection connection2 = null;
       try
       {
          Properties prop = new Properties();
          prop.setProperty("java.naming.factory.initial", "org.jnp.interfaces.NamingContextFactory");
          prop.setProperty("java.naming.provider.url", "jnp://localhost:1099");
          prop.setProperty("java.naming.factory.url.pkgs", "org.jboss.naming:org.jnp.interfaces");
          
          System.out.println("JNDI properties: " + prop);
       
          Context context = new InitialContext(prop);
          
          Queue testQueue = (Queue) context.lookup("/queue/testQueue");
          
          ConnectionFactory factory = (ConnectionFactory) context.lookup("/HAStaticConnectionFactory");
          
          HornetQConnectionFactory fact = (HornetQConnectionFactory)factory;
          
          DiscoveryGroupConfiguration dg = fact.getDiscoveryGroupConfiguration();
          
          assertTrue("dg = " + dg, dg == null);
          
          connection1 = factory.createConnection();
          
          sendAndReceive(connection1, testQueue);
          
          connection1.close();
          connection1 = null;
          
          factory = (ConnectionFactory) context.lookup("/HADiscoveryConnectionFactory");

          fact = (HornetQConnectionFactory)factory;
          
          dg = fact.getDiscoveryGroupConfiguration();
          
          assertTrue("dg = " + dg, dg != null);
          
          connection2 = factory.createConnection();
          
          sendAndReceive(connection2, testQueue);
          
          connection2.close();
          connection2 = null;

       }
       finally
       {
          if (connection1 != null)
          {
             connection1.close();
          }
          if (connection2 != null)
          {
             connection2.close();
          }
       }
    }

    private void sendAndReceive(Connection connection, Queue queue) throws JMSException
    {
       connection.start();
       Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

       //some other tests may left un-consumed messages, clear them first
       MessageConsumer consumer = session.createConsumer(queue);
       while (consumer.receive(3000) != null)
       {
       }

       MessageProducer producer = session.createProducer(queue);
       TextMessage message = session.createTextMessage("Testing connection basic function");
       producer.send(message);

       TextMessage receivedMessage = (TextMessage) consumer.receive(5000);
       assertEquals("Testing connection basic function", receivedMessage.getText());
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

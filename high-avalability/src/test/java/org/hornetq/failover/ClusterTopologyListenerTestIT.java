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

import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.Pair;
import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.api.core.client.ClientSession;
import org.hornetq.api.core.client.ClientSessionFactory;
import org.hornetq.api.core.client.ClusterTopologyListener;
import org.hornetq.api.core.client.HornetQClient;
import org.hornetq.api.core.client.ServerLocator;
import org.hornetq.core.remoting.impl.netty.NettyConnectorFactory;
import org.hornetq.maven.PluginUtil;
import org.hornetq.maven.TestClusterManagerMBean;
import org.junit.Assert;
import org.junit.Test;

public class ClusterTopologyListenerTestIT
{
   ClientSessionFactory sf = null;

   @Test
   public void simpleTopologyChangeTest() throws Exception
   {
      Map<String, Object> params = new HashMap<String, Object>();
      TransportConfiguration tc = new TransportConfiguration(NettyConnectorFactory.class.getCanonicalName(), params);
      ServerLocator locator = HornetQClient.createServerLocatorWithHA(tc);
      locator.setBlockOnNonDurableSend(true);
      locator.setBlockOnDurableSend(true);

      TestClusterManagerMBean clusterControl = PluginUtil.getTestClusterManager();
      int numNodes = clusterControl.getNumNodes();
      
      Assert.assertTrue("Nodes should be more than one. " + numNodes, numNodes > 1);

      final List<String> nodes = new ArrayList<String>();
      final CountDownLatch upLatch = new CountDownLatch(numNodes);

      locator.addClusterTopologyListener(new LatchListener(upLatch, nodes, new CountDownLatch(0)));
      sf = locator.createSessionFactory();
      
      Assert.assertTrue("Was not notified that all servers are UP", upLatch.await(10, SECONDS));
      
      ClientSession session = sf.createSession();
      
      for (int i = numNodes-1; i > 0; i--)
      {
         clusterControl.killNode(i);
         checkSessionOrReconnect(session, locator);
         checkTopology(nodes, i);
      }
      
      sf.close();
      sf = null;
   }

   protected ClientSession checkSessionOrReconnect(ClientSession session, ServerLocator locator) throws Exception
   {
      ClientSessionFactory oldSf = sf;
      String queueName = "topologyChangeTestQueue";
      try
      {
         session.createQueue(queueName, queueName);
         session.deleteQueue(queueName);
      }
      catch (HornetQException e)
      {
         try
         {
            sf = locator.createSessionFactory();
            return sf.createSession();
         }
         finally
         {
            oldSf.close();
         }
      }
      return session;
   }


   protected void checkTopology(List<String> nodes, int num)
   {
      long start = System.currentTimeMillis();
      do
      {
         if (nodes.size() == num) return;
      } while(System.currentTimeMillis() - start < 5000);
      Assert.fail("topology didn't get updated. expect: " + num + " actual: " + nodes.size());
   }

   private static final class LatchListener implements ClusterTopologyListener
   {
      private final CountDownLatch upLatch;
      private final List<String> nodes;
      private final CountDownLatch downLatch;

      /**
       * @param upLatch
       * @param nodes
       * @param downLatch
       */
      private LatchListener(CountDownLatch upLatch, List<String> nodes, CountDownLatch downLatch)
      {
         this.upLatch = upLatch;
         this.nodes = nodes;
         this.downLatch = downLatch;
      }

      public synchronized void nodeUP(long eventUID, String nodeID, Pair<TransportConfiguration, TransportConfiguration> connectorPair, boolean last)
      {
         if (!nodes.contains(nodeID))
         {
            nodes.add(nodeID);
            upLatch.countDown();
         }
      }

      public synchronized void nodeDown(final long uniqueEventID, String nodeID)
      {
         if (nodes.contains(nodeID))
         {
            nodes.remove(nodeID);
            downLatch.countDown();
         }
      }
   }

}

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
package org.hornetq.integration;

import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


import org.hornetq.core.client.impl.ServerLocatorImpl;
import org.hornetq.jms.client.HornetQJMSConnectionFactory;
import org.hornetq.jms.client.HornetQXAConnectionFactory;
import org.junit.Test;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.naming.InitialContext;
import javax.jms.Connection;
import javax.naming.NamingException;
import java.util.Hashtable;

/**
 * @author <a href="mailto:andy.taylor@jboss.org">Andy Taylor</a>
 *         10/27/11
 */
public class ConnectionFactoryJNDITestIT
{
    @Test
    public void testNonXaCf() throws Exception
    {
        Connection c = null;
        try
        {
            Hashtable<String, String> env = new Hashtable<String, String>();
            env.put("java.naming.factory.initial", "org.jnp.interfaces.NamingContextFactory");
            env.put("java.naming.provider.url", "jnp://localhost:1099");
            env.put("java.naming.factory.url.pkgs", "org.jboss.naming:org.jnp.interfaces");
            InitialContext context = new InitialContext(env);
            HornetQJMSConnectionFactory cf = (HornetQJMSConnectionFactory) context.lookup("/NettyNonXAConnectionFactory");
            c = cf.createConnection();
            assertNotNull(cf.getServerLocator());
            ServerLocatorImpl locator = (ServerLocatorImpl) cf.getServerLocator();
            assertNotNull(locator.getTopology());
        }
        finally
        {
            if(c != null)
            {
                try
                {
                    c.close();
                } catch (JMSException e)
                {
                    //ignore
                }
            }
        }
    }

    @Test
    public void testXaCf() throws Exception
    {
        Connection c = null;
        try
        {
            Hashtable<String, String> env = new Hashtable<String, String>();
            env.put("java.naming.factory.initial", "org.jnp.interfaces.NamingContextFactory");
            env.put("java.naming.provider.url", "jnp://localhost:1099");
            env.put("java.naming.factory.url.pkgs", "org.jboss.naming:org.jnp.interfaces");
            InitialContext context = new InitialContext(env);
            HornetQXAConnectionFactory cf = (HornetQXAConnectionFactory) context.lookup("/NettyXAConnectionFactory");
            c = cf.createConnection();
            assertNotNull(cf.getServerLocator());
            ServerLocatorImpl locator = (ServerLocatorImpl) cf.getServerLocator();
            assertNotNull(locator.getTopology());
            c.close();
        }
        finally
        {
            if(c != null)
            {
                try
                {
                    c.close();
                } catch (JMSException e)
                {
                    //ignore
                }
            }
        }
    }

    @Test
    public void testNonHaCf() throws Exception
    {
        Connection c = null;
        try
        {
            Hashtable<String, String> env = new Hashtable<String, String>();
            env.put("java.naming.factory.initial", "org.jnp.interfaces.NamingContextFactory");
            env.put("java.naming.provider.url", "jnp://localhost:1099");
            env.put("java.naming.factory.url.pkgs", "org.jboss.naming:org.jnp.interfaces");
            InitialContext context = new InitialContext(env);
            HornetQJMSConnectionFactory cf = (HornetQJMSConnectionFactory) context.lookup("/NettyNonHAConnectionFactory");
            c = cf.createConnection();
            assertNotNull(cf.getServerLocator());
            ServerLocatorImpl locator = (ServerLocatorImpl) cf.getServerLocator();
            assertNotNull(locator.getTopology());
            assertFalse(cf.isHA());
        }
        finally
        {
            if(c != null)
            {
                try
                {
                    c.close();
                } catch (JMSException e)
                {
                    //ignore
                }
            }
        }
    }


    @Test
    public void testHaCf() throws Exception
    {
        Hashtable<String, String> env = new Hashtable<String, String>();
        env.put("java.naming.factory.initial", "org.jnp.interfaces.NamingContextFactory");
        env.put("java.naming.provider.url", "jnp://localhost:1099");
        env.put("java.naming.factory.url.pkgs", "org.jboss.naming:org.jnp.interfaces");
        InitialContext context = new InitialContext(env);
        HornetQJMSConnectionFactory cf = (HornetQJMSConnectionFactory) context.lookup("/NettyHAConnectionFactory");
        assertNotNull(cf.getServerLocator());
        ServerLocatorImpl locator = (ServerLocatorImpl) cf.getServerLocator();
        assertNotNull(locator.getTopology());
        assertTrue(cf.isHA());
    }
}

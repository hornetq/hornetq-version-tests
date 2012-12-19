/*
 * Copyright 2009 Red Hat, Inc.
 * Red Hat licenses this file to you under the Apache License, version
 * 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *    http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */

package org.hornetq.tests.util;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;


/**
 * Skeleton class necessary for the re-use of "tests/joram-tests" test code in this project.
 * <p>
 * This class should not be used and was added to:
 * <ol>
 * <li> avoid a {@link ClassNotFoundException}</li>
 * <li>make sure you get an exception should it get executed
 * (which not happen as the server is started by Maven).</li>
 * </ol>
 */
public final class SpawnedVMSupport
{

   public static Process spawnVM(final String className, final String... args) throws Exception
   {
	   // noop
	   throw new RuntimeException();
	   }

   public static Process spawnVM(final String className, final boolean logOutput, final String... args) throws Exception
   {
	// noop
	   throw new RuntimeException();

   }

   public static Process spawnVM(final String className, final String[] vmargs, final String... args) throws Exception
   {
	// noop
	   throw new RuntimeException();
   }

   public static Process spawnVM(final String className,
                                 final String[] vmargs,
                                 final boolean logOutput,
                                 final String... args) throws Exception
   {
	   throw new RuntimeException();
   }

   public static Process spawnVM(final String className,
                                 final String memoryArgs,
                                 final String[] vmargs,
                                 final boolean logOutput,
                                 final boolean logErrorOutput,
                                 final String... args) throws Exception
   {	   throw new RuntimeException();
   }

   /**
    * @param className
    * @param process
    * @throws ClassNotFoundException
    */
   public static void startLogger(final String className, final Process process) throws ClassNotFoundException
   {
	   throw new RuntimeException();
   }

   /**
    * Assert that a process exits with the expected value (or not depending if
    * the <code>sameValue</code> is expected or not). The method waits 5
    * seconds for the process to exit, then an Exception is thrown. In any case,
    * the process is destroyed before the method returns.
    */
   public static void assertProcessExits(final boolean sameValue, final int value, final Process p) throws InterruptedException,
                                                                                                   ExecutionException,
                                                                                                   TimeoutException
   {
	   throw new RuntimeException();
   }

}
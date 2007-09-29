/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2006 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

package com.zimbra.cs.launcher;

import java.lang.reflect.Method;

import sun.misc.Signal;
import sun.misc.SignalHandler;

public class TomcatLauncher {

	private static Class mBootstrapClass;
	private static Method mMainMethod;
	
	public static void stop() {
		try {
			Object args = new String[] { "stopd" };
			mMainMethod.invoke(null, args);
		} catch (Exception e) {
			System.out.println("[tomcat launcher] exception occurred during stop: " + e);
			e.printStackTrace();
		}
		System.exit(0);
	}

	public static void start() {
		try {
			Object args = new String[] { "startd" };
			mMainMethod.invoke(null, args);
			Thread.sleep(Long.MAX_VALUE);
		} catch (Exception e) {
			System.out.println("[tomcat launcher] exception occurred during start: " + e);
			e.printStackTrace(System.out);
		}
		stop();  // In case our sleep got interrupted...
	}

	public static void main(String[] args) throws Exception {
		mBootstrapClass = Class.forName("org.apache.catalina.startup.Bootstrap");
		mMainMethod = mBootstrapClass.getMethod("main", (new String[0]).getClass());

		SignalHandler termHandler = new SignalHandler() {
			public void handle(Signal sig) {
				System.out.println("[tomcat launcher] got signal " + sig + " invoking stop");
				stop();
			}
		};
		
		Signal.handle(new Signal("TERM"), termHandler);
		
		start();
	}
}

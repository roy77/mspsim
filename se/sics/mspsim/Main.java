/*
 * Copyright (c) 2008, Swedish Institute of Computer Science.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the Institute nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE INSTITUTE AND CONTRIBUTORS ``AS IS'' AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE INSTITUTE OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 *
 * This file is part of MSPSim.
 *
 * $Id$
 *
 * -----------------------------------------------------------------
 *
 * Main
 *
 * Authors : Joakim Eriksson, Niclas Finne
 * Created : 6 nov 2008
 * Updated : $Date$
 *           $Revision$
 */

package se.sics.mspsim;
import java.io.IOException;
import se.sics.mspsim.platform.GenericNode;
import se.sics.mspsim.util.ArgumentManager;

/**
 *
 */
public class Main {

  public static GenericNode createNode(String className) {
    try {
      Class<? extends GenericNode> nodeClass = Class.forName(className).asSubclass(GenericNode.class);
      return nodeClass.newInstance();
    } catch (ClassNotFoundException e) {
      // Can not find specified class
    } catch (ClassCastException e) {
      // Wrong class type
    } catch (InstantiationException e) {
      // Failed to instantiate
    } catch (IllegalAccessException e) {
      // Failed to access constructor
    }
    return null;
  }

  public static void main(String[] args) throws IOException {
    ArgumentManager config = new ArgumentManager();
    config.handleArguments(args);

    String nodeType = config.getProperty("nodeType");
    String platform = nodeType;
    GenericNode node;
    if (nodeType != null) {
      node = createNode(nodeType);
    } else {
      platform = config.getProperty("platform", "sky");
      nodeType = "se.sics.mspsim.platform." + platform + '.' +
      Character.toUpperCase(platform.charAt(0)) + platform.substring(1).toLowerCase() + "Node";
      node = createNode(nodeType);
      if (node == null) {
        nodeType = "se.sics.mspsim.platform." + platform + '.' + platform.toUpperCase() + "Node";
        node = createNode(nodeType);
      }
    }
    if (node == null) {
      System.err.println("MSPSim does not yet support the platform '" + platform + '\'');
      System.exit(1);
    }
    node.setupArgs(config);
  }

}
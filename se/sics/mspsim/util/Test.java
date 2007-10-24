/**
 * Copyright (c) 2007, Swedish Institute of Computer Science.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
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
 * $Id: Test.java,v 1.2 2007/10/21 21:17:35 nfi Exp $
 *
 * -----------------------------------------------------------------
 *
 * Utils
 *
 * Author  : Joakim Eriksson, Niclas Finne
 * Created : Sun Oct 21 22:00:00 2007
 * Updated : $Date: 2007/10/21 21:17:35 $
 *           $Revision: 1.2 $
 */
package se.sics.mspsim.util;
import se.sics.mspsim.core.*;

/**
 * Test - tests a ihex file and exits when reporting "FAIL:" first
 * on a line...
 */
public class Test implements USARTListener {

  String line = "";
  MSP430 cpu;

  public Test(MSP430 cpu) {
    this.cpu = cpu;
    IOUnit usart = cpu.getIOUnit("USART 1");
    if (usart instanceof USART) {
      ((USART) usart).setUSARTListener(this);
    }
  }

  public void dataReceived(USART source, int data) {
    if (data == '\n') {
      System.out.println("#|" + line);
      if (line.startsWith("FAIL:")) {
	System.exit(0);
      } else if (line.startsWith("EXIT")) {
	System.out.println("Tests succeded!");
	System.exit(0);
      } else if (line.startsWith("DEBUG")) {
	cpu.setDebug(true);
      }
      line = "";
    } else {
      line += (char) data;
    }
  }

  public static void main(String[] args) {
    MSP430 cpu = new MSP430(0);
    int index = 0;
    if (args[index].startsWith("-")) {
      // Flag
      if ("-debug".equalsIgnoreCase(args[index])) {
	cpu.setDebug(true);
      } else {
	System.err.println("Unknown flag: " + args[index]);
	System.exit(1);
      }
      index++;
    }

    IHexReader reader = new IHexReader();
    String ihexFile = args[index++];
    int[] memory = cpu.getMemory();
    reader.readFile(memory, ihexFile);
    cpu.reset();

    if (index < args.length && cpu.getDisAsm() != null) {
      try {
	MapTable map = new MapTable(args[index++]);
	cpu.getDisAsm().setMap(map);
      } catch (Exception e) {
	e.printStackTrace();
	System.exit(1);
      }
    }

    // Create the "tester"
    new Test(cpu);
    cpu.cpuloop();
  }
}
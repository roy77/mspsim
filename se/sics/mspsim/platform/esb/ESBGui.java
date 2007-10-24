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
 * $Id: ESBGui.java,v 1.4 2007/10/21 22:19:07 nfi Exp $
 *
 * -----------------------------------------------------------------
 *
 * ESBGui
 *
 * Author  : Joakim Eriksson
 * Created : Sun Oct 21 22:00:00 2007
 * Updated : $Date: 2007/10/21 22:19:07 $
 *           $Revision: 1.4 $
 */

package se.sics.mspsim.platform.esb;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;

import se.sics.mspsim.chip.Beeper;
import se.sics.mspsim.core.*;
import se.sics.mspsim.util.SerialMon;
import se.sics.mspsim.util.WindowUtils;

public class ESBGui extends JComponent implements KeyListener,
						  MouseMotionListener {

  public static final int GREEN_X = 3;
  public static final int YELLOW_X = 10;
  public static final int RED_X = 17;
  public static final int LED_Y = 4;

  public static final Color RED_TRANS = new Color(0xff,0x40,0x40,0xa0);
  public static final Color YELLOW_TRANS = new Color(0xff, 0xff, 0x00, 0xa0);
  public static final Color GREEN_TRANS = new Color(0x40, 0xf0, 0x40, 0xa0);

  public static final Color RED_C = new Color(0xffff6060);
  public static final Color YELLOW_C = new Color(0xffffff00);
  public static final Color GREEN_C = new Color(0xff40ff40);

  private SerialMon serial;
  private SerialMon radio;
  Beeper beeper;

  private ImageIcon esbImage;
  private JFrame window;
  private ESBNode node;

  public ESBGui(ESBNode node) {
    this.node = node;

    setBackground(Color.black);
    setOpaque(true);

    esbImage = new ImageIcon("images/esb.jpg");
    if (esbImage.getIconWidth() == 0 || esbImage.getIconHeight() == 0) {
      // Image not found
      throw new IllegalStateException("image not found");
    }
    setPreferredSize(new Dimension(esbImage.getIconWidth(),
				   esbImage.getIconHeight()));

    window = new JFrame("ESB");
//     window.setSize(190,240);
    window.add(this);
    WindowUtils.restoreWindowBounds("ESBGui", window);
    WindowUtils.addSaveOnShutdown("ESBGui", window);
    window.setVisible(true);

    window.addKeyListener(this);
    window.addMouseMotionListener(this);

    // Add some windows for listening to serial output
    MSP430 cpu = node.getCPU();
    IOUnit usart = cpu.getIOUnit("USART 1");
    if (usart instanceof USART) {
      serial = new SerialMon((USART)usart, "RS232 Port Output");
      ((USART) usart).setUSARTListener(serial);
    }

    IOUnit usart0 = cpu.getIOUnit("USART 0");
    if (usart0 instanceof USART) {
      radio = new SerialMon((USART)usart0, "TR1001 Output");
      ((USART) usart0).setUSARTListener(radio);
    }

    beeper = new Beeper();
    cpu.addIOUnit(-1,0,-1,0,beeper, true);
  }

  public void mouseMoved(MouseEvent e) {
    //    System.out.println("Mouse moved: " + e.getX() + "," + e.getY());
    int x = e.getX();
    int y = e.getY();
    node.setPIR(x > 5 && x < 80 && y > 50 && y < 130);
    node.setVIB(x > 60 && x < 100 && y > 180 && y < 200);
  }

  public void mouseDragged(MouseEvent e) {
  }


  public void paintComponent(Graphics g) {
    Color old = g.getColor();
    int w = getWidth(), h = getHeight();
    int iw = esbImage.getIconWidth(), ih = esbImage.getIconHeight();
    esbImage.paintIcon(this, g, 0, 0);
    // Clear all areas not covered by the image
    g.setColor(getBackground());
    if (w > iw) {
      g.fillRect(iw, 0, w, h);
    }
    if (h > ih) {
      g.fillRect(0, ih, w, h);
    }

    // Display all active leds
    if (node.greenLed) {
      g.setColor(GREEN_TRANS);
      g.fillOval(GREEN_X - 1, LED_Y - 3, 5, 9);
      g.setColor(GREEN_C);
      g.fillOval(GREEN_X, LED_Y, 3, 4);
    }
    if (node.redLed) {
      g.setColor(RED_TRANS);
      g.fillOval(RED_X - 1, LED_Y - 3, 5, 9);
      g.setColor(RED_C);
      g.fillOval(RED_X, LED_Y, 3, 4);
    }
    if (node.yellowLed) {
      g.setColor(YELLOW_TRANS);
      g.fillOval(YELLOW_X - 1, LED_Y - 3, 5, 9);
      g.setColor(YELLOW_C);
      g.fillOval(YELLOW_X, LED_Y, 3, 4);
    }
    g.setColor(old);
  }

  public void keyPressed(KeyEvent key) {
//     System.out.println("Key Pressed: " + key.getKeyChar());
    if (key.getKeyChar() == 'd') {
      node.setDebug(!node.getDebug());
    }
  }

  public void keyReleased(KeyEvent key) {
  }

  public void keyTyped(KeyEvent key) {
  }

}
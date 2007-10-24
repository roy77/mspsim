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
 * $Id: CC2420.java,v 1.4 2007/10/22 18:03:41 joakime Exp $
 *
 * -----------------------------------------------------------------
 *
 * CC2420
 *
 * Author  : Joakim Eriksson
 * Created : Sun Oct 21 22:00:00 2007
 * Updated : $Date: 2007/10/22 18:03:41 $
 *           $Revision: 1.4 $
 */

package se.sics.mspsim.chip;
import se.sics.mspsim.core.*;
import se.sics.mspsim.util.Utils;

public class CC2420 implements USARTListener {

  public static final boolean DEBUG = true;

  public static final int REG_SNOP		= 0x00;
  public static final int REG_SXOSCON	        = 0x01;
  public static final int REG_STXCAL		= 0x02;
  public static final int REG_SRXON		= 0x03;
  public static final int REG_STXON		= 0x04;
  public static final int REG_STXONCCA	        = 0x05;
  public static final int REG_SRFOFF		= 0x06;
  public static final int REG_SXOSCOFF	        = 0x07;
  public static final int REG_SFLUSHRX	        = 0x08;
  public static final int REG_SFLUSHTX	        = 0x09;
  public static final int REG_SACK		= 0x0A;
  public static final int REG_SACKPEND	        = 0x0B;
  public static final int REG_SRXDEC		= 0x0C;
  public static final int REG_STXENC		= 0x0D;
  public static final int REG_SAES		= 0x0E;
  public static final int REG_foo		= 0x0F;
  public static final int REG_MAIN		= 0x10;
  public static final int REG_MDMCTRL0	        = 0x11;
  public static final int REG_MDMCTRL1	        = 0x12;
  public static final int REG_RSSI		= 0x13;
  public static final int REG_SYNCWORD	        = 0x14;
  public static final int REG_TXCTRL		= 0x15;
  public static final int REG_RXCTRL0	        = 0x16;
  public static final int REG_RXCTRL1	        = 0x17;
  public static final int REG_FSCTRL		= 0x18;
  public static final int REG_SECCTRL0	        = 0x19;
  public static final int REG_SECCTRL1       	= 0x1A;
  public static final int REG_BATTMON   	= 0x1B;
  public static final int REG_IOCFG0		= 0x1C;
  public static final int REG_IOCFG1		= 0x1D;
  public static final int REG_MANFIDL   	= 0x1E;
  public static final int REG_MANFIDH   	= 0x1F;
  public static final int REG_FSMTC		= 0x20;
  public static final int REG_MANAND		= 0x21;
  public static final int REG_MANOR		= 0x22;
  public static final int REG_AGCCTRL    	= 0x23;
  public static final int REG_AGCTST0   	= 0x24;
  public static final int REG_AGCTST1   	= 0x25;
  public static final int REG_AGCTST2   	= 0x26;
  public static final int REG_FSTST0		= 0x27;
  public static final int REG_FSTST1		= 0x28;
  public static final int REG_FSTST2		= 0x29;
  public static final int REG_FSTST3		= 0x2A;
  public static final int REG_RXBPFTST    	= 0x2B;
  public static final int REG_FSMSTATE   	= 0x2C;
  public static final int REG_ADCTST		= 0x2D;
  public static final int REG_DACTST		= 0x2E;
  public static final int REG_TOPTST		= 0x2F;
  public static final int REG_RESERVED   	= 0x30;
  /* 0x31 - 0x3D not used */
  public static final int REG_TXFIFO		= 0x3E;
  public static final int REG_RXFIFO		= 0x3F;

  public static final int ST_XOSC16M_STABLE = 1 << 6;
  public static final int ST_TX_UNDERFLOW   = 1 << 5;
  public static final int ST_ENC_BUSY	    = 1 << 4;
  public static final int ST_TX_ACTIVE	= 1 << 3;
  public static final int ST_LOCK	= 1 << 2;
  public static final int ST_RSSI_VALID	= 1 << 1;

  // RAM Addresses
  public static final int RAM_TXFIFO	= 0x000;
  public static final int RAM_RXFIFO	= 0x080;
  public static final int RAM_KEY0	= 0x100;
  public static final int RAM_RXNONCE	= 0x110;
  public static final int RAM_SABUF	= 0x120;
  public static final int RAM_KEY1	= 0x130;
  public static final int RAM_TXNONCE	= 0x140;
  public static final int RAM_CBCSTATE	= 0x150;
  public static final int RAM_IEEEADDR	= 0x160;
  public static final int RAM_PANID	= 0x168;
  public static final int RAM_SHORTADDR	= 0x16A;

  // when reading registrers this flag is set!
  public static final int FLAG_READ = 0x40;

  public static final int FLAG_RAM = 0x80;
  // When accessing RAM the second byte of the address comtains
  // a flag indicating read/write
  public static final int FLAG_RAM_READ = 0x20;


  public static final int WAITING = 0;
  public static final int WRITE_REGISTER = 1;
  public static final int READ_REGISTER = 2;
  public static final int RAM_ACCESS = 3;

  public static final int READ_RXFIFO = 4;
  public static final int WRITE_TXFIFO = 5;

  private int state = WAITING;
  private int pos;
  private int address;
  private boolean ramRead = false;

  private int status = ST_XOSC16M_STABLE;

  private int[] registers = new int[64];
  // More than needed...
  private int[] memory = new int[512];

  private boolean chipSelect;

  private IOPort ccaPort = null;
  private int ccaPin;

  private IOPort fifopPort = null;
  private int fifopPin;

  private IOPort fifoPort = null;
  private int fifoPin;

  private boolean rxPacket;
  private int rxCursor;
  private int rxLen;

  public CC2420() {
    registers[REG_SNOP] = 0;
  }

  public void dataReceived(USART source, int data) {
    if (chipSelect) {
      System.out.println("CC2420 byte received: " + Utils.hex8(data) +
			 '\'' + (char) data + '\'' +
			 " CS: " + chipSelect + " state: " + state);
      switch(state) {
      case WAITING:
	state = WRITE_REGISTER;
	if ((data & FLAG_READ) != 0) {
	  state = READ_REGISTER;
	}
	if ((data & FLAG_RAM) != 0) {
	  state = RAM_ACCESS;
	  address = data & 0x7f;
	} else {
	  // The register address
	  address = data & 0x3f;

	  if (address == REG_RXFIFO) {
	    // check read/write???
	    System.out.println("CC2420: Reading RXFIFO!!!");
	    state = READ_RXFIFO;
	  } else if (address == REG_TXFIFO) {
	    state = WRITE_TXFIFO;
	  }
	}
	if (data < 0x0f) {
	  strobe(data);
	}
	pos = 0;
	// Assuming that the status always is sent back???
	source.byteReceived(status);
	break;
      case WRITE_REGISTER:
      	if (pos == 0) {
      		source.byteReceived(registers[address] >> 8);
      		// set the high bits
      		registers[address] = registers[address] & 0xff | (data << 8);
      	} else {
      		source.byteReceived(registers[address] & 0xff);
      		// set the low bits
      		registers[address] = registers[address] & 0xff00 | data;
      		System.out.println("CC2420: wrote to " + Utils.hex8(address) + " = "
      				+ registers[address]);
      	}
	break;
      case READ_REGISTER:
      	if (pos == 0) {
      		source.byteReceived(registers[address] >> 8);
      	} else {
      		source.byteReceived(registers[address] & 0xff);
      		System.out.println("CC2420: read from " + Utils.hex8(address) + " = "
      				+ registers[address]);
      	}
      	break;
      case READ_RXFIFO:
      	System.out.println("CC2420: RXFIFO READ => " +
      			memory[RAM_RXFIFO + rxCursor]);
      	source.byteReceived(memory[RAM_RXFIFO + rxCursor++]);
      	// What if wrap cursor???
      	if (rxCursor >= 128) rxCursor = 0;
      	// When is this set to "false" - when is interrupt de-triggered?
      	if (rxPacket) {
      		rxPacket = false;
      		updateFifopPin();
      	}
      	break;
      case RAM_ACCESS:
      	if (pos == 0) {
      		address = address | (data << 1) & 0x180;
      		ramRead = (data & 0x20) != 0;
      		System.out.println("CC2420: Address: " + Utils.hex16(address) +
      				" read: " + ramRead);
      		pos++;
      	} else {
      		if (!ramRead) {
      			memory[address++] = data;
      			if (address == RAM_PANID + 2) {
      				System.out.println("CC2420: Pan ID set to: 0x" +
      						Utils.hex8(memory[RAM_PANID]) +
      						Utils.hex8(memory[RAM_PANID + 1]));
      			}
      		}
      	}
      	break;
      }
    }
  }

  // Needs to get information about when it is possible to write
  // next data...

  private void strobe(int data) {
    // Resets, on/off of different things...
    System.out.println("CC2420: Strobe on: " + Utils.hex8(data));

    switch (data) {
    case REG_SRXON:
      System.out.println("CC2420: Strobe RX-ON!!!");
      break;
    case REG_SFLUSHRX:
      flushRX();
      break;
    }
  }


  public void setChipSelect(boolean select) {
    chipSelect = select;
    if (!chipSelect)
      state = WAITING;
    System.out.println("CC2420: chipSelect: " + chipSelect);
  }

  public void setCCAPort(IOPort port, int pin) {
    ccaPort = port;
    ccaPin = pin;
  }

  public void setFIFOPPort(IOPort port, int pin) {
    fifopPort = port;
    fifopPin = pin;
  }

  public void setFIFOPort(IOPort port, int pin) {
    fifoPort = port;
    fifoPin = pin;
  }


  // -------------------------------------------------------------------
  // Methods for accessing and writing to registers, etc from outside
  // -------------------------------------------------------------------

  public int getRegister(int register) {
    return registers[register];
  }

  public void setRegister(int register, int data) {
    registers[register] = data;
  }

  public void setIncomingPacket(int[] packet) {
  	int adr = RAM_RXFIFO;
  	memory[adr++] = packet.length + 2;
    for (int i = 0, n = packet.length; i < n; i++) {
      memory[adr++] = packet[i] & 0xff;
    }
    // Should take a RSSI value as input or use a set-RSSI value...
    memory[adr++] = (202) & 0xff;
    // Set CRC ok and add a correlation 
    memory[adr++] = (37) | 0x80;
    rxPacket = true;
    rxCursor = 0;  
    rxLen = adr;
    updateFifopPin();
  }

  private void flushRX() {
    if (DEBUG) System.out.println("Flushing RX! was: " + rxPacket + " len = " +
				  rxLen);
    rxPacket = false;
    rxCursor = 0;
    rxLen = 0;
    updateFifopPin();
  }

  private void updateFifopPin() {
    fifopPort.setPinState(fifopPin, rxPacket ? 1 : 0);
  }

  public void setCCA(boolean cca) {
    ccaPort.setPinState(ccaPin, cca ? 1 : 0);
  }

} // CC2420
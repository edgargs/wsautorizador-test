package com.gs.tramas;

import com.solab.iso8583.IsoType;
import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.MessageFactory;
import com.solab.iso8583.parse.ConfigParser;
import com.solab.iso8583.impl.SimpleTraceGenerator;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date

public class LectorBitmaps {

	static def getTrama(String trama){
        MessageFactory mfact;
        byte[] byteTramaOut = trama.getBytes();
        IsoMessage im;
        try {
            //mfact = ConfigParser.createDefault();
            mfact = ConfigParser.createFromClasspathConfig(FILE_CONFIG);
            im = mfact.parseMessage(byteTramaOut ,0);
            imprimirVariablesParseadas(im);
        } catch (Exception e) {
			e.printStackTrace();
        }
		return im
    }
	
	public static void imprimirVariablesParseadas(IsoMessage m)throws Exception {
        /*log.debug("TYPE: " + Integer.toHexString(m.getType()));*/
        for (int i = 0; i <= 128; i++){
            if (m.hasField(i)){
                System.out.println("F " + i + " (" + m.getField(i).getType().toString() + "): " + m.getObjectValue(i) + " -> '" + m.getField(i).toString() + "'");
            }
        }
    }
	
	public static MessageFactory mfact;
	static int MSG_BYTE_100 = 0X100;
	static int MSG_BYTE_400 = 0X400;
	static String FILE_CONFIG = "config8583_pmp.xml";
	
	public static IsoMessage generarTramaConsulta(String tarjeta, int trace) throws Exception{
		leerConfiguracion();
		IsoMessage solic = mfact.newMessage(MSG_BYTE_100);
		
		solic[2] = IsoType.LLVAR(tarjeta, 16)
		solic[3] = IsoType.NUMERIC(303000, 6)
		//bit4:[null]
		//bit7:[1012232728]
		solic[11] = IsoType.NUMERIC(trace,6)
		solic[12] = IsoType.TIME(new Date()) //[113622]
		solic[13] = IsoType.DATE4(new Date()) //[1215]
		solic[14] = IsoType.NUMERIC(2012,4)
		solic[18] = IsoType.NUMERIC(6011,4)
		solic[22] = IsoType.NUMERIC(51,3)
		solic[23] = IsoType.NUMERIC(''   ,3)
		solic[25] = IsoType.NUMERIC(51,2)
		solic[32] = IsoType.LLVAR('476134')
		//bit33:[null]
		solic[35] = IsoType.LLVAR('4890680004356394D21042011393157500000')
		solic[37] = IsoType.ALPHA("628623007946",12)
		//bit39:[null]
		solic[41] = IsoType.ALPHA("ATM01   ",8)
		solic[42] = IsoType.ALPHA("CARD ACCEPTOR  ",15)
		solic[43] = IsoType.ALPHA("DISPENSADOR LOBBY........LIMA.........PE",40)
		//bit45:[null]
		//bit48:[null]
		solic[49] = IsoType.NUMERIC(840,3)
		solic[60] = IsoType.LLLVAR('2500001000  ')
		
//merchantTypeBit18	MERCHANT
//cardAcceptorNameBit43	CARD_LOCATION
//terminalDataBit60	TERMINAL_DATA_B60
//pointServiceConditionBit25	POS_COND_CODE

		imprimirVariablesParseadas(solic)
		
		return solic;
	}
	
	public static IsoMessage generarTramaCompra(String tarjeta, int trace, double monto) throws Exception{
		leerConfiguracion();
		IsoMessage solic = mfact.newMessage(MSG_BYTE_100);
		
		solic[2] = IsoType.LLVAR(tarjeta, 16)
		solic[3] = IsoType.NUMERIC(000000, 6)
		solic[4] = IsoType.AMOUNT(monto)
		solic[6] = IsoType.AMOUNT(monto) //CARDISS_AMOUNT
		//bit7:[1012232728]
		solic[11] = IsoType.NUMERIC(trace,6)
		solic[12] = IsoType.TIME(new Date()) //[113622]
		solic[13] = IsoType.DATE4(new Date()) //[1215]
		solic[14] = IsoType.NUMERIC(2012,4)
		solic[18] = IsoType.NUMERIC(5999,4) //MERCHANT
		solic[22] = IsoType.NUMERIC(51,3) //POS_ENTRY_MODE
		solic[23] = IsoType.NUMERIC(''   ,3)
		solic[25] = IsoType.NUMERIC(51,2) //POS_COND_CODE
		solic[32] = IsoType.LLVAR('476134')
		//bit33:[null]
		solic[35] = IsoType.LLVAR('4890680004356394D21042011393157500000')
		solic[37] = IsoType.ALPHA("724716010412",12)
		//bit39:[null]
		solic[41] = IsoType.ALPHA("2.8     ",8)
		solic[42] = IsoType.ALPHA("CARD ACCEPTOR  ",15)
		solic[43] = IsoType.ALPHA("DISPENSADOR LOBBY........LIMA.........PE",40)
		//bit45:[null]
		//bit48:[null]
		solic[49] = IsoType.NUMERIC(604,3)
		solic[51] = IsoType.NUMERIC(604,3) //CUR_CODE_CARDISS
		solic[60] = IsoType.LLLVAR('2500001000  ')

		imprimirVariablesParseadas(solic)
		
		return solic;
	}
	
	public static IsoMessage generarTramaRetiro(String tarjeta, int trace, double monto) throws Exception{
		leerConfiguracion();
		IsoMessage solic = mfact.newMessage(MSG_BYTE_100);
		
		solic[2] = IsoType.LLVAR(tarjeta, 16)
		solic[3] = IsoType.NUMERIC(10000, 6)
		solic[4] = IsoType.AMOUNT(monto)
		solic[6] = IsoType.AMOUNT(monto) //CARDISS_AMOUNT
		//bit7:[1012232728]
		solic[11] = IsoType.NUMERIC(trace,6)
		solic[12] = IsoType.TIME(new Date()) //[113622]
		solic[13] = IsoType.DATE4(new Date()) //[1215]
		solic[14] = IsoType.NUMERIC(2012,4)
		solic[18] = IsoType.NUMERIC(5999,4) //MERCHANT
		solic[22] = IsoType.NUMERIC(51,3) //POS_ENTRY_MODE
		solic[23] = IsoType.NUMERIC(''   ,3)
		solic[25] = IsoType.NUMERIC(51,2) //POS_COND_CODE
		solic[32] = IsoType.LLVAR('476134')
		//bit33:[null]
		solic[35] = IsoType.LLVAR('4890680004356394D21042011393157500000')
		solic[37] = IsoType.ALPHA("724716010412",12)
		//bit39:[null]
		solic[41] = IsoType.ALPHA("2.8     ",8)
		solic[42] = IsoType.ALPHA("CARD ACCEPTOR  ",15)
		solic[43] = IsoType.ALPHA("DISPENSADOR LOBBY........LIMA.........PE",40)
		//bit45:[null]
		//bit48:[null]
		solic[49] = IsoType.NUMERIC(604,3)
		solic[51] = IsoType.NUMERIC(604,3) //CUR_CODE_CARDISS
		solic[60] = IsoType.LLLVAR('2500001000  ')

		imprimirVariablesParseadas(solic)
		
		return solic;
	}
	
	public static void leerConfiguracion() throws IOException{
        mfact = ConfigParser.createFromClasspathConfig(FILE_CONFIG);
        mfact.setAssignDate(true);
        mfact.setTraceNumberGenerator(new SimpleTraceGenerator((int) (System.currentTimeMillis() % 100000)));
	}
}

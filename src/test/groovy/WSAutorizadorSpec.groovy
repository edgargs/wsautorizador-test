import spock.lang.Specification
import groovy.xml.StreamingMarkupBuilder
import java.util.ArrayList

import groovy.sql.Sql
import spock.lang.Shared
import spock.lang.Ignore
import groovy.xml.XmlUtil

import org.reficio.ws.builder.SoapBuilder
import org.reficio.ws.builder.SoapOperation
import org.reficio.ws.builder.core.Wsdl
import org.reficio.ws.client.core.SoapClient
import org.reficio.ws.common.ResourceUtils

import javax.xml.namespace.QName

import com.solab.iso8583.IsoMessage
import com.gs.tramas.LectorBitmaps

class WSAutorizadorSpec extends Specification {

    @Shared sql 
    
    def strTrace, trace
	@Shared service = "http://10.168.3.168:7001/WSServiceCardAuthorizerAsync/ServiceSendReceiveMessageISO"
	
	@Shared
	SoapBuilder builder
	@Shared
	SoapOperation operation
    
    def setupSpec() {
		Wsdl wsdl = Wsdl.parse("${service}?WSDL");
		
		//List<QName> bindings = wsdl.getBindings(); // (2)
		//println bindings
		//wsdl.printBindings(); // (4)
		
		builder = wsdl.binding()
			.localPart("ISendReceiveMessageISOPortBinding")
			.find();
		//List<SoapOperation> operations = builder.getOperations(); // (5)
		//println operations
		
		operation = builder.operation()
			.name("sendReceiveMsg")
			.find();
	}
	
    def setup() {
    
        sql = Sql.newInstance("jdbc:sqlserver://B200603SV527\\SQLBCO2012;databaseName=BCO_CoreChannels_DESA",
                                        "consulta2","consulta2","com.microsoft.sqlserver.jdbc.SQLServerDriver")
                                        
        def updateSql = "UPDATE TPARAMETROS_CANALES SET VALOR1=VALOR1+1 WHERE IDPARAM='1' AND ESTADO='1'"
        def updateCount = sql.executeUpdate updateSql

        def row = sql.firstRow "SELECT VALOR1 P_SECUENCIA FROM TPARAMETROS_CANALES WHERE IDPARAM='1' AND ESTADO='1'"

		trace = row.P_SECUENCIA%1000000
        strTrace = sprintf('%06d', trace)
        
        println "trace: $strTrace"
        
    }
    
    void '00. test database connection'() {
        when:
            def val = sql.firstRow('SELECT 1')
        then:
            val[0] == 1
    }
	
	void '18. consulta de saldo'() {
        given:
			String request = builder.buildInputMessage(operation)
			
			IsoMessage iso = LectorBitmaps.generarTramaConsulta('4230400000000014',trace)
			String tramaIn = new String(iso.writeData());
			println tramaIn
			
			def slurper = new XmlSlurper().parseText(request)
			slurper.Body.sendReceiveMsg.arg0 = "PMPVG"
			//slurper.Body.sendReceiveMsg.arg1 = "0100623C460128E080001642304000000000143030001012232728007946113622121520126011051   06476134374890680004356394D21042011393157500000628623007946ATM01   CARD ACCEPTOR                                          840"
			slurper.Body.sendReceiveMsg.arg1 = tramaIn
			request = toPrettyXml(slurper)

			println request
			
			SoapClient client = SoapClient.builder()
				.endpointUri(service)
				.build();
        when:
			String response = client.post(request);
			def Messages = new XmlSlurper().parseText(response)
			String resp = Messages.Body.sendReceiveMsgResponse.return
			println resp
			
			IsoMessage trama = LectorBitmaps.getTrama(resp)
        then:
			response != null
			resp[0..3] == '0110'
			trama[2]?.value == '4230400000000014'
			trama[39].value == '00'
        
	}
	
	String toPrettyXml(def xml) {
        XmlUtil.serialize(new StreamingMarkupBuilder().bind { mkp.yield xml })
	}
}
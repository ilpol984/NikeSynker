package com.awsmithson.tcx2nikeplus.cli;

import com.awsmithson.tcx2nikeplus.http.NikePlus;
import com.awsmithson.tcx2nikeplus.nike.NikeActivityData;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;


public class Upload
{
	public static void main(String[] args) {
		String nikePlusEmail = args[0];
		String nikePlusPassword = args[1];
		File tcxFile = new File(args[2]);
		File gpxFile = (args.length > 3) ? new File(args[3]) : null;



		NikePlus np = new NikePlus();
		try {
			DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document runXML = documentBuilder.parse(tcxFile);
			Document gpxXML = (gpxFile == null) ? null : documentBuilder.parse(gpxFile);

			NikeActivityData nikeActivityData = new NikeActivityData(runXML, gpxXML);

			np.fullSync(nikePlusEmail, nikePlusPassword.toCharArray(), nikeActivityData);
		}
		catch (Throwable t) {
			t.printStackTrace();
		}
	}
}

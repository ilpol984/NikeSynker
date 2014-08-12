package com.ingsala.GarminNikeSyncronize;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.ingsala.tcx2nikeplus.convert.ConvertGpx;
import com.ingsala.tcx2nikeplus.convert.ConvertTcx;
import com.ingsala.tcx2nikeplus.garmin.GarminActivityData;
import com.ingsala.tcx2nikeplus.garmin.GarminDataType;
import com.ingsala.tcx2nikeplus.http.Garmin;
import com.ingsala.tcx2nikeplus.http.NikePlus;
import com.ingsala.tcx2nikeplus.nike.NikeActivityData;
import com.ingsala.tcx2nikeplus.util.Log;
import com.ingsala.tcx2nikeplus.util.Util;

import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.http.impl.client.CloseableHttpClient;
import org.w3c.dom.Document;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.TimeZone;
import java.util.Date;

public class ConvertCommandLine
{
private static final Properties nikePlusProperties = new Properties();
		PrintWriter out = null;
		JsonObject jout = null;
	static {
		try (InputStream inputStream = NikePlus.class.getResourceAsStream("/nikecredential.properties")) {
			nikePlusProperties.load(inputStream);
		}
		catch (IOException ioe) {
			throw new ExceptionInInitializerError(ioe);
		}
	}

	public static void main(String[] args) {
		if(args.length<1){System.out.println("Usage: COMANDO <GraminActivityID>"); System.exit(0);}
		System.out.println("Application Started!");
			Integer garminActivityId = Integer.parseInt(args[0]);
			String nikeEmail = nikePlusProperties.getProperty("USER");
			char[] nikePassword = nikePlusProperties.getProperty("PASSWORD").toCharArray();
			ConvertCommandLine ccl=new ConvertCommandLine();
			ccl.start(garminActivityId,nikeEmail,nikePassword);
	}

public void start(Integer garminActivityId,String nikeEmail,char[] nikePassword){
	System.out.println("Conversion Started");
try {


			TimeZone tz = TimeZone.getTimeZone("Europe/Rome");
			Integer clientTimeZoneOffset = tz.getOffset(new Date().getTime()) / 1000 / 60;
			jout = new JsonObject();

				List<GarminActivityData> garminActivitiesData = Lists.newArrayList();

				if (garminActivityId != null) {
					// If we have a garmin actvity ID, download the garmin tcx & gpx data and add to our garmin-activities list..
					System.out.println("Received convert-activity-id request, id: "+garminActivityId);
					try (CloseableHttpClient client = GarminDataType.getGarminHttpSession()) {
						garminActivitiesData.add(new GarminActivityData(
								Garmin.downloadGarminTcx(client, garminActivityId),
								Garmin.downloadGarminGpx(client, garminActivityId)));
					}
				}


				ConvertTcx convertTcx = new ConvertTcx();
				ConvertGpx convertGpx = new ConvertGpx();
				NikeActivityData[] nikeActivitiesData = new NikeActivityData[garminActivitiesData.size()];
				int i = 0;
				for (GarminActivityData garminActivityData : garminActivitiesData) {
					Document runXml = convertTcxDocument(convertTcx, garminActivityData.getTcxDocument(), clientTimeZoneOffset);
					Document gpxXml = (garminActivityData.getGpxDocument() == null)
							? null
							: convertGpxDocument(convertGpx, garminActivityData.getGpxDocument());
					nikeActivitiesData[i++] = new NikeActivityData(runXml, gpxXml);
				}

				// Upload to nikeplus.
				NikePlus u = new NikePlus();
				u.fullSync(nikeEmail, nikePassword, nikeActivitiesData);
				String message = "Conversion & Upload Successful.";
				succeed(out, jout, message, convertTcx.getTotalDurationMillis(), convertTcx.getTotalDistanceMetres());

			
		}
		catch (Throwable t) {
			String msg = ((t != null) && (t.getMessage() != null)) ? t.getMessage() : "Unknown error, please contact me and include details of tcx-file/garmin-activity-id.";
			t.printStackTrace();
			fail(out, jout, msg, t);
		}
		finally {
			
		}	


}


	/**
	 * Check if the DiskFileItem fieldName matches that of requiredFieldName and that the item value has non-zero length.
	 * @param item DiskFileItem to check.
	 * @param itemFieldName the field name of the DiskFileItem.
	 * @param requiredFieldName the field name we are searching for.
	 * @return
	 */
	private boolean haveFieldValue(DiskFileItem item, String itemFieldName, String requiredFieldName) {
		return ((itemFieldName.equals(requiredFieldName)) && (item.getString().length() > 0));
	}
	
	/**
	 * Split the string to obtain the activity-id in case the user
	 * enters the full url "http://connect.garmin.com/activity/23512599"
	 * instead of just the activityid "23512599".
	 * @param input
	 * @return
	 */
	private int getGarminActivityId(DiskFileItem input) {
		String[] split = input.getString().split("/");
		return Integer.parseInt(split[split.length-1]);
	}

	private Document convertTcxDocument(ConvertTcx c, Document garminTcxDocument, int clientTimeZoneOffset) throws Throwable {
		// Generate the nike+ gpx xml.
		Document doc = c.generateNikePlusXml(garminTcxDocument, "", clientTimeZoneOffset);
		System.out.println("Generated nike+ run xml, workout start time: "+ c.getStartTimeHumanReadable());
		return doc;
	}

	private Document convertGpxDocument(ConvertGpx c, Document garminGpxDocument) throws Throwable {
		// Generate the nike+ gpx xml.
		Document doc = c.generateNikePlusGpx(garminGpxDocument);
		System.out.println("Generated nike+ gpx xml.");
		return doc;
	}

	private void fail(PrintWriter out, JsonObject jout, String errorMessage, Throwable t) {
		System.out.println("Failing... Error message:"+ errorMessage);

		//errorMessage = String.format("Nike+ are making ongoing changes to their site which may affect the converter.  Please try again later - I am modifying the converter to keep up with the changes<br /><br />Error message: %s", errorMessage);
		//errorMessage = String.format("Nike+ have made changes which have broken the converter.  I need to make significant changes to the converter to make it work again and hope to fixed by Sunday 16th December.<br /><br />Error message: %s", errorMessage);
		//errorMessage = String.format("Nike+ have made changes to their website and the converter no longer works.  I am on vacation just now but please check back in early June (2014), hopefully I'll have had a chance to fix it by then.  Check the 'news' tab for updates.<br /><br />Error message: %s", errorMessage);
		//errorMessage = String.format("Error message: %s<br /><br />Please check the FAQ, if you can't find an answer there and your problem persists please contact me.", errorMessage);
		errorMessage = String.format("Error message:"+ errorMessage);

		// FIX-ME: Tidy this up!
		if (t != null) {
			if (t.getMessage() == null) System.out.println(t);
			else System.out.println(t.getMessage());
		}

		jout.addProperty("success", false);
		exit(out, jout, -1, errorMessage);
	}

	private void succeed(PrintWriter out, JsonObject jout, String message, long workoutDuration, double workoutDistance)  {
		System.out.println("success duration:"+ workoutDuration);
		System.out.println("success distance:"+  Math.round(workoutDistance));
		jout.addProperty("success", true);
		exit(out, jout, 0, message);
	}


	private void exit(PrintWriter out, JsonObject jout, int errorCode, String errorMessage)  {
		JsonObject data = new JsonObject();
		data.addProperty("errorCode", errorCode);
		data.addProperty("errorMessage", errorMessage);

		jout.add("data", data);

		//System.out.println( jout);
		System.out.println(jout);
	}




}

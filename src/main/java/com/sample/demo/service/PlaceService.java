package com.sample.demo.service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

import com.sample.demo.domain.Place;
import com.sample.demo.utility.PlaceConstants;

@Service
public class PlaceService implements IPlaceService{
	protected final Logger log = LoggerFactory.getLogger(this.getClass());
	
	/*The method finds the list of near-by places based on the address,
	 * it's latitude, it's longitude, radius of search
	 * @input String, double, double, int
	 * @return ArrayList<Place> 
	 * */
	public ArrayList<Place> search(String address, double latitude, double longitude, int radius) {
        ArrayList<Place> resultList = null;

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder placeSearchApi = new StringBuilder(PlaceConstants.PLACES_API_BASE);
            placeSearchApi.append(PlaceConstants.TYPE_SEARCH);
            placeSearchApi.append(PlaceConstants.OUT_JSON);
            placeSearchApi.append("?sensor=false");
            placeSearchApi.append("&key=" + PlaceConstants.API_KEY);
            placeSearchApi.append("&keyword=" + URLEncoder.encode(address, "utf8"));
            placeSearchApi.append("&location=" + String.valueOf(latitude) + "," + String.valueOf(longitude));
            placeSearchApi.append("&radius=" + String.valueOf(radius));

            URL url = new URL(placeSearchApi.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
        	log.error("Error processing Places API URL", e);
        } catch (IOException e) {
        	log.error("Error connecting to Places API", e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("results");

            // Extract the Place descriptions from the results
            resultList = new ArrayList<Place>(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                Place place = new Place();
                place.setReference(predsJsonArray.getJSONObject(i).getString("reference"));
                place.setName(predsJsonArray.getJSONObject(i).getString("name"));
                resultList.add(place);
            }
        } catch (JSONException e) {
            log.error("Error processing JSON results", e);
        }

        return resultList;
    }
	
	/*The method finds the latitude and longitude of a place based on it's address
	 * @input String
	 * @return Map<String, Double>
	 * @throws Exception 
	 * */
	public Map<String, Double> getLatLongPositions(String address) throws Exception
	  {
		try {
	    int responseCode = 0;
	    Map<String, Double> addressDet;
	    StringBuilder placeDetailApi = new StringBuilder(PlaceConstants.PLACES_DETAILS_API_BASE);
	    placeDetailApi.append(URLEncoder.encode(address, "UTF-8"));
	    placeDetailApi.append("?sensor=true");
	    
	    URL url = new URL(placeDetailApi.toString());
	    HttpURLConnection httpConnection = (HttpURLConnection)url.openConnection();
	    httpConnection.connect();
	    responseCode = httpConnection.getResponseCode();
	    if(responseCode == HttpStatus.OK.value())
	    {
	      DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();;
	      Document document = builder.parse(httpConnection.getInputStream());
	      XPathFactory xPathfactory = XPathFactory.newInstance();
	      XPath xpath = xPathfactory.newXPath();
	      XPathExpression expr = xpath.compile(PlaceConstants.GEOCODE_URI);
	      String status = (String)expr.evaluate(document, XPathConstants.STRING);
	      if(status.equals(HttpStatus.OK.toString()))
	      {
	         expr = xpath.compile(PlaceConstants.LAT_URI);
	         String latitude = (String)expr.evaluate(document, XPathConstants.STRING);
	         expr = xpath.compile(PlaceConstants.LONG_URI);
	         String longitude = (String)expr.evaluate(document, XPathConstants.STRING);
	         addressDet = new HashMap<>();
	         addressDet.put(PlaceConstants.LATITUDE, Double.valueOf(latitude));
	         addressDet.put(PlaceConstants.LONGITUDE, Double.valueOf(longitude));
	         return addressDet;
	      }
	      else
	      {
	         throw new Exception("Error while accessing the API - response status: "+status);
	      }
	     }
		}catch (MalformedURLException e) {
			log.error("Error processing Location Details API URL", e);
	    } catch (IOException e) {
	    	log.error("Error connecting to Location Details API", e);
	    }
	    return null;
	  }
}

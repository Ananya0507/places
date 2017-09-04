package com.sample.demo.web.api;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.sample.demo.domain.ErrorResponse;
import com.sample.demo.domain.Place;
import com.sample.demo.exception.SearchException;
import com.sample.demo.service.PlaceService;
import com.sample.demo.utility.PlaceConstants;

@RestController
@RequestMapping(value = "/v2/places")
public class PlaceController {
	protected final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
    private PlaceService placeService;
	
	@RequestMapping(value = "/search/{address}/{radiusOfSearch}",
            method = RequestMethod.GET,
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<Place>> searchNearbyPlaces(@PathVariable("address") String address, 
    		                                                 @PathVariable("radiusOfSearch") int radiusOfSearch) 
	                                                            throws SearchException{
		log.info("Search of places near :"+ address + " within miles: " + radiusOfSearch);
		Map<String, Double> addressDet;
		double longitude;
		double latitude;
		List<Place> listOfNearbyPlaces = null;
		try {
			if(!address.isEmpty()) {
				addressDet = placeService.getLatLongPositions(address);
				longitude = addressDet.get(PlaceConstants.LONGITUDE);
				latitude = addressDet.get(PlaceConstants.LATITUDE);
				
				listOfNearbyPlaces = placeService.search(address, latitude, longitude, radiusOfSearch);
			}else {
				throw new SearchException("The address field as input cannot be blank!");
			}
		}catch(Exception ex){
			log.error("Error occured in search of places: ", ex);
			return new ResponseEntity<List<Place>>(listOfNearbyPlaces, HttpStatus.EXPECTATION_FAILED);
			
		}
		 return new ResponseEntity<List<Place>>(listOfNearbyPlaces, HttpStatus.OK);
    }
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> exceptionHandler(Exception ex) {
	        ErrorResponse error = new ErrorResponse();
	        error.setErrorCode(HttpStatus.EXPECTATION_FAILED.value());
	        error.setMessage(ex.getMessage());
            return new ResponseEntity<ErrorResponse>(error, HttpStatus.OK);
	}
}

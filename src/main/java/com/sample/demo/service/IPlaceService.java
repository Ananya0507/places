package com.sample.demo.service;

import java.util.ArrayList;
import java.util.Map;

import com.sample.demo.domain.Place;

public interface IPlaceService {
	public ArrayList<Place> search(String address, double latitude, double longitude, int radius);
	public Map<String, Double> getLatLongPositions(String address) throws Exception;
}

package com.sample.demo;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import com.sample.demo.domain.Place;
import com.sample.demo.service.PlaceService;
import com.sample.demo.web.api.PlaceController;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = SampleSearchPlacesProjectApplication.class)
@Profile("test")
public class PlaceControllerIT {
	@InjectMocks
    PlaceController controller;
	
	@Mock
	PlaceService service;

    @Autowired
    WebApplicationContext context;

    private MockMvc mvc;
    
    @Before
    public void initTests() {
        MockitoAnnotations.initMocks(this);
        mvc = MockMvcBuilders.webAppContextSetup(context).build();
    }
    
    @Test
    public void testSearchNearbyPlaces() throws Exception {
    	String address = "Feltham";
    	double latitude = 51.4462;
    	double longitude = -0.41388;
    	int radius = 10;
        when(service.search(address, latitude, longitude, radius)).thenReturn(mockPlaceList());
        /*mvc.perform(get("/v2/places/search"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$[0].offerId", is(1)))
                .andExpect(jsonPath("$[0].description", is("If the bill is more than £9000, discount will be 10%")))
                .andExpect(jsonPath("$[1].offerId", is(2)))
                .andExpect(jsonPath("$[1].description", is("If the bill is more than £15000, discount will be 12%"))); */
        verify(service, times(1)).search(address, latitude, longitude, radius);
        verifyNoMoreInteractions(service);
    }
    
    private ArrayList<Place> mockPlaceList(){
    	ArrayList<Place> placeList = new ArrayList<>();
    	Place place1 = new Place();
    	place1.setName("Richmond");
    	place1.setReference("ABC");
    	Place place2 = new Place();
    	place1.setName("Putney");
    	place1.setReference("XYZ");
    	placeList.add(place1);
    	placeList.add(place2);
    	
    	return placeList;
    	
    }
}

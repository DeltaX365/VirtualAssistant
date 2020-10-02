package com.vnsolution.virtualassistant.service.stop_tracking;

import com.vnsolution.virtualassistant.model.Place;
import com.vnsolution.virtualassistant.model.PlaceType;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class StopTrackingManager  {
    @Inject
    public StopTrackingManager(){}

    public List<PlaceType> getPlaceTypes() {
        List<PlaceType> placeTypes = new ArrayList<>();

        placeTypes.add(new PlaceType("accounting", "Accounting"));
        placeTypes.add(new PlaceType("airport", "Airport"));
        placeTypes.add(new PlaceType("amusement_park", "Amusement Park"));
        placeTypes.add(new PlaceType("aquarium", "Aquarium"));
        placeTypes.add(new PlaceType("art_gallery", "Art Gallery"));
        placeTypes.add(new PlaceType("atm", "Atm"));
        placeTypes.add(new PlaceType("bakery", "Bakery"));
        placeTypes.add(new PlaceType("bank", "Bank"));
        placeTypes.add(new PlaceType("bar", "Bar"));
        placeTypes.add(new PlaceType("beauty_salon", "Beauty Salon"));
        placeTypes.add(new PlaceType("bicycle_store", "Bicycle Store"));
        placeTypes.add(new PlaceType("book_store", "Book Store"));
        placeTypes.add(new PlaceType("bowling_alley", "Bowling Alley"));
        placeTypes.add(new PlaceType("bus_station", "Bus Station"));
        placeTypes.add(new PlaceType("cafe", "Cafe"));
        placeTypes.add(new PlaceType("campground", "Campground"));
        placeTypes.add(new PlaceType("car_dealer", "Car Dealer"));
        placeTypes.add(new PlaceType("car_rental", "Car Rental"));
        placeTypes.add(new PlaceType("car_repair", "Car Repair"));
        placeTypes.add(new PlaceType("car_wash", "Car Wash"));
        placeTypes.add(new PlaceType("casino", "Casino"));
        placeTypes.add(new PlaceType("cemetery", "Cemetery"));
        placeTypes.add(new PlaceType("church", "Church"));
        placeTypes.add(new PlaceType("city_hall", "City Hall"));
        placeTypes.add(new PlaceType("clothing_store", "Clothing Store"));
        placeTypes.add(new PlaceType("convenience_store", "Convenience Store"));
        placeTypes.add(new PlaceType("courthouse", "Courthouse"));
        placeTypes.add(new PlaceType("dentist", "Dentist"));
        placeTypes.add(new PlaceType("department_store", "Department Store"));
        placeTypes.add(new PlaceType("doctor", "Doctor"));
        placeTypes.add(new PlaceType("drugstore", "Drugstore"));
        placeTypes.add(new PlaceType("electrician", "Electrician"));
        placeTypes.add(new PlaceType("electronics_store", "Electronics Store"));
        placeTypes.add(new PlaceType("embassy", "Embassy"));
        placeTypes.add(new PlaceType("fire_station", "Fire Station"));
        placeTypes.add(new PlaceType("florist", "Florist"));
        placeTypes.add(new PlaceType("funeral_home", "Funeral Home"));
        placeTypes.add(new PlaceType("furniture_store", "Furniture Store"));
        placeTypes.add(new PlaceType("gas_station", "Gas Station"));
        placeTypes.add(new PlaceType("gym", "Gym"));
        placeTypes.add(new PlaceType("hair_care", "Hair Care"));
        placeTypes.add(new PlaceType("hardware_store", "Hardware Store"));
        placeTypes.add(new PlaceType("hindu_temple", "Hindu Temple"));
        placeTypes.add(new PlaceType("home_goods_store", "Home Goods Store"));
        placeTypes.add(new PlaceType("hospital", "Hospital"));
        placeTypes.add(new PlaceType("insurance_agency", "Insurance Agency"));
        placeTypes.add(new PlaceType("jewelry_store", "Jewelry Store"));
        placeTypes.add(new PlaceType("lawyer", "Lawyer"));
        placeTypes.add(new PlaceType("library", "Library"));
        placeTypes.add(new PlaceType("light_rail_station", "Light Rail Station"));
        placeTypes.add(new PlaceType("liquor_store", "Liquor Store"));
        placeTypes.add(new PlaceType("local_government_office", "Local Government Office"));
        placeTypes.add(new PlaceType("locksmith", "Locksmith"));
        placeTypes.add(new PlaceType("lodging", "Lodging"));
        placeTypes.add(new PlaceType("meal_delivery", "Meal Delivery"));
        placeTypes.add(new PlaceType("meal_takeaway", "Meal Takeaway"));
        placeTypes.add(new PlaceType("mosque", "Mosque"));
        placeTypes.add(new PlaceType("movie_rental", "Movie Rental"));
        placeTypes.add(new PlaceType("movie_theater", "Movie Theater"));
        placeTypes.add(new PlaceType("moving_company", "Moving Company"));
        placeTypes.add(new PlaceType("museum", "Museum"));
        placeTypes.add(new PlaceType("night_club", "Night Club"));
        placeTypes.add(new PlaceType("painter", "Painter"));
        placeTypes.add(new PlaceType("park", "Park"));
        placeTypes.add(new PlaceType("parking", "Parking"));
        placeTypes.add(new PlaceType("pet_store", "Pet Store"));
        placeTypes.add(new PlaceType("pharmacy", "Pharmacy"));
        placeTypes.add(new PlaceType("physiotherapist", "Physiotherapist"));
        placeTypes.add(new PlaceType("plumber", "Plumber"));
        placeTypes.add(new PlaceType("police", "Police"));
        placeTypes.add(new PlaceType("post_office", "Post Office"));
        placeTypes.add(new PlaceType("primary_school", "Primary School"));
        placeTypes.add(new PlaceType("real_estate_agency", "Real Estate Agency"));
        placeTypes.add(new PlaceType("restaurant", "Restaurant"));
        placeTypes.add(new PlaceType("roofing_contractor", "Roofing Contractor"));
        placeTypes.add(new PlaceType("rv_park", "Rv Park"));
        placeTypes.add(new PlaceType("school", "School"));
        placeTypes.add(new PlaceType("secondary_school", "Secondary School"));
        placeTypes.add(new PlaceType("shoe_store", "Shoe Store"));
        placeTypes.add(new PlaceType("shopping_mall", "Shopping Mall"));
        placeTypes.add(new PlaceType("spa", "Spa"));
        placeTypes.add(new PlaceType("stadium", "Stadium"));
        placeTypes.add(new PlaceType("storage", "Storage"));
        placeTypes.add(new PlaceType("store", "Store"));
        placeTypes.add(new PlaceType("subway_station", "Subway Station"));
        placeTypes.add(new PlaceType("supermarket", "Supermarket"));
        placeTypes.add(new PlaceType("synagogue", "Synagogue"));
        placeTypes.add(new PlaceType("taxi_stand", "Taxi Stand"));
        placeTypes.add(new PlaceType("tourist_attraction", "Tourist Attraction"));
        placeTypes.add(new PlaceType("train_station", "Train Station"));
        placeTypes.add(new PlaceType("transit_station", "Transit Station"));
        placeTypes.add(new PlaceType("travel_agency", "Travel Agency"));
        placeTypes.add(new PlaceType("university", "University"));
        placeTypes.add(new PlaceType("veterinary_care", "Veterinary Care"));
        placeTypes.add(new PlaceType("zoo", "Zoo"));

        return placeTypes;
    }

    public void getTotalVisitedPlaces() {}

    public void getTop10RecentVisitedPlaces() {}

    public void getTop10MostVisitedPlaces() {}

    public void getTop10FavouriteVisitedPlaces() {}

    public void getTop10UnvisitedPlaces() {}

    public void favouritePlace(Place place) {}

    public void deletePlace(Place place) {}

    public void categorizePlace(Place place) {}

    public void ratePlace(Place place) {}
}

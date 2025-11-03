package org.example.neighborvehicle.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.neighborvehicle.models.Listing;
import org.example.neighborvehicle.models.Result;
import org.example.neighborvehicle.models.VehicleRequest;
import org.example.neighborvehicle.models.VehicleResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;


@RestController
public class VehicleController {

    @PostMapping("/")
    public String handlePost(@RequestBody List<VehicleRequest> requests) throws IOException {

        //Read in listings from the file and make it a list to read from. Sorted it by so listings with same locationIds
        //are together and lowest price first in that location
        ObjectMapper mapper = new ObjectMapper();
        InputStream is = getClass().getResourceAsStream("/listings.json");
        List<Listing> listings = mapper.readValue(is, new TypeReference<List<Listing>>() {});
        listings.sort(Comparator.comparing(Listing::getLocationId).thenComparingInt(Listing::getPrice));

        //Initialize result list to for response
        List<VehicleResponse> results = new ArrayList<>();


        int i =0;
        while(i < listings.size()) {
            //Group together listings with same locationId in order to find if the listings can satisfy the request and
            //later for finding least expensive for each locationId
            String currentLocation = listings.get(i).getLocationId();
            List<Listing> sameLocationListings = new ArrayList<>();

            while(i < listings.size() && listings.get(i).getLocationId().equals(currentLocation)) {
                sameLocationListings.add(listings.get(i));
                i++;
            }

            //Mapping length of car(s) to quantity to use in calculating if listings satisfy the request
            Map<Integer, Integer> remaining = new HashMap<>();
            for(VehicleRequest req : requests) {
                remaining.put(req.getLength(), req.getQuantity());
            }

            //Initialize bestResult to store best combination (if there is one) that satisfies the request
            Result bestResult = new Result();

            //Recursive function call on the same locationId listings to store in bestResult for best price
            bestPriceCombination(sameLocationListings, 0, remaining, new ArrayList<>(), 0, bestResult);


            //If there is a bestResult, add it to our results
            if(!bestResult.listingIds.isEmpty()) {
                results.add(new VehicleResponse(currentLocation, bestResult.listingIds, bestResult.totalPrice));
            }


        }

        //Sort results with the cheapest price on the top
        results.sort(Comparator.comparingInt((VehicleResponse::getTotal_price_in_cents)));

        //Return JSON results
        return mapper.writeValueAsString(results);



    }


    //Recursive function to find cheapest id(s) at that location that can satisfy the request, returns empty if none
    void bestPriceCombination(List<Listing> listings, int index, Map<Integer, Integer> remaining, List<String> currentIds, int currentTotal, Result bestResult) {
        //Check if every value in our map has been reduced to 0, meaning all the requests have been satisfied
        if(remaining.values().stream().allMatch(v -> v == 0)) {
            //If our total we are calculating is cheaper than our current stored best price, store it as bestResult for
            //this list
            if(currentTotal < bestResult.totalPrice) {
                bestResult.totalPrice = currentTotal;
                bestResult.listingIds = new ArrayList<>(currentIds);
            }

            return;
        }

        //If we've gone through every iteration, stop
        if (index >= listings.size()) {
            return;
        }

        //Get current listing and calculate what cars it can hold
        Listing listing = listings.get(index);

        Map<Integer, Integer> newRemaining = new HashMap<>(remaining);
        boolean used = false;

        for (var req : newRemaining.entrySet()) {
            int carLength = req.getKey();
            int needed = req.getValue();

            //If we still have more cars to place and there is room to place it at the listing, calculate how many can fit
            if(needed > 0 && listing.getLength() >= carLength && listing.getWidth() >= 10) {
                int fitLength = listing.getLength() / carLength;
                int fitWidth = listing.getWidth() / 10;
                int canFit = fitLength * fitWidth;

                //Calculate how many were actually placed and update our map of how many are still needed
                int placed = Math.min(needed, canFit);
                newRemaining.put(carLength, needed-placed);

                //If we placed any at the listing, we mark used as true to later add it to a list of used ids
                if (placed > 0) used = true;
            }
        }

        //If we placed any cars at this listing, we put it into the list of ids and recursively calculate the best price
        //for this iteration starting at the specific index. Recursively remove listings to restart
        if (used) {
            currentIds.add(listing.getId());
            bestPriceCombination(listings, index+ 1, newRemaining, currentIds, currentTotal + listing.getPrice(), bestResult);
            currentIds.remove(currentIds.size() -1);
        }

        //Recursive call on next index to see if we started at the next location if we get a cheaper price
        bestPriceCombination(listings, index + 1, remaining, currentIds, currentTotal, bestResult);
    }
}

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
//        for(VehicleRequest req : requests) {
//            System.out.println("Length: " + req.getLength() + " Quantity: " + req.getQuantity());
//        }

        ObjectMapper mapper = new ObjectMapper();
        InputStream is = getClass().getResourceAsStream("/listings.json");
        List<Listing> listings = mapper.readValue(is, new TypeReference<List<Listing>>() {});

        listings.sort(Comparator.comparing(Listing::getLocationId).thenComparingInt(Listing::getPrice));
        List<VehicleResponse> results = new ArrayList<>();

        //System.out.println("Loaded " + listings.size() + " listings");

        int i =0;
        while(i < listings.size()) {
            String currentLocation = listings.get(i).getLocationId();
            List<Listing> sameLocationListings = new ArrayList<>();

            while(i < listings.size() && listings.get(i).getLocationId().equals(currentLocation)) {
                sameLocationListings.add(listings.get(i));
                i++;
            }

            Map<Integer, Integer> remaining = new HashMap<>();
            for(VehicleRequest req : requests) {
                remaining.put(req.getLength(), req.getQuantity());
            }

            Result bestResult = new Result();
            bestPriceCombination(sameLocationListings, 0, remaining, new ArrayList<>(), 0, bestResult);

            if(!bestResult.listingIds.isEmpty()) {
                results.add(new VehicleResponse(currentLocation, bestResult.listingIds, bestResult.totalPrice));
            }


        }

        results.sort(Comparator.comparingInt((VehicleResponse::getTotal_price_in_cents)));

//        for (VehicleResponse response : results) {
//            System.out.println(
//                    "Location: " + response.getLocation_id() +
//                            " | Listings: " + response.getListing_ids() +
//                            " | Total Price (¢): " + response.getTotal_price_in_cents()
//            );
//        }

        //System.out.println("Got " + results.size() + " results");

        //return "Recieved " + requests.size() + " vehicle requests";
        return mapper.writeValueAsString(results);



    }


    void bestPriceCombination(List<Listing> listings, int index, Map<Integer, Integer> remaining, List<String> currentIds, int currentTotal, Result bestResult) {
        if(remaining.values().stream().allMatch(v -> v == 0)) {
            if(currentTotal < bestResult.totalPrice) {
                bestResult.totalPrice = currentTotal;
                bestResult.listingIds = new ArrayList<>(currentIds);

            }

            return;
        }

        if (index >= listings.size()) {
            return;
        }

        Listing listing = listings.get(index);

        Map<Integer, Integer> newRemaining = new HashMap<>(remaining);
        boolean used = false;

        for (var req : newRemaining.entrySet()) {
            int carLength = req.getKey();
            int needed = req.getValue();

            if(needed > 0 && listing.getLength() >= carLength && listing.getWidth() >= 10) {
                int fitLength = listing.getLength() / carLength;
                int fitWidth = listing.getWidth() / 10;
                int canFit = fitLength * fitWidth;

                int placed = Math.min(needed, canFit);
                newRemaining.put(carLength, needed-placed);
                if (placed > 0) used = true;
            }
        }

        if (used) {
            currentIds.add(listing.getId());
            bestPriceCombination(listings, index+ 1, newRemaining, currentIds, currentTotal + listing.getPrice(), bestResult);
            currentIds.remove(currentIds.size() -1);
        }

        bestPriceCombination(listings, index + 1, remaining, currentIds, currentTotal, bestResult);
    }
}

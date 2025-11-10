## Overview

This API receives a list of vehicles, each defined by their `length` and `quantity`, and searches through a set of listings to determine:

- Which **locations** can store all the requested vehicles  
- The **cheapest combination** of listings for each location  
- One result per `location_id`, sorted by total price in ascending order  

## How It Works

1. **Input**  
   The request provides an array of vehicles, each specifying:
   - `length`: length of the vehicle in feet  
   - `quantity`: number of vehicles of that size  

2. **Processing**  
   - Parses all listings from `listings.json`  
   - Groups listings by `location_id`  
   - Filters out locations that cannot fit all requested vehicles  
   - Evaluates all possible listing combinations for each valid location  
   - Selects the **cheapest combination** capable of storing all vehicles using recursion  

3. **Output**  
   Returns a list of locations, each containing:
   - `location_id`  
   - `listing_ids` that hold all vehicles  
   - `total_price_in_cents`, sorted ascending  

## Key Features

- Finds all valid locations that can store multiple vehicles in a grid-like order without buffers
- Selects the most cost-efficient combinations automatically   
- Simple, well-structured Spring Boot API design  
- Fast response times (depending on server/API startup)
- Clear and minimal JSON request/response format  

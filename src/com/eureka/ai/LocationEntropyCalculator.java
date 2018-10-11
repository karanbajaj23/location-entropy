package com.eureka.ai;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;

import static java.lang.Math.log;

class UserVisitCountMap extends HashMap<Integer, Integer> {}
class LocationVisitMap extends HashMap<Integer, UserVisitCountMap>{}

public class LocationEntropyCalculator {

    private static boolean testing = true;

    private static double calculateLocationEntropy(UserVisitCountMap userVisitCountMap) {
        double entropy = 0.0;
        Integer totalVisits = userVisitCountMap.values().stream().mapToInt(Number::intValue).sum();
        double userProb;
        for(Integer userId : userVisitCountMap.keySet()) {
            userProb = userVisitCountMap.get(userId) / (double)totalVisits;
            entropy += userProb*log(userProb)/log(2);
        }
        return (-1)*entropy;
    }

    public static void main(String[] args) {
        try {
            File file;
            if(testing) {
                file = new File("/Users/karanbajaj/Git/location-entropy/src/com/eureka/ai/test.txt");
            }
            else    {
                file = new File("/Users/karanbajaj/Git/location-entropy/src/com/eureka/ai/loc-gowalla_totalCheckins.txt");
            }
            BufferedReader br = new BufferedReader(new FileReader(file));

            String line;
            int recordSize;
            // Maintaining a map of location to userVisits
            LocationVisitMap locationVisitMap = new LocationVisitMap();

            while ((line = br.readLine()) != null)    {
                String words[];
                words = line.split("\\s+");
                recordSize = words.length;

                // Accept complete record lines only
                if((testing && recordSize == 3) || recordSize == 5) {

                    Integer userId = Integer.parseInt(words[0]);
                    Integer locationId = Integer.parseInt(words[recordSize-1]);

                    // Get Users' visit count map for the location
                    UserVisitCountMap userVisitCountMap;
                    if(!locationVisitMap.containsKey(locationId))
                        userVisitCountMap = new UserVisitCountMap();
                    else
                        userVisitCountMap = locationVisitMap.get(locationId);

                    // Update User's visit count
                    Integer userVisitCount;
                    if(!userVisitCountMap.containsKey(userId))
                        userVisitCount = 0;
                    else
                        userVisitCount = userVisitCountMap.get(userId);
                    userVisitCountMap.put(userId, userVisitCount+1);

                    // Update the visit count map
                    locationVisitMap.put(locationId, userVisitCountMap);
                }
            }

            System.out.println("LocationID\tLocation Entropy");
            for(Integer locationId : locationVisitMap.keySet())   {
                System.out.println(locationId+"\t\t"+calculateLocationEntropy(locationVisitMap.get(locationId)));
            }

        }
        catch(Exception e)  {
            e.printStackTrace();
        }
    }
}
package com.ufund.api.ufundapi.persistence;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Value;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ufund.api.ufundapi.model.Need;

public class ManagerFileDAO implements ManagerDAO{
    Map<Integer, Need> needs;   // Provides a local cache of the need objects
    // so that we don't need to read from the file
    // each time
    private ObjectMapper objectMapper;  // Provides conversion between Need
    // objects and JSON text format written
    // to the file
    private static int nextId;  // The next Id to assign to a new Need
    private String filename;    // Filename to read from and write to

    public ManagerFileDAO(@Value("${needs.file}") String filename, ObjectMapper objectMapper) throws IOException {
        this.filename = filename;
        this.objectMapper = objectMapper;
        load();  // load the needs from the file
    }

    private boolean load() throws IOException {
        needs = new TreeMap<>();
        nextId = 0;

        // Deserializes the JSON objects from the file into an array of heroes
        // readValue will throw an IOException if there's an issue with the file
        // or reading from the file
        Need[] needArray = objectMapper.readValue(new File(filename), Need[].class);

        // Add each hero to the tree map and keep track of the greatest id
        for (Need need : needArray) {
            needs.put(need.getId(), need);
            if (need.getId() > nextId) {
                nextId = need.getId();
            }
        }
        // Make the next id one greater than the maximum from the file
        ++nextId;
        return true;
    }

    /**
     * Saves the @Need from the map into the file as an array
     * of JSON objects
     *
     * @return true if the @Needs were written successfully
     *
     * @throws IOException when file cannot be accessed or written to
     */


    @Override
    public Need viewDetails(int needID) throws IOException {
        synchronized (needs) {
            if (needs.containsKey(needID)) {
                return needs.get(needID);
            } else {
                return null;
            }
        }
    }

    /**
     * Creates a Need File Data Access Object
     *
     * @param filename Filename to read from and write to
     * @param objectMapper Provides JSON Object to/from Java Object
     * serialization and deserialization
     *
     * @throws IOException when file cannot be accessed or read from
     */
}

package com.ufund.api.ufundapi.persistence;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ufund.api.ufundapi.model.Need;

/**
 * Cupboard Data Access Object, interacts with storage to get Need objects
 * 
 * @author Zach Coy
 */
@Component
public class CupboardFileDAO implements CupboardDAO {
    Map<Integer, Need> needs; // Provides a local cache of the need objects
    // so that we don't need to read from the file
    // each time
    private ObjectMapper objectMapper; // Provides conversion between Need
    // objects and JSON text format written
    // to the file
    private static int nextId; // The next Id to assign to a new Need
    private String filename; // Filename to read from and write to

    /**
     * Creates a Need File Data Access Object
     *
     * @param filename     Filename to read from and write to
     * @param objectMapper Provides JSON Object to/from Java Object
     *                     serialization and deserialization
     *
     * @throws IOException when file cannot be accessed or read from
     */
    public CupboardFileDAO(@Value("${needs.file}") String filename, ObjectMapper objectMapper) throws IOException {
        this.filename = filename;
        this.objectMapper = objectMapper;
        load(); // load the needs from the file
    }

    /**
     * Generates the next id for a new {@link Need need}
     * 
     * @return The next id
     */
    private synchronized static int nextId() {
        int id = nextId;
        ++nextId;
        return id;
    }

    /**
     * Generates an array of {@link Need needs} from the tree map
     *
     * @return The array of {@link Need needs}, may be empty
     */
    @Override
    public Need[] getNeeds() {
        return getNeeds(null);
    }

    /**
     * Generates an array of {@link Need needs} from the tree map
     * 
     * @param containsText filter text, if null then no filter
     * @return The array of {@link Need needs}, may be empty
     */
    public Need[] getNeeds(String containsText) { // if containsText == null, no filter
        ArrayList<Need> needArrayList = new ArrayList<>();

        for (Need need : needs.values()) {
            if (containsText == null || need.getName().contains(containsText)) {
                needArrayList.add(need);
            }
        }

        Need[] needArray = new Need[needArrayList.size()];
        needArrayList.toArray(needArray);
        return needArray;
    }

    /**
     * Saves the {@link Need needs} from the map into the file as an array
     * of JSON objects
     *
     * @return true if the {@link Need needs} were written successfully
     *
     * @throws IOException when file cannot be accessed or written to
     */
    private boolean save() throws IOException {
        Need[] needArray = getNeeds();

        // Serializes the Java Objects to JSON objects into the file
        // writeValue will thrown an IOException if there is an issue
        // with the file or reading from the file
        objectMapper.writeValue(new File(filename), needArray);
        return true;
    }

    /**
     * Loads {@link Need needs} from the JSON file into the map
     * Also sets next id to one more than the greatest id found in the file
     *
     * @return true if the file was read successfully
     *
     * @throws IOException when file cannot be accessed or read from
     */
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
     ** {@inheritDoc}
     */
    @Override
    public Need[] searchNeeds(String containsText) {
        synchronized (needs) {
            return getNeeds(containsText);
        }
    }

    /**
     ** {@inheritDoc}
     */
    @Override
    public Need getNeed(int id) {
        synchronized (needs) {
            if (needs.containsKey(id)) {
                return needs.get(id);
            } else {
                return null;
            }
        }
    }

    /**
     ** {@inheritDoc}
     */
    @Override
    public Need createNeed(Need need) throws IOException {
        synchronized (needs) {
            // We create a new need object because the id field is immutable
            // and we need to assign the next unique id
            Need newNeed = new Need(need.getName(), need.getId(), need.getDescription());
            needs.put(newNeed.getId(), newNeed);
            save(); // may throw an IOException
            return newNeed;
        }
    }

    /**
     ** {@inheritDoc}
     */
    @Override
    public Need updateNeed(Need need) throws IOException {
        synchronized (needs) {
            if (needs.containsKey(need.getId()) == false) {
                return null; // need does not exist
            }
            needs.put(need.getId(), need);
            save(); // may throw an IOException
            return need;
        }
    }

    /**
     ** {@inheritDoc}
     */
    @Override
    public boolean deleteNeed(int id) throws IOException {
        synchronized (needs) {
            if (needs.containsKey(id)) {
                needs.remove(id);
                return save();
            } else {
                return false;
            }
        }
    }
}

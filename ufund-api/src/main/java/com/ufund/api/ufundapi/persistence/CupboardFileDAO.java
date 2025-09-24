package com.ufund.api.ufundapi.persistence;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Value;
import org.yaml.snakeyaml.internal.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ufund.api.ufundapi.model.Need;

public class CupboardFileDAO implements CupboardDAO {
    
private static final Logger LOG = Logger.getLogger(CupboardFileDAO.class.getName());
    Map<Integer,Need> needs;   // Provides a local cache of the hero objects
                                // so that we don't need to read from the file
                                // each time
    private ObjectMapper objectMapper;  // Provides conversion between Hero
                                        // objects and JSON text format written
                                        // to the file
    private static int nextId;  // The next Id to assign to a new hero
    private String filename;    // Filename to read from and write to

    /**
     * Creates a Hero File Data Access Object
     * 
     * @param filename Filename to read from and write to
     * @param objectMapper Provides JSON Object to/from Java Object serialization and deserialization
     * 
     * @throws IOException when file cannot be accessed or read from
     */
    public CupboardFileDAO(@Value("${needs.file}") String filename,ObjectMapper objectMapper) throws IOException {
        this.filename = filename;
        this.objectMapper = objectMapper;
        load();  // load the heroes from the file
    }

    /**
     * Generates the next id for a new {@linkplain Hero hero}
     * 
     * @return The next id
     */
    private synchronized static int nextId() {
        int id = nextId;
        ++nextId;
        return id;
    }

    /**
     * Generates an array of {@linkplain Hero heroes} from the tree map
     * 
     * @return  The array of {@link Hero heroes}, may be empty
     */
    public Need[] getNeeds() {
        return getNeeds(null);
    }

    /**
     * Generates an array of {@linkplain Hero heroes} from the tree map for any
     * {@linkplain Hero heroes} that contains the text specified by containsText
     * <br>
     * If containsText is null, the array contains all of the {@linkplain Hero heroes}
     * in the tree map
     * 
     * @return  The array of {@link Hero heroes}, may be empty
     */
    public Need[] getNeeds(String containsText) { // if containsText == null, no filter
        ArrayList<Need> needArrayList = new ArrayList<>();

        for (Need need : needs.values()) {
            if (containsText == null || need.getName().contains(containsText)) {
                needArrayList.add(need);
            }
        }

        Need[] heroArray = new Need[needArrayList.size()];
        needArrayList.toArray(heroArray);
        return heroArray;
    }

    /**
     * Saves the {@linkplain Hero heroes} from the map into the file as an array of JSON objects
     * 
     * @return true if the {@link Hero heroes} were written successfully
     * 
     * @throws IOException when file cannot be accessed or written to
     */
    private boolean save() throws IOException {
        Need[] needArray = getNeeds();

        // Serializes the Java Objects to JSON objects into the file
        // writeValue will thrown an IOException if there is an issue
        // with the file or reading from the file
        objectMapper.writeValue(new File(filename),needArray);
        return true;
    }

    /**
     * Loads {@linkplain Hero heroes} from the JSON file into the map
     * <br>
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
        Hero[] heroArray = objectMapper.readValue(new File(filename),Hero[].class);

        // Add each hero to the tree map and keep track of the greatest id
        for (Hero hero : heroArray) {
            heroes.put(hero.getId(),hero);
            if (hero.getId() > nextId)
                nextId = hero.getId();
        }
        // Make the next id one greater than the maximum from the file
        ++nextId;
        return true;
    }

    /**
    ** {@inheritDoc}
     */
    @Override
    public Hero[] getHeroes() {
        synchronized(heroes) {
            return getHeroesArray();
        }
    }

    /**
    ** {@inheritDoc}
     */
    @Override
    public Hero[] findHeroes(String containsText) {
        synchronized(heroes) {
            return getHeroesArray(containsText);
        }
    }

    /**
    ** {@inheritDoc}
     */
    @Override
    public Hero getHero(int id) {
        synchronized(heroes) {
            if (heroes.containsKey(id))
                return heroes.get(id);
            else
                return null;
        }
    }

    /**
    ** {@inheritDoc}
     */
    @Override
    public Hero createHero(Hero hero) throws IOException {
        synchronized(heroes) {
            // We create a new hero object because the id field is immutable
            // and we need to assign the next unique id
            Hero newHero = new Hero(nextId(),hero.getName());
            heroes.put(newHero.getId(),newHero);
            save(); // may throw an IOException
            return newHero;
        }
    }

    /**
    ** {@inheritDoc}
     */
    @Override
    public Hero updateHero(Hero hero) throws IOException {
        synchronized(heroes) {
            if (heroes.containsKey(hero.getId()) == false)
                return null;  // hero does not exist

            heroes.put(hero.getId(),hero);
            save(); // may throw an IOException
            return hero;
        }
    }

    /**
    ** {@inheritDoc}
     */
    @Override
    public boolean deleteHero(int id) throws IOException {
        synchronized(heroes) {
            if (heroes.containsKey(id)) {
                heroes.remove(id);
                return save();
            }
            else
                return false;
        }
    }
}

package com.ufund.api.ufundapi.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ufund.api.ufundapi.model.CompletedNeed;
import com.ufund.api.ufundapi.model.Need;
import com.ufund.api.ufundapi.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class CompletedNeedFileDAO implements CompletedNeedDAO {

    private ArrayList<CompletedNeed> completedNeeds; // Provides a local cache of the need objects
    // so that we don't need to read from the file
    // each time
    private ObjectMapper objectMapper; // Provides conversion between Need
    // objects and JSON text format written
    // to the file
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
    public CompletedNeedFileDAO(@Value("${completed_needs.file}") String filename, ObjectMapper objectMapper)
            throws IOException {
        this.filename = filename;
        this.objectMapper = objectMapper;
        load(); // load the needs from the file
    }

    /**
     * {@inheritdoc}
     */
    @Override
    public CompletedNeed[] getRecentNeeds(int count, int offset) {
        if (offset >= completedNeeds.size()) {
            return new CompletedNeed[0];
        }
        if ((offset + count) >= completedNeeds.size()) {
            count = completedNeeds.size() - offset;
        }
        List<CompletedNeed> needArrayList = completedNeeds.subList(offset, offset + count);
        CompletedNeed[] needArray = new CompletedNeed[needArrayList.size()];
        needArrayList.toArray(needArray);
        return needArray;
    }

    /**
     * {@inheritdoc}
     */
    @Override
    public CompletedNeed[] getRecentNeeds() {
        CompletedNeed[] items = new CompletedNeed[completedNeeds.size()];
        completedNeeds.toArray(items);
        return items;
    }

    /**
     * {@inheritdoc}
     */
    @Override
    public void completeNeed(Need need, User user) throws IOException {
        CompletedNeed completed = new CompletedNeed(need, user.getId(), user.getUsername(),
                Instant.now().getEpochSecond());
        completedNeeds.add(0, completed);
        save();
    }

    /**
     * Saves the {@link CompletedNeed completed needs} from the map into the file as
     * an array
     * of JSON objects
     *
     * @return true if the {@link CompletedNeed needs} were written successfully
     *
     * @throws IOException when file cannot be accessed or written to
     */
    private boolean save() throws IOException {
        CompletedNeed[] needArray = getRecentNeeds();

        // Serializes the Java Objects to JSON objects into the file
        // writeValue will throw an IOException if there is an issue
        // with the file or reading from the file
        objectMapper.writeValue(new File(filename), needArray);
        return true;
    }

    /**
     * Loads {@link CompletedNeed completed needs} from the JSON file into the map
     *
     * @return true if the file was read successfully
     *
     * @throws IOException when file cannot be accessed or read from
     */
    private boolean load() throws IOException {
        this.completedNeeds = new ArrayList<>();
        CompletedNeed[] needArray = objectMapper.readValue(new File(filename), CompletedNeed[].class);
        completedNeeds.addAll(Arrays.asList(needArray));
        return true;
    }

    /**
     * {@inheritdoc}
     */
    public int[] getNumbers() throws IOException {
        int[] nums = new int[4];

        long yearAgo = Instant.now().minus(365, ChronoUnit.DAYS).getEpochSecond();
        long monthAgo = Instant.now().minus(30, ChronoUnit.DAYS).getEpochSecond();
        long weekAgo = Instant.now().minus(7, ChronoUnit.DAYS).getEpochSecond();
        long yesterday = Instant.now().minus(1, ChronoUnit.DAYS).getEpochSecond();

        for (CompletedNeed cn : completedNeeds) {
            if (cn.getTimestamp() >= yearAgo) {
                nums[3]++;
                if (cn.getTimestamp() >= monthAgo) {
                    nums[2]++;
                    if (cn.getTimestamp() >= weekAgo) {
                        nums[1]++;
                        if (cn.getTimestamp() >= yesterday) {
                            nums[0]++;
                        }
                    }
                }
            }
        }
        return nums;
    }
}

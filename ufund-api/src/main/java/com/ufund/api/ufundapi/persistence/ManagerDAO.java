package com.ufund.api.ufundapi.persistence;

import java.io.IOException;

import com.ufund.api.ufundapi.model.Need;

public interface ManagerDAO {

    Need viewDetails(int needID) throws IOException;


}

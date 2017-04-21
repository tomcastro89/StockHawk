package com.udacity.stockhawk.sync;

/**
 * Created by Tcastrovillari on 21.04.2017.
 */

public interface AsyncResponse {
    void processFinish(Boolean isValid);
}
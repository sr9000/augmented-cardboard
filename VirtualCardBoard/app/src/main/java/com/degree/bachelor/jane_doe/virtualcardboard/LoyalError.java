package com.degree.bachelor.jane_doe.virtualcardboard;

import com.degree.bachelor.jane_doe.virtualcardboard.information.InfoException;

/**
 * Created by Jane-Doe on 5/23/2016.
 * Throw Manual Exception with specific error message
 * only if possible number of errors are happened
 */
public class LoyalError {
    private static final String _default = "Be sure that many bad things happened @_@";
    private int _counter;
    private int _max_value;
    private String _msg;

    private volatile boolean _catched;
    private final Object _syncCatched = new Object();
    private final Object _syncChanges = new Object();

    private LoyalError(){}

    public LoyalError(int possibleErrorsNumber, String msg) {
        _catched = true;
        _counter = 0;
        _max_value = Math.max(1, possibleErrorsNumber);
        if (msg == null) {
            _msg = _default;
        }
    }

    public LoyalError SetHappenedErrorCount(int happenedErrorCount) throws InfoException {
        _counter = Math.max(0, happenedErrorCount);

        _counter -= 1;
        return Ouch();
    }

    public LoyalError Ouch() throws InfoException {
        synchronized (_syncCatched) {
            if (!_catched) {
                return this;
            }
        }
        boolean limitExceeded;
        synchronized (_syncChanges) {
            if (_counter < _max_value) {
                _counter += 1;
            }
            limitExceeded = _counter > _max_value;
        }

        if (limitExceeded) {
            synchronized (_syncCatched) {
                if (_catched) {
                    _catched = false;
                }
            }
            throw new InfoException(_msg);
        }

        return this;
    }

    public LoyalError Catch() {
        synchronized (_syncCatched) {
            _catched = true;
            synchronized (_syncChanges) {
                _counter = 0;
            }
        }
        return this;
    }
}

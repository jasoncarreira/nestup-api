package com.ppi.api.model;

import java.io.Serializable;

/**
 * AccountType
 *
 * @author jcarreira@gmail.com
 * @version 1.0
 */
public enum AccountType implements Serializable {
    _401K("401k"),HSA("HSA"),ROTH_401k("ROTH_401K");

    AccountType(String name) {
        this.name = name;
    }

    private String name;

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "AccountType{" + name + '}';
    }
}

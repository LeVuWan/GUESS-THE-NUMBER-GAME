package com.inmobi.dtos.res;

public class IntrospectRes {
    private boolean valid;

    public IntrospectRes(boolean valid) {
        this.valid = valid;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

}

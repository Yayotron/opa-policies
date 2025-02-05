package com.capgemini.opapolicies.security;

import java.util.Objects;

public class OPAResponse {
    private boolean allow;

    public OPAResponse() {
    }

    public OPAResponse(boolean allow) {
        this.allow = allow;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OPAResponse that = (OPAResponse) o;
        return allow == that.allow;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(allow);
    }

    @Override
    public String toString() {
        return "OPAResponse{" +
                "allow=" + allow +
                '}';
    }

    public boolean isAllow() {
        return allow;
    }

    public void setAllow(boolean allow) {
        this.allow = allow;
    }
}
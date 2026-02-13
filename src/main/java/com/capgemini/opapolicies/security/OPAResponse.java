package com.capgemini.opapolicies.security;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;
import java.util.Objects;

public class OPAResponse {
    private boolean allow;
    private Decision decision;

    public OPAResponse() {
    }

    public OPAResponse(boolean allow, Decision decision) {
        this.allow = allow;
        this.decision = decision;
    }

    public boolean isAllow() {
        return allow;
    }

    public void setAllow(boolean allow) {
        this.allow = allow;
    }

    public Decision getDecision() {
        return decision;
    }

    public void setDecision(Decision decision) {
        this.decision = decision;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OPAResponse that = (OPAResponse) o;
        return allow == that.allow && Objects.equals(decision, that.decision);
    }

    @Override
    public int hashCode() {
        return Objects.hash(allow, decision);
    }

    @Override
    public String toString() {
        return "OPAResponse{" +
                "allow=" + allow +
                ", decision=" + decision +
                '}';
    }

    public static class Decision {
        private String action;
        private boolean allow;
        private String path;
        @JsonProperty("policies_evaluated")
        private Map<String, Boolean> policiesEvaluated;
        private String subject;

        public Decision() {
        }

        public Decision(String action, boolean allow, String path, Map<String, Boolean> policiesEvaluated, String subject) {
            this.action = action;
            this.allow = allow;
            this.path = path;
            this.policiesEvaluated = policiesEvaluated;
            this.subject = subject;
        }

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }

        public boolean isAllow() {
            return allow;
        }

        public void setAllow(boolean allow) {
            this.allow = allow;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public Map<String, Boolean> getPoliciesEvaluated() {
            return policiesEvaluated;
        }

        public void setPoliciesEvaluated(Map<String, Boolean> policiesEvaluated) {
            this.policiesEvaluated = policiesEvaluated;
        }

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Decision decision = (Decision) o;
            return allow == decision.allow &&
                    Objects.equals(action, decision.action) &&
                    Objects.equals(path, decision.path) &&
                    Objects.equals(policiesEvaluated, decision.policiesEvaluated) &&
                    Objects.equals(subject, decision.subject);
        }

        @Override
        public int hashCode() {
            return Objects.hash(action, allow, path, policiesEvaluated, subject);
        }

        @Override
        public String toString() {
            return "Decision{" +
                    "action='" + action + '\'' +
                    ", allow=" + allow +
                    ", path='" + path + '\'' +
                    ", policiesEvaluated=" + policiesEvaluated +
                    ", subject='" + subject + '\'' +
                    '}';
        }
    }
}

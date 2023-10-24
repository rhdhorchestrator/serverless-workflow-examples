package dev.parodos.escalationswf.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class JiraIssue {
    private String key;
    private String self;

    public String getKey() {
        return key;
    }

    public JiraIssue setKey(String key) {
        this.key = key;
        return this;
    }

    public String getSelf() {
        return self;
    }

    public JiraIssue setSelf(String self) {
        this.self = self;
        return this;
    }

    @Override
    public String toString() {
        return "JiraIssue [key=" + key + ", self=" + self + "]";
    }
}

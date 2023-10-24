package dev.parodos.escalationswf.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Namespace {
    private String apiVersion = "v1";
    private String kind = "Namespace";
    private Metadata metadata = new Metadata();

    public static Namespace of(String name) {
        Namespace namespace = new Namespace();
        namespace.getMetadata().setName(name);
        return namespace;
    }

    public static class Metadata {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "Metadata [name=" + name + "]";
        }
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    @Override
    public String toString() {
        return "Namespace [apiVersion=" + apiVersion + ", kind=" + kind + ", metadata=" + metadata + "]";
    }
}

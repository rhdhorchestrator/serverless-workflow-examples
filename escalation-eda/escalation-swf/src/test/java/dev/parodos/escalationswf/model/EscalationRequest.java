package dev.parodos.escalationswf.model;

public class EscalationRequest {
    private String namespace;
    private String manager;

    public String getNamespace() {
        return namespace;
    }

    public EscalationRequest setNamespace(String namespace) {
        this.namespace = namespace;
        return this;
    }

    public String getManager() {
        return manager;
    }

    public EscalationRequest setManager(String manager) {
        this.manager = manager;
        return this;
    }

    @Override
    public String toString() {
        return "EscalationRequest [namespace=" + namespace + ", manager=" + manager + "]";
    }
}

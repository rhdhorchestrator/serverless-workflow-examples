package dev.parodos.escalationswf.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateResponse {
    private String id;
    private Workflowdata workflowdata;

    public static class Workflowdata {
        private String namespace;
        private String manager;
        private JiraIssue jiraIssue;

        public String getNamespace() {
            return namespace;
        }

        public void setNamespace(String namespace) {
            this.namespace = namespace;
        }

        public String getManager() {
            return manager;
        }

        public void setManager(String manager) {
            this.manager = manager;
        }

        public JiraIssue getJiraIssue() {
            return jiraIssue;
        }

        public void setJiraIssue(JiraIssue jiraIssue) {
            this.jiraIssue = jiraIssue;
        }

        @Override
        public String toString() {
            return "Workflowdata [namespace=" + namespace + ", manager=" + manager + ", jiraIssue=" + jiraIssue + "]";
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Workflowdata getWorkflowdata() {
        return workflowdata;
    }

    public void setWorkflowdata(Workflowdata workflowdata) {
        this.workflowdata = workflowdata;
    }

    @Override
    public String toString() {
        return "CreateResponse [id=" + id + ", workflowdata=" + workflowdata + "]";
    }

}

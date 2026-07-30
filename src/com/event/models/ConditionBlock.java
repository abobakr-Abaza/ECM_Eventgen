package com.event.models;

import java.util.List;

public  class ConditionBlock {
    private List<Condition> conditions;
    private Operations operations;
    private boolean sleep;
    private String mood;

    public List<Condition> getConditions() {
        return conditions;
    }

    public void setConditions(List<Condition> conditions) {
        this.conditions = conditions;
    }

    public Operations getOperations() {
        return operations;
    }

    public void setOperations(Operations operations) {
        this.operations = operations;
    }

    public boolean isSleep() {
        return sleep;
    }

    public void setSleep(boolean sleep) {
        this.sleep = sleep;
    }

    public String getMood() {
        return mood;
    }

    public void setMood(String mood) {
        this.mood = mood;
    }
    
    public static class Condition {
        private String property;
        private String docClass;
        private String operator;
        private String value;
        private String conditionType;

        public String getProperty() {
            return property;
        }

        public void setProperty(String property) {
            this.property = property;
        }

        public String getDocClass() {
            return docClass;
        }

        public void setDocClass(String docClass) {
            this.docClass = docClass;
        }

        public String getOperator() {
            return operator;
        }

        public void setOperator(String operator) {
            this.operator = operator;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getConditionType() {
            return conditionType;
        }

        public void setConditionType(String conditionType) {
            this.conditionType = conditionType;
        }
    }

    public static class Operations {
        private List<PropertyUpdate> propertyUpdates;
        private List<FolderOperation> folderOperations;
        private List<SecurityUpdate> securityUpdates;

        public List<PropertyUpdate> getPropertyUpdates() {
            return propertyUpdates;
        }

        public void setPropertyUpdates(List<PropertyUpdate> propertyUpdates) {
            this.propertyUpdates = propertyUpdates;
        }

        public List<FolderOperation> getFolderOperations() {
            return folderOperations;
        }

        public void setFolderOperations(List<FolderOperation> folderOperations) {
            this.folderOperations = folderOperations;
        }

        public List<SecurityUpdate> getSecurityUpdates() {
            return securityUpdates;
        }

        public void setSecurityUpdates(List<SecurityUpdate> securityUpdates) {
            this.securityUpdates = securityUpdates;
        }
    }
    
}

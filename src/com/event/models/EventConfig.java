package com.event.models;

import java.util.List;

public class EventConfig {

    private List<PropertyUpdate> properties;
    private List<FolderOperation> folderOperations;
    private List<SecurityUpdate> security;
    private List<String> climbUpList;
    private List<String> dynamicParentList;
    private List<ConditionBlock> conditions;


    public List<PropertyUpdate> getProperties() {
        return properties;
    }

    public void setProperties(List<PropertyUpdate> properties) {
        this.properties = properties;
    }

    public List<FolderOperation> getFolderOperations() {
        return folderOperations;
    }

    public void setFolderOperations(List<FolderOperation> folderOperations) {
        this.folderOperations = folderOperations;
    }

    public List<SecurityUpdate> getSecurity() {
        return security;
    }

    public void setSecurity(List<SecurityUpdate> security) {
        this.security = security;
    }

    public List<String> getClimbUpList() {
        return climbUpList;
    }

    public void setClimbUpList(List<String> climbUpList) {
        this.climbUpList = climbUpList;
    }

    public List<String> getDynamicParentList() {
        return dynamicParentList;
    }

    public void setDynamicParentList(List<String> dynamicParentList) {
        this.dynamicParentList = dynamicParentList;
    }

    public List<ConditionBlock> getConditions() {
        return conditions;
    }

    public void setConditions(List<ConditionBlock> conditions) {
        this.conditions = conditions;
    }
}

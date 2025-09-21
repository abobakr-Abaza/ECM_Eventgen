package com.event;

import java.util.List;
import java.util.Map;

public class EventConfig {

    private List<PropertyUpdate> properties;
    private List<FolderOperation> folderOperations;
    private List<SecurityUpdate> security;
    private List<String> climbUpList;        // Static parent folders
    private List<String> dynamicParentList;  // Dynamic parent folders

    // Conditions can remain the same if you are using them
    private List<ConditionBlock> conditions;

    // Getters and Setters
    public List<PropertyUpdate> getProperties() { return properties; }
    public void setProperties(List<PropertyUpdate> properties) { this.properties = properties; }

    public List<FolderOperation> getFolderOperations() { return folderOperations; }
    public void setFolderOperations(List<FolderOperation> folderOperations) { this.folderOperations = folderOperations; }

    public List<SecurityUpdate> getSecurity() { return security; }
    public void setSecurity(List<SecurityUpdate> security) { this.security = security; }

    public List<String> getClimbUpList() { return climbUpList; }
    public void setClimbUpList(List<String> climbUpList) { this.climbUpList = climbUpList; }

    public List<String> getDynamicParentList() { return dynamicParentList; }
    public void setDynamicParentList(List<String> dynamicParentList) { this.dynamicParentList = dynamicParentList; }

    public List<ConditionBlock> getConditions() { return conditions; }
    public void setConditions(List<ConditionBlock> conditions) { this.conditions = conditions; }

    // --- Nested Classes ---

    public static class PropertyUpdate {
        private String action; // update / print
        private String name;
        private String value;
        private String type; // String, Boolean, Integer, DateTime

        public String getAction() { return action; }
        public void setAction(String action) { this.action = action; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getValue() { return value; }
        public void setValue(String value) { this.value = value; }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
    }

    public static class FolderOperation {
        private String name;
        private String parentPath; // Doc, current, climbUp:..., dynamic:..., path:/...
        private boolean unfileFirst;
        private boolean saveAfterOps;
        private List<FolderNode> tree;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getParentPath() { return parentPath; }
        public void setParentPath(String parentPath) { this.parentPath = parentPath; }

        public boolean isUnfileFirst() { return unfileFirst; }
        public void setUnfileFirst(boolean unfileFirst) { this.unfileFirst = unfileFirst; }

        public boolean isSaveAfterOps() { return saveAfterOps; }
        public void setSaveAfterOps(boolean saveAfterOps) { this.saveAfterOps = saveAfterOps; }

        public List<FolderNode> getTree() { return tree; }
        public void setTree(List<FolderNode> tree) { this.tree = tree; }
    }

    public static class FolderNode {
        private String type; // static / property / date / classBased
        private String name;
        private String propertyName;
        private String dateFormat;
        private String className;
        private boolean fileHere;
        private List<FolderNode> children;

        // NEW field for classBased type
        private Map<String, String> classToFolder;

        // --- getters and setters ---
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getPropertyName() { return propertyName; }
        public void setPropertyName(String propertyName) { this.propertyName = propertyName; }

        public String getDateFormat() { return dateFormat; }
        public void setDateFormat(String dateFormat) { this.dateFormat = dateFormat; }

        public String getClassName() { return className; }
        public void setClassName(String className) { this.className = className; }

        public boolean isFileHere() { return fileHere; }
        public void setFileHere(boolean fileHere) { this.fileHere = fileHere; }

        public List<FolderNode> getChildren() { return children; }
        public void setChildren(List<FolderNode> children) { this.children = children; }

        public Map<String, String> getClassToFolder() { return classToFolder; }
        public void setClassToFolder(Map<String, String> classToFolder) { this.classToFolder = classToFolder; }
    }

    public static class SecurityUpdate {
        private String grantee;
        private List<String> permissions;

        public String getGrantee() { return grantee; }
        public void setGrantee(String grantee) { this.grantee = grantee; }

        public List<String> getPermissions() { return permissions; }
        public void setPermissions(List<String> permissions) { this.permissions = permissions; }
    }

    public static class ConditionBlock {
        private List<Condition> conditions;
        private Operations operations;

        public List<Condition> getConditions() { return conditions; }
        public void setConditions(List<Condition> conditions) { this.conditions = conditions; }

        public Operations getOperations() { return operations; }
        public void setOperations(Operations operations) { this.operations = operations; }
    }

    public static class Condition {
        private String property;
        private String operator; // equals, contains, gt, lt, before, after
        private String value;

        public String getProperty() { return property; }
        public void setProperty(String property) { this.property = property; }

        public String getOperator() { return operator; }
        public void setOperator(String operator) { this.operator = operator; }

        public String getValue() { return value; }
        public void setValue(String value) { this.value = value; }
    }

    public static class Operations {
        private List<PropertyUpdate> propertyUpdates;
        private List<FolderOperation> folderOperations;
        private List<SecurityUpdate> securityUpdates;

        public List<PropertyUpdate> getPropertyUpdates() { return propertyUpdates; }
        public void setPropertyUpdates(List<PropertyUpdate> propertyUpdates) { this.propertyUpdates = propertyUpdates; }

        public List<FolderOperation> getFolderOperations() { return folderOperations; }
        public void setFolderOperations(List<FolderOperation> folderOperations) { this.folderOperations = folderOperations; }

        public List<SecurityUpdate> getSecurityUpdates() { return securityUpdates; }
        public void setSecurityUpdates(List<SecurityUpdate> securityUpdates) { this.securityUpdates = securityUpdates; }
    }
}

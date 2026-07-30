package com.event.models;

import java.util.List;
import java.util.Map;


public  class FolderOperation {
    private String name;
    private String parentPath;
    private boolean unfileFirst = true;
    private boolean saveAfterOps = true;
    private List<FolderNode> tree;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParentPath() {
        return parentPath;
    }

    public void setParentPath(String parentPath) {
        this.parentPath = parentPath;
    }

    public boolean isUnfileFirst() {
        return unfileFirst;
    }

    public void setUnfileFirst(boolean unfileFirst) {
        this.unfileFirst = unfileFirst;
    }

    public boolean isSaveAfterOps() {
        return saveAfterOps;
    }

    public void setSaveAfterOps(boolean saveAfterOps) {
        this.saveAfterOps = saveAfterOps;
    }

    public List<FolderNode> getTree() {
        return tree;
    }

    public void setTree(List<FolderNode> tree) {
        this.tree = tree;
    }
    
    
    public static class FolderNode {
        private String type;
        private String name;
        private String propertyName;
        private String dateFormat;
        private String className;
        private boolean fileHere;
        private List<FolderNode> children;
        private Map<String, String> classToFolder;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPropertyName() {
            return propertyName;
        }

        public void setPropertyName(String propertyName) {
            this.propertyName = propertyName;
        }

        public String getDateFormat() {
            return dateFormat;
        }

        public void setDateFormat(String dateFormat) {
            this.dateFormat = dateFormat;
        }

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }

        public boolean isFileHere() {
            return fileHere;
        }

        public void setFileHere(boolean fileHere) {
            this.fileHere = fileHere;
        }

        public List<FolderNode> getChildren() {
            return children;
        }

        public void setChildren(List<FolderNode> children) {
            this.children = children;
        }

        public Map<String, String> getClassToFolder() {
            return classToFolder;
        }

        public void setClassToFolder(Map<String, String> classToFolder) {
            this.classToFolder = classToFolder;
        }
    }
}

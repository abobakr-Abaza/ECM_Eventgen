package com.event;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

public class eventgen {

    private static final Map<Integer, String> ACCESS_RIGHTS = new LinkedHashMap<>();
    static {
        ACCESS_RIGHTS.put(1, "READ");
        ACCESS_RIGHTS.put(2, "WRITE");
        ACCESS_RIGHTS.put(3, "DELETE");
        ACCESS_RIGHTS.put(4, "VIEW_CONTENT");
        ACCESS_RIGHTS.put(5, "MINOR_VERSION");
        ACCESS_RIGHTS.put(6, "MAJOR_VERSION");
        ACCESS_RIGHTS.put(7, "CREATE_INSTANCE");
        ACCESS_RIGHTS.put(8, "LINK");
        ACCESS_RIGHTS.put(9, "UNLINK");
        ACCESS_RIGHTS.put(10, "CHANGE_STATE");
        ACCESS_RIGHTS.put(11, "WRITE_ACL");
        ACCESS_RIGHTS.put(12, "READ_ACL");
        ACCESS_RIGHTS.put(13, "DELEGATE_ACCESS");
    }

    public static void main(String[] args) {
        try {
            ObjectMapper mapper = new ObjectMapper();

            EventConfig config = mapper.readValue(
                    new File("C:\\Users\\6190\\eclipse-workspace\\evengen\\src\\com\\event\\config.json"),
                    EventConfig.class
            );

            System.out.println("\nGenerating Outputevent.java ...");

            FileWriter fw = new FileWriter("Outputevent.java");
            writeHeader(fw);
            writeOnEvent(fw, config);
            writeHelperMethods(fw);
            fw.write("}\n"); // close class
            fw.close();

            System.out.println("✅ Outputevent.java generated successfully!");

        } catch (IOException e) {
            System.err.println("❌ Failed to read config.json: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void writeHeader(FileWriter fw) throws IOException {
        fw.write("package com.event;\n");
        fw.write("import com.filenet.api.collection.ReferentialContainmentRelationshipSet;\n");
        fw.write("import com.filenet.api.constants.RefreshMode;\n");
        fw.write("import com.filenet.api.core.*;\n");
        fw.write("import com.filenet.api.engine.EventActionHandler;\n");
        fw.write("import com.filenet.api.events.ObjectChangeEvent;\n");
        fw.write("import com.filenet.api.exception.EngineRuntimeException;\n");
        fw.write("import com.filenet.api.property.*;\n");
        fw.write("import com.filenet.api.security.AccessPermission;\n");
        fw.write("import com.filenet.api.collection.AccessPermissionList;\n");
        fw.write("import com.filenet.api.constants.AccessRight;\n");
        fw.write("import com.filenet.api.util.Id;\n");
        fw.write("import java.text.SimpleDateFormat;\n");
        fw.write("import com.filenet.api.property.Properties;\n");
        fw.write("import java.util.*;\n\n");
        fw.write("public class Outputevent implements EventActionHandler {\n\n");
    }

    private static void writeOnEvent(FileWriter fw, EventConfig config) throws IOException {
        fw.write("    @Override\n");
        fw.write("    public void onEvent(ObjectChangeEvent event, Id subId) {\n");
        fw.write("        try {\n");
        fw.write("            ObjectStore os = event.getObjectStore();\n");
        fw.write("            Document doc = Factory.Document.fetchInstance(os, event.get_SourceObjectId(), null);\n");
        fw.write("            Properties props = doc.getProperties();\n\n");

        // --- Property updates ---
        if (config.getProperties() != null) {
            for (EventConfig.PropertyUpdate def : config.getProperties()) {
                fw.write(generatePropertyCode(def));
            }
        }

        // --- Folder operations ---
        if (config.getFolderOperations() != null) {
            int opIndex = 0;
            for (EventConfig.FolderOperation op : config.getFolderOperations()) {
                opIndex++;
                fw.write("            // Folder operation: " + safeString(op.getName()) + "\n");

                // Determine parent folder
                fw.write("            Folder parentFolder" + opIndex + " = resolveParentFolder(doc, os, \"" + safeString(op.getParentPath()) + "\");\n");
                fw.write("            if (parentFolder" + opIndex + " != null) {\n");
                if (op.isUnfileFirst()) fw.write("                unfileFromAll(doc);\n");

                // generate folder tree recursively
                if (op.getTree() != null) {
                    for (EventConfig.FolderNode node : op.getTree()) {
                        generateFolderCode(fw, node, "parentFolder" + opIndex, 4);
                    }
                }
                if (op.isSaveAfterOps()) fw.write("                doc.save(RefreshMode.REFRESH);\n");
                fw.write("            }\n\n");
            }
        }

        // --- Security updates ---
        if (config.getSecurity() != null) {
            for (EventConfig.SecurityUpdate sec : config.getSecurity()) {
                StringBuilder mask = new StringBuilder();
                for (String right : sec.getPermissions()) {
                    String mapped = right.toUpperCase();
                    if (ACCESS_RIGHTS.containsValue(mapped)) {
                        if (mask.length() > 0) mask.append(" | ");
                        mask.append("AccessRight.").append(mapped).append("_AS_INT");
                    }
                }
                if (mask.length() == 0) mask.append("0");
                fw.write("            setPermissions(doc, \"" + escapeForJava(sec.getGrantee()) + "\", " + mask + ");\n");
            }
        }

        // --- Conditions ---
        if (config.getConditions() != null) {
            int condIndex = 0;
            for (EventConfig.ConditionBlock block : config.getConditions()) {
                condIndex++;
                String conditionExpr = generateConditionCode(block);
                if (conditionExpr == null || conditionExpr.trim().isEmpty()) continue;

                fw.write("            // Condition block " + condIndex + "\n");
                fw.write("            if (" + conditionExpr + ") {\n");

                if (block.getOperations() != null) {
                    // Property updates
                    if (block.getOperations().getPropertyUpdates() != null) {
                        for (EventConfig.PropertyUpdate def : block.getOperations().getPropertyUpdates()) {
                            String code = generatePropertyCode(def);
                            fw.write(code.replaceFirst(" {12}", "            "));
                        }
                    }
                    // Folder operations
                    if (block.getOperations().getFolderOperations() != null) {
                        int opIndex2 = 0;
                        for (EventConfig.FolderOperation op : block.getOperations().getFolderOperations()) {
                            opIndex2++;
                            fw.write("                // Conditional folder operation: " + safeString(op.getName()) + "\n");
                            fw.write("                Folder condFolder" + condIndex + "_" + opIndex2 + " = resolveParentFolder(doc, os, \"" + safeString(op.getParentPath()) + "\");\n");
                            fw.write("                if (condFolder" + condIndex + "_" + opIndex2 + " != null) {\n");
                            if (op.isUnfileFirst()) fw.write("                    unfileFromAll(doc);\n");
                            if (op.getTree() != null) {
                                for (EventConfig.FolderNode node : op.getTree()) {
                                    generateFolderCode(fw, node, "condFolder" + condIndex + "_" + opIndex2, 5);
                                }
                            }
                            if (op.isSaveAfterOps()) fw.write("                    doc.save(RefreshMode.REFRESH);\n");
                            fw.write("                }\n");
                        }
                    }
                    // Security updates
                    if (block.getOperations().getSecurityUpdates() != null) {
                        for (EventConfig.SecurityUpdate sec : block.getOperations().getSecurityUpdates()) {
                            StringBuilder mask = new StringBuilder();
                            for (String right : sec.getPermissions()) {
                                String mapped = right.toUpperCase();
                                if (ACCESS_RIGHTS.containsValue(mapped)) {
                                    if (mask.length() > 0) mask.append(" | ");
                                    mask.append("AccessRight.").append(mapped).append("_AS_INT");
                                }
                            }
                            if (mask.length() == 0) mask.append("0");
                            fw.write("                setPermissions(doc, \"" + escapeForJava(sec.getGrantee()) + "\", " + mask + ");\n");
                        }
                    }
                }

                fw.write("            }\n\n");
            }
        }

        fw.write("            doc.save(RefreshMode.REFRESH);\n");
        fw.write("            System.out.println(\"Event completed successfully.\");\n");
        fw.write("        } catch (Exception e) { e.printStackTrace(); }\n");
        fw.write("    }\n\n");

        // --- setPermissions method ---
        fw.write("    private void setPermissions(Document doc, String granteeName, int accessMask) {\n");
        fw.write("        try {\n");
        fw.write("            AccessPermissionList perms = doc.get_Permissions();\n");
        fw.write("            boolean found = false;\n");
        fw.write("            Iterator it = perms.iterator();\n");
        fw.write("            while(it.hasNext()) {\n");
        fw.write("                AccessPermission perm = (AccessPermission) it.next();\n");
        fw.write("                if(granteeName.equalsIgnoreCase(perm.get_GranteeName())) {\n");
        fw.write("                    perm.set_AccessMask(accessMask);\n");
        fw.write("                    found = true;\n");
        fw.write("                }\n");
        fw.write("            }\n");
        fw.write("            if(!found) {\n");
        fw.write("                AccessPermission newPerm = Factory.AccessPermission.createInstance();\n");
        fw.write("                newPerm.set_GranteeName(granteeName);\n");
        fw.write("                newPerm.set_AccessMask(accessMask);\n");
        fw.write("                perms.add(newPerm);\n");
        fw.write("            }\n");
        fw.write("            doc.set_Permissions(perms);\n");
        fw.write("        } catch (Exception e) { e.printStackTrace(); }\n");
        fw.write("    }\n\n");
    }

    // ---------------- Helper Methods ----------------
    private static void writeHelperMethods(FileWriter fw) throws IOException {
        // resolveParentFolder
        fw.write("    private Folder resolveParentFolder(Document doc, ObjectStore os, String parentPath) {\n");
        fw.write("        try {\n");
        fw.write("            if(\"Doc\".equalsIgnoreCase(parentPath) || \"current\".equalsIgnoreCase(parentPath)) {\n");
        fw.write("                ReferentialContainmentRelationshipSet c = doc.get_Containers();\n");
        fw.write("                Iterator it = c.iterator();\n");
        fw.write("                if(it.hasNext()) return (Folder)((ReferentialContainmentRelationship)it.next()).get_Tail();\n");
        fw.write("            } else if(parentPath != null && parentPath.startsWith(\"climbUp:\")) {\n");
        fw.write("                String[] climbList = parentPath.substring(8).split(\",\");\n");
        fw.write("                return getStaticParentFolder(doc, climbList);\n");
        fw.write("            } else if(parentPath != null && parentPath.startsWith(\"dynamic:\")) {\n");
        fw.write("                String[] dynamicList = parentPath.substring(8).split(\",\");\n");
        fw.write("                return getDynamicParentFolder(doc, dynamicList);\n");
        fw.write("            } else if(parentPath != null && parentPath.startsWith(\"path:\")) {\n");
        fw.write("                return Factory.Folder.fetchInstance(os, parentPath.substring(5), null);\n");
        fw.write("            } else if(parentPath != null && parentPath.startsWith(\"conditionalClimb:\")) {\n");
        fw.write("                String[] parts = parentPath.substring(17).split(\":\");\n");
        fw.write("                if(parts.length == 2) {\n");
        fw.write("                    String[] allowedFolders = parts[0].split(\",\");\n");
        fw.write("                    int maxLevels = Integer.parseInt(parts[1]);\n");
        fw.write("                    return getConditionalParentFolder(doc, allowedFolders, maxLevels);\n");
        fw.write("                }\n");
        fw.write("            }\n");
        fw.write("        } catch(Exception e){ e.printStackTrace(); }\n");
        fw.write("        return null;\n");
        fw.write("    }\n\n");

        // Static climb
        fw.write("    private Folder getStaticParentFolder(Document doc, String[] climbUpList) {\n");
        fw.write("        try {\n");
        fw.write("            ReferentialContainmentRelationshipSet c = doc.get_Containers();\n");
        fw.write("            Iterator it = c.iterator();\n");
        fw.write("            Folder current = null;\n");
        fw.write("            if(it.hasNext()) current = (Folder)((ReferentialContainmentRelationship)it.next()).get_Tail();\n");
        fw.write("            while(current != null) {\n");
        fw.write("                for(String name : climbUpList) {\n");
        fw.write("                    if(current.get_FolderName().equalsIgnoreCase(name.trim())) return current;\n");
        fw.write("                }\n");
        fw.write("                current = current.get_Parent();\n");
        fw.write("            }\n");
        fw.write("        } catch(Exception e){ e.printStackTrace(); }\n");
        fw.write("        return null;\n");
        fw.write("    }\n\n");

        // Dynamic climb
        fw.write("    private Folder getDynamicParentFolder(Document doc, String[] dynamicList) {\n");
        fw.write("        try {\n");
        fw.write("            ReferentialContainmentRelationshipSet c = doc.get_Containers();\n");
        fw.write("            Iterator it = c.iterator();\n");
        fw.write("            Folder current = null;\n");
        fw.write("            if(it.hasNext()) current = (Folder)((ReferentialContainmentRelationship)it.next()).get_Tail();\n");
        fw.write("            while(current != null) {\n");
        fw.write("                for(String name : dynamicList) {\n");
        fw.write("                    if(current.get_FolderName().toLowerCase().contains(name.trim().toLowerCase())) return current;\n");
        fw.write("                }\n");
        fw.write("                current = current.get_Parent();\n");
        fw.write("            }\n");
        fw.write("        } catch(Exception e){ e.printStackTrace(); }\n");
        fw.write("        return null;\n");
        fw.write("    }\n\n");

        // Conditional climb
        fw.write("    private Folder getConditionalParentFolder(Document doc, String[] allowedFolders, int maxLevels) {\n");
        fw.write("        try {\n");
        fw.write("            ReferentialContainmentRelationshipSet c = doc.get_Containers();\n");
        fw.write("            Iterator it = c.iterator();\n");
        fw.write("            if(!it.hasNext()) return null;\n");
        fw.write("            Folder current = (Folder)((ReferentialContainmentRelationship)it.next()).get_Tail();\n");
        fw.write("            boolean allowed = false;\n");
        fw.write("            for(String name : allowedFolders) {\n");
        fw.write("                if(name.equalsIgnoreCase(current.get_FolderName())) {\n");
        fw.write("                    allowed = true;\n");
        fw.write("                    break;\n");
        fw.write("                }\n");
        fw.write("            }\n");
        fw.write("            if(!allowed) return current;\n");
        fw.write("            int levels = 0;\n");
        fw.write("            while(current.get_Parent() != null && levels < maxLevels) {\n");
        fw.write("                current = current.get_Parent();\n");
        fw.write("                levels++;\n");
        fw.write("            }\n");
        fw.write("            return current;\n");
        fw.write("        } catch(Exception e) { e.printStackTrace(); return null; }\n");
        fw.write("    }\n\n");

        // Existing folder helpers
        writeFolderHelpers(fw);
    }

    private static void writeFolderHelpers(FileWriter fw) throws IOException {
        fw.write("    private Folder getOrCreateFolder(ObjectStore os, Folder parent, String name, String cls) {\n");
        fw.write("        try { return Factory.Folder.fetchInstance(os, parent.get_PathName() + \"/\" + name, null); }\n");
        fw.write("        catch (EngineRuntimeException e) { Folder f = Factory.Folder.createInstance(os, cls); f.set_FolderName(name); f.set_Parent(parent); f.save(RefreshMode.REFRESH); return f; }\n");
        fw.write("    }\n\n");

        fw.write("    private Folder getOrCreateStaticFolder(Document doc, ObjectStore os, Folder parent, String name, String cls, boolean fileHere) throws Exception {\n");
        fw.write("        Folder f = getOrCreateFolder(os, parent, name==null||name.isEmpty()?\"unnamed\":name, cls);\n");
        fw.write("        if(fileHere) safeFile(doc,f,os);\n");
        fw.write("        return f;\n");
        fw.write("    }\n\n");

        fw.write("    private Folder getOrCreatePropertyFolder(Document doc, ObjectStore os, Folder parent, String prop, String cls, boolean fileHere) throws Exception {\n");
        fw.write("        String val = null;\n");
        fw.write("        try { val = doc.getProperties().getStringValue(prop); } catch(Exception ex) { val = \"<undefined>\"; }\n");
        fw.write("        Folder f = getOrCreateFolder(os,parent,val,cls);\n");
        fw.write("        if(fileHere) safeFile(doc,f,os);\n");
        fw.write("        return f;\n");
        fw.write("    }\n\n");

        fw.write("    private Folder getOrCreateDateFolder(Document doc, ObjectStore os, Folder parent, String prop, String fmt, String cls, boolean fileHere) throws Exception {\n");
        fw.write("        Date d = null;\n");
        fw.write("        try { d = doc.getProperties().getDateTimeValue(prop); } catch(Exception ex) { d = new Date(); }\n");
        fw.write("        SimpleDateFormat sdf = new SimpleDateFormat(fmt); sdf.setTimeZone(TimeZone.getTimeZone(\"UTC\"));\n");
        fw.write("        String name = sdf.format(d);\n");
        fw.write("        Folder f = getOrCreateFolder(os,parent,name,cls);\n");
        fw.write("        if(fileHere) safeFile(doc,f,os);\n");
        fw.write("        return f;\n");
        fw.write("    }\n\n");

        fw.write("    private void safeFile(Document doc, Folder f, ObjectStore os) {\n");
        fw.write("        try { ReferentialContainmentRelationship rcr = Factory.ReferentialContainmentRelationship.createInstance(os, null, null, null); rcr.set_Head(doc); rcr.set_Tail(f); rcr.save(RefreshMode.NO_REFRESH); }\n");
        fw.write("        catch(Exception e){ e.printStackTrace(); }\n");
        fw.write("    }\n\n");

        fw.write("    private void unfileFromAll(Document doc) throws Exception {\n");
        fw.write("        ReferentialContainmentRelationshipSet rcrs = doc.get_Containers(); Iterator it = rcrs.iterator();\n");
        fw.write("        while(it.hasNext()) { ReferentialContainmentRelationship rcr = (ReferentialContainmentRelationship)it.next(); rcr.delete(); rcr.save(RefreshMode.NO_REFRESH); }\n");
        fw.write("        doc.save(RefreshMode.REFRESH);\n");
        fw.write("    }\n\n");
    }

    // ---------------- Utilities ----------------
    private static String safeString(String s) { return s == null ? "" : s; }

    private static String escapeForJava(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private static String safeVar(String s) {
        if (s == null) return "var";
        return s.replaceAll("[^A-Za-z0-9_]", "_");
    }

    private static String generatePropertyCode(EventConfig.PropertyUpdate def) {
        StringBuilder sb = new StringBuilder();
        String type = (def.getType() == null || def.getType().isEmpty()) ? "String" : def.getType();
        String indent = "            ";

        if ("update".equalsIgnoreCase(def.getAction())) {
            if ("String".equalsIgnoreCase(type)) sb.append(indent).append("props.putValue(\"").append(escapeForJava(def.getName())).append("\", \"").append(escapeForJava(def.getValue())).append("\");\n");
            else if ("Boolean".equalsIgnoreCase(type)) sb.append(indent).append("props.putValue(\"").append(escapeForJava(def.getName())).append("\", Boolean.valueOf(\"").append(escapeForJava(def.getValue())).append("\"));\n");
            else if ("Integer".equalsIgnoreCase(type)) sb.append(indent).append("props.putValue(\"").append(escapeForJava(def.getName())).append("\", Integer.valueOf(\"").append(escapeForJava(def.getValue())).append("\"));\n");
            else if ("DateTime".equalsIgnoreCase(type)) sb.append(indent).append("props.putValue(\"").append(escapeForJava(def.getName())).append("\", new SimpleDateFormat(\"yyyy-MM-dd'T'HH:mm:ss\").parse(\"").append(escapeForJava(def.getValue())).append("\"));\n");
            else sb.append(indent).append("props.putValue(\"").append(escapeForJava(def.getName())).append("\", \"").append(escapeForJava(def.getValue())).append("\");\n");
            sb.append(indent).append("System.out.println(\"Updated ").append(def.getName()).append("\");\n");
        } else if ("print".equalsIgnoreCase(def.getAction())) {
            sb.append(indent).append("Property p_").append(safeVar(def.getName())).append(" = props.get(\"").append(escapeForJava(def.getName())).append("\");\n");
            sb.append(indent).append("System.out.println(\"").append(def.getName()).append(": \" + (p_").append(safeVar(def.getName())).append(" != null ? p_").append(safeVar(def.getName())).append(".getObjectValue() : \"<null>\"));\n");
        }
        return sb.toString();
    }

    private static void generateFolderCode(FileWriter fw, EventConfig.FolderNode node, String parentVar, int indent) throws IOException {
        StringBuilder space = new StringBuilder();
        for (int i = 0; i < indent; i++) space.append("    "); // indentation

        // Create readable variable name
        String raw = node.getName() != null ? node.getName() :
                     (node.getPropertyName() != null ? node.getPropertyName() :
                     ("folder_" + node.getType())); // fallback readable name

        // Prefix with parentVar to avoid duplicates, remove special chars
        String var = safeVar(parentVar + "_" + raw);

        if ("property".equalsIgnoreCase(node.getType())) {
            fw.write(space + "Folder " + var + " = getOrCreatePropertyFolder(doc, os, " + parentVar + ", \"" + safeString(node.getPropertyName()) + "\", \"" + safeString(node.getClassName()) + "\", " + node.isFileHere() + ");\n");

        } else if ("date".equalsIgnoreCase(node.getType())) {
            fw.write(space + "Folder " + var + " = getOrCreateDateFolder(doc, os, " + parentVar + ", \"" + safeString(node.getPropertyName()) + "\", \"" + safeString(node.getDateFormat()) + "\", \"" + safeString(node.getClassName()) + "\", " + node.isFileHere() + ");\n");

        } else if ("classBased".equalsIgnoreCase(node.getType())) {
            fw.write(space + "String folderName_" + var + " = null;\n");
            fw.write(space + "String docClass_" + var + " = doc.getClassName();\n");
            fw.write(space + "switch(docClass_" + var + ") {\n");
            if (node.getClassToFolder() != null) {
                for (Map.Entry<String, String> entry : node.getClassToFolder().entrySet()) {
                    if (!"default".equalsIgnoreCase(entry.getKey())) {
                        fw.write(space + "    case \"" + entry.getKey() + "\": folderName_" + var + " = \"" + entry.getValue() + "\"; break;\n");
                    }
                }
                if (node.getClassToFolder().containsKey("default")) {
                    fw.write(space + "    default: folderName_" + var + " = \"" + node.getClassToFolder().get("default") + "\"; break;\n");
                } else {
                    fw.write(space + "    default: folderName_" + var + " = \"UNKNOWN\"; break;\n");
                }
            } else {
                fw.write(space + "    default: folderName_" + var + " = \"UNKNOWN\"; break;\n");
            }
            fw.write(space + "}\n");
            fw.write(space + "Folder " + var + " = getOrCreateStaticFolder(doc, os, " + parentVar + ", folderName_" + var + ", \"" + safeString(node.getClassName()) + "\", " + node.isFileHere() + ");\n");

        } else { // static folder
            fw.write(space + "Folder " + var + " = getOrCreateStaticFolder(doc, os, " + parentVar + ", \"" + safeString(node.getName()) + "\", \"" + safeString(node.getClassName()) + "\", " + node.isFileHere() + ");\n");
        }

        // Recursively generate code for children
        if (node.getChildren() != null) {
            for (EventConfig.FolderNode child : node.getChildren()) {
                generateFolderCode(fw, child, var, indent + 1);
            }
        }
    }
    private static String generateConditionCode(EventConfig.ConditionBlock block) {
        if (block.getConditions() == null || block.getConditions().isEmpty()) return "";
        List<String> conds = new ArrayList<>();
        for (EventConfig.Condition c : block.getConditions()) {
            String prop = "props.get(\"" + safeString(c.getProperty()) + "\")";
            String val = "\"" + escapeForJava(c.getValue()) + "\"";
            switch (c.getOperator()) {
                case "equals":
                    conds.add("(" + prop + " != null && String.valueOf(" + prop + ".getObjectValue()).equals(" + val + "))");
                    break;
                case "contains":
                    conds.add("(" + prop + " != null && String.valueOf(" + prop + ".getObjectValue()).contains(" + val + "))");
                    break;
                case "gt":
                    conds.add("(" + prop + " != null && Double.parseDouble(String.valueOf(" + prop + ".getObjectValue())) > Double.parseDouble(" + val + "))");
                    break;
                case "lt":
                    conds.add("(" + prop + " != null && Double.parseDouble(String.valueOf(" + prop + ".getObjectValue())) < Double.parseDouble(" + val + "))");
                    break;
                case "before":
                    conds.add("(" + prop + " != null && new SimpleDateFormat(\"yyyy-MM-dd'T'HH:mm:ss\").parse(String.valueOf(" + prop + ".getObjectValue())).getTime() < new SimpleDateFormat(\"yyyy-MM-dd'T'HH:mm:ss\").parse(" + val + ").getTime())");
                    break;
                case "after":
                    conds.add("(" + prop + " != null && new SimpleDateFormat(\"yyyy-MM-dd'T'HH:mm:ss\").parse(String.valueOf(" + prop + ".getObjectValue())).getTime() > new SimpleDateFormat(\"yyyy-MM-dd'T'HH:mm:ss\").parse(" + val + ").getTime())");
                    break;
            }
        }
        return String.join(" && ", conds);
    }
}

//This is The pojo for the json 
/*
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
*/
/*
 * 
 */

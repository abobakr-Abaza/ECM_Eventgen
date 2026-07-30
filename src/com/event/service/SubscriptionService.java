package com.event.service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.event.models.*;
import com.event.Utils.*;

public class SubscriptionService {
    
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


	public static void processSubscription(SubscriptionDefination subscriptionDefination) throws IOException {
		   for (SubscriptionEvent subscriptionEvent : subscriptionDefination.getSubscriptionEvent()) {
			   System.out.println("Generate subscription code for case: " + subscriptionEvent.getCaseType());
	            Writer fw = new OutputStreamWriter(
	                new FileOutputStream(subscriptionEvent.getCaseType() + ".java"),
	                StandardCharsets.UTF_8
	            );
	            writeHeader(fw , subscriptionEvent);
                // writeHelperMethods(fw);
                writeOnEvent(fw, subscriptionEvent.getEventConfig());

//	           writeHelperMethods(fw);
	           fw.write("}\n"); // close class
	           fw.close();
               System.out.println("Generated file: " + subscriptionEvent.getCaseType() + ".java completed successfully!\n");
	    }
	}
	
	private static void writeHeader(Writer fw , SubscriptionEvent subscriptionEvent) throws IOException {   
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
        // fw.write("import com.intercom.filenet.SubscriptionHelpers;\n");
        fw.write("import java.util.*;\n\n");
        fw.write("public class " + subscriptionEvent.getCaseType() + " implements EventActionHandler {\n\n");
    }

    private static void writeOnEvent(Writer fw, EventConfig config) throws IOException {
        fw.write("    @Override\n");
        fw.write("    public void onEvent(ObjectChangeEvent event, Id subId) {\n");
        fw.write("        try {\n");
        fw.write("            ObjectStore os = event.getObjectStore();\n");
        fw.write("            Document doc = (Document) event.get_SourceObject();\n");
        fw.write("            doc.refresh();\n");
        fw.write("            Properties props = doc.getProperties();\n\n");

        // --- Property updates ---
        
        if (config.getProperties() != null) {
            for (PropertyUpdate def : config.getProperties()) {
                fw.write(generatePropertyCode(def));
            }
        }

        // --- Folder operations ---
        if (config.getFolderOperations() != null) {
            int opIndex = 0;
            for (FolderOperation op : config.getFolderOperations()) {
                opIndex++;
                fw.write("            // Folder operation: " + StringUtils.safeString(op.getName()) + "\n");
                if (op.getParentPath().startsWith("path")) {
                                fw.write("                Folder DocumentFolder = (Folder) doc.get_FoldersFiledIn().iterator().next();\n");
                                fw.write("                String folderName = DocumentFolder.getName();\n");
                                fw.write("                String fullPath = \"" + StringUtils.safeString(op.getParentPath()) + "\" + \"/\" + folderName;\n");
                                fw.write("                Folder parentFolder" + opIndex + " = SubscriptionHelpers.resolveParentFolder(doc, os, fullPath);\n");
                    }
                    else{
                    fw.write("                Folder parentFolder" + opIndex + " = SubscriptionHelpers.resolveParentFolder(doc, os, \"" + StringUtils.safeString(op.getParentPath()) + "\");\n");
                    }
                // Determine parent folder
                fw.write("            Folder parentFolder" + opIndex + " = SubscriptionHelpers.resolveParentFolder(doc, os, \"" + StringUtils.safeString(op.getParentPath()) + "\");\n");
                fw.write("            if (parentFolder" + opIndex + " != null) {\n");
                if (op.isUnfileFirst()) fw.write("                SubscriptionHelpers.unfileFromAll(doc);\n");

                // generate folder tree recursively
                if (op.getTree() != null) {
                    for (FolderOperation.FolderNode node : op.getTree()) {
                        generateFolderCode(fw, node, "parentFolder" + opIndex, 4);
                    }
                }
                if (op.isSaveAfterOps()) fw.write("                doc.save(RefreshMode.REFRESH);\n");
                fw.write("            }\n\n");
            }
        }

        // --- Security updates ---
        if (config.getSecurity() != null) {
            int secIndex = 0;
            for (SecurityUpdate sec : config.getSecurity()) {
                secIndex++;
                fw.write("            // Security update for " + StringUtils.escapeForJava(sec.getGrantee()) + "\n");
                fw.write("            String grantee" + secIndex + " = \"" + StringUtils.escapeForJava(sec.getGrantee()) + "\";\n");
                fw.write("            List<String> perms" + secIndex + " = new ArrayList<>();\n");
                for (String right : sec.getPermissions()) {
                    String mapped = right.toUpperCase();
                    if (ACCESS_RIGHTS.containsValue(mapped)) {
                        fw.write("            perms" + secIndex + ".add(\"" + mapped + "\");\n");
                    }
                }
                fw.write("            SubscriptionHelpers.setPermissions(doc, os, grantee" + secIndex + ", perms" + secIndex + ");\n\n");
            }
        }

        // --- Conditions ---
        if (config.getConditions() != null) {
        	
            int condIndex = 0;
            boolean firstCondition = true;
            for (ConditionBlock block : config.getConditions()) {
                condIndex++;
                String conditionExpr = generateConditionCode(block);
                if (conditionExpr == null || conditionExpr.trim().isEmpty()) continue;

                fw.write("            // Condition block " + condIndex + "\n");
                if (firstCondition) {
                    fw.write("            if (" + conditionExpr + ") {\n");
                    firstCondition = false;
                }
                else {
                    fw.write("            else if (" + conditionExpr + ") {\n");
                }

                if (block.getOperations() != null) {
                    // Property updates
                    if (block.getOperations().getPropertyUpdates() != null) {
                        for (PropertyUpdate def : block.getOperations().getPropertyUpdates()) {
                            String code = generatePropertyCode(def);
                            fw.write(code.replaceFirst(" {12}", "            "));
                        }
                    }
                    // Folder operations
                    if (block.getOperations().getFolderOperations() != null) {
                        int opIndex2 = 0;
                        for (FolderOperation op : block.getOperations().getFolderOperations()) {
                            opIndex2++;
                            fw.write("                // Conditional folder operation: " + StringUtils.safeString(op.getName()) + "\n");
                            if (op.getParentPath().startsWith("path")) {
                                fw.write("                Folder DocumentFolder = (Folder) doc.get_FoldersFiledIn().iterator().next();\n");
                                fw.write("                String folderName = DocumentFolder.getName();\n");
                                fw.write("                String fullPath = \"" + StringUtils.safeString(op.getParentPath()) + "\" + \"/\" + folderName;\n");
                                fw.write("                Folder condFolder" + condIndex + "_" + opIndex2 + " = SubscriptionHelpers.resolveParentFolder(doc, os, fullPath);\n");
                            }
                            else{
                            fw.write("                Folder condFolder" + condIndex + "_" + opIndex2 + " = SubscriptionHelpers.resolveParentFolder(doc, os, \"" + StringUtils.safeString(op.getParentPath()) + "\" );\n");
                            }
                            fw.write("                if (condFolder" + condIndex + "_" + opIndex2 + " != null) {\n");
                            if (op.isUnfileFirst()) fw.write("                    SubscriptionHelpers.unfileFromAll(doc);\n");
                            if (op.getTree() != null) {
                                for (FolderOperation.FolderNode node : op.getTree()) {
                                    generateFolderCode(fw, node, "condFolder" + condIndex + "_" + opIndex2, 5);
                                }
                            }
                            if (op.isSaveAfterOps()) fw.write("                    doc.save(RefreshMode.REFRESH);\n");
                            fw.write("                }\n");
                        }
                    }
                    // Security updates
                    if (block.getOperations().getSecurityUpdates() != null) {
                        int condSecIndex = 0;
                        for (SecurityUpdate sec : block.getOperations().getSecurityUpdates()) {
                            condSecIndex++;
                            fw.write("                // Security update for " + StringUtils.escapeForJava(sec.getGrantee()) + "\n");
                            fw.write("                String grantee" + condIndex + "_" + condSecIndex + " = \"" + StringUtils.escapeForJava(sec.getGrantee()) + "\";\n");
                            fw.write("                List<String> perms" + condIndex + "_" + condSecIndex + " = new ArrayList<>();\n");
                            for (String right : sec.getPermissions()) {
                                String mapped = right.toUpperCase();
                                if (ACCESS_RIGHTS.containsValue(mapped)) {
                                    fw.write("                perms" + condIndex + "_" + condSecIndex + ".add(\"" + mapped + "\");\n");
                                }
                            }
                            fw.write("                SubscriptionHelpers.setPermissions(doc, os, grantee" + condIndex + "_" + condSecIndex + ", perms" + condIndex + "_" + condSecIndex + ");\n");
                        }
                    }
                }
                else  if (block.isSleep()) {
                    
                    fw.write("                System.out.println(\"IntercomJavaEventHandler::onEvent():: Subscription to Sleep\");\n");
                }
                // else{
                    
                //     fw.write("                System.out.println(\"IntercomJavaEventHandler::onEvent():: Subscription to Sleep\");\n");
                // }
                
                fw.write("            }\n\n");
            }
        }

        fw.write("            doc.save(RefreshMode.REFRESH);\n");
        fw.write("            System.out.println(\"Event completed successfully.\");\n");
        fw.write("        } catch (Exception e) { e.printStackTrace(); }\n");
        fw.write("    }\n\n");
    }
    
    private static String generatePropertyCode(PropertyUpdate def) {
        StringBuilder sb = new StringBuilder();
        String type = (def.getType() == null || def.getType().isEmpty()) ? "String" : def.getType();
        String indent = "            ";
        
        if ("update".equalsIgnoreCase(def.getAction())) {
            if ("String".equalsIgnoreCase(type)) sb.append(indent).append("props.putValue(\"").append(StringUtils.escapeForJava(def.getName())).append("\", \"").append(StringUtils.escapeForJava(def.getValue())).append("\");\n");
            else if ("Boolean".equalsIgnoreCase(type)) sb.append(indent).append("props.putValue(\"").append(StringUtils.escapeForJava(def.getName())).append("\", Boolean.valueOf(\"").append(StringUtils.escapeForJava(def.getValue())).append("\"));\n");
            else if ("Integer".equalsIgnoreCase(type)) sb.append(indent).append("props.putValue(\"").append(StringUtils.escapeForJava(def.getName())).append("\", Integer.valueOf(\"").append(StringUtils.escapeForJava(def.getValue())).append("\"));\n");
            else if ("DateTime".equalsIgnoreCase(type)) sb.append(indent).append("props.putValue(\"").append(StringUtils.escapeForJava(def.getName())).append("\", new SimpleDateFormat(\"yyyy-MM-dd'T'HH:mm:ss\").parse(\"").append(StringUtils.escapeForJava(def.getValue())).append("\"));\n");
            else sb.append(indent).append("props.putValue(\"").append(StringUtils.escapeForJava(def.getName())).append("\", \"").append(StringUtils.escapeForJava(def.getValue())).append("\");\n");
            sb.append(indent).append("System.out.println(\"Updated ").append(def.getName()).append("\");\n");
        } else if ("print".equalsIgnoreCase(def.getAction())) {
            sb.append(indent).append("Property p_").append(StringUtils.safeVar(def.getName())).append(" = props.get(\"").append(StringUtils.escapeForJava(def.getName())).append("\");\n");
            sb.append(indent).append("System.out.println(\"").append(def.getName()).append(": \" + (p_").append(StringUtils.safeVar(def.getName())).append(" != null ? p_").append(StringUtils.safeVar(def.getName())).append(".getObjectValue() : \"<null>\"));\n");
        }
        return sb.toString();
    }
    
    private static void generateFolderCode(Writer fw, FolderOperation.FolderNode node, String parentVar, int indent)
    throws IOException {
        StringBuilder space = new StringBuilder();
        for (int i = 0; i < indent; i++) space.append("    "); // indentation
        
        // Create readable variable name
        String raw = node.getName() != null ? StringUtils.sanitizeIdentifier(node.getName()) :
        (node.getPropertyName() != null ? node.getPropertyName() :
        ("folder_" + node.getType())); // fallback readable name
        
        // Prefix with parentVar to avoid duplicates, remove special chars
        String var = StringUtils.safeVarUnicode(parentVar + "_" + raw);
//        System.out.println("raw: " + raw);
//        
//        System.out.println("ParentVar: " + parentVar);
//        System.out.println("var: " + var);
        
        if ("property".equalsIgnoreCase(node.getType())) {
            fw.write(space + "Folder " + var + " = SubscriptionHelpers.getOrCreatePropertyFolder(doc, os, " + parentVar + ", \"" + StringUtils.safeString(node.getPropertyName()) + "\", \"" + StringUtils.safeString(node.getClassName()) + "\", " + node.isFileHere() + ");\n");
            
        } else if ("date".equalsIgnoreCase(node.getType())) {
            fw.write(space + "Folder " + var + " = SubscriptionHelpers.getOrCreateDateFolder(doc, os, " + parentVar + ", \"" + StringUtils.safeString(node.getPropertyName()) + "\", \"" + StringUtils.safeString(node.getDateFormat()) + "\", \"" + StringUtils.safeString(node.getClassName()) + "\", " + node.isFileHere() + ");\n");

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
            fw.write(space + "Folder " + var + " = SubscriptionHelpers.getOrCreateStaticFolder(doc, os, " + parentVar + ", folderName_" + var + ", \"" + StringUtils.safeString(node.getClassName()) + "\", " + node.isFileHere() + ");\n");
            
        } else { // static folder
            fw.write(space + "Folder " + var + " = SubscriptionHelpers.getOrCreateStaticFolder(doc, os, " + parentVar + ", \"" + StringUtils.safeString(node.getName()) + "\", \"" + StringUtils.safeString(node.getClassName()) + "\", " + node.isFileHere() + ");\n");
        }
        
        // Recursively generate code for children
        if (node.getChildren() != null) {
            for (FolderOperation.FolderNode child : node.getChildren()) {
                generateFolderCode(fw, child, var, indent + 1);
            }
        }
    }
   
    private static String generateConditionCode(ConditionBlock block) {
        if (block.getConditions() == null || block.getConditions().isEmpty()) return "";
        List<String> conds = new ArrayList<>();
        String conditionType = "";
        for (ConditionBlock.Condition c : block.getConditions()) {
            conditionType = c.getConditionType();

            // Handle docClass condition separately (doc.getClassName() returns String directly)
            if (c.getDocClass() != null && !c.getDocClass().isEmpty()) {
                String val = "\"" + StringUtils.escapeForJava(c.getDocClass()) + "\"";
                conds.add("(doc.getClassName() != null && doc.getClassName().equals(" + val + "))");
                continue;
            }

            // Handle property-based conditions
            if (c.getProperty() == null || c.getProperty().isEmpty()) continue;

            String prop = "props.get(\"" + StringUtils.safeString(c.getProperty()) + "\")";
            String val = "\"" + StringUtils.escapeForJava(c.getValue()) + "\"";

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
                case "not equals":
                    conds.add("(" + prop + " != null && !String.valueOf(" + prop + ".getObjectValue()).equals(" + val + "))");
                    break;

            }
        }
        conditionType = conditionType!=null ? conditionType : "&&" ;
        if(conditionType.equalsIgnoreCase("or"))
        	conditionType = " || ";
        else
        	conditionType = " && ";
        
        return String.join(conditionType, conds);
    }

}

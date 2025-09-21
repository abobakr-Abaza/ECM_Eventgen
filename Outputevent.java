package com.event;
import com.filenet.api.collection.ReferentialContainmentRelationshipSet;
import com.filenet.api.constants.RefreshMode;
import com.filenet.api.core.*;
import com.filenet.api.engine.EventActionHandler;
import com.filenet.api.events.ObjectChangeEvent;
import com.filenet.api.exception.EngineRuntimeException;
import com.filenet.api.property.*;
import com.filenet.api.security.AccessPermission;
import com.filenet.api.collection.AccessPermissionList;
import com.filenet.api.constants.AccessRight;
import com.filenet.api.util.Id;
import java.text.SimpleDateFormat;
import com.filenet.api.property.Properties;
import java.util.*;

public class Outputevent implements EventActionHandler {

    @Override
    public void onEvent(ObjectChangeEvent event, Id subId) {
        try {
            ObjectStore os = event.getObjectStore();
            Document doc = Factory.Document.fetchInstance(os, event.get_SourceObjectId(), null);
            Properties props = doc.getProperties();

            props.putValue("Status", "Pending");
            System.out.println("Updated Status");
            props.putValue("Priority", Integer.valueOf("5"));
            System.out.println("Updated Priority");
            Property p_Region = props.get("Region");
            System.out.println("Region: " + (p_Region != null ? p_Region.getObjectValue() : "<null>"));
            // Folder operation: TopLevelFolderOps
            Folder parentFolder1 = resolveParentFolder(doc, os, "path:/incoming");
            if (parentFolder1 != null) {
                unfileFromAll(doc);
                Folder parentFolder1_MainStatic = getOrCreateStaticFolder(doc, os, parentFolder1, "MainStatic", "", false);
                    Folder parentFolder1_MainStatic_Status = getOrCreatePropertyFolder(doc, os, parentFolder1_MainStatic, "Status", "", true);
                Folder parentFolder1_ByDate = getOrCreateDateFolder(doc, os, parentFolder1, "", "yyyy/MM/dd", "", false);
                String folderName_parentFolder1_ClassMap = null;
                String docClass_parentFolder1_ClassMap = doc.getClassName();
                switch(docClass_parentFolder1_ClassMap) {
                    case "com.acme.Contract": folderName_parentFolder1_ClassMap = "ContractsFolder"; break;
                    case "com.acme.Invoice": folderName_parentFolder1_ClassMap = "InvoicesFolder"; break;
                    case "com.acme.Report": folderName_parentFolder1_ClassMap = "ReportsFolder"; break;
                    default: folderName_parentFolder1_ClassMap = "UNKNOWN"; break;
                }
                Folder parentFolder1_ClassMap = getOrCreateStaticFolder(doc, os, parentFolder1, folderName_parentFolder1_ClassMap, "", false);
                    Folder parentFolder1_ClassMap_NestedStatic = getOrCreateStaticFolder(doc, os, parentFolder1_ClassMap, "NestedStatic", "", true);
                doc.save(RefreshMode.REFRESH);
            }

            setPermissions(doc, "Admin", 0);
            setPermissions(doc, "Auditor", AccessRight.READ_AS_INT | AccessRight.VIEW_CONTENT_AS_INT);
            setPermissions(doc, "User", AccessRight.READ_AS_INT | AccessRight.WRITE_AS_INT);
            // Condition block 1
            if ((props.get("Status") != null && String.valueOf(props.get("Status").getObjectValue()).equals("Approved")) && (props.get("Priority") != null && Double.parseDouble(String.valueOf(props.get("Priority").getObjectValue())) > Double.parseDouble("5")) && (props.get("Region") != null && String.valueOf(props.get("Region").getObjectValue()).contains("US"))) {
            props.putValue("Maker", Integer.valueOf("1"));
            System.out.println("Updated Maker");
            props.putValue("Checker", Integer.valueOf("1"));
            System.out.println("Updated Checker");
                // Conditional folder operation: ApprovedOps
                Folder condFolder1_1 = resolveParentFolder(doc, os, "climbUp:/Global");
                if (condFolder1_1 != null) {
                    Folder condFolder1_1_ApprovedHighPriority = getOrCreateStaticFolder(doc, os, condFolder1_1, "ApprovedHighPriority", "", true);
                    Folder condFolder1_1_ApprovedByDate = getOrCreateDateFolder(doc, os, condFolder1_1, "", "yyyy-MM-dd", "", false);
                    doc.save(RefreshMode.REFRESH);
                }
                setPermissions(doc, "Managers", AccessRight.READ_AS_INT | AccessRight.WRITE_AS_INT | AccessRight.VIEW_CONTENT_AS_INT);
                setPermissions(doc, "Reviewers", AccessRight.READ_AS_INT | AccessRight.VIEW_CONTENT_AS_INT);
            }

            // Condition block 2
            if ((props.get("Status") != null && String.valueOf(props.get("Status").getObjectValue()).equals("Rejected")) && (props.get("Priority") != null && Double.parseDouble(String.valueOf(props.get("Priority").getObjectValue())) < Double.parseDouble("3"))) {
            props.putValue("Maker", Integer.valueOf("0"));
            System.out.println("Updated Maker");
            props.putValue("Checker", Integer.valueOf("0"));
            System.out.println("Updated Checker");
                // Conditional folder operation: RejectedOps
                Folder condFolder2_1 = resolveParentFolder(doc, os, "dynamic:/Archive/${DocumentType}");
                if (condFolder2_1 != null) {
                    unfileFromAll(doc);
                    Folder condFolder2_1_RejectedLowPriority = getOrCreateStaticFolder(doc, os, condFolder2_1, "RejectedLowPriority", "", true);
                    String folderName_condFolder2_1_RejectedClassMap = null;
                    String docClass_condFolder2_1_RejectedClassMap = doc.getClassName();
                    switch(docClass_condFolder2_1_RejectedClassMap) {
                        case "com.acme.Contract": folderName_condFolder2_1_RejectedClassMap = "RejectedContracts"; break;
                        case "com.acme.Invoice": folderName_condFolder2_1_RejectedClassMap = "RejectedInvoices"; break;
                        default: folderName_condFolder2_1_RejectedClassMap = "UNKNOWN"; break;
                    }
                    Folder condFolder2_1_RejectedClassMap = getOrCreateStaticFolder(doc, os, condFolder2_1, folderName_condFolder2_1_RejectedClassMap, "", false);
                }
                setPermissions(doc, "Managers", AccessRight.READ_AS_INT | AccessRight.VIEW_CONTENT_AS_INT);
                setPermissions(doc, "Reviewers", AccessRight.READ_AS_INT | AccessRight.WRITE_AS_INT | AccessRight.VIEW_CONTENT_AS_INT);
            }

            // Condition block 3
            if ((props.get("Status") != null && String.valueOf(props.get("Status").getObjectValue()).equals("Pending")) && (props.get("DocumentType") != null && String.valueOf(props.get("DocumentType").getObjectValue()).equals("Contract"))) {
            props.putValue("Priority", Integer.valueOf("7"));
            System.out.println("Updated Priority");
            props.putValue("Region", "APAC");
            System.out.println("Updated Region");
                // Conditional folder operation: PendingOps
                Folder condFolder3_1 = resolveParentFolder(doc, os, "path:/incoming");
                if (condFolder3_1 != null) {
                    Folder condFolder3_1_DocumentType = getOrCreatePropertyFolder(doc, os, condFolder3_1, "DocumentType", "", true);
                    Folder condFolder3_1_PendingByDate = getOrCreateDateFolder(doc, os, condFolder3_1, "", "dd-MM-yyyy", "", false);
                    doc.save(RefreshMode.REFRESH);
                }
                setPermissions(doc, "Legal", AccessRight.READ_AS_INT | AccessRight.WRITE_AS_INT | AccessRight.VIEW_CONTENT_AS_INT);
                setPermissions(doc, "Finance", AccessRight.READ_AS_INT | AccessRight.VIEW_CONTENT_AS_INT);
            }

            // Condition block 4
            if ((props.get("Status") != null && String.valueOf(props.get("Status").getObjectValue()).equals("Not Approved")) && (props.get("Maker") != null && String.valueOf(props.get("Maker").getObjectValue()).equals("0")) && (props.get("Checker") != null && String.valueOf(props.get("Checker").getObjectValue()).equals("1"))) {
            props.putValue("Maker", Integer.valueOf("1"));
            System.out.println("Updated Maker");
            props.putValue("Checker", Integer.valueOf("0"));
            System.out.println("Updated Checker");
                // Conditional folder operation: NotApprovedOps
                Folder condFolder4_1 = resolveParentFolder(doc, os, "current");
                if (condFolder4_1 != null) {
                    unfileFromAll(doc);
                    Folder condFolder4_1_NotApprovedFolder = getOrCreateStaticFolder(doc, os, condFolder4_1, "NotApprovedFolder", "", true);
                    String folderName_condFolder4_1_NAClassMap = null;
                    String docClass_condFolder4_1_NAClassMap = doc.getClassName();
                    switch(docClass_condFolder4_1_NAClassMap) {
                        case "com.acme.Contract": folderName_condFolder4_1_NAClassMap = "NAContracts"; break;
                        case "com.acme.Invoice": folderName_condFolder4_1_NAClassMap = "NAInvoices"; break;
                        default: folderName_condFolder4_1_NAClassMap = "UNKNOWN"; break;
                    }
                    Folder condFolder4_1_NAClassMap = getOrCreateStaticFolder(doc, os, condFolder4_1, folderName_condFolder4_1_NAClassMap, "", false);
                    doc.save(RefreshMode.REFRESH);
                }
                setPermissions(doc, "FN_ILC_Mkr", AccessRight.READ_AS_INT | AccessRight.VIEW_CONTENT_AS_INT);
                setPermissions(doc, "FN_ILC_Chkr", AccessRight.READ_AS_INT | AccessRight.WRITE_AS_INT | AccessRight.VIEW_CONTENT_AS_INT);
            }

            doc.save(RefreshMode.REFRESH);
            System.out.println("Event completed successfully.");
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void setPermissions(Document doc, String granteeName, int accessMask) {
        try {
            AccessPermissionList perms = doc.get_Permissions();
            boolean found = false;
            Iterator it = perms.iterator();
            while(it.hasNext()) {
                AccessPermission perm = (AccessPermission) it.next();
                if(granteeName.equalsIgnoreCase(perm.get_GranteeName())) {
                    perm.set_AccessMask(accessMask);
                    found = true;
                }
            }
            if(!found) {
                AccessPermission newPerm = Factory.AccessPermission.createInstance();
                newPerm.set_GranteeName(granteeName);
                newPerm.set_AccessMask(accessMask);
                perms.add(newPerm);
            }
            doc.set_Permissions(perms);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private Folder resolveParentFolder(Document doc, ObjectStore os, String parentPath) {
        try {
            if("Doc".equalsIgnoreCase(parentPath) || "current".equalsIgnoreCase(parentPath)) {
                ReferentialContainmentRelationshipSet c = doc.get_Containers();
                Iterator it = c.iterator();
                if(it.hasNext()) return (Folder)((ReferentialContainmentRelationship)it.next()).get_Tail();
            } else if(parentPath != null && parentPath.startsWith("climbUp:")) {
                String[] climbList = parentPath.substring(8).split(",");
                return getStaticParentFolder(doc, climbList);
            } else if(parentPath != null && parentPath.startsWith("dynamic:")) {
                String[] dynamicList = parentPath.substring(8).split(",");
                return getDynamicParentFolder(doc, dynamicList);
            } else if(parentPath != null && parentPath.startsWith("path:")) {
                return Factory.Folder.fetchInstance(os, parentPath.substring(5), null);
            } else if(parentPath != null && parentPath.startsWith("conditionalClimb:")) {
                String[] parts = parentPath.substring(17).split(":");
                if(parts.length == 2) {
                    String[] allowedFolders = parts[0].split(",");
                    int maxLevels = Integer.parseInt(parts[1]);
                    return getConditionalParentFolder(doc, allowedFolders, maxLevels);
                }
            }
        } catch(Exception e){ e.printStackTrace(); }
        return null;
    }

    private Folder getStaticParentFolder(Document doc, String[] climbUpList) {
        try {
            ReferentialContainmentRelationshipSet c = doc.get_Containers();
            Iterator it = c.iterator();
            Folder current = null;
            if(it.hasNext()) current = (Folder)((ReferentialContainmentRelationship)it.next()).get_Tail();
            while(current != null) {
                for(String name : climbUpList) {
                    if(current.get_FolderName().equalsIgnoreCase(name.trim())) return current;
                }
                current = current.get_Parent();
            }
        } catch(Exception e){ e.printStackTrace(); }
        return null;
    }

    private Folder getDynamicParentFolder(Document doc, String[] dynamicList) {
        try {
            ReferentialContainmentRelationshipSet c = doc.get_Containers();
            Iterator it = c.iterator();
            Folder current = null;
            if(it.hasNext()) current = (Folder)((ReferentialContainmentRelationship)it.next()).get_Tail();
            while(current != null) {
                for(String name : dynamicList) {
                    if(current.get_FolderName().toLowerCase().contains(name.trim().toLowerCase())) return current;
                }
                current = current.get_Parent();
            }
        } catch(Exception e){ e.printStackTrace(); }
        return null;
    }

    private Folder getConditionalParentFolder(Document doc, String[] allowedFolders, int maxLevels) {
        try {
            ReferentialContainmentRelationshipSet c = doc.get_Containers();
            Iterator it = c.iterator();
            if(!it.hasNext()) return null;
            Folder current = (Folder)((ReferentialContainmentRelationship)it.next()).get_Tail();
            boolean allowed = false;
            for(String name : allowedFolders) {
                if(name.equalsIgnoreCase(current.get_FolderName())) {
                    allowed = true;
                    break;
                }
            }
            if(!allowed) return current;
            int levels = 0;
            while(current.get_Parent() != null && levels < maxLevels) {
                current = current.get_Parent();
                levels++;
            }
            return current;
        } catch(Exception e) { e.printStackTrace(); return null; }
    }

    private Folder getOrCreateFolder(ObjectStore os, Folder parent, String name, String cls) {
        try { return Factory.Folder.fetchInstance(os, parent.get_PathName() + "/" + name, null); }
        catch (EngineRuntimeException e) { Folder f = Factory.Folder.createInstance(os, cls); f.set_FolderName(name); f.set_Parent(parent); f.save(RefreshMode.REFRESH); return f; }
    }

    private Folder getOrCreateStaticFolder(Document doc, ObjectStore os, Folder parent, String name, String cls, boolean fileHere) throws Exception {
        Folder f = getOrCreateFolder(os, parent, name==null||name.isEmpty()?"unnamed":name, cls);
        if(fileHere) safeFile(doc,f,os);
        return f;
    }

    private Folder getOrCreatePropertyFolder(Document doc, ObjectStore os, Folder parent, String prop, String cls, boolean fileHere) throws Exception {
        String val = null;
        try { val = doc.getProperties().getStringValue(prop); } catch(Exception ex) { val = "<undefined>"; }
        Folder f = getOrCreateFolder(os,parent,val,cls);
        if(fileHere) safeFile(doc,f,os);
        return f;
    }

    private Folder getOrCreateDateFolder(Document doc, ObjectStore os, Folder parent, String prop, String fmt, String cls, boolean fileHere) throws Exception {
        Date d = null;
        try { d = doc.getProperties().getDateTimeValue(prop); } catch(Exception ex) { d = new Date(); }
        SimpleDateFormat sdf = new SimpleDateFormat(fmt); sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String name = sdf.format(d);
        Folder f = getOrCreateFolder(os,parent,name,cls);
        if(fileHere) safeFile(doc,f,os);
        return f;
    }

    private void safeFile(Document doc, Folder f, ObjectStore os) {
        try { ReferentialContainmentRelationship rcr = Factory.ReferentialContainmentRelationship.createInstance(os, null, null, null); rcr.set_Head(doc); rcr.set_Tail(f); rcr.save(RefreshMode.NO_REFRESH); }
        catch(Exception e){ e.printStackTrace(); }
    }

    private void unfileFromAll(Document doc) throws Exception {
        ReferentialContainmentRelationshipSet rcrs = doc.get_Containers(); Iterator it = rcrs.iterator();
        while(it.hasNext()) { ReferentialContainmentRelationship rcr = (ReferentialContainmentRelationship)it.next(); rcr.delete(); rcr.save(RefreshMode.NO_REFRESH); }
        doc.save(RefreshMode.REFRESH);
    }

}

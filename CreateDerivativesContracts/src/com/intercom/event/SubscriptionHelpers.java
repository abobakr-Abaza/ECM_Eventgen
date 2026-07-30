package com.intercom.event;
import com.filenet.api.collection.ReferentialContainmentRelationshipSet;
import com.filenet.api.constants.RefreshMode;
import com.filenet.api.core.*;
import com.filenet.api.exception.EngineRuntimeException;
import com.filenet.api.security.AccessPermission;
import com.filenet.api.collection.AccessPermissionList;
// import java.text.SimpleDateFormat;
import java.util.*;

public class SubscriptionHelpers {

	
	
	public static String checkJar(){
		return "All Working fine";
	}
	
	@SuppressWarnings("unchecked")
	public static void setPermissions(Document doc, String granteeName, int accessMask) {
	        try {
	            AccessPermissionList perms = doc.get_Permissions();
	            boolean found = false;
	            Iterator<?> it = perms.iterator();
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
	@SuppressWarnings("unchecked")
	public static void setFolderPermissions(Folder folder, String granteeName, int accessMask) {
	    try {
	        AccessPermissionList perms = folder.get_Permissions();
	        boolean found = false;

	        Iterator<?> it = perms.iterator();
	        while (it.hasNext()) {
	            AccessPermission perm = (AccessPermission) it.next();
	            if (granteeName.equalsIgnoreCase(perm.get_GranteeName())) {
	                perm.set_AccessMask(accessMask);
	                found = true;
	            }
	        }

	        if (!found) {
	            AccessPermission newPerm = Factory.AccessPermission.createInstance();
	            newPerm.set_GranteeName(granteeName);
	            newPerm.set_AccessMask(accessMask);
	            perms.add(newPerm);
	        }

	        folder.set_Permissions(perms);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

	     public static Folder resolveParentFolder(Document doc, ObjectStore os, String parentPath) {
	        try {
	            if("Doc".equalsIgnoreCase(parentPath) || "current".equalsIgnoreCase(parentPath)) {
	                ReferentialContainmentRelationshipSet c = doc.get_Containers();
	                Iterator<?> it = c.iterator();
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

	     public static Folder getStaticParentFolder(Document doc, String[] climbUpList) {
	        try {
	            ReferentialContainmentRelationshipSet c = doc.get_Containers();
	            Iterator<?> it = c.iterator();
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

	     public static Folder getDynamicParentFolder(Document doc, String[] dynamicList) {
	        try {
	            ReferentialContainmentRelationshipSet c = doc.get_Containers();
	            Iterator<?> it = c.iterator();
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

	     public static Folder getConditionalParentFolder(Document doc, String[] allowedFolders, int maxLevels) {
	        try {
	            ReferentialContainmentRelationshipSet c = doc.get_Containers();
	            Iterator<?> it = c.iterator();
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

	     public static Folder getOrCreateFolder(ObjectStore os, Folder parent, String name, String cls) {
	        try { return Factory.Folder.fetchInstance(os, parent.get_PathName() + "/" + name, null); }
	        catch (EngineRuntimeException e) { Folder f = Factory.Folder.createInstance(os, cls); f.set_FolderName(name); f.set_Parent(parent); f.save(RefreshMode.REFRESH); return f; }
	    }

	     public static Folder getOrCreateStaticFolder(Document doc, ObjectStore os, Folder parent, String name, String cls, boolean fileHere) throws Exception {
	        Folder f = getOrCreateFolder(os, parent, name==null||name.isEmpty()?"unnamed":name, cls);
	        if(fileHere) safeFile(doc,f,os);
	        return f;
	    }

	     public static Folder getOrCreatePropertyFolder(Document doc, ObjectStore os, Folder parent, String prop, String cls, boolean fileHere) throws Exception {
	        String val = null;
	        try { val = doc.getProperties().getStringValue(prop); } catch(Exception ex) { val = "<undefined>"; }
	        Folder f = getOrCreateFolder(os,parent,val,cls);
	        if(fileHere) safeFile(doc,f,os);
	        return f;
	    }

	     /*
	     public static Folder getOrCreateDateFolder(Document doc, ObjectStore os, Folder parent, String prop, String fmt, String cls, boolean fileHere) throws Exception {
	        Date d = null;
	        try { d = doc.getProperties().getDateTimeValue(prop); } catch(Exception ex) { d = new Date(); }
	        SimpleDateFormat sdf = new SimpleDateFormat(fmt); sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
	        String name = sdf.format(d);
	        Folder f = getOrCreateFolder(os,parent,name,cls);
	        if(fileHere) safeFile(doc,f,os);
	        return f;
	    }*/
	     
	     public static Folder getOrCreateDateFolder(Document doc, ObjectStore os, Folder parent, String prop, String fmt, String cls, boolean fileHere) throws Exception {
	    	 Date d = null;
	    	    try { 	
	    	        d = doc.getProperties().getDateTimeValue(prop);
	    	    } catch(Exception ex) { 
	    	        d = new Date(); //current date
	    	    }

	    	    // Step 2: Extract year, month, day from the date
	    	    Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
	    	    cal.setTime(d);
	    	    String year = String.valueOf(cal.get(Calendar.YEAR));
	    	    // Month is 0-based, so add 1 and pad with leading zero
	    	    String month = String.format("%02d", cal.get(Calendar.MONTH) + 1);
	    	    String day = String.format("%02d", cal.get(Calendar.DAY_OF_MONTH));

	    	    // Step 3: Create or get folders for year -> month -> day
	    	    Folder yearFolder = getOrCreateFolder(os, parent, year,cls);
	    	    Folder monthFolder = getOrCreateFolder(os, yearFolder, month, cls);
	    	    Folder dayFolder = getOrCreateFolder(os, monthFolder, day, cls);

	    	    // Step 4: File the document if needed
	    	    if(fileHere) safeFile(doc, dayFolder, os);

	    	    return dayFolder;
		    }
	     
	     public static Folder getParentFolder(Document doc){
	 		Folder parentFolder = null;

	 		try {
	 			DynamicReferentialContainmentRelationship srcr = null;
	 			ReferentialContainmentRelationshipSet rcrs = doc.get_Containers();
	 			Iterator<?> iter = rcrs.iterator();
	 			if (iter.hasNext()) {
	 				srcr = (DynamicReferentialContainmentRelationship) iter.next();
	 				System.out.println(
	 						"IntercomJavaEventHandler::onEvent()::Src Object created ======>" + srcr.get_Name());
	 				parentFolder = (Folder) srcr.get_Tail();
	 			}
	 		}catch(Exception e){
	 			e.printStackTrace();
	 		}

	 		return parentFolder;
	 	}
	     
	     

	     public static void safeFile(Document doc, Folder f, ObjectStore os) {
	        try { ReferentialContainmentRelationship rcr = Factory.ReferentialContainmentRelationship.createInstance(os, null, null, null); rcr.set_Head(doc); rcr.set_Tail(f); rcr.save(RefreshMode.NO_REFRESH); }
	        catch(Exception e){ e.printStackTrace(); }
	    }

	     public static void unfileFromAll(Document doc) throws Exception {
	        ReferentialContainmentRelationshipSet rcrs = doc.get_Containers(); Iterator<?> it = rcrs.iterator();
	        while(it.hasNext()) { ReferentialContainmentRelationship rcr = (ReferentialContainmentRelationship)it.next(); rcr.delete(); rcr.save(RefreshMode.NO_REFRESH); }
	        doc.save(RefreshMode.REFRESH);
	    }
	     
}

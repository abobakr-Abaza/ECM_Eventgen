# EventGen.java – Event Output Code Generator for FileNet P8

## Overview

`EventGen.java` is a Java utility that **reads a JSON configuration file** (`config.json`) and **generates a fully functional `Outputevent.java`** class for FileNet P8.

- Automates **document event handling**, **folder creation**, and **permission assignment**.  
- Supports **static and dynamic folder hierarchies**, property-based folder names, date-based folder names, and conditional updates.  
- Designed to be compatible with **Java 1.8** and **FileNet P8 APIs**, with **no external dependencies**.  
- Intended for use in **document archiving workflows**, **event-driven automation**, and **compliance-driven content management**.

---

## Key Features

1. **Property Updates**
   - Supports `update` and `print` actions for document properties.
   - Data types supported: `String`, `Boolean`, `Integer`, `DateTime`.
   - Auto-generates Java code to set or log property values.

2. **Folder Operations**
   - Supports nested folder creation:
     - **Static folders** – hardcoded names.
     - **Property folders** – folder name based on a document property.
     - **Date folders** – folder name derived from a property formatted as a date.
   - Supports **dynamic parent folder resolution**:
     - **Static climbing** (`climbUp:`) – exact match folder traversal.
     - **Dynamic climbing** (`dynamic:`) – first folder whose name contains a keyword.
   - Supports `unfileFirst` to remove the document from all folders before filing.
   - Supports optional `saveAfterOps`.

3. **Security Updates**
   - Configurable access rights for users/groups.
   - Supports multiple permissions per grantee.
   - Updates existing permissions or adds new entries if not found.

4. **Conditional Operations**
   - Supports multiple condition blocks.
   - Conditions can check:
     - `equals`, `contains` for strings.
     - `gt`, `lt` for numeric values.
     - `before`, `after` for dates.
   - Conditional operations can include:
     - Property updates.
     - Folder operations.
     - Security updates.

5. **JSON-Driven Configuration**
   - Reads `config.json` defining:
     - `properties`: property updates to perform.
     - `folderOperations`: nested folder creation operations.
     - `security`: inline security updates.
     - `conditions`: conditional operations with nested folders/security.
     - `climbUpList` and `dynamicParentList` for parent folder resolution.

6. **Output**
   - Generates `Outputevent.java` with:
     - Complete `onEvent` method.
     - Folder helper methods (`getOrCreateFolder`, `getOrCreatePropertyFolder`, `getOrCreateDateFolder`, `getOrCreateStaticFolder`).
     - Permission helper method (`setPermissions`).
     - Full static and dynamic climbing logic.

---

## Architecture & Workflow

### 1. Main Method

```java
ObjectMapper mapper = new ObjectMapper();
EventConfig config = mapper.readValue(new File("config.json"), EventConfig.class);

FileWriter fw = new FileWriter("Outputevent.java");
writeHeader(fw);
writeOnEvent(fw, config);
writeHelperMethods(fw);
fw.write("}\n"); // close class
fw.close();
```

- Loads configuration from `config.json` into a **POJO** (`EventConfig`).
- Opens `Outputevent.java` for writing.
- Sequentially generates:
  - Class header and imports.
  - `onEvent` logic (properties, folder operations, security, conditions).
  - Helper methods for folder creation and permissions.

---

### 2. Folder Resolution

**Static Climbing (`climbUp:`)**

```java
private Folder getStaticParentFolder(Document doc, String[] climbUpList)
```

- Starts from the document’s current folder.
- Moves **up the folder hierarchy** using `get_Parent()`.
- Returns the **first folder whose name exactly matches** a name in `climbUpList`.

**Dynamic Climbing (`dynamic:`)**

```java
private Folder getDynamicParentFolder(Document doc, String[] dynamicList)
```

- Starts from the current folder.
- Moves **up the hierarchy**.
- Returns the **first folder whose name contains** a string in `dynamicList`, case-insensitive.

---

### 3. Folder Creation Helpers

- `getOrCreateFolder` → fetches or creates a folder with a given name.
- `getOrCreateStaticFolder` → static folder name, optional file the document.
- `getOrCreatePropertyFolder` → folder named by document property.
- `getOrCreateDateFolder` → folder named by formatted date property.
- `safeFile` → files the document into a folder safely.
- `unfileFromAll` → removes a document from all existing folders.

---

### 4. Security Helper

```java
private void setPermissions(Document doc, String granteeName, int accessMask)
```

- Updates or creates an **AccessPermission** object.
- Supports multiple rights combined using `|` bitwise OR.
- Works for `READ`, `WRITE`, `DELETE`, `VIEW_CONTENT`, `MINOR_VERSION`, `MAJOR_VERSION`, etc.

---

### 5. Property Code Generation

```java
private static String generatePropertyCode(EventConfig.PropertyUpdate def)
```

- Produces Java code for updating or printing properties.
- Handles type-specific conversions (String, Boolean, Integer, DateTime).

---

### 6. Folder Code Generation

```java
private static void generateFolderCode(FileWriter fw, EventConfig.FolderNode node, String parentVar, int indent)
```

- Recursively generates code for **nested folder trees**.
- Supports all folder types (static, property-based, date-based).
- Properly indents and assigns unique variable names.

---

### 7. Condition Code Generation

```java
private static String generateConditionCode(EventConfig.ConditionBlock block)
```

- Produces boolean expressions for conditions.
- Supports string, numeric, and date comparisons.
- Integrated directly into generated `onEvent` code.

---

## Configuration Structure (`config.json`)

- **properties**: list of property updates.
- **folderOperations**: array of folder creation operations.
- **security**: inline security updates.
- **conditions**: conditional property/folder/security operations.
- **climbUpList**: for static parent folder resolution.
- **dynamicParentList**: for dynamic parent folder resolution.

**Example JSON snippet:**

```json
{
  "properties": [
    {"name": "DocumentTitle", "action": "update", "type": "String", "value": "Invoice 123"}
  ],
  "folderOperations": [
    {
      "name": "Invoices2025",
      "parentPath": "climbUp:Projects,Archives",
      "unfileFirst": true,
      "tree": [
        {"type": "static", "name": "Invoices"},
        {"type": "date", "propertyName": "CreationDate", "dateFormat": "yyyy", "className": "Folder", "fileHere": true}
      ],
      "saveAfterOps": true
    }
  ],
  "security": [
    {"grantee": "JohnDoe", "permissions": ["READ", "WRITE"]}
  ],
  "conditions": [
    {
      "conditions": [{"property": "Priority", "operator": "equals", "value": "High"}],
      "operations": {
        "propertyUpdates": [{"name": "Reviewed", "action": "update", "type": "Boolean", "value": "false"}]
      }
    }
  ],
  "climbUpList": ["Projects", "Archives"],
  "dynamicParentList": ["Client", "Department"]
}
```

---

## Usage

1. Prepare `config.json` with desired properties, folders, permissions, and conditions.
2. Run `EventGen.java`:

```bash
java -cp .;jackson-databind-2.x.jar com.event.EventGen
```

3. `Outputevent.java` is generated automatically.
4. Compile and deploy `Outputevent.java` as a FileNet Event Handler.

---

## Design Considerations

- **Safety**: Handles missing properties, null folders, and missing parent folders without throwing runtime errors.  
- **Extensibility**: Easy to extend with new folder types, permission types, or condition operators.  
- **Reusability**: Same JSON config can be reused across multiple events.  
- **Java 1.8 compatible**: Uses only standard Java and FileNet APIs.

---

## Conclusion

`EventGen.java` provides a **robust, maintainable, and configurable framework** for generating event-driven FileNet handlers. It enables:

- Automated document organization.  
- Metadata-driven folder structures.  
- Dynamic security updates.  
- Conditional logic for enterprise-grade document management.

This tool is ideal for **large-scale content management automation**, ensuring consistent and compliant folder hierarchies, metadata, and security policies across FileNet repositories.


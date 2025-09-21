# JSON Designer – Configuration Designer for EventGen

## Overview

The JSON Designer is a **visual and structured guide** for creating `config.json` files used by `EventGen.java`.

- Helps developers and administrators define **folder structures**, **property updates**, **security permissions**, and **conditional logic**.
- Ensures that the JSON configuration is **valid, consistent, and compatible** with EventGen.
- Provides clear definitions and **examples** for each configuration block.

---

## Configuration Structure

### 1. Properties

**Purpose:** Define updates or prints for document properties.

**Structure:**
```json
"properties": [
  {
    "name": "<PropertyName>",
    "action": "update|print",
    "type": "String|Boolean|Integer|DateTime",
    "value": "<Value>"
  }
]
```

**Example:**
```json
"properties": [
  {"name": "DocumentTitle", "action": "update", "type": "String", "value": "Invoice 123"},
  {"name": "Reviewed", "action": "update", "type": "Boolean", "value": "false"}
]
```

---

### 2. Folder Operations

**Purpose:** Define folder creation and filing operations.

**Structure:**
```json
"folderOperations": [
  {
    "name": "<OperationName>",
    "parentPath": "climbUp:<Folder1>,<Folder2>" | "dynamic:<Keyword1>,<Keyword2>" | "path:/Absolute/Path",
    "unfileFirst": true|false,
    "tree": [<FolderNode>, ...],
    "saveAfterOps": true|false
  }
]
```

**Folder Node Structure:**
```json
{
  "type": "static|property|date",
  "name": "<StaticName>",
  "propertyName": "<PropertyForFolderName>",
  "dateFormat": "<DateFormat>",
  "className": "Folder",
  "fileHere": true|false,
  "children": [<FolderNode>, ...]
}
```

**Example:**
```json
"folderOperations": [
  {
    "name": "Invoices2025",
    "parentPath": "climbUp:Projects,Archives",
    "unfileFirst": true,
    "tree": [
      {"type": "static", "name": "Invoices", "className": "Folder", "fileHere": false},
      {"type": "date", "propertyName": "CreationDate", "dateFormat": "yyyy", "className": "Folder", "fileHere": true}
    ],
    "saveAfterOps": true
  }
]
```

---

### 3. Security Updates

**Purpose:** Define access rights for users or groups.

**Structure:**
```json
"security": [
  {
    "grantee": "<UserOrGroupName>",
    "permissions": ["READ", "WRITE", "DELETE", "VIEW_CONTENT", "MINOR_VERSION", "MAJOR_VERSION"]
  }
]
```

**Example:**
```json
"security": [
  {"grantee": "JohnDoe", "permissions": ["READ", "WRITE"]},
  {"grantee": "JaneSmith", "permissions": ["VIEW_CONTENT", "MINOR_VERSION"]}
]
```

---

### 4. Conditions

**Purpose:** Execute property/folder/security updates only if certain conditions are met.

**Structure:**
```json
"conditions": [
  {
    "conditions": [
      {"property": "<PropertyName>", "operator": "equals|contains|gt|lt|before|after", "value": "<Value>"}
    ],
    "operations": {
      "propertyUpdates": [<PropertyUpdate>],
      "folderOperations": [<FolderOperation>],
      "securityUpdates": [<SecurityUpdate>]
    }
  }
]
```

**Example:**
```json
"conditions": [
  {
    "conditions": [{"property": "Priority", "operator": "equals", "value": "High"}],
    "operations": {
      "propertyUpdates": [{"name": "Reviewed", "action": "update", "type": "Boolean", "value": "false"}]
    }
  }
]
```

---

### 5. Parent Folder Lists

**Static Climb List:**
- Exact match folder names used with `climbUp:` paths.
```json
"climbUpList": ["Projects", "Archives"]
```

**Dynamic Parent List:**
- Keywords used for dynamic climbing (`dynamic:`).
```json
"dynamicParentList": ["Client", "Department"]
```

---

## Best Practices

1. **Always define parentPath** for each folder operation (`climbUp:`, `dynamic:`, `path:`).
2. **Use meaningful names** for folder operations and property updates.
3. **Validate JSON syntax** before running EventGen to prevent generation errors.
4. **Test with a sample document** to ensure folders and permissions are applied correctly.
5. **Keep tree depth reasonable** to prevent performance issues during event execution.
6. **Combine static and dynamic climbing** only if necessary, for flexible document placement.

---

## Example Full JSON

```json
{
  "properties": [
    {"name": "DocumentTitle


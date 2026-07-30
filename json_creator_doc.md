
# 📘 JSON Creator (JSON Designer) Documentation  

## Overview  
**JSON Creator (also called JSON Designer)** is a configuration-driven utility designed to generate and manage structured JSON configuration files.  
These JSON files act as the **blueprint** for generating Java event handlers (like `Outputevent.java`) in an Event-Driven FileNet system.  

Instead of hardcoding logic, users describe **folders, properties, climbing strategies, permissions, and security rules** inside a single `config.json` file.  
The `EventGen` program then consumes this JSON and outputs Java code implementing the rules.  

---

## 🔧 Configuration File: `config.json`  

The heart of the system is the JSON file. This file defines **everything** about how documents should be filed, secured, and handled.  

A full example is below:  

```json
{
  "climbUpList": ["Projects", "Archives"],
  "dynamicParentList": ["Client", "Department"],

  "staticFolders": [
    {
      "name": "Invoices",
      "class": "Folder",
      "fileHere": false,
      "children": [
        {
          "name": "2025",
          "class": "Folder",
          "fileHere": false,
          "children": [
            {
              "type": "property",
              "name": "DocumentTitle",
              "class": "Folder",
              "fileHere": true
            },
            {
              "type": "property",
              "name": "Author",
              "class": "Folder",
              "fileHere": true
            }
          ]
        }
      ]
    }
  ],

  "dynamicFolders": [
    {
      "name": "Reports",
      "class": "Folder",
      "fileHere": false,
      "children": [
        {
          "type": "property",
          "name": "Priority",
          "class": "Folder",
          "fileHere": true
        },
        {
          "type": "property",
          "name": "Reviewed",
          "class": "Folder",
          "fileHere": true
        }
      ]
    }
  ],

  "permissions": [
    {
      "grantee": "JohnDoe",
      "accessMask": "READ|WRITE"
    },
    {
      "grantee": "JaneSmith",
      "accessMask": "VIEW_CONTENT|MINOR_VERSION"
    }
  ]
}
```

---

## 📂 Configuration Sections  

### 1. **`climbUpList`**  
- **Type:** Array of strings  
- **Purpose:** Defines **static folder names** to climb up to in the folder hierarchy.  
- **How it works:**  
  - The system climbs **upward** from the document’s current folder until it finds one matching an entry in this list.  
  - This folder becomes the **anchor point** for static folder creation.  

🔹 *Example:*  
```json
"climbUpList": ["Projects", "Archives"]
```
→ Will climb up until it finds **Projects** or **Archives**.  

---

### 2. **`dynamicParentList`**  
- **Type:** Array of strings  
- **Purpose:** Defines **dynamic folder names** to match by substring when climbing upward.  
- **How it works:**  
  - The system searches upward until it finds a folder name containing one of these terms.  
  - This folder becomes the **anchor point** for dynamic folder creation.  

🔹 *Example:*  
```json
"dynamicParentList": ["Client", "Department"]
```
→ Matches folders like `Client123`, `SalesDepartment`, etc.  

---

### 3. **`staticFolders`**  
- **Type:** Array of folder definitions  
- **Purpose:** Defines a **tree of static folders** to create/file documents into.  

Each folder object supports:  
- `name`: Name of the folder to create.  
- `class`: Usually `"Folder"`.  
- `fileHere`: Boolean. If true, the document is filed directly into this folder.  
- `children`: Array of nested folder definitions.  
- `type`: `"static"`, `"property"`, or `"date"` (default is `"static"`).  

🔹 *Example:*  
```json
"staticFolders": [
  {
    "name": "Invoices",
    "class": "Folder",
    "fileHere": false,
    "children": [
      {
        "name": "2025",
        "class": "Folder",
        "fileHere": false,
        "children": [
          { "type": "property", "name": "DocumentTitle", "class": "Folder", "fileHere": true }
        ]
      }
    ]
  }
]
```
→ Creates a folder hierarchy: `Invoices/2025/[DocumentTitle value]` and files the document inside.  

---

### 4. **`dynamicFolders`**  
- **Type:** Array of folder definitions  
- **Purpose:** Same structure as `staticFolders`, but applies when a **dynamic parent folder** is found.  

🔹 *Example:*  
```json
"dynamicFolders": [
  {
    "name": "Reports",
    "class": "Folder",
    "fileHere": false,
    "children": [
      { "type": "property", "name": "Priority", "class": "Folder", "fileHere": true }
    ]
  }
]
```
→ Under the dynamic parent (e.g., `Client123`), creates `Reports/[Priority value]`.  

---

### 5. **`permissions`**  
- **Type:** Array of objects  
- **Purpose:** Defines access control rules applied to the document.  

Each entry has:  
- `grantee`: The user or group name.  
- `accessMask`: Combination of FileNet access rights (pipe-separated).  

🔹 *Example:*  
```json
"permissions": [
  { "grantee": "JohnDoe", "accessMask": "READ|WRITE" },
  { "grantee": "JaneSmith", "accessMask": "VIEW_CONTENT|MINOR_VERSION" }
]
```
→ Grants JohnDoe read/write rights, JaneSmith content viewing + minor versioning rights.  

---

## 🔀 Folder Types Explained  

1. **Static Folders (`type: "static"`)**  
   - Predefined folder names.  
   - Always created as-is.  
   - Example: `"name": "Invoices"`.  

2. **Property Folders (`type: "property"`)**  
   - Folder names are taken from a document property.  
   - Example: `"name": "Author"` → creates a folder named after the document’s Author property.  

3. **Date Folders (`type: "date"`)**  
   - Folder names are based on formatted dates.  
   - Supports format strings like `"yyyy"`, `"MM"`, `"dd"`.  
   - Example: `"name": "CreatedDate", "format": "yyyy/MM/dd"` → folder structure like `2025/09/06`.  

---

## 🔑 Access Rights Mapping  

- `READ` → `AccessRight.READ_AS_INT`  
- `WRITE` → `AccessRight.WRITE_AS_INT`  
- `VIEW_CONTENT` → `AccessRight.VIEW_CONTENT_AS_INT`  
- `MINOR_VERSION` → `AccessRight.MINOR_VERSION_AS_INT`  
- (additional rights can be added per FileNet documentation)  

---

## ⚙️ EventGen Workflow  

1. `config.json` is written using the above schema.  
2. `EventGen` reads the JSON.  
3. It generates `Outputevent.java` with logic:  
   - Resolving static/dynamic parents.  
   - Creating nested folder structures.  
   - Filing the document.  
   - Applying permissions.  
4. Developer compiles and deploys `Outputevent.java` as an Event Handler in FileNet.  
5. Whenever a document event occurs, FileNet executes the handler.  

---

## ✅ Example Scenario  

**Input config.json**:  
```json
{
  "climbUpList": ["Projects"],
  "dynamicParentList": ["Client"],
  "staticFolders": [
    { "name": "Invoices", "class": "Folder", "fileHere": false }
  ],
  "dynamicFolders": [
    { "name": "Reports", "class": "Folder", "fileHere": true }
  ],
  "permissions": [
    { "grantee": "Admin", "accessMask": "READ|WRITE" }
  ]
}
```

**Resulting Behavior:**  
- System climbs up to `Projects`.  
- Files document under `Invoices`.  
- If under `Client123`, also files into `Client123/Reports`.  
- Grants Admin user read/write access.  

---

## 📑 Best Practices  

1. Always define **at least one** `climbUpList` or `dynamicParentList`.  
2. Use **property folders** for high-cardinality categories like `ClientID`.  
3. Use **date folders** for time-based filing (e.g., invoices).  
4. Keep `permissions` simple; overuse of access rights can slow event execution.  
5. Validate JSON with a schema checker before deploying.  

---

## 🚀 Future Extensions  

- **Custom Actions:** JSON entries that trigger Java methods.  
- **Conditional Rules:** e.g., apply permissions only if `Department=Finance`.  
- **Multi-Repository Support:** Define repository IDs in JSON.  

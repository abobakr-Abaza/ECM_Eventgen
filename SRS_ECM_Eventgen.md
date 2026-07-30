# Software Requirements Specification (SRS)
# ECM_Eventgen - FileNet P8 Event Handler Code Generator

**Version:** 1.0
**Date:** December 14, 2025
**Project:** ECM_Eventgen

---

## Table of Contents

1. [Introduction](#1-introduction)
2. [Overall Description](#2-overall-description)
3. [System Architecture](#3-system-architecture)
4. [Functional Requirements](#4-functional-requirements)
5. [Input Specifications](#5-input-specifications)
6. [Output Specifications](#6-output-specifications)
7. [Input/Output Scenarios](#7-inputoutput-scenarios)
8. [Data Models](#8-data-models)
9. [External Interfaces](#9-external-interfaces)
10. [Non-Functional Requirements](#10-non-functional-requirements)
11. [Constraints and Limitations](#11-constraints-and-limitations)
12. [Integration with CreateDerivativesContracts](#12-integration-with-createderivativescontracts)
13. [End-to-End Pipeline](#13-end-to-end-pipeline)

---

## 1. Introduction

### 1.1 Purpose

This Software Requirements Specification (SRS) document describes the functional and non-functional requirements for **ECM_Eventgen**, a Java-based code generation tool designed to automate the creation of FileNet P8 event handler classes from declarative JSON configuration files.

### 1.2 Scope

ECM_Eventgen is a developer productivity tool that:
- Reads JSON configuration files defining document event handling workflows
- Generates fully functional Java event handler classes implementing the FileNet `EventActionHandler` interface
- Automates repetitive boilerplate code for document management operations

### 1.3 Definitions and Acronyms

| Term | Definition |
|------|------------|
| **ECM** | Enterprise Content Management |
| **FileNet P8** | IBM's enterprise content management platform |
| **Event Handler** | A class that responds to document lifecycle events in FileNet |
| **Subscription** | A FileNet mechanism that triggers event handlers on document changes |
| **POJO** | Plain Old Java Object |
| **JSON** | JavaScript Object Notation |

### 1.4 Target Users

- FileNet P8 developers
- ECM solution architects
- System integrators working with IBM Content Navigator

---

## 2. Overall Description

### 2.1 Product Perspective

ECM_Eventgen is a standalone code generation utility that operates as part of the FileNet P8 development workflow. It sits between the requirements/design phase and the implementation phase, transforming declarative configurations into executable Java code.

```
[JSON Configuration] --> [ECM_Eventgen] --> [Java Event Handler Classes] --> [FileNet P8 Deployment]
```

### 2.2 Product Functions

The tool performs the following high-level functions:

| ID | Function | Description |
|----|----------|-------------|
| F1 | Configuration Parsing | Parse JSON configuration files into internal data models |
| F2 | Property Update Generation | Generate code for document property modifications |
| F3 | Folder Operation Generation | Generate code for folder creation and document filing |
| F4 | Security Management Generation | Generate code for permission grants and access control |
| F5 | Conditional Logic Generation | Generate if-else chains based on property conditions |
| F6 | Java File Output | Write complete, compilable Java class files |

### 2.3 Operating Environment

- **Platform:** Windows, Linux, macOS (any Java-supported OS)
- **Java Version:** Java 8 (1.8) or higher
- **IDE Support:** Eclipse (project files included), IntelliJ IDEA, NetBeans
- **Build System:** Manual compilation or IDE-based builds

### 2.4 Dependencies

| Dependency | Version | Purpose |
|------------|---------|---------|
| Jackson Databind | 2.15.2 | JSON parsing and object mapping |
| Jackson Core | 2.15.2 | Core JSON processing |
| Jackson Annotations | 2.15.2 | JSON annotation support |
| SubscriptionHelpers.jar | Custom | FileNet utility methods for generated code |
| FileNet P8 API | - | Runtime dependency for generated classes |

---

## 3. System Architecture

### 3.1 Component Overview

```
[JSON Configuration]
        |
        v
+-------------------+
|   MainApp         |
+-------------------+
        |
        v
+-------------------+
| SubscriptionService|
+-------------------+
        |
        v
+-------------------+
|  FileWriter       |
+-------------------+
        |
        v
[Generated Java Code]

```

### 3.2 Class Responsibilities

| Component                | Responsibility                           |
| ------------------------ | ---------------------------------------- |
| `MainApp`                | Application entry point                  |
| `SubscriptionService`    | Orchestrates parsing and code generation |
| `StringUtils`            | Safe string handling and Java escaping   |
| `SubscriptionDefinition` | Root subscription model                  |
| `EventConfig`            | Event behavior definition                |
| `ConditionBlock`         | Conditional execution blocks             |
| `Operations`             | Actions executed when conditions match   |


### 3.3 Processing Flow

1. **Initialization**: Load Jackson ObjectMapper
2. **Parsing**: Deserialize JSON file into `SubscriptionDefination` object
3. **Iteration**: Loop through each `SubscriptionEvent` in the configuration
4. **Generation**: For each event, generate a complete Java class file
5. **Output**: Write generated code to `.java` files

---

## 4. Functional Requirements

### 4.1 Configuration Loading (FR-01)

| ID | FR-01 |
|----|-------|
| **Title** | Load JSON Configuration |
| **Description** | The system shall load and parse JSON configuration files using Jackson |
| **Input** | Path to JSON configuration file |
| **Output** | Populated `SubscriptionDefination` object |
| **Priority** | High |

### 4.2 Property Update Code Generation (FR-02)

| ID | FR-02 |
|----|-------|
| **Title** | Generate Property Update Code |
| **Description** | The system shall generate Java code for updating document properties |
| **Supported Types** | String, Boolean, Integer, DateTime |
| **Actions** | `update` (set value), `print` (output value) |
| **Priority** | High |

### 4.3 Folder Operation Code Generation (FR-03)

| ID | FR-03 |
|----|-------|
| **Title** | Generate Folder Operation Code |
| **Description** | The system shall generate code for creating folder hierarchies and filing documents |
| **Folder Types** | static, property, date, classBased |
| **Parent Resolution** | path, climbUp, dynamic, current |
| **Priority** | High |

### 4.4 Security Code Generation (FR-04)

| ID | FR-04 |
|----|-------|
| **Title** | Generate Security Update Code |
| **Description** | The system shall generate code for applying access permissions to documents |
| **Permissions** | READ, WRITE, DELETE, VIEW_CONTENT, MINOR_VERSION, MAJOR_VERSION, CREATE_INSTANCE, LINK, UNLINK, CHANGE_STATE, WRITE_ACL, READ_ACL, DELEGATE_ACCESS |
| **Priority** | Medium |

### 4.5 Conditional Logic Code Generation (FR-05)

| ID | FR-05 |
|----|-------|
| **Title** | Generate Conditional Logic Code |
| **Description** | The system shall generate if-else chains based on property conditions |
| **Operators** | equals, not equals, contains, gt, lt, before, after |
| **Logic** | AND (&&), OR (or) |
| **Priority** | High |

### 4.6 Java Class Generation (FR-06)

| ID | FR-06 |
|----|-------|
| **Title** | Generate Complete Java Classes |
| **Description** | The system shall output complete, compilable Java class files implementing `EventActionHandler` |
| **Components** | Package declaration, imports, class definition, onEvent method |
| **Priority** | High |

---

## 5. Input Specifications

### 5.1 Input File Format

**Format:** JSON (JavaScript Object Notation)
**Encoding:** UTF-8
**File Extension:** `.json`

### 5.2 JSON Structure

#### 5.2.1 Root Structure (SubscriptionDefination)

```json
{
  "subscriptionName": "string",
  "subscriptionEvent": [
    {
      "caseType": "string",
      "eventConfig": { /* EventConfig object */ }
    }
  ]
}
```

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `subscriptionName` | String | Yes | Name of the subscription for logging |
| `subscriptionEvent` | Array | Yes | List of events to generate handlers for |
| `subscriptionEvent[].caseType` | String | Yes | Name of the generated Java class |
| `subscriptionEvent[].eventConfig` | Object | Yes | Event configuration details |

#### 5.2.2 EventConfig Structure

```json
{
  "properties": [ /* PropertyUpdate[] */ ],
  "folderOperations": [ /* FolderOperation[] */ ],
  "security": [ /* SecurityUpdate[] */ ],
  "conditions": [ /* ConditionBlock[] */ ],
  "climbUpList": [ "string" ],
  "dynamicParentList": [ "string" ]
}
```

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `properties` | Array | No | Property update operations |
| `folderOperations` | Array | No | Folder creation and filing operations |
| `security` | Array | No | Security permission assignments |
| `conditions` | Array | No | Conditional logic blocks |
| `climbUpList` | Array | No | Folder names for climbUp resolution |
| `dynamicParentList` | Array | No | Keywords for dynamic parent resolution |

#### 5.2.3 PropertyUpdate Structure

```json
{
  "action": "update|print",
  "name": "PropertyName",
  "value": "PropertyValue",
  "type": "String|Boolean|Integer|DateTime"
}
```

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `action` | String | Yes | `update` to set value, `print` to output value |
| `name` | String | Yes | Name of the document property |
| `value` | String | Conditional | Value to set (required for `update`) |
| `type` | String | No | Data type for casting (default: String) |

#### 5.2.4 FolderOperation Structure

```json
{
  "name": "OperationName",
  "parentPath": "path:/Path|climbUp:Name|dynamic:Keyword|current",
  "unfileFirst": true|false,
  "saveAfterOps": true|false,
  "tree": [ /* FolderNode[] */ ]
}
```

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `name` | String | Yes | Identifier for this operation |
| `parentPath` | String | Yes | Parent folder resolution strategy |
| `unfileFirst` | Boolean | No | Remove document from all folders first |
| `saveAfterOps` | Boolean | No | Save document after filing operations |
| `tree` | Array | Yes | Folder hierarchy to create |

**Parent Path Strategies:**

| Strategy | Format | Description |
|----------|--------|-------------|
| `path` | `path:/Folder/Path` | Absolute path in repository |
| `climbUp` | `climbUp:FolderName` | Traverse up until folder name matches |
| `dynamic` | `dynamic:Keyword` | Traverse up until folder name contains keyword |
| `current` | `current` | Use document's current folder |

#### 5.2.5 FolderNode Structure

```json
{
  "type": "static|property|date|classBased",
  "name": "StaticFolderName",
  "propertyName": "PropertyName",
  "dateFormat": "yyyy-MM-dd",
  "className": "DocumentClassName",
  "classToFolder": { "ClassName": "FolderName" },
  "fileHere": true|false,
  "children": [ /* FolderNode[] (recursive) */ ]
}
```

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `type` | String | Yes | Folder creation type |
| `name` | String | Conditional | Folder name (for `static` type) |
| `propertyName` | String | Conditional | Property to derive name from |
| `dateFormat` | String | Conditional | Date format pattern (for `date` type) |
| `className` | String | No | Target document class for classBased |
| `classToFolder` | Object | Conditional | Class-to-folder mapping (for `classBased`) |
| `fileHere` | Boolean | No | File document at this folder level |
| `children` | Array | No | Nested folder definitions |

**Folder Types:**

| Type | Description |
|------|-------------|
| `static` | Hardcoded folder name using `name` field |
| `property` | Folder name from document property value |
| `date` | Folder name from formatted date property |
| `classBased` | Folder name mapped from document class |

#### 5.2.6 SecurityUpdate Structure

```json
{
  "grantee": "UserOrGroupName",
  "permissions": ["READ", "WRITE", "DELETE"]
}
```

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `grantee` | String | Yes | User or group to grant permissions to |
| `permissions` | Array | Yes | List of access rights to grant |

**Available Permissions:**

| ID | Permission | Description |
|----|------------|-------------|
| 1 | READ | Read document metadata |
| 2 | WRITE | Modify document metadata |
| 3 | DELETE | Delete the document |
| 4 | VIEW_CONTENT | View document content |
| 5 | MINOR_VERSION | Create minor versions |
| 6 | MAJOR_VERSION | Create major versions |
| 7 | CREATE_INSTANCE | Create instances |
| 8 | LINK | Link document to folders |
| 9 | UNLINK | Unlink document from folders |
| 10 | CHANGE_STATE | Change document lifecycle state |
| 11 | WRITE_ACL | Modify access control list |
| 12 | READ_ACL | Read access control list |
| 13 | DELEGATE_ACCESS | Delegate access to others |

#### 5.2.7 ConditionBlock Structure

```json
{
  "conditions": [ /* Condition[] */ ],
  "operations": {
    "propertyUpdates": [ /* PropertyUpdate[] */ ],
    "folderOperations": [ /* FolderOperation[] */ ],
    "securityUpdates": [ /* SecurityUpdate[] */ ]
  }
}
```

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `conditions` | Array | Yes | List of conditions to evaluate |
| `operations` | Object | Yes | Operations to execute if conditions match |

#### 5.2.8 Condition Structure

```json
{
  "property": "PropertyName",
  "docClass": "DocumentClassName",
  "operator": "equals|contains|gt|lt|before|after|not equals",
  "value": "CompareValue",
  "conditionType": "&&|or"
}
```

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `property` | String | Conditional | Property to evaluate |
| `docClass` | String | Conditional | Document class to check |
| `operator` | String | No | Comparison operator |
| `value` | String | Conditional | Value to compare against |
| `conditionType` | String | No | Logical operator for chaining |

**Operators:**

| Operator | Description | Applicable Types |
|----------|-------------|------------------|
| `equals` | Exact match | All types |
| `not equals` | Not equal | All types |
| `contains` | Substring match | String |
| `gt` | Greater than | Numeric |
| `lt` | Less than | Numeric |
| `before` | Date before | DateTime |
| `after` | Date after | DateTime |

---

## 6. Output Specifications

### 6.1 Output File Format

**Format:** Java source code
**Encoding:** UTF-8
**File Extension:** `.java`
**Naming Convention:** `{caseType}.java` (e.g., `AddDoc_Derivatives_Contracts.java`)

### 6.2 Generated Class Structure

```java
package com.event;

// Import statements
import com.filenet.api.core.*;
import com.filenet.api.events.*;
import com.filenet.api.engine.*;
import com.filenet.api.constants.*;
import com.filenet.api.security.*;
import com.filenet.api.collection.*;
import com.filenet.api.property.*;
import com.filenet.api.util.*;
import com.filenet.api.exception.*;
import com.intercom.filenet.SubscriptionHelpers;
import java.util.*;
import java.text.SimpleDateFormat;

public class {CaseType} implements EventActionHandler {
    @Override
    public void onEvent(ObjectChangeEvent event, Id subId) {
        try {
            ObjectStore os = event.getObjectStore();
            Document doc = (Document) event.get_SourceObject();
            doc.refresh();
            Properties props = doc.getProperties();

            // Generated property updates
            // Generated folder operations
            // Generated conditional logic
            // Generated security updates

            doc.save(RefreshMode.REFRESH);
            System.out.println("Event completed successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

### 6.3 Console Output

| Output Type | Format | Example |
|-------------|--------|---------|
| Generation Start | `"Generating {subscriptionName} subscription ..."` | `Generating Derivatives_Contracts subscription ...` |
| Debug (Folder) | `"raw: {name}\nParentVar: {parent}\nvar: {sanitized}"` | `raw: Input\nParentVar: folder1\nvar: Input` |
| Property Update | `"Updated {propertyName}"` | `Updated Status` |
| Completion | `"Event completed successfully."` | `Event completed successfully.` |
| Error | `"Failed to read config.json: {message}"` | `Failed to read config.json: File not found` |

---

## 7. Input/Output Scenarios

### Scenario 1: Simple Property Update

**Input JSON:**
```json
{
  "subscriptionName": "UpdateStatus",
  "subscriptionEvent": [
    {
      "caseType": "UpdateDocumentStatus",
      "eventConfig": {
        "properties": [
          {
            "action": "update",
            "name": "DocumentStatus",
            "value": "Processed",
            "type": "String"
          }
        ]
      }
    }
  ]
}
```

**Generated Output (`UpdateDocumentStatus.java`):**
```java
package com.event;
import com.filenet.api.core.*;
// ... imports ...

public class UpdateDocumentStatus implements EventActionHandler {
    @Override
    public void onEvent(ObjectChangeEvent event, Id subId) {
        try {
            ObjectStore os = event.getObjectStore();
            Document doc = (Document) event.get_SourceObject();
            doc.refresh();
            Properties props = doc.getProperties();

            props.putValue("DocumentStatus", "Processed");
            System.out.println("Updated DocumentStatus");

            doc.save(RefreshMode.REFRESH);
            System.out.println("Event completed successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

**Console Output:**
```
Generating UpdateStatus subscription ...
```

---

### Scenario 2: Static Folder Creation and Filing

**Input JSON:**
```json
{
  "subscriptionName": "FileToArchive",
  "subscriptionEvent": [
    {
      "caseType": "ArchiveDocument",
      "eventConfig": {
        "folderOperations": [
          {
            "name": "ArchiveOp",
            "parentPath": "path:/Archive",
            "unfileFirst": true,
            "saveAfterOps": true,
            "tree": [
              {
                "type": "static",
                "name": "2025",
                "fileHere": false,
                "children": [
                  {
                    "type": "static",
                    "name": "December",
                    "fileHere": true
                  }
                ]
              }
            ]
          }
        ]
      }
    }
  ]
}
```

**Generated Output (`ArchiveDocument.java`):**
```java
// ... imports and class declaration ...
public void onEvent(ObjectChangeEvent event, Id subId) {
    try {
        ObjectStore os = event.getObjectStore();
        Document doc = (Document) event.get_SourceObject();
        doc.refresh();
        Properties props = doc.getProperties();

        // ArchiveOp folder operation
        SubscriptionHelpers.unfileFromAll(doc);
        Folder parentFolder1 = SubscriptionHelpers.resolveParentFolder(doc, os, "path:/Archive");

        Folder parentFolder1_2025 = SubscriptionHelpers.getOrCreateStaticFolder(
            doc, os, parentFolder1, "2025", "", false
        );
        Folder parentFolder1_2025_December = SubscriptionHelpers.getOrCreateStaticFolder(
            doc, os, parentFolder1_2025, "December", "", true
        );

        doc.save(RefreshMode.REFRESH);
        System.out.println("Event completed successfully.");
    } catch (Exception e) {
        e.printStackTrace();
    }
}
```

---

### Scenario 3: Property-Based Dynamic Folder

**Input JSON:**
```json
{
  "subscriptionName": "OrganizeByDepartment",
  "subscriptionEvent": [
    {
      "caseType": "FileByDepartment",
      "eventConfig": {
        "folderOperations": [
          {
            "name": "DeptFiling",
            "parentPath": "path:/Departments",
            "unfileFirst": false,
            "saveAfterOps": true,
            "tree": [
              {
                "type": "property",
                "propertyName": "Department",
                "fileHere": true
              }
            ]
          }
        ]
      }
    }
  ]
}
```

**Generated Output:**
```java
Folder parentFolder1_Department = SubscriptionHelpers.getOrCreatePropertyFolder(
    doc, os, parentFolder1, "Department", "", true
);
```

---

### Scenario 4: Date-Based Folder Organization

**Input JSON:**
```json
{
  "subscriptionName": "DateOrganization",
  "subscriptionEvent": [
    {
      "caseType": "OrganizeByDate",
      "eventConfig": {
        "folderOperations": [
          {
            "name": "DateFiling",
            "parentPath": "path:/Reports",
            "unfileFirst": true,
            "saveAfterOps": true,
            "tree": [
              {
                "type": "date",
                "propertyName": "ReportDate",
                "dateFormat": "yyyy-MM-dd",
                "fileHere": false,
                "children": [
                  {
                    "type": "static",
                    "name": "Completed",
                    "fileHere": true
                  }
                ]
              }
            ]
          }
        ]
      }
    }
  ]
}
```

**Generated Output:**
```java
SubscriptionHelpers.unfileFromAll(doc);
Folder parentFolder1 = SubscriptionHelpers.resolveParentFolder(doc, os, "path:/Reports");

Folder parentFolder1_ReportDate = SubscriptionHelpers.getOrCreateDateFolder(
    doc, os, parentFolder1, "ReportDate", "yyyy-MM-dd", "", false
);
Folder parentFolder1_ReportDate_Completed = SubscriptionHelpers.getOrCreateStaticFolder(
    doc, os, parentFolder1_ReportDate, "Completed", "", true
);
```

---

### Scenario 5: Security Permission Assignment

**Input JSON:**
```json
{
  "subscriptionName": "ApplySecurity",
  "subscriptionEvent": [
    {
      "caseType": "SetDocumentPermissions",
      "eventConfig": {
        "security": [
          {
            "grantee": "DocumentManagers",
            "permissions": ["READ", "WRITE", "VIEW_CONTENT", "MINOR_VERSION"]
          },
          {
            "grantee": "Auditors",
            "permissions": ["READ", "VIEW_CONTENT", "READ_ACL"]
          }
        ]
      }
    }
  ]
}
```

**Generated Output:**
```java
// Security updates
String grantee1 = "DocumentManagers";
List<String> perms1 = new ArrayList<>();
perms1.add("READ");
perms1.add("WRITE");
perms1.add("VIEW_CONTENT");
perms1.add("MINOR_VERSION");
SubscriptionHelpers.setPermissions(doc, os, grantee1, perms1);

String grantee2 = "Auditors";
List<String> perms2 = new ArrayList<>();
perms2.add("READ");
perms2.add("VIEW_CONTENT");
perms2.add("READ_ACL");
SubscriptionHelpers.setPermissions(doc, os, grantee2, perms2);
```

---

### Scenario 6: Conditional Logic with Property Check

**Input JSON:**
```json
{
  "subscriptionName": "ConditionalProcessing",
  "subscriptionEvent": [
    {
      "caseType": "ProcessByStatus",
      "eventConfig": {
        "conditions": [
          {
            "conditions": [
              {
                "property": "Status",
                "operator": "equals",
                "value": "Approved"
              }
            ],
            "operations": {
              "propertyUpdates": [
                {
                  "action": "update",
                  "name": "ProcessedFlag",
                  "value": "true",
                  "type": "Boolean"
                }
              ],
              "folderOperations": [
                {
                  "name": "ApprovedFiling",
                  "parentPath": "path:/Approved",
                  "unfileFirst": true,
                  "saveAfterOps": true,
                  "tree": [
                    {
                      "type": "static",
                      "name": "Current",
                      "fileHere": true
                    }
                  ]
                }
              ]
            }
          },
          {
            "conditions": [
              {
                "property": "Status",
                "operator": "equals",
                "value": "Rejected"
              }
            ],
            "operations": {
              "folderOperations": [
                {
                  "name": "RejectedFiling",
                  "parentPath": "path:/Rejected",
                  "unfileFirst": true,
                  "saveAfterOps": true,
                  "tree": [
                    {
                      "type": "static",
                      "name": "Archive",
                      "fileHere": true
                    }
                  ]
                }
              ]
            }
          }
        ]
      }
    }
  ]
}
```

**Generated Output:**
```java
if ((props.get("Status") != null && String.valueOf(props.get("Status").getObjectValue()).equals("Approved"))) {
    props.putValue("ProcessedFlag", true);
    System.out.println("Updated ProcessedFlag");

    // ApprovedFiling folder operation
    SubscriptionHelpers.unfileFromAll(doc);
    Folder parentFolder1 = SubscriptionHelpers.resolveParentFolder(doc, os, "path:/Approved");
    Folder parentFolder1_Current = SubscriptionHelpers.getOrCreateStaticFolder(
        doc, os, parentFolder1, "Current", "", true
    );
    doc.save(RefreshMode.REFRESH);
} else if ((props.get("Status") != null && String.valueOf(props.get("Status").getObjectValue()).equals("Rejected"))) {
    // RejectedFiling folder operation
    SubscriptionHelpers.unfileFromAll(doc);
    Folder parentFolder1 = SubscriptionHelpers.resolveParentFolder(doc, os, "path:/Rejected");
    Folder parentFolder1_Archive = SubscriptionHelpers.getOrCreateStaticFolder(
        doc, os, parentFolder1, "Archive", "", true
    );
    doc.save(RefreshMode.REFRESH);
}
```

---

### Scenario 7: Document Class-Based Condition

**Input JSON:**
```json
{
  "subscriptionName": "ClassBasedRouting",
  "subscriptionEvent": [
    {
      "caseType": "RouteByClass",
      "eventConfig": {
        "conditions": [
          {
            "conditions": [
              {
                "docClass": "Invoice"
              }
            ],
            "operations": {
              "folderOperations": [
                {
                  "name": "InvoiceFiling",
                  "parentPath": "path:/Finance/Invoices",
                  "unfileFirst": true,
                  "saveAfterOps": true,
                  "tree": [
                    {
                      "type": "date",
                      "propertyName": "InvoiceDate",
                      "dateFormat": "yyyy-MM",
                      "fileHere": true
                    }
                  ]
                }
              ]
            }
          },
          {
            "conditions": [
              {
                "docClass": "Contract"
              }
            ],
            "operations": {
              "folderOperations": [
                {
                  "name": "ContractFiling",
                  "parentPath": "path:/Legal/Contracts",
                  "unfileFirst": true,
                  "saveAfterOps": true,
                  "tree": [
                    {
                      "type": "property",
                      "propertyName": "ContractType",
                      "fileHere": true
                    }
                  ]
                }
              ]
            }
          }
        ]
      }
    }
  ]
}
```

**Generated Output:**
```java
if (doc.getClassName().equals("Invoice")) {
    // InvoiceFiling folder operation
    SubscriptionHelpers.unfileFromAll(doc);
    Folder parentFolder1 = SubscriptionHelpers.resolveParentFolder(doc, os, "path:/Finance/Invoices");
    Folder parentFolder1_InvoiceDate = SubscriptionHelpers.getOrCreateDateFolder(
        doc, os, parentFolder1, "InvoiceDate", "yyyy-MM", "", true
    );
    doc.save(RefreshMode.REFRESH);
} else if (doc.getClassName().equals("Contract")) {
    // ContractFiling folder operation
    SubscriptionHelpers.unfileFromAll(doc);
    Folder parentFolder1 = SubscriptionHelpers.resolveParentFolder(doc, os, "path:/Legal/Contracts");
    Folder parentFolder1_ContractType = SubscriptionHelpers.getOrCreatePropertyFolder(
        doc, os, parentFolder1, "ContractType", "", true
    );
    doc.save(RefreshMode.REFRESH);
}
```

---

### Scenario 8: Complex Multi-Condition with AND/OR Logic

**Input JSON:**
```json
{
  "subscriptionName": "ComplexConditions",
  "subscriptionEvent": [
    {
      "caseType": "MultiConditionHandler",
      "eventConfig": {
        "conditions": [
          {
            "conditions": [
              {
                "property": "Priority",
                "operator": "equals",
                "value": "High",
                "conditionType": "&&"
              },
              {
                "property": "Amount",
                "operator": "gt",
                "value": "10000"
              }
            ],
            "operations": {
              "propertyUpdates": [
                {
                  "action": "update",
                  "name": "ReviewRequired",
                  "value": "true",
                  "type": "Boolean"
                }
              ],
              "securityUpdates": [
                {
                  "grantee": "SeniorReviewers",
                  "permissions": ["READ", "WRITE", "VIEW_CONTENT"]
                }
              ]
            }
          }
        ]
      }
    }
  ]
}
```

**Generated Output:**
```java
if ((props.get("Priority") != null && String.valueOf(props.get("Priority").getObjectValue()).equals("High")) && (props.get("Amount") != null && Integer.parseInt(String.valueOf(props.get("Amount").getObjectValue())) > 10000)) {
    props.putValue("ReviewRequired", true);
    System.out.println("Updated ReviewRequired");

    String grantee1 = "SeniorReviewers";
    List<String> perms1 = new ArrayList<>();
    perms1.add("READ");
    perms1.add("WRITE");
    perms1.add("VIEW_CONTENT");
    SubscriptionHelpers.setPermissions(doc, os, grantee1, perms1);
}
```

---

### Scenario 9: ClimbUp Parent Resolution

**Input JSON:**
```json
{
  "subscriptionName": "ClimbUpExample",
  "subscriptionEvent": [
    {
      "caseType": "FileInParentProject",
      "eventConfig": {
        "climbUpList": ["Projects", "Cases"],
        "folderOperations": [
          {
            "name": "ProjectFiling",
            "parentPath": "climbUp:Projects",
            "unfileFirst": false,
            "saveAfterOps": true,
            "tree": [
              {
                "type": "static",
                "name": "Documents",
                "fileHere": true
              }
            ]
          }
        ]
      }
    }
  ]
}
```

**Generated Output:**
```java
// climbUp parent resolution uses document's current location to find "Projects" folder
Folder parentFolder1 = SubscriptionHelpers.resolveParentFolder(doc, os, "climbUp:Projects");
Folder parentFolder1_Documents = SubscriptionHelpers.getOrCreateStaticFolder(
    doc, os, parentFolder1, "Documents", "", true
);
```

---

### Scenario 10: Class-Based Folder Mapping

**Input JSON:**
```json
{
  "subscriptionName": "ClassBasedFoldering",
  "subscriptionEvent": [
    {
      "caseType": "OrganizeByDocClass",
      "eventConfig": {
        "folderOperations": [
          {
            "name": "ClassFiling",
            "parentPath": "path:/DocumentTypes",
            "unfileFirst": true,
            "saveAfterOps": true,
            "tree": [
              {
                "type": "classBased",
                "classToFolder": {
                  "Invoice": "Financial",
                  "Contract": "Legal",
                  "Report": "Reports",
                  "Memo": "Communications"
                },
                "fileHere": true
              }
            ]
          }
        ]
      }
    }
  ]
}
```

**Generated Output:**
```java
SubscriptionHelpers.unfileFromAll(doc);
Folder parentFolder1 = SubscriptionHelpers.resolveParentFolder(doc, os, "path:/DocumentTypes");

Map<String, String> classToFolder1 = new HashMap<>();
classToFolder1.put("Invoice", "Financial");
classToFolder1.put("Contract", "Legal");
classToFolder1.put("Report", "Reports");
classToFolder1.put("Memo", "Communications");

String folderName1 = classToFolder1.getOrDefault(doc.getClassName(), "Other");
Folder parentFolder1_ClassBased = SubscriptionHelpers.getOrCreateStaticFolder(
    doc, os, parentFolder1, folderName1, "", true
);
```

---

### Scenario 11: Multiple Subscription Events in One Configuration

**Input JSON:**
```json
{
  "subscriptionName": "MultiEventSubscription",
  "subscriptionEvent": [
    {
      "caseType": "OnCreate_Handler",
      "eventConfig": {
        "properties": [
          {
            "action": "update",
            "name": "CreatedBy",
            "value": "System",
            "type": "String"
          }
        ]
      }
    },
    {
      "caseType": "OnUpdate_Handler",
      "eventConfig": {
        "properties": [
          {
            "action": "update",
            "name": "LastModified",
            "value": "now()",
            "type": "DateTime"
          }
        ]
      }
    }
  ]
}
```

**Output Files Generated:**
1. `OnCreate_Handler.java`
2. `OnUpdate_Handler.java`

---

### Scenario 12: Error Handling - Invalid JSON

**Input:** Malformed JSON file

**Console Output:**
```
Failed to read config.json: Unexpected character ('}' (code 125)): was expecting double-quote to start field name
   at [Source: (File); line: 5, column: 3]
com.fasterxml.jackson.core.JsonParseException: Unexpected character...
    at com.fasterxml.jackson.core.JsonParser._constructError(JsonParser.java:2391)
    ...
```

---

### Scenario 13: Print Action for Debugging

**Input JSON:**
```json
{
  "subscriptionName": "DebugProperties",
  "subscriptionEvent": [
    {
      "caseType": "PrintDocumentInfo",
      "eventConfig": {
        "properties": [
          {
            "action": "print",
            "name": "DocumentTitle"
          },
          {
            "action": "print",
            "name": "CreationDate"
          },
          {
            "action": "print",
            "name": "DocumentClass"
          }
        ]
      }
    }
  ]
}
```

**Generated Output:**
```java
System.out.println("DocumentTitle: " + props.get("DocumentTitle").getObjectValue());
System.out.println("CreationDate: " + props.get("CreationDate").getObjectValue());
System.out.println("DocumentClass: " + props.get("DocumentClass").getObjectValue());
```

---

### Scenario 14: Arabic/Unicode Folder Names

**Input JSON:**
```json
{
  "subscriptionName": "ArabicFolders",
  "subscriptionEvent": [
    {
      "caseType": "ArabicFolderHandler",
      "eventConfig": {
        "folderOperations": [
          {
            "name": "ArabicFiling",
            "parentPath": "path:/اداره تقارير القروض",
            "unfileFirst": true,
            "saveAfterOps": true,
            "tree": [
              {
                "type": "static",
                "name": "التقارير",
                "fileHere": true
              }
            ]
          }
        ]
      }
    }
  ]
}
```

**Generated Output:**
```java
SubscriptionHelpers.unfileFromAll(doc);
Folder parentFolder1 = SubscriptionHelpers.resolveParentFolder(doc, os, "path:/اداره تقارير القروض");
Folder parentFolder1__ = SubscriptionHelpers.getOrCreateStaticFolder(
    doc, os, parentFolder1, "التقارير", "", true
);
```

---

### Scenario 15: Nested Folder Hierarchy (3+ Levels)

**Input JSON:**
```json
{
  "subscriptionName": "DeepHierarchy",
  "subscriptionEvent": [
    {
      "caseType": "DeepFolderHandler",
      "eventConfig": {
        "folderOperations": [
          {
            "name": "DeepFiling",
            "parentPath": "path:/Root",
            "unfileFirst": true,
            "saveAfterOps": true,
            "tree": [
              {
                "type": "date",
                "propertyName": "Year",
                "dateFormat": "yyyy",
                "fileHere": false,
                "children": [
                  {
                    "type": "date",
                    "propertyName": "Month",
                    "dateFormat": "MM",
                    "fileHere": false,
                    "children": [
                      {
                        "type": "property",
                        "propertyName": "Department",
                        "fileHere": false,
                        "children": [
                          {
                            "type": "static",
                            "name": "Documents",
                            "fileHere": true
                          }
                        ]
                      }
                    ]
                  }
                ]
              }
            ]
          }
        ]
      }
    }
  ]
}
```

**Generated Output:**
```java
SubscriptionHelpers.unfileFromAll(doc);
Folder parentFolder1 = SubscriptionHelpers.resolveParentFolder(doc, os, "path:/Root");

Folder parentFolder1_Year = SubscriptionHelpers.getOrCreateDateFolder(
    doc, os, parentFolder1, "Year", "yyyy", "", false
);
Folder parentFolder1_Year_Month = SubscriptionHelpers.getOrCreateDateFolder(
    doc, os, parentFolder1_Year, "Month", "MM", "", false
);
Folder parentFolder1_Year_Month_Department = SubscriptionHelpers.getOrCreatePropertyFolder(
    doc, os, parentFolder1_Year_Month, "Department", "", false
);
Folder parentFolder1_Year_Month_Department_Documents = SubscriptionHelpers.getOrCreateStaticFolder(
    doc, os, parentFolder1_Year_Month_Department, "Documents", "", true
);
```

---

## 8. Data Models

### 8.1 SubscriptionDefination Class

```
SubscriptionDefination
├── subscriptionName: String
└── subscriptionEvent: List<SubscriptionEvent>
    └── SubscriptionEvent
        ├── caseType: String
        └── eventConfig: EventConfig
```

### 8.2 EventConfig Class

```
EventConfig
├── subscriptionName: String
├── properties: List<PropertyUpdate>
├── folderOperations: List<FolderOperation>
├── security: List<SecurityUpdate>
├── conditions: List<ConditionBlock>
├── climbUpList: List<String>
└── dynamicParentList: List<String>
```

### 8.3 Complete Data Model Hierarchy

```
SubscriptionDefination
│
├── subscriptionName: String
│
└── subscriptionEvent: List<SubscriptionEvent>
    │
    └── SubscriptionEvent
        ├── caseType: String
        │
        └── eventConfig: EventConfig
            │
            ├── properties: List<PropertyUpdate>
            │   └── PropertyUpdate
            │       ├── action: String
            │       ├── name: String
            │       ├── value: String
            │       └── type: String
            │
            ├── folderOperations: List<FolderOperation>
            │   └── FolderOperation
            │       ├── name: String
            │       ├── parentPath: String
            │       ├── unfileFirst: Boolean
            │       ├── saveAfterOps: Boolean
            │       └── tree: List<FolderNode>
            │           └── FolderNode (recursive)
            │               ├── type: String
            │               ├── name: String
            │               ├── propertyName: String
            │               ├── dateFormat: String
            │               ├── className: String
            │               ├── classToFolder: Map<String,String>
            │               ├── fileHere: Boolean
            │               └── children: List<FolderNode>
            │
            ├── security: List<SecurityUpdate>
            │   └── SecurityUpdate
            │       ├── grantee: String
            │       └── permissions: List<String>
            │
            ├── conditions: List<ConditionBlock>
            │   └── ConditionBlock
            │       ├── conditions: List<Condition>
            │       │   └── Condition
            │       │       ├── property: String
            │       │       ├── docClass: String
            │       │       ├── operator: String
            │       │       ├── value: String
            │       │       └── conditionType: String
            │       └── operations: Operations
            │           ├── propertyUpdates: List<PropertyUpdate>
            │           ├── folderOperations: List<FolderOperation>
            │           └── securityUpdates: List<SecurityUpdate>
            │
            ├── climbUpList: List<String>
            └── dynamicParentList: List<String>
```

---

## 9. External Interfaces

### 9.1 File System Interface

| Operation | Description |
|-----------|-------------|
| Read JSON | Read configuration file from specified path |
| Write Java | Write generated Java source files to current directory |

### 9.2 FileNet P8 API Interface (Generated Code)

| API Class | Usage |
|-----------|-------|
| `ObjectStore` | Repository access |
| `Document` | Document manipulation |
| `Folder` | Folder creation and navigation |
| `Properties` | Property read/write |
| `AccessPermission` | Security management |
| `EventActionHandler` | Event handling interface |

### 9.3 SubscriptionHelpers Interface (Generated Code)

| Method | Description |
|--------|-------------|
| `resolveParentFolder(doc, os, parentPath)` | Resolve parent folder based on strategy |
| `getOrCreateStaticFolder(doc, os, parent, name, className, fileHere)` | Create static folder |
| `getOrCreatePropertyFolder(doc, os, parent, propName, className, fileHere)` | Create property-based folder |
| `getOrCreateDateFolder(doc, os, parent, propName, format, className, fileHere)` | Create date-based folder |
| `unfileFromAll(doc)` | Remove document from all folders |
| `setPermissions(doc, os, grantee, permissions)` | Apply access permissions |

---

## 10. Non-Functional Requirements

### 10.1 Performance

| ID | Requirement |
|----|-------------|
| NFR-01 | Code generation shall complete within 5 seconds for configurations with up to 100 events |
| NFR-02 | Memory usage shall not exceed 256MB during normal operation |

### 10.2 Reliability

| ID | Requirement |
|----|-------------|
| NFR-03 | Generated code shall be syntactically correct and compilable |
| NFR-04 | The tool shall gracefully handle malformed JSON with descriptive error messages |

### 10.3 Maintainability

| ID | Requirement |
|----|-------------|
| NFR-05 | Generated code shall follow Java coding conventions |
| NFR-06 | Source code shall be organized in a standard package structure |

### 10.4 Portability

| ID | Requirement |
|----|-------------|
| NFR-07 | The tool shall run on any Java 8+ compatible platform |
| NFR-08 | Generated code shall be compatible with FileNet P8 5.x API |

---

## 11. Constraints and Limitations

### 11.1 Current Limitations

| ID | Limitation | Impact |
|----|------------|--------|
| L-01 | JSON file path is hardcoded | Must modify source code to change input file |
| L-02 | No command-line argument support | Cannot parameterize execution |
| L-03 | Output directory is current working directory | Cannot specify custom output location |
| L-04 | No validation of JSON schema | Invalid configurations may produce incomplete code |
| L-05 | Single-threaded execution | Large configurations processed sequentially |

### 11.2 Technical Constraints

| ID | Constraint |
|----|------------|
| C-01 | Requires Java 8 or higher |
| C-02 | Requires Jackson 2.15.2 library |
| C-03 | Generated code requires FileNet P8 API at runtime |
| C-04 | Generated code requires SubscriptionHelpers.jar at runtime |

### 11.3 Recommended Improvements

| Priority | Improvement |
|----------|-------------|
| High | Add command-line argument for input file path |
| High | Add command-line argument for output directory |
| Medium | Add JSON schema validation |
| Medium | Add configuration file for tool settings |
| Low | Add support for multiple output formats (e.g., Kotlin) |
| Low | Add dry-run mode to preview generated code |

---

## Appendix A: Sample Configuration Files

### A.1 Minimal Configuration

```json
{
  "subscriptionName": "MinimalExample",
  "subscriptionEvent": [
    {
      "caseType": "MinimalHandler",
      "eventConfig": {}
    }
  ]
}
```

### A.2 Complete Configuration (All Features)

```json
{
  "subscriptionName": "CompleteExample",
  "subscriptionEvent": [
    {
      "caseType": "CompleteHandler",
      "eventConfig": {
        "properties": [
          {"action": "update", "name": "Status", "value": "Processed", "type": "String"},
          {"action": "print", "name": "DocumentId"}
        ],
        "folderOperations": [
          {
            "name": "MainFiling",
            "parentPath": "path:/Documents",
            "unfileFirst": true,
            "saveAfterOps": true,
            "tree": [
              {
                "type": "date",
                "propertyName": "CreatedDate",
                "dateFormat": "yyyy-MM",
                "fileHere": false,
                "children": [
                  {
                    "type": "property",
                    "propertyName": "Department",
                    "fileHere": true
                  }
                ]
              }
            ]
          }
        ],
        "security": [
          {"grantee": "Managers", "permissions": ["READ", "WRITE", "VIEW_CONTENT"]}
        ],
        "conditions": [
          {
            "conditions": [
              {"property": "Priority", "operator": "equals", "value": "High"}
            ],
            "operations": {
              "propertyUpdates": [
                {"action": "update", "name": "RequiresReview", "value": "true", "type": "Boolean"}
              ]
            }
          }
        ]
      }
    }
  ]
}
```

---

## Appendix B: Error Messages Reference

| Error | Cause | Resolution |
|-------|-------|------------|
| `Failed to read config.json: FileNotFoundException` | Input file not found | Verify file path exists |
| `Failed to read config.json: JsonParseException` | Malformed JSON | Validate JSON syntax |
| `Failed to read config.json: UnrecognizedPropertyException` | Unknown field in JSON | Check field names against schema |
| `NullPointerException during generation` | Missing required fields | Ensure all required fields are present |

---

## 12. Integration with CreateDerivativesContracts

### 12.1 Overview

The **CreateDerivativesContracts** project is a companion runtime project that:
- Provides all FileNet P8 API dependencies required for compilation
- Contains the `SubscriptionHelpers` utility class used by generated code
- Serves as the build environment for creating deployable JAR files
- Acts as a template project for event handler deployment

### 12.2 Project Structure

```
CreateDerivativesContracts/
├── src/
│   └── com/
│       └── intercom/
│           └── event/
│               ├── CreateDerivativesContracts.java  # Example/generated handler
│               └── main.java                         # Test utility
├── bin/                                              # Compiled classes
├── lib/
│   ├── Jace.jar                                      # FileNet P8 API
│   ├── Jace_t.jar                                    # FileNet P8 API (thin)
│   ├── p8cel10n.jar                                  # FileNet localization
│   ├── log4j-1.2.17.jar                              # Logging framework
│   └── SubscriptionHelpers.jar                       # Custom helper library
├── .classpath                                        # Eclipse classpath
├── .project                                          # Eclipse project config
└── CreateDerivativesContracts.jar                    # Output JAR file
```

### 12.3 Dependencies in CreateDerivativesContracts

| Library | Purpose |
|---------|---------|
| `Jace.jar` | FileNet P8 Content Engine Java API |
| `Jace_t.jar` | FileNet P8 API (thin client) |
| `p8cel10n.jar` | FileNet localization resources |
| `log4j-1.2.17.jar` | Apache Log4j for logging |
| `SubscriptionHelpers.jar` | Custom helper methods for folder/document operations |

### 12.4 SubscriptionHelpers Methods

The `SubscriptionHelpers` class provides the following methods used by generated code:

| Method | Signature | Description |
|--------|-----------|-------------|
| `resolveParentFolder` | `(Document, ObjectStore, String) : Folder` | Resolves parent folder using path strategies |
| `getOrCreateStaticFolder` | `(Document, ObjectStore, Folder, String, String, boolean) : Folder` | Creates/gets static folders |
| `getOrCreatePropertyFolder` | `(Document, ObjectStore, Folder, String, String, boolean) : Folder` | Creates folders from property values |
| `getOrCreateDateFolder` | `(Document, ObjectStore, Folder, String, String, String, boolean) : Folder` | Creates date-based folder hierarchies (year/month/day) |
| `unfileFromAll` | `(Document) : void` | Removes document from all folders |
| `setPermissions` | `(Document, String, int) : void` | Applies access permissions to document |
| `safeFile` | `(Document, Folder, ObjectStore) : void` | Safely files document into folder |
| `checkJar` | `() : String` | Utility to verify JAR loading |

### 12.5 Package Configuration

**ECM_Eventgen generates code with:**
```java
package com.event;
```

**CreateDerivativesContracts uses:**
```java
package com.intercom.event;
```

**Important:** When copying generated files, update the package declaration to match the target project structure.

---

## 13. End-to-End Pipeline

### 13.1 Complete Workflow Diagram

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                          DEVELOPMENT PIPELINE                                │
└─────────────────────────────────────────────────────────────────────────────┘

┌──────────────┐    ┌──────────────┐    ┌────────────────────┐    ┌──────────┐
│  JSON Config │───>│ ECM_Eventgen │───>│ Generated .java    │───>│ Copy to  │
│  (Input)     │    │ (Generator)  │    │ (Output)           │    │ Target   │
└──────────────┘    └──────────────┘    └────────────────────┘    └────┬─────┘
                                                                        │
                                                                        v
┌──────────────┐    ┌──────────────┐    ┌────────────────────┐    ┌──────────┐
│  FileNet P8  │<───│ Deploy JAR   │<───│ Build JAR          │<───│ Compile  │
│  Server      │    │              │    │ (jar -cf)          │    │ (javac)  │
└──────────────┘    └──────────────┘    └────────────────────┘    └──────────┘
```

### 13.2 Step-by-Step Integration Process

#### Step 1: Create JSON Configuration

Create a JSON configuration file defining your event handler requirements:

**Location:** `ECM_Eventgen/src/com/event/MySubscription.json`

```json
{
  "subscriptionName": "MySubscription",
  "subscriptionEvent": [
    {
      "caseType": "MyEventHandler",
      "eventConfig": {
        "folderOperations": [
          {
            "name": "FilingOperation",
            "parentPath": "path:/Documents/Archive",
            "unfileFirst": true,
            "saveAfterOps": true,
            "tree": [
              {
                "type": "property",
                "propertyName": "Department",
                "fileHere": true
              }
            ]
          }
        ]
      }
    }
  ]
}
```

#### Step 2: Update ECM_Eventgen Input Path

Modify `eventgen.java` line 38 to point to your JSON file:

```java
SubscriptionDefination subscriptionDefination = mapper.readValue(
    new File("path/to/MySubscription.json"),
    SubscriptionDefination.class
);
```

#### Step 3: Run ECM_Eventgen

**Using Command Line:**
```bash
cd D:\Work\intercom\intercom_projects\ECM\ECM-Agent\ECM_Eventgen\ECM_Eventgen

# Compile (if not already done)
javac -cp "lib/*" -d bin src/com/event/*.java

# Run generator
java -cp "lib/*;bin" com.event.eventgen
```

**Output:**
```
Generating MySubscription subscription ...
raw: Department
ParentVar: parentFolder1
var: parentFolder1_Department
```

**Generated File:** `MyEventHandler.java` (in current directory)

#### Step 4: Copy Generated File to CreateDerivativesContracts

**Manual Copy:**
```bash
copy MyEventHandler.java D:\Work\intercom\intercom_projects\ECM\ECM-Agent\CreateDerivativesContracts\CreateDerivativesContracts\src\com\intercom\event\
```

**Update Package Declaration:**

Edit the copied file's first line:
```java
// Change FROM:
package com.event;

// Change TO:
package com.intercom.event;
```

#### Step 5: Compile the Project

**Using Command Line:**
```bash
cd D:\Work\intercom\intercom_projects\ECM\ECM-Agent\CreateDerivativesContracts\CreateDerivativesContracts

# Compile all Java files
javac -cp "lib/*" -d bin src/com/intercom/event/*.java
```

**Using Eclipse:**
1. Open CreateDerivativesContracts project
2. Right-click project > Build Project
3. Verify no compilation errors

#### Step 6: Create JAR File

**Using Command Line:**
```bash
cd D:\Work\intercom\intercom_projects\ECM\ECM-Agent\CreateDerivativesContracts\CreateDerivativesContracts

# Create JAR from compiled classes
jar -cvf MyEventHandler.jar -C bin .
```

**JAR Contents:**
```
MyEventHandler.jar
└── com/
    └── intercom/
        └── event/
            ├── MyEventHandler.class
            └── (other classes)
```

#### Step 7: Deploy to FileNet P8

1. Upload JAR to FileNet Content Engine
2. Create Code Module referencing the JAR
3. Create Event Action referencing the handler class
4. Create Subscription linking Event Action to document class

### 13.3 Automated Build Script (Recommended)

Create `build.bat` (Windows) or `build.sh` (Linux/Mac):

**build.bat:**
```batch
@echo off
setlocal

REM Configuration
set JSON_FILE=Derivatives_Contracts.json
set PROJECT_ROOT=D:\Work\intercom\intercom_projects\ECM\ECM-Agent
set EVENTGEN=%PROJECT_ROOT%\ECM_Eventgen\ECM_Eventgen
set TARGET=%PROJECT_ROOT%\CreateDerivativesContracts\CreateDerivativesContracts

REM Step 1: Run ECM_Eventgen
echo [1/4] Running ECM_Eventgen...
cd /d %EVENTGEN%
java -cp "lib\*;bin" com.event.eventgen

REM Step 2: Copy generated files
echo [2/4] Copying generated files...
for %%f in (*.java) do (
    echo Copying %%f...
    copy /Y "%%f" "%TARGET%\src\com\intercom\event\"
)

REM Step 3: Update package declarations
echo [3/4] Updating package declarations...
cd /d %TARGET%\src\com\intercom\event
powershell -Command "(Get-Content *.java) -replace 'package com.event;', 'package com.intercom.event;' | Set-Content *.java"

REM Step 4: Compile and create JAR
echo [4/4] Building JAR...
cd /d %TARGET%
javac -cp "lib\*" -d bin src\com\intercom\event\*.java
jar -cvf EventHandler.jar -C bin .

echo.
echo Build complete! JAR file: %TARGET%\EventHandler.jar
endlocal
```

### 13.4 Input/Output Summary for Complete Pipeline

#### Pipeline Inputs

| Stage | Input | Format | Location |
|-------|-------|--------|----------|
| Stage 1 | JSON Configuration | `.json` | `ECM_Eventgen/src/com/event/` |
| Stage 2 | Generated Java File | `.java` | `ECM_Eventgen/` (root) |
| Stage 3 | Java Source Files | `.java` | `CreateDerivativesContracts/src/com/intercom/event/` |
| Stage 4 | Compiled Classes | `.class` | `CreateDerivativesContracts/bin/` |

#### Pipeline Outputs

| Stage | Output | Format | Location |
|-------|--------|--------|----------|
| Stage 1 | Generated Java Class | `.java` | `ECM_Eventgen/` (root) |
| Stage 2 | Copied Java Class | `.java` | `CreateDerivativesContracts/src/com/intercom/event/` |
| Stage 3 | Compiled Classes | `.class` | `CreateDerivativesContracts/bin/com/intercom/event/` |
| Stage 4 | Deployable JAR | `.jar` | `CreateDerivativesContracts/` |

### 13.5 Complete Pipeline Scenarios

#### Scenario P1: Single Event Handler Generation and Build

**Input:**
```json
{
  "subscriptionName": "InvoiceProcessing",
  "subscriptionEvent": [
    {
      "caseType": "ProcessInvoice",
      "eventConfig": {
        "properties": [
          {"action": "update", "name": "Status", "value": "Processed", "type": "String"}
        ]
      }
    }
  ]
}
```

**Pipeline Execution:**
```
[InvoiceProcessing.json]
    -> ECM_Eventgen
    -> ProcessInvoice.java
    -> Copy to CreateDerivativesContracts
    -> javac
    -> ProcessInvoice.class
    -> jar -cvf
    -> ProcessInvoice.jar
```

**Final Output:** `ProcessInvoice.jar` ready for FileNet deployment

---

#### Scenario P2: Multiple Event Handlers in One Configuration

**Input:**
```json
{
  "subscriptionName": "DocumentLifecycle",
  "subscriptionEvent": [
    {
      "caseType": "OnDocumentCreate",
      "eventConfig": { ... }
    },
    {
      "caseType": "OnDocumentUpdate",
      "eventConfig": { ... }
    },
    {
      "caseType": "OnDocumentDelete",
      "eventConfig": { ... }
    }
  ]
}
```

**Pipeline Execution:**
```
[DocumentLifecycle.json]
    -> ECM_Eventgen
    -> OnDocumentCreate.java
    -> OnDocumentUpdate.java
    -> OnDocumentDelete.java
    -> Copy all to CreateDerivativesContracts
    -> javac (compile all)
    -> jar -cvf DocumentLifecycle.jar
```

**Final Output:** `DocumentLifecycle.jar` containing all three handlers

---

#### Scenario P3: Error Handling During Build

**Possible Errors and Resolutions:**

| Error | Cause | Resolution |
|-------|-------|------------|
| `package com.event does not exist` | Package not updated | Change `package com.event;` to `package com.intercom.event;` |
| `cannot find symbol: SubscriptionHelpers` | Missing dependency | Ensure `SubscriptionHelpers.jar` is in lib folder and classpath |
| `class file has wrong version` | Java version mismatch | Compile with Java 8: `javac -source 1.8 -target 1.8` |
| JAR file empty | No classes compiled | Check bin folder for `.class` files before JAR creation |

### 13.6 Deployment Requirements

The generated JAR file requires the following at runtime on FileNet P8:

| Requirement | Description |
|-------------|-------------|
| **FileNet CE** | Content Engine 5.x or higher |
| **SubscriptionHelpers.jar** | Must be deployed as Code Module |
| **Event Action** | Must reference the handler class fully qualified name |
| **Subscription** | Must be created on target document class |

**FileNet Configuration:**

1. **Code Module Creation:**
   - Upload `EventHandler.jar`
   - Upload `SubscriptionHelpers.jar`

2. **Event Action Configuration:**
   ```
   Class Name: com.intercom.event.ProcessInvoice
   Code Module: EventHandler
   ```

3. **Subscription Configuration:**
   ```
   Target Class: Invoice
   Event: Creation/Update/etc.
   Event Action: [Reference to Event Action above]
   ```

---

**End of Document**

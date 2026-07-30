# ECM_Eventgen

A Java code generator for IBM FileNet Content Engine event handlers. This tool generates event handler classes from JSON configuration files, enabling automated document filing, property updates, security management, and conditional logic.

## Overview

ECM_Eventgen reads JSON configuration files and generates Java event handler classes that implement `EventActionHandler` interface for FileNet P8 Content Engine.

## Project Structure

```
ECM_Eventgen/
├── src/
│   ├── com/event/
│   │   ├── Main.java                    # Entry point
│   │   ├── service/
│   │   │   └── SubscriptionService.java # Code generator
│   │   ├── models/                      # Data models (POJOs)
│   │   │   ├── SubscriptionDefination.java
│   │   │   ├── SubscriptionEvent.java
│   │   │   ├── EventConfig.java
│   │   │   ├── PropertyUpdate.java
│   │   │   ├── FolderOperation.java
│   │   │   ├── SecurityUpdate.java
│   │   │   └── ConditionBlock.java
│   │   └── Utils/
│   │       └── StringUtils.java
│   └── JsonFiles/                       # JSON configuration files
│       └── *.json
├── lib/                                 # Dependencies
│   ├── jackson-core-2.15.2.jar
│   ├── jackson-annotations-2.15.2.jar
│   ├── jackson-databind-2.15.2.jar
│   └── SubscriptionHelpers.jar
└── CreateDerivativesContracts/          # Output project for JAR creation
    ├── src/com/intercom/event/          # Generated Java files go here
    ├── lib/                             # FileNet dependencies
    │   ├── Jace.jar
    │   ├── Jace_t.jar
    │   ├── SubscriptionHelpers.jar
    │   ├── log4j-1.2.17.jar
    │   └── p8cel10n.jar
    └── output/                          # Final JAR output
```

## Requirements

- **Java JDK 8+** (JDK 17 recommended for development)
- **FileNet P8 Content Engine** libraries (Jace.jar)
- **Jackson JSON** library (2.15.2)

## Quick Start

### 1. Compile the Generator

```bash
cd ECM_Eventgen
javac -encoding UTF-8 -cp "lib/*" -d bin src/com/event/Utils/*.java src/com/event/models/*.java src/com/event/service/*.java src/com/event/*.java
```

### 2. Create JSON Configuration

Create a JSON file in `src/JsonFiles/` (see JSON Configuration section below).

### 3. Generate Java Files

```bash
java -cp "lib/*;bin" com.event.Main "src/JsonFiles/YourConfig.json"
```

### 4. Build the JAR

```bash
# Copy generated files to CreateDerivativesContracts
cp *.java CreateDerivativesContracts/src/com/intercom/event/

# Update package declaration
sed -i 's/package com.event;/package com.intercom.event;/g' CreateDerivativesContracts/src/com/intercom/event/*.java

# Compile for Java 8 (required for FileNet compatibility)
cd CreateDerivativesContracts
javac --release 8 -cp "lib/Jace.jar;lib/SubscriptionHelpers.jar" -d bin src/com/intercom/event/*.java

# Create MANIFEST
mkdir -p bin/META-INF
echo "Manifest-Version: 1.0" > bin/META-INF/MANIFEST.MF
echo "" >> bin/META-INF/MANIFEST.MF

# Copy libraries
mkdir -p bin/lib
cp lib/*.jar bin/lib/

# Create JAR (without directory entries, like Eclipse export)
cd bin
jar -c0Mf ../output/EventHandlers.jar META-INF/MANIFEST.MF lib/*.jar com/intercom/event/*.class
```

## JSON Configuration

### Structure

```json
{
  "subscriptionName": "YourSubscriptionName",
  "subscriptionEvent": [
    {
      "caseType": "EventHandlerClassName",
      "eventConfig": {
        "properties": [...],
        "folderOperations": [...],
        "security": [...],
        "conditions": [...],
        "climbUpList": [...],
        "dynamicParentList": [...]
      }
    }
  ]
}
```

### Property Updates

```json
"properties": [
  {
    "action": "update",
    "name": "PropertyName",
    "value": "NewValue",
    "type": "String"
  }
]
```

**Supported Types:** `String`, `Boolean`, `Integer`, `DateTime`

### Folder Operations

```json
"folderOperations": [
  {
    "name": "OperationName",
    "parentPath": "path:/RootFolder",
    "unfileFirst": true,
    "saveAfterOps": true,
    "tree": [
      {
        "type": "date",
        "propertyName": "DateProperty",
        "dateFormat": "yyyy-MM-dd",
        "fileHere": false,
        "children": [
          {
            "type": "static",
            "name": "SubFolder",
            "fileHere": true
          }
        ]
      }
    ]
  }
]
```

**Folder Types:**
- `static` - Fixed folder name
- `property` - Folder name from document property
- `date` - Date-based folder from property
- `classBased` - Folder based on document class

**Parent Path Strategies:**
- `path:/FolderPath` - Absolute path
- `climbUp:/FolderName` - Traverse up hierarchy
- `dynamic:/PropertyName` - Path from property value
- `current` - Current folder

### Security Updates

```json
"security": [
  {
    "grantee": "GroupName",
    "permissions": ["READ", "WRITE", "VIEW_CONTENT"]
  }
]
```

**Available Permissions:**
`READ`, `WRITE`, `DELETE`, `VIEW_CONTENT`, `MINOR_VERSION`, `MAJOR_VERSION`, `CREATE_INSTANCE`, `LINK`, `UNLINK`, `CHANGE_STATE`, `WRITE_ACL`, `READ_ACL`, `DELEGATE_ACCESS`

### Conditional Logic

```json
"conditions": [
  {
    "conditions": [
      {
        "docClass": "DocumentClassName",
        "conditionType": "and"
      }
    ],
    "operations": {
      "propertyUpdates": [...],
      "folderOperations": [...],
      "securityUpdates": [...]
    }
  }
]
```

**Operators:** `equals`, `not equals`, `contains`, `gt`, `lt`, `before`, `after`

**Condition Types:** `and`, `or`

## Restrictions and Limitations

### Java Version
- Generated classes must be compiled with `--release 8` for FileNet compatibility
- FileNet P8 typically runs on Java 8

### JAR Structure Requirements
- JAR must NOT include directory entries (use `-0M` flag with explicit file list)
- MANIFEST.MF must only contain `Manifest-Version: 1.0`
- Do NOT include `Created-By` in manifest
- Library JARs must be in `lib/` folder inside the JAR

### FileNet Dependencies
- `Jace.jar` and `SubscriptionHelpers.jar` must be included in the JAR
- These libraries are NOT extracted; they remain as nested JARs

### Naming Conventions
- Class names should not contain special characters or spaces
- Use CamelCase for event handler class names
- Property names must match FileNet property symbolic names exactly

### Unicode/Arabic Text
- JSON files support UTF-8 encoding
- Generated Java files are UTF-8 encoded
- Compile with `-encoding UTF-8` flag

### Path Limitations
- Folder paths are case-sensitive
- Maximum path length depends on FileNet configuration
- Special characters in folder names may cause issues

## Deployment to FileNet

1. **Upload JAR as Code Module**
   - ACCE > Object Stores > [Your OS] > Code Modules > New
   - Upload the generated JAR file

2. **Create Event Action**
   - ACCE > Object Stores > [Your OS] > Events, Actions, Processes > Event Actions > New
   - Class: `com.intercom.event.YourClassName`
   - Code Module: Select uploaded JAR

3. **Create Subscription**
   - ACCE > Object Stores > [Your OS] > Events, Actions, Processes > Subscriptions > New
   - Select document class
   - Select event type (Creation, Update, etc.)
   - Link to Event Action

## Troubleshooting

### FNRAC1004E - Unable to load event handler class
- Verify JAR structure matches Eclipse export format
- Ensure compiled with Java 8 (`--release 8`)
- Check class name matches exactly

### Arabic/Unicode showing as ?????
- Ensure JSON files are UTF-8 encoded
- Compile generator with `-encoding UTF-8`
- Verify FileNet server supports UTF-8

### Class not found errors
- Verify all dependencies are in `lib/` folder inside JAR
- Check package declarations match folder structure

## License

Internal use only.

## Version History

- **1.0.0** - Initial release with support for properties, folders, security, and conditions

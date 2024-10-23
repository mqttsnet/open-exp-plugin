
# **插件管理接口使用指南**

## **介绍**
本接口是为 **mqttsnet** 社区的插件开发人员提供的一组便捷接口，旨在帮助用户测试插件的预加载、安装、卸载等功能。开发者可以通过这些接口在测试环境中验证插件的功能及兼容性，并确保插件在不同租户和环境下的正常运行。

## **接口概览**
- `/base/hello`：测试服务是否可用。
- `/base/run`：执行租户相关的用户服务操作。
- `/base/preload`：预加载插件，支持从远程URL加载。
- `/base/install`：安装插件，支持从远程URL下载并安装。
- `/base/unInstall`：卸载插件。

## **快速开始**

### **1. Hello 测试接口**

**URL**: `/base/hello`

**请求方式**: `GET`

**描述**: 测试系统是否正常运行，返回基本的响应模型。

**示例**:
```bash
curl http://localhost:18888/base/hello
```

**响应**:
```json
{
  "message": "Hello from the system!"
}
```

---

### **2. 运行租户操作 (`/run`)**

**URL**: `/base/run`

**请求方式**: `GET`

**参数**:
- `tenantId` (必填): 租户的唯一标识符，用于指定哪个租户执行相关操作。

**描述**: 根据传入的租户ID，执行与该租户相关的操作，主要用于创建用户扩展服务。

**示例**:
```bash
curl "http://localhost:18888/base/run?tenantId=12345"
```

**响应**:
- 成功：`"success"`
- 失败：`"not found"`

---

### **3. 预加载插件 (`/preload`)**

**URL**: `/base/preload`

**请求方式**: `GET`

**参数**:
- `path` (必填): 插件的本地路径或远程URL地址，支持本地文件和远程HTTP下载。

**描述**: 预加载插件文件，确保在正式安装前完成所有必要的预处理。如果路径是URL，将自动下载插件并进行预加载。

**示例**:
```bash
curl "http://localhost:18888/base/preload?path=http://example.com/plugins/example-plugin.jar"
```

**响应**:
```json
{
  "pluginId": "example-plugin_1.0.0",
  "status": "Preloaded"
}
```

---

### **4. 安装插件 (`/install`)**

**URL**: `/base/install`

**请求方式**: `GET`

**参数**:
- `path` (必填): 插件的本地路径或远程URL地址，支持本地文件和远程HTTP下载。
- `tenantId` (必填): 租户的唯一标识符，表示该插件安装到哪个租户下。

**描述**: 安装插件，并为指定租户关联该插件。支持从远程URL下载插件并完成安装。

**示例**:
```bash
curl "http://localhost:18888/base/install?path=http://example.com/plugins/example-plugin.jar&tenantId=12345"
```

**响应**:
```json
{
  "pluginId": "example-plugin_1.0.0",
  "status": "Installed"
}
```

---

### **5. 卸载插件 (`/unInstall`)**

**URL**: `/base/unInstall`

**请求方式**: `GET`

**参数**:
- `pluginId` (必填): 插件的唯一标识符，表示需要卸载的插件。

**描述**: 卸载指定的插件，并清理该插件与租户的关联。

**示例**:
```bash
curl "http://localhost:18888/base/unInstall?pluginId=example-plugin_1.0.0"
```

**响应**:
```json
{
  "status": "ok"
}
```

---

## **使用场景**

### **插件开发测试**

插件开发者可以使用 `/preload` 和 `/install` 接口来测试插件的预加载和安装功能，确保插件在实际部署前能够正常工作。通过这些接口，开发者能够模拟从远程服务器下载插件并加载到系统中。

### **插件卸载**

开发者通过 `/unInstall` 接口可以快速卸载已经安装的插件，适合在开发和测试阶段频繁安装和卸载插件的场景。

### **租户操作**

通过 `/run` 接口，开发者可以模拟插件在不同租户下的操作，确保插件能够正确地处理租户信息，并执行相应的业务逻辑。

---

## **错误处理**

所有接口的错误信息将通过标准的 HTTP 状态码和 JSON 错误响应返回，示例：
```json
{
  "error": "Plugin not found",
  "message": "The requested plugin was not found in the system."
}
```

---

## **反馈与支持**

本项目由 **mqttsnet** 社区维护和支持。  
如果在使用过程中遇到问题或需要帮助，请在 [mqttsnet GitHub 社区](https://github.com/mqttsnet) 提交问题或反馈。  
社区开发者将尽快帮助您解决问题，并提供相关支持。

---

## **总结**

本插件管理接口为开发者提供了简单、方便的方式来测试插件的预加载、安装、卸载和运行逻辑。通过这些接口，开发者可以轻松验证插件在不同租户和环境下的正常运行，并确保插件的高效、稳定运行。  
欢迎大家积极参与 **mqttsnet** 社区的讨论和开发，共同打造高质量的插件生态系统。

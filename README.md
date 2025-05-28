# Operation Marble Road

## Java Network Communication PoC

This project is a **Proof of Concept (PoC)** for establishing network-based communication between systems using Java. It is designed to explore and validate communication mechanisms in distributed systems, focusing on modular separation of concerns between clients and multiple types of servers.

---

## 📁 Project Structure

```text
Operation-Marble-Road
├── client
├── server
|   ├── legacy
|   └── http
├── LICENSE.md
└── README.md
```

## 🎯 Goals

- Enable modular communication between client and multiple server types
- Allow for easy extension of communication protocols
- Demonstrate both modern (HTTP) and legacy (e.g., socket-based) approaches
- Maintain a clean separation between modules for testing and reusability

---

## 🚀 Getting Started

### Prerequisites

- Java 21+
- Maven 3.8+
- Git

### Cloning the Repository

```bash
git clone https://github.com/Barbatos-Rex/Operation-Marble-Road.git
cd Operation-Marble-Road
```

## Build & Run

### Build

```bash
mvn clean install -DskipTests
```

### Run (HTTP)

#### Server
```bash
java -jar ./server/http-server/target/http-server-1.0-SNAPSHOT.jar
```

#### Client
```bash
java -cp ./client/target/client-1.0-SNAPSHOT.jar barbatos_rex1.client.Http
```

### Run (Legacy)

#### Server
```bash
java -jar ./server/legacy-server/target/legacy-server-1.0-SNAPSHOT.jar
```

#### Client
```bash
java -cp ./client/target/client-1.0-SNAPSHOT.jar barbatos_rex1.client.Legacy
```

## 🧩 Module Breakdown
### 🖥️ client Module
The client module is responsible for:

* Initiating requests to one or more server types
* Supporting switching between legacy and HTTP communication modes
* Demonstrating asynchronous or synchronous communication patterns

This module can be run standalone to test communication with either the legacy or http server implementations.

Example Features:

* TCP client socket handling
* HTTP client using Java's HttpClient

### 🖧 server Module
The server module serves as the container for different types of server-side communication handlers. It is structured to support a plugin-like architecture so different server types can be added independently.

### 🧱 legacy Sub-module
The legacy server is intended to emulate communication with older systems, which may rely on:

* TCP sockets
* Custom binary protocols

### 🌐 http Sub-module

The http server module is built around modern web service paradigms. It supports:
* HTTP queries
* binary payloads payloads

# 📄 License
This project is licensed under the MIT License — see the [LICENSE](LICENSE) file for details.



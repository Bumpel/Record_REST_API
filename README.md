# Record REST API

Eine vollständige REST API für die Verwaltung einer persönlichen Schallplatten-Sammlung, entwickelt in Kotlin mit Ktor Framework.

## 🎵 Übersicht

Diese API ermöglicht es Benutzern, ihre Schallplatten-Sammlung zu verwalten. Jeder Benutzer kann nur seine eigenen Records erstellen, bearbeiten und löschen. Die API bietet vollständige CRUD-Operationen mit owner-basierter Authentifizierung.

## ✨ Features

- **Vollständige CRUD-Operationen**
  - ✅ Records erstellen (POST)
  - ✅ Alle Records anzeigen (GET)
  - ✅ Einzelne Records abrufen (GET)
  - ✅ Records aktualisieren (PUT)
  - ✅ Records löschen (DELETE)

- **Sicherheit & Berechtigung**
  - Owner-basierte Zugriffskontrolle
  - Nur Record-Besitzer können ihre Einträge ändern/löschen
  - Umfassende Eingabevalidierung

- **Datenbank**
  - SQLite-Datenbank mit Exposed ORM
  - Automatische Tabellenerstellung
  - Asynchrone Datenbankoperationen

- **API-Design**
  - RESTful Design-Prinzipien
  - JSON Content-Negotiation
  - Strukturierte Fehlerbehandlung
  - HTTP-Status-Code-konforme Antworten

## 🛠️ Technologie-Stack

- **Framework**: Ktor 2.x
- **Sprache**: Kotlin
- **Datenbank**: SQLite
- **ORM**: Jetbrains Exposed
- **Serialisierung**: Jackson
- **Server**: Netty

## 📊 Datenmodell

### Record (Vollständiger Datensatz)
```kotlin
data class DBRecord(
    val id: Int,          // Eindeutige ID (Auto-generiert)
    val owner: String,    // Besitzer des Records (max. 50 Zeichen)
    val title: String,    // Album-Titel (max. 100 Zeichen)
    val artist: String,   // Künstler (max. 100 Zeichen)
    val year: Int         // Erscheinungsjahr
)
```

### Record Upload (Für POST/PUT Requests)
```kotlin
data class DBRecordUpload(
    val owner: String,    // Besitzer des Records
    val title: String,    // Album-Titel
    val artist: String,   // Künstler
    val year: Int         // Erscheinungsjahr
)
```

## 🚀 Installation & Setup

### Voraussetzungen
- JDK 11 oder höher
- Kotlin 1.8+
- Gradle

### Projekt starten

1. **Repository klonen**
   ```bash
   git clone <repository-url>
   cd record-rest-api
   ```

2. **Dependencies installieren**
   ```bash
   ./gradlew build
   ```

3. **Server starten**
   ```bash
   ./gradlew run
   ```

4. **API ist verfügbar unter**
   ```
   http://localhost:8100
   ```

### Konfiguration

Die SQLite-Datenbank wird automatisch erstellt unter:
```
~/Desktop/RecordDB.sqlite
```

## 🔌 API-Endpunkte

### Base URL
```
http://localhost:8100
```

### Endpunkt-Übersicht

| Method | Endpoint | Beschreibung | Authentifizierung |
|--------|----------|--------------|-------------------|
| GET | `/records` | Alle Records abrufen | Keine |
| GET | `/records/{id}` | Record nach ID abrufen | Keine |
| POST | `/records` | Neuen Record erstellen | Owner im Body |
| PUT | `/records/{id}` | Record aktualisieren | Owner muss übereinstimmen |
| DELETE | `/records/{id}?owner={owner}` | Record löschen | Owner-Parameter erforderlich |

## 📝 API-Dokumentation

### 1. Alle Records abrufen
```http
GET /records
```

**Response:**
```json
[
  {
    "id": 1,
    "owner": "Max",
    "title": "Dark Side of the Moon",
    "artist": "Pink Floyd",
    "year": 1973
  },
  {
    "id": 2,
    "owner": "Anna",
    "title": "Abbey Road",
    "artist": "The Beatles",
    "year": 1969
  }
]
```

### 2. Record nach ID abrufen
```http
GET /records/{id}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "owner": "Max",
  "title": "Dark Side of the Moon",
  "artist": "Pink Floyd",
  "year": 1973
}
```

**Response (404 Not Found):**
```json
404 Not Found
```

### 3. Neuen Record erstellen
```http
POST /records
Content-Type: application/json

{
  "owner": "Max",
  "title": "Wish You Were Here",
  "artist": "Pink Floyd",
  "year": 1975
}
```

**Response (201 Created):**
```json
{
  "id": 3,
  "owner": "Max",
  "title": "Wish You Were Here",
  "artist": "Pink Floyd",
  "year": 1975
}
```

### 4. Record aktualisieren
```http
PUT /records/{id}
Content-Type: application/json

{
  "owner": "Max",
  "title": "Wish You Were Here (Remastered)",
  "artist": "Pink Floyd",
  "year": 1975
}
```

**Response (200 OK):**
```json
{
  "id": 3,
  "owner": "Max",
  "title": "Wish You Were Here (Remastered)",
  "artist": "Pink Floyd",
  "year": 1975
}
```

**Response (403 Forbidden):**
```json
"Nur der Owner darf den Record aktualisieren"
```

### 5. Record löschen
```http
DELETE /records/{id}?owner=Max
```

**Response (204 No Content):**
```
(Leerer Body)
```

**Response (403 Forbidden):**
```json
"Nur der Owner darf den Record löschen"
```

## 🔒 Sicherheitsmodell

### Owner-basierte Authentifizierung
- Jeder Record hat einen **Owner** (Besitzer)
- Nur der Owner kann seinen Record **aktualisieren** oder **löschen**
- Alle Benutzer können Records **lesen**
- Jeder kann neue Records **erstellen**

### Zugriffskontrolle
- **UPDATE**: Owner im Request-Body muss mit dem Owner des existierenden Records übereinstimmen
- **DELETE**: Owner-Parameter in der URL muss mit dem Record-Owner übereinstimmen
- **READ**: Keine Authentifizierung erforderlich

## 📋 HTTP-Status-Codes

| Code | Beschreibung | Verwendung |
|------|--------------|------------|
| 200 | OK | Erfolgreiche GET/PUT-Operation |
| 201 | Created | Record erfolgreich erstellt |
| 204 | No Content | Record erfolgreich gelöscht |
| 400 | Bad Request | Ungültige Eingabe oder fehlende Parameter |
| 403 | Forbidden | Keine Berechtigung (falscher Owner) |
| 404 | Not Found | Record nicht gefunden |
| 500 | Internal Server Error | Server-Fehler |

## 🗃️ Datenbankschema

```sql
CREATE TABLE Records (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    owner VARCHAR(50) NOT NULL,
    title VARCHAR(100) NOT NULL,
    artist VARCHAR(100) NOT NULL,
    year INTEGER NOT NULL
);
```

## 📁 Projektstruktur

```
src/
├── main/
│   └── kotlin/
│       └── com/
│           └── RecordAPI/
│               ├── Application.kt        # Main-Anwendung
│               ├── DatabaseConfig.kt     # Datenbank-Konfiguration & Routing
│               ├── RecordService.kt      # Business Logic & Datenbank-Service
│               └── Routing.kt           # Basis-Routing-Konfiguration
├── resources/
│   ├── application.conf                 # Ktor-Konfiguration
│   └── logback.xml                     # Logging-Konfiguration
└── test/                               # Unit Tests
```

## 🔧 Dependencies

### Core Dependencies
```kotlin
// Ktor Server
implementation("io.ktor:ktor-server-core:$ktor_version")
implementation("io.ktor:ktor-server-netty:$ktor_version")
implementation("io.ktor:ktor-server-content-negotiation:$ktor_version")

// Database
implementation("org.jetbrains.exposed:exposed-core:$exposed_version")
implementation("org.jetbrains.exposed:exposed-jdbc:$exposed_version")
implementation("org.xerial:sqlite-jdbc:3.42.0.0")

// JSON Serialization
implementation("io.ktor:ktor-serialization-jackson:$ktor_version")
implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.17.0")
implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.15.2")

// Logging
implementation("ch.qos.logback:logback-classic:$logback_version")
```

## 🧪 Beispiel-Requests

### cURL-Beispiele

**Alle Records abrufen:**
```bash
curl -X GET http://localhost:8100/records
```

**Record erstellen:**
```bash
curl -X POST http://localhost:8100/records \
  -H "Content-Type: application/json" \
  -d '{
    "owner": "John",
    "title": "Hotel California",
    "artist": "Eagles",
    "year": 1976
  }'
```

**Record aktualisieren:**
```bash
curl -X PUT http://localhost:8100/records/1 \
  -H "Content-Type: application/json" \
  -d '{
    "owner": "John",
    "title": "Hotel California (Remastered)",
    "artist": "Eagles",
    "year": 1976
  }'
```

**Record löschen:**
```bash
curl -X DELETE "http://localhost:8100/records/1?owner=John"
```


## 🐛 Troubleshooting

### Häufige Probleme

**Datenbank-Verbindungsfehler**
- Stellen Sie sicher, dass das Desktop-Verzeichnis existiert
- Überprüfen Sie Dateiberechtigungen für SQLite-Datei

**Port bereits in Verwendung**
- Ändern Sie den Port in `application.conf`
- Oder beenden Sie den Prozess auf Port 8100

**JSON-Parsing-Fehler**
- Überprüfen Sie die Content-Type-Header
- Validieren Sie die JSON-Syntax

**403 Forbidden bei Update/Delete**
- Owner-Parameter muss exakt übereinstimmen (Case-sensitive)
- Verwenden Sie denselben Owner-String wie beim Erstellen

## 📈 Erweiterungsmöglichkeiten

- **Authentifizierung**: JWT-Token-basierte Authentifizierung
- **Paginierung**: Große Datenmengen effizient handhaben
- **Suchfunktion**: Records nach Titel, Artist oder Jahr filtern
- **Validierung**: Erweiterte Eingabevalidierung
- **Logging**: Strukturiertes Logging für bessere Überwachung
- **Tests**: Umfassende Unit- und Integrationstests

## 🔗 Weiterführende Ressourcen

- 🧪 **Test Client zur API verfügbar unter:**  
  [https://github.com/Bumpel/Record_API_Test_Client](https://github.com/Bumpel/Record_API_Test_Client)  
  Ideal für schnelles Testen und Evaluieren der API-Endpunkte ohne eigene Implementierung.

## 📄 Lizenz

Bitte beachten Sie die Lizenzbestimmungen Ihres Projekts.

## 🤝 Beitragen

Bug-Reports und Feature-Requests sind willkommen!

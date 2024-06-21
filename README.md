# Codex Naturalis: Prova finale di Ingegneria del Software - Politecnico di Milano, A.A. 2023-24

## Features

- [x] Complete rules
- [x] TUI
- [x] GUI
- [x] Socket
- [x] RMI
- [x] Multiple games
- [x] Chat

## Instructions

### Server

```bash
java -jar <server.jar> server
```

### Client

```bash
java -jar <client.jar> [cli|gui] [socket|rmi] <server-IP>
``` 
Startup options:

- `cli` : starts client in TUI visualisation mode.
- `gui` : start client in GUI visualisation mode.
- `socket` : starts client in socket connection mode.
- `rmi` : starts client in RMI connection mode.

>Note: with no arguments, the software starts in client TUI mode with socket connection (default modes). With just the first argument specified, the second one is set to default. If no server IP is provided, "localhost" is selected.

## Authors

- Lorenzo Esposito
- Elia Falzoni
- Alessandro Frisone
- Francesco Grassi
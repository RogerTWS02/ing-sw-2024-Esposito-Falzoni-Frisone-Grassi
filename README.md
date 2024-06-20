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
java -jar PSP25.jar server
```

### Client

```bash
java -jar PSP25.jar [cli|gui] [socket|rmi]
``` 
Startup options:

- `cli` : starts client in TUI visualisation mode.
- `gui` : start client in GUI visualisation mode.
- `socket` : starts client in socket connection mode.
- `rmi` : starts client in RMI connection mode.

>Note: with no arguments, the client starts in TUI mode with socket connection (default modes). With one argument specified, the second one is set to default.

## Authors

- Lorenzo Esposito
- Elia Falzoni
- Alessandro Frisone
- Francesco Grassi
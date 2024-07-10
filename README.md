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
java -jar Codex_Naturalis.jar server
```

### Client

```bash
java -jar Codex_Naturalis.jar [cli|gui] [socket|rmi] <server-IP>
``` 
Startup options:

- `cli` : starts client in TUI visualisation mode.
- `gui` : start client in GUI visualisation mode.
- `socket` : starts client in socket connection mode.
- `rmi` : starts client in RMI connection mode.

>Note: with no arguments, the software starts in client TUI mode with socket connection (default modes). With just the first argument specified, the second one is set to default. If no server IP is provided, "localhost" is selected.

>Note: in order to join a specific match, launch subsequent clients after the first one has effectively created a new lobby.

## Authors

- Lorenzo Esposito
- Elia Falzoni
- Alessandro Frisone
- Francesco Grassi

*NOTA: Codex Naturalis è un gioco da tavolo sviluppato ed edito da Cranio Creations Srl. I contenuti grafici di questo progetto riconducibili al prodotto editoriale da tavolo sono utilizzati previa approvazione di Cranio Creations Srl a solo scopo didattico. È vietata la distribuzione, la copia o la riproduzione dei contenuti e immagini in qualsiasi forma al di fuori del progetto, così come la redistribuzione e la pubblicazione dei contenuti e immagini a fini diversi da quello sopracitato. È inoltre vietato l'utilizzo commerciale di suddetti contenuti.*

Valutazione: 29/30

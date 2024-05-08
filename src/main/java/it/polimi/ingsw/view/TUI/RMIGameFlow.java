package it.polimi.ingsw.view.TUI;

import it.polimi.ingsw.network.server.RMIServerImpl;
import it.polimi.ingsw.network.server.RMIServerInterface;

public class RMIGameFlow {
    RMIServerInterface stub;

    public RMIGameFlow(RMIServerInterface stub) {
        this.stub = stub;
    }

}

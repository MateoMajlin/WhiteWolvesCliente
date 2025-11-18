package winterwolves.network;

import winterwolves.utilidades.PlayerManager;

public interface GameController {
    void connect(int numPlayer);
    void start(int[] personajesElegidos);

    PlayerManager getPlayerManager();

    void updatePlayersPosition(float x, float y, int jugador);
}



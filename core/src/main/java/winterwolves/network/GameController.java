package winterwolves.network;

public interface GameController {
    void connect(int numPlayer);
    void start(int[] personajesElegidos);
    void updatePlayerState(int playerId, float x, float y, float velX, float velY, int dir);
}



package winterwolves.network;

public interface GameController {
    void connect(int numPlayer);
    void start(int[] personajesElegidos);
    void updatePlayersPosition(float x, float y,String dirMirando,int jugador);
    void updatePlayersVida(int vida, int jugador);
    void volverAlMenu();
    void ganadorPorDefault();
}



package winterwolves.utilidades;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.math.Vector2;
import winterwolves.Jugador;
import winterwolves.personajes.Personaje;

public class PlayerManager {

    private final int NUM_JUGADORES = 2;
    private Jugador[] jugadores = new Jugador[NUM_JUGADORES];

    public PlayerManager(World world, int[] personajesElegidosIdx, float PPM, OrthographicCamera camaraHud) {

        Personaje[] personajes = new Personaje[NUM_JUGADORES];

        float[] posicionesX = {4f, 21f};
        float[] posicionesY = {2.5f, 10f};

        for (int i = 0; i < NUM_JUGADORES; i++) {
            switch (personajesElegidosIdx[i]) {
                case 0: personajes[i] = new winterwolves.personajes.clases.Guerrero(world, posicionesX[i], posicionesY[i], PPM, camaraHud); break;
                case 1: personajes[i] = new winterwolves.personajes.clases.Mago(world, posicionesX[i], posicionesY[i], PPM, camaraHud); break;
                case 2: personajes[i] = new winterwolves.personajes.clases.Clerigo(world, posicionesX[i], posicionesY[i], PPM, camaraHud); break;
                default: throw new RuntimeException("Índice de personaje inválido: " + personajesElegidosIdx[i]);
            }
        }

        for (int i = 0; i < NUM_JUGADORES; i++) {
            jugadores[i] = new Jugador("Jugador " + (i+1), world, 450 / PPM, 450 / PPM, PPM, camaraHud, personajes[i], i + 1);
        }
    }

    public void actualizar(float delta) {
        for (Jugador j : jugadores) {
            j.getPersonaje().actualizarInventario();
        }
    }

    public void draw(Batch batch) {
        for (Jugador j : jugadores) {
            j.getPersonaje().draw(batch);
        }
    }

    public void drawHud(Batch batch) {
        for (Jugador j : jugadores) {
            j.drawHud(Render.batch);
        }
    }

    public void toggleInventario() {
        jugadores[0].toggleInventario();
    }

    /**
     * Devuelve el jugador según el índice del servidor (1 o 2)
     */
    public Jugador getJugador(int serverIndex) {
        int arrayIndex = serverIndex - 1; // normalizamos
        if (arrayIndex >= 0 && arrayIndex < NUM_JUGADORES) {
            return jugadores[arrayIndex];
        }
        return null;
    }

    public Vector2 getPosicionJugador(int serverIndex) {
        Jugador j = getJugador(serverIndex);
        if (j != null) {
            return new Vector2(
                j.getPersonaje().getX() + j.getPersonaje().getWidth() / 2,
                j.getPersonaje().getY() + j.getPersonaje().getHeight() / 2
            );
        }
        return null;
    }

    public void setPuedeMoverse(boolean puedeMoverse) {
        for (Jugador j : jugadores) {
            j.getPersonaje().setPuedeMoverse(puedeMoverse);
        }
    }

    public void dispose() {
        for (Jugador j : jugadores) {
            j.dispose();
        }
    }
}

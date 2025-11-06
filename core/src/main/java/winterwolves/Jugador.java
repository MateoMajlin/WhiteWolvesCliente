package winterwolves;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.graphics.OrthographicCamera;
import winterwolves.io.EntradasJugador;
import winterwolves.network.ClientThread;
import winterwolves.personajes.Hud;
import winterwolves.personajes.InventarioHud;
import winterwolves.personajes.Personaje;

public class Jugador {

    private Personaje personaje;
    private EntradasJugador entradas;
    private Hud hud;
    private InventarioHud inventarioHud;

    private OrthographicCamera camaraHud;
    private World world;
    private float ppm;
    private int id;

    private String nombre;
    public EntradasJugador entradasJugador;

    // 🔹 NUEVO: sincronización de red
    private ClientThread clientThread;
    private boolean esLocal = false;
    private float tiempoEnvio = 0f; // para no saturar red

    public Jugador(String nombre, World world, float x, float y, float ppm,
                   OrthographicCamera camaraHud, Personaje personaje, int id) {
        this.nombre = nombre;
        this.world = world;
        this.ppm = ppm;
        this.camaraHud = camaraHud;
        this.entradas = new EntradasJugador();
        this.personaje = personaje;
        this.personaje.entradas = this.entradas;
        this.hud = personaje.hud;
        this.inventarioHud = personaje.inventarioHud;
        this.id = id;
    }

    public void draw(SpriteBatch batch) {
        personaje.draw(batch);
    }

    public void drawHud(SpriteBatch batch) {
        if (inventarioHud != null && inventarioHud.isVisible()) {
            personaje.dibujarInventario(batch);
        } else {
            personaje.dibujarHud(batch);
        }
    }

    public Personaje getPersonaje() {
        return personaje;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nuevoNombre) {
        this.nombre = nuevoNombre;
    }

    public void toggleInventario() {
        personaje.toggleInventario();
    }

    public void dispose() {
        personaje.dispose();
    }

    public EntradasJugador getEntradas() {
        return entradas;
    }

    // 🔹 Métodos nuevos para red
    public void setClientThread(ClientThread clientThread) {
        this.clientThread = clientThread;
    }

    public void setEsLocal(boolean esLocal) {
        this.esLocal = esLocal;
    }

    public boolean esLocal() {
        return esLocal;
    }
}

package winterwolves;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.graphics.OrthographicCamera;
import winterwolves.io.EntradasJugador;
import winterwolves.personajes.Hud;
import winterwolves.personajes.InventarioHud;
import winterwolves.personajes.Personaje;
import winterwolves.personajes.clases.Mago;

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

    public Jugador(String nombre, World world, float x, float y, float ppm, OrthographicCamera camaraHud, Personaje personaje, int id) {
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

    public void update() {
        personaje.actualizarInventario();
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
}

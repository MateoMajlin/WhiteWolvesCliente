package winterwolves.props;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.OrthographicCamera;
import winterwolves.items.Item;
import winterwolves.items.Inventario;
import winterwolves.elementos.Imagen;
import winterwolves.elementos.Texto;
import winterwolves.personajes.Personaje;
import winterwolves.utilidades.Recursos;

public class CofreHud {

    private Inventario inventarioPersonaje;
    private Inventario inventarioCofre;
    private boolean visible = false;

    private Imagen fondoHud;
    private ShapeRenderer shapeRenderer;
    private Texto textoItem;
    private Texto textoNombre;
    private Texto textoEstadisticas;
    private OrthographicCamera cameraHud;

    private static final float ANCHO_CASILLA = 272;
    private static final float ALTO_CASILLA = 234;

    private CasillaInventario[] casillasPersonaje = new CasillaInventario[3];
    private CasillaInventario[] casillasCofre = new CasillaInventario[3];

    private Personaje personaje;

    private int seleccionado = 0; // solo recorre los slots del cofre (0,1,2)

    public CofreHud(Inventario inventarioCofre, Personaje personaje, OrthographicCamera cameraHud) {
        this.inventarioCofre = inventarioCofre;
        this.personaje = personaje;
        this.cameraHud = cameraHud;

        fondoHud = new Imagen("inventarioFondo.png");
        shapeRenderer = new ShapeRenderer();
        textoItem = new Texto(Recursos.FUENTEMENU, 14, Color.WHITE, true);
        textoNombre = new Texto(Recursos.FUENTEMENU, 50, Color.BLACK, true);
        textoEstadisticas = new Texto(Recursos.FUENTEMENU, 25, Color.WHITE, true);

        // Slots del personaje
        casillasPersonaje[0] = new CasillaInventario(84, 366, ANCHO_CASILLA, ALTO_CASILLA);
        casillasPersonaje[1] = new CasillaInventario(404, 366, ANCHO_CASILLA, ALTO_CASILLA);
        casillasPersonaje[2] = new CasillaInventario(724, 366, ANCHO_CASILLA, ALTO_CASILLA);

        // Slots del cofre
        casillasCofre[0] = new CasillaInventario(84, 100, ANCHO_CASILLA, ALTO_CASILLA);
        casillasCofre[1] = new CasillaInventario(404, 100, ANCHO_CASILLA, ALTO_CASILLA);
        casillasCofre[2] = new CasillaInventario(724, 100, ANCHO_CASILLA, ALTO_CASILLA);
    }

    public void toggle() {
        visible = !visible;
        personaje.setPuedeMoverse(!visible);

        if (visible) {
            inventarioPersonaje = personaje.getInventario();
        }
    }

    public boolean isVisible() {
        return visible;
    }

    public void actualizar() {
        if (!visible) return;

        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            seleccionado = (seleccionado + 1) % casillasCofre.length;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            seleccionado--;
            if (seleccionado < 0) seleccionado = casillasCofre.length - 1;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
        int idx = seleccionado;
        Item itemPersonaje = inventarioPersonaje.getItem(idx);
        Item itemCofre = inventarioCofre.getItem(idx);

            if (itemCofre != null) {
                personaje.intercambiarItems(itemCofre, idx);
                inventarioPersonaje.setItemEnSlot(itemCofre, idx);
            }
            if (itemPersonaje != null) {
                inventarioCofre.setItemEnSlot(itemPersonaje, idx);
            }
        }

        // Cerrar HUD con ESC
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            toggle();
        }
    }

    public void dibujar(SpriteBatch batch) {
        if (!visible) return;

        batch.setProjectionMatrix(cameraHud.combined);
        batch.begin();

        // Fondo y stats
        fondoHud.setTransparencia(0.9f);
        fondoHud.dibujar();
        textoNombre.setTexto(personaje.getClase());
        textoNombre.setPosition(80, 680);
        textoNombre.dibujar();

        textoEstadisticas.setTexto(
            "ATQ: " + personaje.getAtaque() +
                " ATQ.M: " + personaje.getAtaqueMagico() +
                " DEF: " + personaje.getDefensa() +
                " PV: " + personaje.getVida()
        );
        textoEstadisticas.setPosition(400, 670);
        textoEstadisticas.dibujar();

        batch.end();

        // --- Dibujar casillas (fondo gris o naranja si seleccionada)
        shapeRenderer.setProjectionMatrix(cameraHud.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for (int i = 0; i < casillasPersonaje.length; i++) {
            shapeRenderer.setColor(Color.DARK_GRAY);
            shapeRenderer.rect(casillasPersonaje[i].x, casillasPersonaje[i].y, ANCHO_CASILLA, ALTO_CASILLA);
        }

        for (int i = 0; i < casillasCofre.length; i++) {
            shapeRenderer.setColor(i == seleccionado ? Color.ORANGE : Color.DARK_GRAY);
            shapeRenderer.rect(casillasCofre[i].x, casillasCofre[i].y, ANCHO_CASILLA, ALTO_CASILLA);
        }

        shapeRenderer.end();

        batch.begin();
        for (int i = 0; i < casillasPersonaje.length; i++) {
            Item item = inventarioPersonaje.getItem(i);
            dibujarItem(batch, item, casillasPersonaje[i]);
        }

        for (int i = 0; i < casillasCofre.length; i++) {
            Item item = inventarioCofre.getItem(i);
            dibujarItem(batch, item, casillasCofre[i]);
        }
        batch.end();
    }

    private void dibujarItem(SpriteBatch batch, Item item, CasillaInventario casilla) {
        if (item == null) return;

        float margenInterno = 8;
        if (item.getTextura() != null) {
            batch.draw(item.getTextura(),
                casilla.x + margenInterno,
                casilla.y + margenInterno + 12,
                casilla.ancho - 2 * margenInterno,
                casilla.alto - 2 * margenInterno - 12
            );
        }

        textoItem.setTexto(item.getNombre());
        textoItem.setPosition(casilla.x + 5, casilla.y + casilla.alto - 5);
        textoItem.dibujar();
    }

    public void dispose() {
        shapeRenderer.dispose();
        fondoHud.dispose();
    }

    private static class CasillaInventario {
        float x, y, ancho, alto;

        CasillaInventario(float x, float y, float ancho, float alto) {
            this.x = x;
            this.y = y;
            this.ancho = ancho;
            this.alto = alto;
        }
    }
}

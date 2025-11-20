package winterwolves.personajes.habilidades;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import java.util.*;
import com.badlogic.gdx.utils.Timer;
import winterwolves.personajes.Personaje;

public class BolaDeFuego extends Habilidad {

    private final float velocidad = 200f;
    private final int daño;
    private final List<Proyectil> proyectiles = new ArrayList<>();
    private final Texture textura;
    private final Animation<TextureRegion> animacionBola;

    private static final Map<DireccionUtil.Direccion, Vector2> startOffsets = new HashMap<>();
    static {
        startOffsets.put(DireccionUtil.Direccion.UP, new Vector2(32, 10));
        startOffsets.put(DireccionUtil.Direccion.DOWN, new Vector2(-32, -10));
        startOffsets.put(DireccionUtil.Direccion.LEFT, new Vector2(-10, 32));
        startOffsets.put(DireccionUtil.Direccion.RIGHT, new Vector2(10, -32));
        startOffsets.put(DireccionUtil.Direccion.UP_LEFT, new Vector2(15, 30));
        startOffsets.put(DireccionUtil.Direccion.UP_RIGHT, new Vector2(30, -15));
        startOffsets.put(DireccionUtil.Direccion.DOWN_LEFT, new Vector2(-30, 15));
        startOffsets.put(DireccionUtil.Direccion.DOWN_RIGHT, new Vector2(-15, -30));
    }

    public BolaDeFuego(float duracion, float cooldown, int daño) {
        super(duracion, cooldown);
        this.daño = daño;

        textura = new Texture("bolaDeFuego.png");

        int offsetX = 18;
        int offsetY = 4;
        int anchoFrame = 64;
        int altoFrame = 64;

        TextureRegion[] frames = new TextureRegion[5];
        for (int i = 0; i < 5; i++) {
            frames[i] = new TextureRegion(
                textura,
                offsetX + i * anchoFrame,
                offsetY,
                anchoFrame,
                altoFrame
            );
        }

        animacionBola = new Animation<>(0.08f, frames);
    }

    @Override
    protected void iniciarEfecto() {

        personaje.setPuedeMoverse(false);
        final float stunDuracion = 0.5f;

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                personaje.setPuedeMoverse(true);
            }
        }, stunDuracion);


        Vector2 centro = new Vector2(
            personaje.getPosition().x,
            personaje.getPosition().y
        );


        Vector2 dir = new Vector2(personaje.direccionMirando).nor();
        DireccionUtil.Direccion direccionEnum = DireccionUtil.vectorADireccion(dir);

        Vector2 offsetVisual = startOffsets.getOrDefault(direccionEnum, new Vector2(0, 0));

        Vector2 pos = centro.cpy().add(dir.cpy().scl(40f));

        // Nuevo constructor sin Box2D ni hitbox offset
        proyectiles.add(new Proyectil(
            pos,
            dir,
            velocidad,
            daño,
            animacionBola,
            duracion,
            personaje,
            direccionEnum,
            offsetVisual
        ));
    }

    @Override
    protected void finalizarEfecto() {}

    @Override
    public void actualizar(float delta) {
        super.actualizar(delta);

        Iterator<Proyectil> it = proyectiles.iterator();
        while (it.hasNext()) {
            Proyectil p = it.next();
            p.actualizar(delta);
            if (p.estaMuerto()) {
                it.remove();
            }
        }
    }

    @Override
    public void dibujar(Batch batch, float x, float y, float width, float height) {
        for (Proyectil p : proyectiles) {
            p.dibujar(batch);
        }
    }

    @Override
    public void dispose() {
        textura.dispose();
    }
}

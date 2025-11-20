package winterwolves.personajes.habilidades;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import winterwolves.personajes.Personaje;

public class Proyectil {

    private Vector2 posicion;
    private Vector2 dir;
    private float velocidad;

    public int daño;
    private Animation<TextureRegion> animacion;
    private float stateTime = 0f;

    private float tiempoRestante;
    private boolean muerto = false;

    public Personaje lanzador;
    public DireccionUtil.Direccion direccion;

    private Vector2 offsetVisual;

    private float ancho;
    private float alto;

    public Proyectil(
        Vector2 pos,
        Vector2 dir,
        float velocidad,
        int daño,
        Animation<TextureRegion> animacion,
        float duracion,
        Personaje lanzador,
        DireccionUtil.Direccion direccion,
        Vector2 offsetVisual
    ) {
        TextureRegion first = animacion.getKeyFrame(0);
        this.ancho = first.getRegionWidth();
        this.alto = first.getRegionHeight();
        this.posicion = new Vector2(pos);
        this.dir = new Vector2(dir).nor();
        this.velocidad = velocidad;
        this.daño = daño;
        this.animacion = animacion;
        this.tiempoRestante = duracion;
        this.lanzador = lanzador;
        this.direccion = direccion;
        this.offsetVisual = offsetVisual != null ? offsetVisual : new Vector2();
    }

    public void actualizar(float delta) {
        if (muerto) return;

        // movimiento simple
        posicion.x += dir.x * velocidad * delta;
        posicion.y += dir.y * velocidad * delta;

        // duración del proyectil
        tiempoRestante -= delta;
        if (tiempoRestante <= 0) muerto = true;
    }

    public void dibujar(Batch batch) {
        if (muerto) return;

        stateTime += Gdx.graphics.getDeltaTime();
        TextureRegion frame = animacion.getKeyFrame(stateTime, true);

        // rotación según dirección
        float angle = dir.angleDeg();

        batch.draw(
            frame,
            posicion.x - ancho / 2f + offsetVisual.x,
            posicion.y - alto / 2f + offsetVisual.y,
            ancho / 2f,
            alto / 2f,
            ancho,
            alto,
            1.5f,
            1.5f,
            angle
        );
    }

    public boolean estaMuerto() {
        return muerto;
    }
}

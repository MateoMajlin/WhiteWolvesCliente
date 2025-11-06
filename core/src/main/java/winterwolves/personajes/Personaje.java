package winterwolves.personajes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import winterwolves.Dañable;
import winterwolves.io.EntradasJugador;
import winterwolves.items.Inventario;
import winterwolves.items.Item;
import winterwolves.personajes.habilidades.Habilidad;
import winterwolves.personajes.armas.Arma;
import winterwolves.network.ClientThread;

public class Personaje extends Sprite implements Hudeable, Dañable {

    public Body body;
    protected float speedBase = 2.5f;
    protected float speed = speedBase;
    protected float multiplicadorCorrer = 1.7f;
    public float ppm;

    protected int vida;
    protected int vidaMax;
    protected float ataque, ataqueMagico, defensa;

    protected AnimacionPersonaje animaciones;
    protected Vector2 movimiento = new Vector2();
    public Vector2 direccionMirando = new Vector2(0, -1);

    protected Dash dash = new Dash(5f, 0.2f, 10f);
    protected boolean puedeMoverse = true;

    protected Inventario inventario;

    protected Arma armaBasica;
    protected Habilidad habilidad1;
    protected Habilidad habilidad2;

    protected Item slotArma;
    protected Item slotHabilidad1;
    protected Item slotHabilidad2;

    protected String nombreClase = "Personaje";

    public World world;
    public InventarioHud inventarioHud;
    public Hud hud;
    protected OrthographicCamera camaraHud;
    public EntradasJugador entradas;

    // ==== CAMPOS NUEVOS PARA RED ====
    private boolean esLocal = true;
    private int playerNum = -1;
    private ClientThread clientThread;
    private float tiempoSync = 0f;
    // ================================

    private int kills = 0;

    public Personaje(World world, float x, float y, float ppm, OrthographicCamera camaraHud) {
        this.world = world;
        this.inventario = new Inventario();
        this.ppm = ppm;
        this.animaciones = new AnimacionPersonaje("zorrito.png");
        this.vidaMax = vida;
        this.camaraHud = camaraHud;
        this.hud = new Hud(this,camaraHud);
        this.inventarioHud = new InventarioHud(this.inventario, camaraHud);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);
        body = world.createBody(bodyDef);

        setSize(30, 30);
        float margen = 0.7f;
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(getWidth()/2f * margen / ppm, getHeight()/2f * margen / ppm);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        fixtureDef.friction = 0.2f;
        fixtureDef.restitution = 0f;
        body.createFixture(fixtureDef);
        shape.dispose();

        body.setUserData(this);
    }

    // ==========================
    // 🔌 CONFIGURACIÓN DE RED
    // ==========================
    public void configurarRed(ClientThread client, int playerNum, boolean esLocal) {
        this.clientThread = client;
        this.playerNum = playerNum;
        this.esLocal = esLocal;
    }

    private void enviarMovimientoRed() {
        if (clientThread == null || !esLocal) return;
        tiempoSync += Gdx.graphics.getDeltaTime();
        if (tiempoSync < 0.05f) return;
        tiempoSync = 0f;

        Vector2 pos = body.getPosition();
        String msg = String.format("Move:%.2f:%.2f:%.2f:%.2f",
            pos.x, pos.y, direccionMirando.x, direccionMirando.y);
        clientThread.sendMessage(msg);
    }

    public void actualizarDesdeRed(float x, float y, float dirX, float dirY) {
        if (esLocal) return;
        body.setTransform(x, y, 0);
        direccionMirando.set(dirX, dirY);
    }

    // ==========================
    // INVENTARIO Y HUD
    // ==========================
    public void toggleInventario() {
        if (inventarioHud == null) return;
        inventarioHud.toggle();
        setPuedeMoverse(!inventarioHud.isVisible());
    }

    public void actualizarInventario() {
        if (inventarioHud != null && inventarioHud.isVisible()) {
            inventarioHud.actualizar();
        }
    }

    public void dibujarInventario(SpriteBatch batch) {
        if (inventarioHud != null && inventarioHud.isVisible()) {
            batch.setProjectionMatrix(camaraHud.combined);
            inventarioHud.dibujar(batch, this);
        }
    }

    public void dibujarHud(SpriteBatch batch) {
        if (hud != null) {
            hud.render(batch);
        }
    }

    // ==========================
    // DIBUJO PRINCIPAL
    // ==========================
    @Override
    public void draw(Batch batch) {
        float delta = Gdx.graphics.getDeltaTime();

        if (esLocal) {
            if (habilidad1 != null) {
                habilidad1.actualizar(delta);
                if (entradas.isHabilidad1()) habilidad1.usar();
            }
            if (habilidad2 != null) {
                habilidad2.actualizar(delta);
                if (entradas.isHabilidad2()) habilidad2.usar();
            }

            mover();
            procesarHabilidades();
            enviarMovimientoRed();
        }

        Vector2 pos = body.getPosition();
        setPosition(pos.x * ppm - getWidth()/2, pos.y * ppm - getHeight()/2);

        TextureRegion frame = (!puedeMoverse)
            ? animaciones.getIdleFrame(direccionMirando)
            : animaciones.getFrame(movimiento, direccionMirando, speed, speedBase, delta);
        batch.draw(frame, getX(), getY(), getWidth(), getHeight());

        if (armaBasica != null) {
            float desplazamiento = getWidth() * 0.6f;
            float armaX = getX() + direccionMirando.x * desplazamiento;
            float armaY = getY() + direccionMirando.y * desplazamiento;

            if (esLocal && entradas.isGolpeBasico()) {
                armaBasica.atacar(armaX, armaY, direccionMirando, this);
            }

            armaBasica.actualizar(delta, armaX, armaY);
            armaBasica.draw(batch, armaX, armaY, getWidth(), getHeight(), direccionMirando.angleDeg());
        }

        if (habilidad1 != null) habilidad1.dibujar(batch, getX(), getY(), getWidth(), getHeight());
        if (habilidad2 != null) habilidad2.dibujar(batch, getX(), getY(), getWidth(), getHeight());
    }

    // ==========================
    // MOVIMIENTO Y HABILIDADES
    // ==========================
    protected void mover() {
        if (!puedeMoverse || !esLocal) {
            body.setLinearVelocity(0,0);
            return;
        }

        float delta = Gdx.graphics.getDeltaTime();
        dash.update(delta, body, direccionMirando);

        if (entradas.isDash() && dash.intentarActivar(direccionMirando)) return;
        if (dash.isActivo()) return;

        movimiento.set(0,0);
        speed = entradas.isCorrer() ? speedBase * multiplicadorCorrer : speedBase;

        if (entradas.isArriba()) movimiento.y = 1;
        if (entradas.isAbajo()) movimiento.y = -1;
        if (entradas.isIzquierda()) movimiento.x = -1;
        if (entradas.isDerecha()) movimiento.x = 1;

        if (movimiento.len() > 0) direccionMirando.set(movimiento).nor();
        movimiento.nor().scl(speed);
        body.setLinearVelocity(movimiento.x, movimiento.y);
    }

    protected void procesarHabilidades() {
        if (entradas.isGolpeBasico()) usarHabilidadBasica();
        if (entradas.isHabilidad1()) usarHabilidadEspecial();
        if (entradas.isHabilidad2()) usarUltimate();
    }

    public void usarHabilidadBasica() {}
    public void usarHabilidadEspecial() {}
    public void usarUltimate() {}

    public float getTiempoHabilidad1() { return habilidad1 != null ? habilidad1.getTiempoDesdeUltimoUso() : 0f; }
    public float getCooldownHabilidad1() { return habilidad1 != null ? habilidad1.getCooldown() : 0f; }
    public float getTiempoHabilidad2() { return habilidad2 != null ? habilidad2.getTiempoDesdeUltimoUso() : 0f; }
    public float getCooldownHabilidad2() { return habilidad2 != null ? habilidad2.getCooldown() : 0f; }
    public float getTiempoDesdeUltimoDash() { return dash.getTiempoDesdeUltimo(); }
    public float getCooldownDash() { return dash.getCooldown(); }
    public Arma getArma() { return armaBasica; }
    public String getClase() { return nombreClase; }

    public int getVida() { return vida; }
    public void setVida(int nuevaVida) {
        vida = Math.min(nuevaVida, this.getVidaMax());
        if (vida < 0) vida = 0;
    }

    public int getVidaMax() { return this.vidaMax; }
    public float getAtaque() { return ataque; }
    public float modifAtaque(float monto) { return ataque += monto; }
    public float modifSpeedBase(float monto) { return speedBase += monto; }
    public float getAtaqueMagico() { return ataqueMagico; }
    public float getDefensa() { return defensa; }
    public void setPuedeMoverse(boolean valor) { puedeMoverse = valor; }
    public boolean getPuedeMoverse() { return puedeMoverse; }

    public Inventario getInventario() { return inventario; }

    public void intercambiarItems(Item item, int slot) {
        switch (slot) {
            case 0:
                if(this.armaBasica != null) this.armaBasica.dispose();
                this.slotArma = item;
                this.armaBasica = item.crearArma(world,ppm);
                break;
            case 1:
                if (this.habilidad1 != null) this.habilidad1.dispose();
                this.slotHabilidad1 = item;
                this.habilidad1 = item.crearHabilidad();
                this.habilidad1.setPersonaje(this);
                break;
            case 2:
                if (this.habilidad2 != null) this.habilidad2.dispose();
                this.slotHabilidad2 = item;
                this.habilidad2 = item.crearHabilidad();
                this.habilidad2.setPersonaje(this);
                break;
        }
        inventario.setItemEnSlot(item, slot);
    }

    public Item getSlot(int slot) {
        switch (slot) {
            case 0: return slotArma;
            case 1: return slotHabilidad1;
            case 2: return slotHabilidad2;
            default: return null;
        }
    }

    @Override
    public void recibirDaño(float cantidad) {
        this.vida -= cantidad;
        if (vida <= 0) vida = 0;
    }

    public boolean estaMuerto() { return vida <= 0; }
    public void incrementarKill() { kills++; }
    public int getKills() { return kills; }

    public void respawn(float x, float y) {
        setVida(getVidaMax());
        body.setTransform(x, y, 0);
        setPuedeMoverse(true);
    }


    public void dispose() {
        animaciones.dispose();
        if (armaBasica != null) armaBasica.dispose();
        if (habilidad1 != null) habilidad1.dispose();
        if (habilidad2 != null) habilidad2.dispose();
    }

    public void moverVisualSegunServidor(float x, float y) {
        this.setPosition(x, y);
    }


    public int getPpm() { return (int) ppm; }
}

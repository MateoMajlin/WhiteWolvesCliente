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

    private boolean esLocal = true;

    public float getSpeedBase() {
        return speedBase;
    }

    public void setSpeedBase(float speedBase) {
        this.speedBase = speedBase;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getMultiplicadorCorrer() {
        return multiplicadorCorrer;
    }

    public void setMultiplicadorCorrer(float multiplicadorCorrer) {
        this.multiplicadorCorrer = multiplicadorCorrer;
    }

    public void setPpm(float ppm) {
        this.ppm = ppm;
    }

    public void setVidaMax(int vidaMax) {
        this.vidaMax = vidaMax;
    }

    public void setAtaque(float ataque) {
        this.ataque = ataque;
    }

    public void setAtaqueMagico(float ataqueMagico) {
        this.ataqueMagico = ataqueMagico;
    }

    public void setDefensa(float defensa) {
        this.defensa = defensa;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public void setPlayerNum(int playerNum) {
        this.playerNum = playerNum;
    }

    public boolean isEsLocal() {
        return esLocal;
    }

    public void setEsLocal(boolean esLocal) {
        this.esLocal = esLocal;
    }

    public float getTiempoSync() {
        return tiempoSync;
    }

    public void setTiempoSync(float tiempoSync) {
        this.tiempoSync = tiempoSync;
    }

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public int getPlayerNum() {
        return playerNum;
    }

    private int playerNum = -1;
    private ClientThread clientThread;
    private float tiempoSync = 0f;

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

//        BodyDef bodyDef = new BodyDef();
//        bodyDef.type = BodyDef.BodyType.DynamicBody;
//        bodyDef.position.set(x, y);
//        body = world.createBody(bodyDef);

        setSize(30, 30);
//        float margen = 0.7f;
//        PolygonShape shape = new PolygonShape();
//        shape.setAsBox(getWidth()/2f * margen / ppm, getHeight()/2f * margen / ppm);
//
//        FixtureDef fixtureDef = new FixtureDef();
//        fixtureDef.shape = shape;
//        fixtureDef.density = 1f;
//        fixtureDef.friction = 0.2f;
//        fixtureDef.restitution = 0f;
//        body.createFixture(fixtureDef);
//        shape.dispose();
//
//        body.setUserData(this);
    }

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

//            mover();
//            procesarHabilidades();
        }

//        Vector2 pos = body.getPosition();
//        setPosition(pos.x * ppm - getWidth()/2, pos.y * ppm - getHeight()/2);

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

//    protected void mover() {
//        if (!puedeMoverse || !esLocal) {
//            body.setLinearVelocity(0,0);
//            return;
//        }
//        float delta = Gdx.graphics.getDeltaTime();
//        dash.update(delta, body, direccionMirando);
//
//        if (entradas.isDash() && dash.intentarActivar(direccionMirando)) return;
//        if (dash.isActivo()) return;
//
//        movimiento.set(0,0);
//        speed = entradas.isCorrer() ? speedBase * multiplicadorCorrer : speedBase;
//
//        if (entradas.isArriba()) movimiento.y = 1;
//        if (entradas.isAbajo()) movimiento.y = -1;
//        if (entradas.isIzquierda()) movimiento.x = -1;
//        if (entradas.isDerecha()) movimiento.x = 1;
//
//        if (movimiento.len() > 0) direccionMirando.set(movimiento).nor();
//        movimiento.nor().scl(speed);
//        body.setLinearVelocity(movimiento.x, movimiento.y);
//    }

//    protected void procesarHabilidades() {
//        if (entradas.isGolpeBasico()) usarHabilidadBasica();
//        if (entradas.isHabilidad1()) usarHabilidadEspecial();
//        if (entradas.isHabilidad2()) usarUltimate();
//    }
//
//    public void usarHabilidadBasica() {}
//    public void usarHabilidadEspecial() {}
//    public void usarUltimate() {}

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

    public void respawn(float x, float y)
    {
        setVida(getVidaMax());
        body.setTransform(x, y, 0);
        setPuedeMoverse(true);
    }


    public void dispose()
    {
        animaciones.dispose();
        if (armaBasica != null) armaBasica.dispose();
        if (habilidad1 != null) habilidad1.dispose();
        if (habilidad2 != null) habilidad2.dispose();
    }

    public void moverVisualSegunServidor(float x, float y)
    {
        this.setPosition(x, y);
    }

    public boolean canUseGolpe() {
        return armaBasica != null && armaBasica.puedeAtacar();
    }

    public boolean canUseHabilidad1() {
        return habilidad1 != null && habilidad1.puedeUsarse();
    }

    public boolean canUseHabilidad2() {
        return habilidad2 != null && habilidad2.puedeUsarse();
    }

    public boolean canUseDash() {
        return dash.getTiempoDesdeUltimo() >= dash.getCooldown();
    }


    public int getPpm() { return (int) ppm; }

    public Habilidad getHabilidad1() {
        return habilidad1;
    }

    public Habilidad getHabilidad2() {
        return habilidad2;
    }

    public Dash getDash() {
        return dash;
    }

    public void updatePosition(float x, float y) {
        float pixelX = x * ppm - getWidth() / 2f;
        float pixelY = y * ppm - getHeight() / 2f;
        setPosition(pixelX, pixelY);
    }

    public void updateDirMirando(String direccion) {
        if (direccion == null) return;

        switch (direccion.toUpperCase()) {

            case "ARRIBA":
                direccionMirando.set(0, 1);
                break;

            case "ABAJO":
                direccionMirando.set(0, -1);
                break;

            case "IZQUIERDA":
                direccionMirando.set(-1, 0);
                break;

            case "DERECHA":
                direccionMirando.set(1, 0);
                break;

            case "ARRIBA_DERECHA":
                direccionMirando.set(1, 1).nor();
                break;

            case "ARRIBA_IZQUIERDA":
                direccionMirando.set(-1, 1).nor();
                break;

            case "ABAJO_DERECHA":
                direccionMirando.set(1, -1).nor();
                break;

            case "ABAJO_IZQUIERDA":
                direccionMirando.set(-1, -1).nor();
                break;

            case "QUIETO":
            default:
                direccionMirando.set(0, 0);
                break;
        }
    }


    public Vector2 getPosition() {
        return new Vector2(
            getX() + getWidth() / 2f,
            getY() + getHeight() / 2f
        );
    }

}

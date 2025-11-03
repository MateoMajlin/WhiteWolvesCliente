package winterwolves.personajes.clases;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.physics.box2d.World;
import winterwolves.io.EntradasJugador;
import winterwolves.items.EspadaItem;
import winterwolves.items.GemaDeFuego;
import winterwolves.items.GemaElectrica;
import winterwolves.items.Item;
import winterwolves.personajes.AnimacionPersonaje;
import winterwolves.personajes.Personaje;
import winterwolves.personajes.armas.Baston;
import winterwolves.personajes.habilidades.BolaDeFuego;
import winterwolves.personajes.habilidades.RayoElectrico;

public class Mago extends Personaje {

    Item baston = new EspadaItem();
    Item gemaDeFuego = new GemaDeFuego();
    Item gemaElectrica = new GemaElectrica();

    public Mago(World world, float x, float y, float ppm, OrthographicCamera camera) {
        super(world, x, y, ppm, camera);
        nombreClase = "Mago";

        animaciones = new AnimacionPersonaje("pajarito.png");

        armaBasica = baston.crearArma(world,ppm);
        habilidad1 = gemaDeFuego.crearHabilidad();
        habilidad2 = gemaElectrica.crearHabilidad();

        habilidad1.setPersonaje(this);
        habilidad2.setPersonaje(this);

        ataque = armaBasica.getDaño();
        ataqueMagico = 50;
        defensa = 30;
        vida = 100;
        vidaMax = vida;

        slotArma = baston;
        slotHabilidad1 = gemaDeFuego;
        slotHabilidad2 = gemaElectrica;
    }

    @Override
    public void draw(Batch batch) {
        super.draw(batch);
    }
}

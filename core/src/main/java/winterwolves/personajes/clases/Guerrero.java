package winterwolves.personajes.clases;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.physics.box2d.World;
import winterwolves.io.EntradasJugador;
import winterwolves.items.AmuletoCuracion;
import winterwolves.items.AnilloConcentracion;
import winterwolves.items.EspadaItem;
import winterwolves.items.Item;
import winterwolves.personajes.Personaje;
import winterwolves.personajes.armas.Espada;
import winterwolves.personajes.habilidades.ConcentracionMaxima;
import winterwolves.personajes.habilidades.HabilidadCuracion;

public class Guerrero extends Personaje {

    Item espada = new EspadaItem();
    Item amuleto = new AmuletoCuracion();
    Item anillo = new AnilloConcentracion();

    public Guerrero(World world, float x, float y, float ppm, OrthographicCamera camera) {
        super(world, x, y, ppm, camera);
        nombreClase = "Guerrero";

        armaBasica = espada.crearArma(world,ppm);
        habilidad1 = amuleto.crearHabilidad();
        habilidad2 = anillo.crearHabilidad();

        habilidad1.setPersonaje(this);
        habilidad2.setPersonaje(this);

        ataque = armaBasica.getDaño();
        ataqueMagico = 10;
        defensa = 30;
        vida = 150;
        vidaMax = vida;

        inventario.agregarItem(espada);
        inventario.agregarItem(amuleto);
        inventario.agregarItem(anillo);

        slotArma = espada;
        slotHabilidad1 = amuleto;
        slotHabilidad2 = anillo;
    }

    @Override
    public void draw(Batch batch) {
        super.draw(batch);
    }
}

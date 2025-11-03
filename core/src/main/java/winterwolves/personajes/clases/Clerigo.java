package winterwolves.personajes.clases;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.physics.box2d.World;
import winterwolves.io.EntradasJugador;
import winterwolves.items.AmuletoCuracion;
import winterwolves.items.EspadaItem;
import winterwolves.items.GemaElectrica;
import winterwolves.items.Item;
import winterwolves.personajes.AnimacionPersonaje;
import winterwolves.personajes.Personaje;
import winterwolves.personajes.armas.Baston;
import winterwolves.personajes.armas.Espada;
import winterwolves.personajes.habilidades.ConcentracionMaxima;
import winterwolves.personajes.habilidades.HabilidadCuracion;
import winterwolves.personajes.habilidades.RayoElectrico;

public class Clerigo extends Personaje{

    Item baston = new EspadaItem();
    Item amuleto = new AmuletoCuracion();
    Item gemaRayo = new GemaElectrica();

    public Clerigo(World world, float x, float y, float ppm, OrthographicCamera camera) {
        super(world, x, y, ppm, camera);

        nombreClase = "Clerigo";

        animaciones = new AnimacionPersonaje("gatito.png");

        armaBasica = baston.crearArma(world,ppm);
        habilidad1 = amuleto.crearHabilidad();
        habilidad2 = gemaRayo.crearHabilidad();

        habilidad1.setPersonaje(this);
        habilidad2.setPersonaje(this);

        ataque = armaBasica.getDaño();
        ataqueMagico = 40;
        defensa = 60;
        vida = 150;
        vidaMax = vida;

        slotArma = baston;
        slotHabilidad1 = amuleto;
        slotHabilidad2 = gemaRayo;
    }

    @Override
    public void draw(Batch batch) {
        super.draw(batch);
    }
}

package winterwolves.items;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.World;
import winterwolves.personajes.habilidades.Habilidad;
import winterwolves.personajes.armas.Arma;

public abstract class Item {

    protected String nombre;
    protected TextureRegion textura;

    public Item(String nombre, TextureRegion textura) {
        this.nombre = nombre;
        this.textura = textura;
    }


    public String getNombre() { return nombre; }
    public TextureRegion getTextura() { return textura; }


    public abstract Habilidad crearHabilidad();
    public abstract Arma crearArma(World world, float ppm);

    public abstract String getDescripcion();
}

package winterwolves.utilidades;

import com.badlogic.gdx.physics.box2d.*;
import winterwolves.Dañable;
import winterwolves.personajes.Personaje;
import winterwolves.personajes.armas.Arma;
import winterwolves.personajes.habilidades.Proyectil;
import winterwolves.personajes.habilidades.ProyectilRayo;
import winterwolves.props.Caja;

public class CollisionListener implements ContactListener {

    @Override
    public void beginContact(Contact contact) {
    }

    @Override
    public void endContact(Contact contact) {}

    @Override
    public void preSolve(Contact contact, Manifold manifold) {}

    @Override
    public void postSolve(Contact contact, ContactImpulse contactImpulse) {}
}

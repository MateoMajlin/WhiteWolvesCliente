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
//        Object a = contact.getFixtureA().getBody().getUserData();
//        Object b = contact.getFixtureB().getBody().getUserData();
//
//        if (a instanceof Arma && b instanceof Dañable) {
//            Arma arma = (Arma) a;
//            Dañable target = (Dañable) b;
//
//            if (!(target instanceof Personaje) || ((Personaje) target) != arma.propietario) {
//                target.recibirDaño(arma.getDañoReal());
//            }
//        } else if (a instanceof Dañable && b instanceof Arma) {
//            Arma arma = (Arma) b;
//            Dañable target = (Dañable) a;
//
//            if (!(target instanceof Personaje) || ((Personaje) target) != arma.propietario) {
//                target.recibirDaño(arma.getDañoReal());
//            }
//        }
//
//        if (a instanceof Proyectil && b instanceof Dañable) {
//            Proyectil proj = (Proyectil) a;
//            Dañable target = (Dañable) b;
//
//            if (!(target instanceof Personaje) || ((Personaje) target) != proj.lanzador) {
//                target.recibirDaño(proj.daño);
//                proj.muerto = true;
//            }
//
//        } else if (a instanceof Dañable && b instanceof Proyectil) {
//            Proyectil proj = (Proyectil) b;
//            Dañable target = (Dañable) a;
//
//            if (!(target instanceof Personaje) || ((Personaje) target) != proj.lanzador) {
//                target.recibirDaño(proj.daño);
//                proj.muerto = true;
//            }
//
//        }
//
//
//        if (a instanceof ProyectilRayo && b instanceof Dañable) {
//            ProyectilRayo proj = (ProyectilRayo) a;
//            Dañable target = (Dañable) b;
//
//            if (!(target instanceof Personaje) || ((Personaje) target) != proj.lanzador) {
//                target.recibirDaño(proj.daño);
//            }
//        } else if (a instanceof Dañable && b instanceof ProyectilRayo) {
//            ProyectilRayo proj = (ProyectilRayo) b;
//            Dañable target = (Dañable) a;
//
//            if (!(target instanceof Personaje) || ((Personaje) target) != proj.lanzador) {
//                target.recibirDaño(proj.daño);
//            }
//        }
    }



    @Override
    public void endContact(Contact contact) {}

    @Override
    public void preSolve(Contact contact, Manifold manifold) {}

    @Override
    public void postSolve(Contact contact, ContactImpulse contactImpulse) {}
}

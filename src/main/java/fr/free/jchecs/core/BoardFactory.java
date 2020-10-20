/*
 jChecs: a simple Java chess game sample

 Copyright (C) 2006-2017 by David Cotton

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package fr.free.jchecs.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Classe utilitaire délivrant des instances de représentation d'un état de jeu.
 *
 * @author David Cotton
 */
public final class BoardFactory {
    /**
     * Log de la classe.
     */
    private static final Logger LOG = LoggerFactory.getLogger(BoardFactory.class);

    /**
     * Classe utilitaire : ne pas instancier.
     */
    private BoardFactory() {
        // Rien de spécifique...
    }

    /**
     * Renvoi une instance de description de l'état d'une partie.
     *
     * @param pType Type de la méthode de représentation de l'instance souhaitée.
     * @param pEtat Etat de la partie.
     * @return Instance correspondante.
     */
    public static MoveGenerator valueOf(final Type pType, final State pEtat) {
        MoveGenerator res = null;
        switch (pEtat) {
            case EMPTY:
                res = newInstance(pType.getClassName(), ArrayBoard.EMPTY);
                break;
            case STARTING:
                res = newInstance(pType.getClassName(), ArrayBoard.STARTING);
                break;
            default:
                assert false;
        }

        return res;
    }

    /**
     * Renvoi une instance de générateur de mouvements, obtenue par réflexion.
     *
     * @param pClassName    Nom de la classe.
     * @param pInitialState Etat initial.
     * @return Nouvelle instance (ou null en cas d'erreur).
     */
    private static MoveGenerator newInstance(final String pClassName,
                                             final Board pInitialState) {
        MoveGenerator res = null;

        Class<?> cls = null;
        try {
            cls = Class.forName(pClassName);
        } catch (final ClassNotFoundException e1) {
            LOG.warn("Board class [{}] not found.", pClassName);
        }
        if (cls != null) {
            Constructor<?> cst = null;
            try {
                cst = cls.getDeclaredConstructor(Board.class);
            } catch (final SecurityException e) {
                LOG.warn("Security exception on class [[}].", pClassName);
            } catch (final NoSuchMethodException e) {
                LOG.warn("No such method: {}", e.getLocalizedMessage());
            }
            if (cst != null) {
                Object o;
                try {
                    o = cst.newInstance(pInitialState);
                    if (o instanceof MoveGenerator) {
                        res = (MoveGenerator) o;
                    }
                } catch (final IllegalArgumentException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    LOG.warn("New instance creation failure.", e);
                }
            }
        }

        return res;
    }

    /**
     * Enumération des états initiaux reconnus.
     */
    public enum State {
        /**
         * Etat initial, sans pièces.
         */
        EMPTY,

        /**
         * Etat initial standard.
         */
        STARTING
    }

    /**
     * Enumération des types de représentation d'une partie disponibles.
     */
    public enum Type {
        /**
         * Description basée sur un tableau à deux dimensions.
         */
        ARRAY("fr.free.jchecs.core.ArrayBoard"),

        /**
         * Description basée sur un tableau bordé, à une dimension.
         */
        MAILBOX("fr.free.jchecs.core.MailboxBoard"),

        /**
         * Description la plus rapide : actuellement équivalent à MAILBOX.
         */
        FASTEST("fr.free.jchecs.core.MailboxBoard"),

        /**
         * Description basée sur un tableau à une dimension avec indice filtré
         * par la valeur 0x88.
         */
        X88("fr.free.jchecs.core.X88Board");

        /**
         * Nom de la classe d'implémentation.
         */
        private final String _className;

        /**
         * Instancie une nouvelle constante de type de MoveGenerator.
         *
         * @param pClassName Nom de la classe d'implémentation.
         */
        Type(final String pClassName) {
            _className = pClassName;
        }

        /**
         * Renvoi le nom de la classe.
         *
         * @return Nom de la classe implémentant ce type.
         */
        String getClassName() {
            return _className;
        }
    }
}

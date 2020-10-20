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
package fr.free.jchecs.ai;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Classe fabrique des instances de moteurs d'IA.
 *
 * @author David Cotton
 */
public final class EngineFactory {
    /**
     * Log de la classe.
     */
    private static final Logger LOG = LoggerFactory.getLogger(EngineFactory.class);

    /**
     * Liste des moteurs d'IA internes.
     */
    private static final Map<String, Class<? extends Engine>> INTERNAL_ENGINES = new HashMap<>();

    static {
        registerEngine("jChecs.AlphaBeta", "fr.free.jchecs.ai.AlphaBetaEngine");
        registerEngine("jChecs.Debug", "fr.free.jchecs.ai.DebugEngine");
        registerEngine("jChecs.MiniMax", "fr.free.jchecs.ai.MiniMaxEngine");
        registerEngine("jChecs.MiniMax++",
                "fr.free.jchecs.ai.EnhancedMiniMaxEngine");
        registerEngine("jChecs.NegaScout", "fr.free.jchecs.ai.NegaScoutEngine");
        registerEngine("jChecs.Random", "fr.free.jchecs.ai.RandomEngine");
    }

    /**
     * Classe utilitaire : ne pas instancier.
     */
    private EngineFactory() {
        // Rien de spécifique...
    }

    /**
     * Renvoi la liste des moteurs IA disponibles.
     *
     * @return Liste des noms des moteurs d'IA disponibles.
     */
    public static String[] getNames() {
        final Set<String> lst = INTERNAL_ENGINES.keySet();
        return lst.toArray(new String[lst.size()]);
    }

    /**
     * Renvoi une nouvelle instance du moteur d'IA par défaut.
     *
     * @return Nouvelle instance du moteur d'IA par défaut.
     */
    public static Engine newInstance() {
        return newInstance("jChecs.NegaScout");
    }

    /**
     * Renvoi une nouvelle instance du moteur d'IA dont le nom identifiant est
     * transmis.
     *
     * @param pNom Nom identifiant le moteur IA.
     * @return Nouvelle instance du moteur d'IA correspondant (ou null si aucune
     * correspondance).
     */
    public static Engine newInstance(final String pNom) {
        Engine res = null;

        final Class<? extends Engine> cls = INTERNAL_ENGINES.get(pNom);
        if (cls != null) {
            try {
                res = cls.newInstance();
            } catch (final InstantiationException | IllegalAccessException e) {
                LOG.trace(e.toString());
            }
        }

        if (res == null) {
            LOG.warn("Invalid engine [{}]", pNom);
        }

        return res;
    }

    /**
     * Ajoute, si possible, un moteur d'IA à la liste des moteurs disponibles.
     *
     * @param pNom    Nom identifiant le moteur IA.
     * @param pClasse Nom complet de la classe implémentant le moteur d'IA.
     */
    private static void registerEngine(final String pNom, final String pClasse) {
        Class<?> classe;
        try {
            classe = Class.forName(pClasse);
            if ((classe != null) && (Engine.class.isAssignableFrom(classe))) {
                INTERNAL_ENGINES.put(pNom, classe.asSubclass(Engine.class));
            }
        } catch (final ClassNotFoundException e) {
            LOG.warn("Class [{}] not found for engine [{}].", pClasse, pNom);
        } catch (final ClassFormatError e) {
            // Exception launched when a custom 404 HTTP error code is defined
            // on the applet's server.
            LOG.warn("Class [{}] format error.", pClasse);
        }
    }
}

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

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import fr.free.jchecs.core.BoardFactory;
import fr.free.jchecs.core.Move;
import fr.free.jchecs.core.MoveGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static fr.free.jchecs.core.BoardFactory.State.EMPTY;
import static fr.free.jchecs.core.BoardFactory.Type.FASTEST;

/**
 * Implémentation de base des moteurs d'IA pour les échecs.
 *
 * @author David Cotton
 */
abstract class AbstractEngine implements Engine {
    /**
     * Générateur de nombres aléatoires.
     */
    static final Random RANDOMIZER = new Random();

    /**
     * Valeur d'un Mat.
     */
    static final int MATE_VALUE = Integer.MIN_VALUE / 2;

    /**
     * Modèle de découpage des enregistrements des ouvertures suivant les ';'.
     */
    static final Pattern SPLITTER = Pattern.compile(";");

    /**
     * Log de la classe.
     */
    private static final Logger LOG = LoggerFactory.getLogger(AbstractEngine.class);

    /**
     * Durée maxi. d'attente d'une réponse du Web Service.
     */
    private static final long ELAPSED_TIME_LIMIT = 10000;

    /**
     * Buffer des ouvertures.
     */
    private static Map<Integer, int[]> S_openings;

    static {
        final Thread preload = new Thread(new Runnable() {
            /**
             * Tâche de fond pour masquer le temps de chargement...
             */
            @Override
            public void run() {
                getFromOpenings(BoardFactory.valueOf(FASTEST, EMPTY));
            }
        });
        preload.setPriority(Thread.MIN_PRIORITY);
        preload.start();
    }

    /**
     * Limite basse de la profondeur de recherche.
     */
    private final int _minimalSearchDepth;

    /**
     * Limite haute de la profondeur de recherche.
     */
    private final int _maximalSearchDepth;

    /**
     * Temps total passé en traitement par le moteur.
     */
    private long _elapsedTime;

    /**
     * Nombre total de demi-coups évalué par le moteur.
     */
    private int _halfmoveCount;

    /**
     * Fonction d'évalutation utilisée par le moteur.
     */
    private Heuristic _heuristic;

    /**
     * Fonction de tri des mouvements.
     */
    private Comparator<Move> _moveSorter;

    /**
     * Drapeau signalant l'activation du web service de fin de partie.
     */
    private boolean _endgamesWSEnabled;

    /**
     * Drapeau signalant l'activation de la bibliothèque d'ouvertures.
     */
    private boolean _openingsEnabled;

    /**
     * Score du dernier mouvement.
     */
    private int _score;

    /**
     * Limite de la profondeur de recherche (en demi-coups).
     */
    private int _searchDepthLimit;

    /**
     * Instancie un nouveau moteur IA.
     *
     * @param pProfMin Limite basse de la profondeur de recherche (>= 1).
     * @param pProfMax Limite haute de la profondeur de recherche (>= pProfMin).
     * @param pProfDef Limite par défaut de la profondeur de recherche ([pProfMin,
     *                 pProfMax]).
     */
    AbstractEngine(final int pProfMin, final int pProfMax,
                   final int pProfDef) {
        _minimalSearchDepth = pProfMin;
        _maximalSearchDepth = pProfMax;

        setSearchDepthLimit(pProfDef);

        setHeuristic(new MobilityHeuristic());

        setOpeningsEnabled(true);
        setEndgamesWSEnabled(true);
    }

    /**
     * Ajoute une durée (en ms) au temps total de traitement par le moteur.
     *
     * @param pDuree Duree en ms à ajouter.
     */
    private void addElapsedTime(final long pDuree) {
        _elapsedTime += pDuree;
    }

    /**
     * Ajoute un décompte de demi-coups au nombre de demi-coups évalués par le
     * moteur.
     *
     * @param pNombre Nombre de demi-coups à ajouter.
     */
    final void addHalfmove(final int pNombre) {
        _halfmoveCount += pNombre;
    }

    /**
     * Renvoi le temps total passé en traitement par le moteur.
     *
     * @return Temps total de traitement par le moteur (en ms).
     */
    @Override
    public final long getElapsedTime() {
        return _elapsedTime;
    }

    /**
     * Renvoi le mouvement correspondant à une position dans la bibliothèque de
     * fermeture.
     *
     * @param pEtat Etat du jeu.
     * @return Mouvement correspondant (ou null)
     */
    final Move getFromEndgames(final MoveGenerator pEtat) {
        final Move res = null;

        final EndgamesWSRequest wsRequest = new EndgamesWSRequest(pEtat);
        wsRequest.start();
        try {
            wsRequest.join();
        } catch (final InterruptedException e) {
            LOG.trace("Request to endgames WebService interrupted.");
        }

        return res;
    }

    /**
     * Renvoi le mouvement correspondant à une position dans la bibliothèque
     * d'ouverture.
     *
     * @param pEtat Etat du jeu.
     * @return Mouvement correspondant (ou null)
     */
    private static synchronized Move getFromOpenings(final MoveGenerator pEtat) {
        Move res = null;

        if (S_openings == null) {
            final InputStream is = Engine.class
                    .getResourceAsStream("jchecs.opn");
            if (is != null) {
                S_openings = new HashMap<>();
                DataInputStream in = null;
                try {
                    in = new DataInputStream(new GZIPInputStream(is));
                    while (in.available() > 0) {
                        final int nb = in.readByte();
                        final int[] mvtsId = new int[nb];
                        final int cle = in.readInt();
                        for (int i = 0; i < nb; i++) {
                            mvtsId[i] = (in.readUnsignedShort() << 8)
                                    + in.readUnsignedByte();
                        }
                        S_openings.put(cle, mvtsId);
                    }
                } catch (final IOException e) {
                    // Pas grave, le coup sera calculé...
                } finally {
                    if (in != null) {
                        try {
                            in.close();
                        } catch (final IOException e1) {
                            // On aura essayé :-)
                        }
                    }
                }
            }
        }

        int[] ids = null;
        if (S_openings != null) {
            ids = S_openings.get(pEtat.hashCode());
        }
        if (ids != null) {
            res = Move.valueOf(ids[RANDOMIZER.nextInt(ids.length)]);
            // Les hashcodes n'étant pas infaïbles, il vaut mieux valider le
            // mouvement
            // obtenu...
            boolean erreurHashcode = true;
            for (final Move mvt : pEtat.getValidMoves(pEtat.isWhiteActive())) {
                if (mvt.equals(res)) {
                    erreurHashcode = false;
                    break;
                }
            }
            if (erreurHashcode) {
                res = null;
            }
        }

        return res;
    }

    /**
     * Renvoi le nombre total de demi-coups évalués par le moteur.
     *
     * @return Nombre total de demi-coups évalués par le moteur.
     */
    @Override
    public final int getHalfmoveCount() {
        return _halfmoveCount;
    }

    /**
     * Renvoi la fonction d'évaluation utilisée par le moteur.
     *
     * @return Fonction d'évaluation utilisée.
     */
    @Override
    public final Heuristic getHeuristic() {
        return _heuristic;
    }

    /**
     * Renvoi la limite haute de la profondeur de recherche supportées par le
     * moteur.
     *
     * @return Limite haute de la profondeur de recherche (>=
     * getMinimalSearchDepth()).
     */
    @Override
    public final int getMaximalSearchDepth() {
        return _maximalSearchDepth;
    }

    /**
     * Renvoi la limite basse de la profondeur de recherche supportées par le
     * moteur.
     *
     * @return Limite basse de la profondeur de recherche (>= 1).
     */
    @Override
    public final int getMinimalSearchDepth() {
        return _minimalSearchDepth;
    }

    /**
     * Recherche un mouvement répondant à un état de l'échiquier.
     *
     * @param pEtat Etat de l'échiquier.
     * @return Mouvement trouvé.
     */
    @Override
    public final synchronized Move getMoveFor(final MoveGenerator pEtat) {
        final long debut = System.currentTimeMillis();

        Move res = null;

        setScore(0);
        EndgamesWSRequest wsRequest = null;

        if (_openingsEnabled && (pEtat.getFullmoveNumber() < 20)) {
            res = getFromOpenings(pEtat);
        }

        if (res == null) {
            if (_endgamesWSEnabled && (pEtat.getPiecesCount() < 7)) {
                wsRequest = new EndgamesWSRequest(pEtat);
                wsRequest.start();
            }

            // Calcul du meilleur coup...
            final Move[] coups = pEtat.getValidMoves(pEtat.isWhiteActive());

            res = searchMoveFor(pEtat, coups);

            if (wsRequest != null) {
                // ... mais préférer les éventuelles réponses du Web Service en
                // fin de
                // partie.
                final long ecoule = System.currentTimeMillis() - debut;
                if (ecoule < ELAPSED_TIME_LIMIT) {
                    // Attendre un peu le Web Service si le moteur interne est
                    // allé trop
                    // vite.
                    try {
                        wsRequest.join(ELAPSED_TIME_LIMIT - ecoule);
                    } catch (final InterruptedException e) {
                        // Pas grave, on peut s'en passer.
                    }
                }

                final Move mvt = wsRequest.getMove();
                if (mvt != null) {
                    res = mvt;
                }
            }
        }

        final long duree = System.currentTimeMillis() - debut;
        addElapsedTime(duree);

        return res;
    }

    /**
     * Renvoi la fonction de tri des mouvements.
     *
     * @return Fonction de tri des mouvements.
     */
    @Override
    public final Comparator<Move> getMoveSorter() {
        return _moveSorter;
    }

    /**
     * Renvoi le score obtenu par le dernier mouvement calculé.
     *
     * @return Score du dernier mouvement.
     */
    @Override
    public final int getScore() {
        return _score;
    }

    /**
     * Renvoi la valeur limite de la profondeur de recherche (en demi-coups).
     *
     * @return Limite de la profondeur de recherche ([getMinimalSearchDepth(),
     * getMaximalSearchDepth()]).
     */
    @Override
    public final int getSearchDepthLimit() {
        return _searchDepthLimit;
    }

    /**
     * Indique si l'utilisation du web service de fin de partie est activée.
     *
     * @return "true" si le web service est utilisé, "false" sinon.
     */
    @Override
    public final boolean isEndgamesWSEnabled() {
        return _endgamesWSEnabled;
    }

    /**
     * Indique si l'utilisation de la bibliothèque d'ouvertures est activée.
     *
     * @return "true" si les ouvertures sont utilisées, "false" sinon.
     */
    @Override
    public final boolean isOpeningsEnabled() {
        return _openingsEnabled;
    }

    /**
     * Corps de la recherche du "meilleur" demi-coup pour un état de
     * l'échiquier.
     *
     * @param pEtat  Etat de l'échiquier.
     * @param pCoups Liste des mouvement initiaux valides.
     * @return Mouvement trouvé.
     */
    protected abstract Move searchMoveFor(final MoveGenerator pEtat,
                                          final Move[] pCoups);

    /**
     * Active / désactive l'utilisation du web service de fin de partie.
     *
     * @param pActif A "true" pour activer l'utilisation du web service, à "false"
     *               sinon.
     */
    @Override
    public final void setEndgamesWSEnabled(final boolean pActif) {
        _endgamesWSEnabled = pActif;
    }

    /**
     * Modifie la fonction d'évaluation utilisée par le moteur.
     *
     * @param pHeuristique Nouvelle fonction d'évaluation à utiliser.
     */
    @Override
    public final void setHeuristic(final Heuristic pHeuristique) {
        _heuristic = pHeuristique;
    }

    /**
     * Modifie la fonction d'ordenancement des mouvements.
     *
     * @param pComparateur Nouvelle fonction de tri des mouvements.
     */
    @Override
    public final void setMoveSorter(final Comparator<Move> pComparateur) {
        _moveSorter = pComparateur;
    }

    /**
     * Active / désactive l'utilisation de la bibliothèque d'ouvertures.
     *
     * @param pActif A "true" pour activer l'utilisation des ouvertures, à "false"
     *               sinon.
     */
    @Override
    public final void setOpeningsEnabled(final boolean pActif) {
        _openingsEnabled = pActif;
    }

    /**
     * Alimente le score obtenu par le dernier mouvement calculé.
     *
     * @param pScore Score du dernier mouvement.
     */
    final void setScore(final int pScore) {
        _score = pScore;
    }

    /**
     * Aliment la valeur de la limite de la profondeur de recherche (en
     * demi-coups).
     *
     * @param pLimite Limite de la profondeur de recherche
     *                ([getMinimalSearchDepth(), getMaximalSearchDepth()]).
     */
    @Override
    public final void setSearchDepthLimit(final int pLimite) {
        _searchDepthLimit = pLimite;
    }
}

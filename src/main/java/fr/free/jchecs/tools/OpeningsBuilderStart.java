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
package fr.free.jchecs.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Pattern;
import java.util.zip.GZIPOutputStream;

import fr.free.jchecs.core.Board;
import fr.free.jchecs.core.BoardFactory;
import fr.free.jchecs.core.FENException;
import fr.free.jchecs.core.Move;
import fr.free.jchecs.core.MoveGenerator;
import fr.free.jchecs.core.SANException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static fr.free.jchecs.core.BoardFactory.State.EMPTY;
import static fr.free.jchecs.core.BoardFactory.State.STARTING;
import static fr.free.jchecs.core.BoardFactory.Type.FASTEST;
import static fr.free.jchecs.core.FENUtils.toBoard;
import static fr.free.jchecs.core.FENUtils.toFENKey;
import static fr.free.jchecs.core.PGNUtils.toNormalizedSAN;
import static fr.free.jchecs.core.SANUtils.toMove;

/**
 * Classe utilitaire permettant de créer :
 * <ul>
 * <li>une liste des meilleurs coups au format EPD à partir d'un fichier de
 * parties au format PGN, ou</li>
 * <li>une liste intermédiaire, épurée, des parties utilisables à partir d'un
 * fichier PGN, ou</li>
 * <li>un fichier EPD des ouvertures à partir d'une liste épurée des parties
 * utilisables, ou</li>
 * <li>une bibliothèque d'ouvertures à partir d'un fichier EPD des meilleurs
 * coups.</li>
 * </ul>
 * <p>
 * Syntaxe d'appel : java fr.free.jchecs.tools.OpeningsBuilderStart
 * &lt;chemin_fichier_pgn&gt; &lt;profondeur_analyse&gt; <br>
 * ou : java fr.free.jchecs.tools.OpeningsBuilderStart -cln
 * &lt;chemin_fichier_pgn&gt; <br>
 * ou : java fr.free.jchecs.tools.OpeningsBuilderStart -epd
 * &lt;chemin_fichier_cln&gt; &lt;profondeur_analyse&gt;<br>
 * ou : java fr.free.jchecs.tools.OpeningsBuilderStart -opn
 * &lt;chemin_fichier_epd&gt;
 * </p>
 *
 * @author David Cotton
 */
final class OpeningsBuilderStart {
    /**
     * Nombre de lignes maximum du buffer de tri interne.
     */
    private static final int MAX_LINE_COUNT = 50000;

    /**
     * Log de la classe.
     */
    private static final Logger LOG = LoggerFactory.getLogger(OpeningsBuilderStart.class);

    /**
     * Plateau vide servant de référence.
     */
    private static final MoveGenerator EMPTY_BOARD = BoardFactory.valueOf(
            FASTEST, EMPTY);

    /**
     * Liste des fichiers de travail.
     */
    private static final List<File> TEMP_FILES = new ArrayList<>();

    /**
     * Buffer servant à pré-trier les enregistrements des fichiers de travail.
     */
    private static final Set<String> INTERNAL_SORT = new TreeSet<>();

    /**
     * Modèle de découpage de chaines suivant les espaces.
     */
    private static final Pattern SPLITTER = Pattern.compile("[ ]+");

    /**
     * Clé de la position initiale.
     */
    private static final String STARTING_KEY = toFENKey(BoardFactory.valueOf(FASTEST, STARTING));

    /**
     * Ensemble des positions analysées.
     */
    private static final Map<String, Map<String, Integer>> POSITIONS = new TreeMap<>();

    static {
        POSITIONS.put(STARTING_KEY, new TreeMap<>());
    }

    /**
     * Tri les mouvements du meilleur au plus mauvais ( dans l'ordre inverse de
     * leurs scores).
     */
    private static final Comparator<Map.Entry<String, Integer>> BEST_MOVE_SORTER = new Comparator<Map.Entry<String, Integer>>() {
        /**
         * Tri les mouvements du meilleur au plus mauvais.
         *
         * @param pMvt1
         *            Premier mouvement.
         * @param pMvt2
         *            Deuxième mouvement.
         * @return Cf contrat de Comparator().
         */
        @Override
        public int compare(final Map.Entry<String, Integer> pMvt1,
                           final Map.Entry<String, Integer> pMvt2) {
            return -pMvt1.getValue().compareTo(pMvt2.getValue());
        }
    };

    /**
     * Tri les mouvements dans l'ordre alphanumérique.
     */
    private static final Comparator<Map.Entry<String, Integer>> ALPHA_MOVE_SORTER = new Comparator<Map.Entry<String, Integer>>() {
        /**
         * Tri les mouvements suivant l'ordre alphanumérique.
         *
         * @param pMvt1
         *            Premier mouvement.
         * @param pMvt2
         *            Deuxième mouvement.
         * @return Cf contrat de Comparator().
         */
        @Override
        public int compare(final Map.Entry<String, Integer> pMvt1,
                           final Map.Entry<String, Integer> pMvt2) {
            return pMvt1.getKey().compareTo(pMvt2.getKey());
        }
    };

    /**
     * Compteur des parties lues.
     */
    private static int S_read;

    /**
     * Compteur des parties non valides.
     */
    private static int S_invalid;

    /**
     * Compteur des parties incomplètes.
     */
    private static int S_incomplete;

    /**
     * Compteur des parties nulles.
     */
    private static int S_drawn;

    /**
     * Compteur des parties terminées sur autre chose qu'un vrai mat (timeout ?
     * rédition ?).
     */
    private static int S_notRealMat;

    /**
     * Compteur des parties stockées temporairement.
     */
    private static int S_temporary;

    /**
     * Compteur des parties en double.
     */
    private static int S_duplicate;

    /**
     * Compteur des parties uniques.
     */
    private static int S_unique;

    /**
     * Compteur des positions d'ouverture obtenues.
     */
    private static int S_openingsPositions;

    /**
     * Classe utilitaire : ne pas instancier.
     */
    private OpeningsBuilderStart() {
        // Rien à faire de spécifique...
    }

    /**
     * Conserve temporairement une partie valide.
     *
     * @param pPartie Chaine SAN des mouvements composant la partie.
     */
    private static void addGameToTemp(final String pPartie) {
        if (INTERNAL_SORT.add(pPartie)) {
            S_temporary++;
            if (INTERNAL_SORT.size() >= MAX_LINE_COUNT) {
                flushTemp();
            }
        } else {
            S_duplicate++;
        }
    }

    /**
     * Conserve une partie lors du tri.
     *
     * @param pSortie     Buffer de sortie du tri.
     * @param pPartie     Chaine SAN des mouvements composant la partie.
     * @param pPrecedente Partie précédemment écrite (peut être à null).
     * @return Renvoi la dernière partie écrite.
     */
    private static String addGameToSort(final BufferedWriter pSortie,
                                        final String pPartie, final String pPrecedente) {
        if (pPartie.equals(pPrecedente)) {
            S_duplicate++;
        } else {
            S_unique++;
            try {
                pSortie.write(pPartie);
                pSortie.newLine();
            } catch (final IOException e) {
                error(e);
            }
        }

        return pPartie;
    }

    /**
     * Analyse la liste des parties.
     *
     * @param pProfondeur Profondeur d'analyse (>= 0).
     */
    private static void analyseGames(final int pProfondeur) {
        try (final FileReader ficIn = new FileReader(TEMP_FILES.get(0));
             final BufferedReader in = new BufferedReader(ficIn)) {
            String ligne = in.readLine();
            int partiesAnalysees = 0;
            while (ligne != null) {
                partiesAnalysees++;
                if (partiesAnalysees % 10000 == 0) {
                    LOG.info("  ... {} games analysed ...", partiesAnalysees);
                }
                final boolean gagnant = ligne.endsWith(" 1-0");
                final String[] mvts = SPLITTER.split(ligne);
                final int l = mvts.length - 1;
                int i = 0;
                Map<String, Integer> pos = POSITIONS.get(STARTING_KEY);
                MoveGenerator etat = BoardFactory.valueOf(FASTEST, STARTING);
                while (true) {
                    final String mvt = toNormalizedSAN(mvts[i]);
                    Integer note = pos.get(mvt);
                    if (note == null) {
                        note = 0;
                    }
                    if (etat.isWhiteActive() == gagnant) {
                        pos.put(mvt, note + 1);
                    } else {
                        pos.put(mvt, note - 1);
                    }
                    i++;
                    if ((i >= pProfondeur) || (i >= l)) {
                        break;
                    }
                    try {
                        etat = etat.derive(toMove(etat, mvt), true);
                        final String cle = toFENKey(etat);
                        pos = POSITIONS.computeIfAbsent(cle, k -> new TreeMap<>());
                    } catch (final SANException e) {
                        error(e);
                    }
                }
                ligne = in.readLine();
            }
            TEMP_FILES.remove(0).delete();
        } catch (final IOException e) {
            error(e);
        }
    }

    /**
     * Transforme un fichier épuré des parties utilisables en fichier
     * d'ouvertures EPD.
     *
     * @param pFichier    Chemin du fichier épuré.
     * @param pProfondeur Profondeur de mouvement à prendre en compte.
     */

    private static void cln2epd(final String pFichier, final int pProfondeur) {
        LOG.info("Analysing games (depth=[})...", pProfondeur);
        analyseGames(pProfondeur);
        LOG.info("  Analysed positions = {}", POSITIONS.size());

        LOG.info("Writing openings file...");
        writeOpenings(pFichier);
        LOG.info("  Openings positions = {}", S_openingsPositions);

        LOG.info("Openings builded.");
    }

    /**
     * Copie un fichier vers un autre.
     *
     * @param pSource Fichier source.
     * @param pCible  Fichier cible.
     */
    private static void copy(final File pSource, final File pCible) {
        try (final FileInputStream src = new FileInputStream(pSource);
             final FileOutputStream dst = new FileOutputStream(pCible)) {
            final byte[] buffer = new byte[4096];
            int l = src.read(buffer);
            while (l > 0) {
                dst.write(buffer, 0, l);
                l = src.read(buffer);
            }
            src.close();
            dst.close();
        } catch (final IOException e) {
            error(e);
        }
    }

    /**
     * Termine le programme sur une erreur.
     *
     * @param pError Erreur fatale.
     */
    private static void error(final Throwable pError) {
        LOG.error("Fatal error.", pError);
        System.exit(-1);
    }

    /**
     * Termine le programme sur une erreur.
     *
     * @param pMessage Message d'erreur.
     */
    private static void error(final String pMessage) {
        LOG.error("{}", pMessage);
        System.exit(-1);
    }

    /**
     * Vide le buffer interne vers un fichier temporaire.
     */
    private static void flushTemp() {
        if (!INTERNAL_SORT.isEmpty()) {
            FileWriter ficOut = null;
            try {
                final File ficTmp = File.createTempFile(
                        "obs" + TEMP_FILES.size() + "t", null);
                ficTmp.deleteOnExit();
                TEMP_FILES.add(ficTmp);
                ficOut = new FileWriter(ficTmp);
            } catch (final IOException e) {
                error(e);
            }

            try (final BufferedWriter out = new BufferedWriter(ficOut)) {
                for (final String s : INTERNAL_SORT) {
                    out.write(s);
                    out.newLine();
                }
                out.close();
            } catch (final IOException e) {
                error(e);
            }

            S_unique = INTERNAL_SORT.size();
            INTERNAL_SORT.clear();
        }
    }

    /**
     * Lance la construction d'une bibliothèque d'ouverture à partir d'un
     * fichier PGN.
     *
     * @param pArgs Liste des arguments en ligne de commande.
     */
    public static void main(final String[] pArgs) {
        if ((pArgs.length != 2)
                && ((pArgs.length != 3) || (!"-epd".equals(pArgs[0])))) {
            error("Usage: java fr.free.jchecs.tools.OpeningsBuilderStart <path_to_pgn> <depth>\n"
                    + "   or: java fr.free.jchecs.tools.OpeningsBuilderStart -cln <path_to_pgn>\n"
                    + "   or: java fr.free.jchecs.tools.OpeningsBuilderStart -epd <path_to_cln> <depth>\n"
                    + "   or: java fr.free.jchecs.tools.OpeningsBuilderStart -opn <path_to_epd>");
        }

        if ("-opn".equals(pArgs[0])) {
            LOG.info("Parsing EPD file...");
            parseEPDFile(pArgs[1]);
            LOG.info("  Positions read     = {}", S_read);
            LOG.info("  Invalid positions  = {}", S_invalid);
            LOG.info("  Unique refs writed = {}", S_unique);
            if (S_read != S_unique) {
                LOG.info("RESULT IS NOT SAFE : HASHCODE CLASH ?");
            }
        } else if ("-cln".equals(pArgs[0])) {
            pgn2cln(pArgs[1]);

            copy(TEMP_FILES.get(0), new File(pArgs[1] + ".cln"));
        } else if ("-epd".equals(pArgs[0])) {
            File ficTmp = null;
            try {
                ficTmp = File.createTempFile("obs" + TEMP_FILES.size() + "c",
                        null);
            } catch (final IOException e) {
                error(e.toString());
            }

            if (ficTmp != null) {
                ficTmp.deleteOnExit();
                TEMP_FILES.add(ficTmp);
                copy(new File(pArgs[1]), ficTmp);

                cln2epd(pArgs[1], Integer.parseInt(pArgs[2]));
            }
        } else {
            pgn2cln(pArgs[0]);

            cln2epd(pArgs[0], Integer.parseInt(pArgs[1]));
        }

        TEMP_FILES.clear();

        System.exit(0);
    }

    /**
     * Interprète le fichier EPD en entrée.
     *
     * @param pChemin Chemin du fichier EPD en entrée.
     */
    private static void parseEPDFile(final String pChemin) {
        try (final FileReader ficIn = new FileReader(pChemin);
             final BufferedReader in = new BufferedReader(ficIn);
             final DataOutputStream ficOut = new DataOutputStream(new GZIPOutputStream(new FileOutputStream(pChemin + ".opn")))) {
            final Map<Integer, int[]> infos = new HashMap<>();

            String ligneLue = in.readLine();
            while (ligneLue != null) {
                S_read++;
                final String ligne = ligneLue.trim();
                int finCle = ligne.indexOf(' ');
                finCle = ligne.indexOf(' ', finCle + 1);
                finCle = ligne.indexOf(' ', finCle + 1);
                finCle = ligne.indexOf(' ', finCle + 1);
                final String cle = ligne.substring(0, finCle);
                final int debutBm = ligne.indexOf("bm ", cle.length());
                final int finBm = ligne.indexOf(';', debutBm);
                final String[] mvtsSAN = SPLITTER.split(ligne.substring(
                        debutBm + 3, finBm));
                final int l = mvtsSAN.length;
                if (l > 0) {
                    final int[] mvtsId = new int[l];
                    try {
                        final Board etat = toBoard(cle + " 0 1");
                        for (int i = 0; i < l; i++) {
                            try {
                                final Move mvt = toMove(
                                        EMPTY_BOARD.derive(etat),
                                        toNormalizedSAN(mvtsSAN[i]));
                                mvtsId[i] = mvt.toId();
                            } catch (final SANException e) {
                                S_invalid++;
                                LOG.trace(e.toString());
                            }
                        }
                        infos.put(etat.hashCode(), mvtsId);
                    } catch (final FENException e) {
                        S_invalid++;
                        LOG.trace(e.toString());
                    }
                }
                if (S_read % 25000 == 0) {
                    LOG.info("  ... {} positions read ({} invalid) ...", S_read, S_invalid);
                }

                ligneLue = in.readLine();
            }
            for (final Map.Entry<Integer, int[]> entree : infos.entrySet()) {
                final int nb = entree.getValue().length;
                ficOut.writeByte(nb);
                ficOut.writeInt(entree.getKey());
                for (final int id : entree.getValue()) {
                    // Les Id de mouvement sont actuellement sur 24 bits...
                    ficOut.writeShort((id >>> 8) & 0xFFFF);
                    ficOut.writeByte(id & 0xFF);
                }
                S_unique++;
            }
            ficOut.close();
        } catch (final IOException e) {
            error(e.toString());
        }
    }

    /**
     * Interprète une liste de mouvements d'une partie du fichier PGN en entrée.
     * <p>
     * Seuls les marqueurs des mouvements sont conservés, épurés des
     * commentaires et autres marqueurs sans intérêt pour le traitement.
     * </p>
     *
     * @param pEntree Lecteur du fichier PGN.
     * @param pLigne  Première ligne de la liste de mouvements.
     */

    private static void parseGame(final BufferedReader pEntree,
                                  final String pLigne) {
        S_read++;
        if (S_read % 250000 == 0) {
            LOG.info("  ... {} games read ({} kept) ...", S_read, S_temporary);
        }
        final StringBuilder sb = new StringBuilder(pLigne);
        String sanLue;
        try {
            sanLue = pEntree.readLine();
            while (sanLue != null) {
                final String san = sanLue.trim();
                if ((san.length() == 0) || san.startsWith("[Event")) {
                    break;
                }
                sb.append(' ').append(san);
                sanLue = pEntree.readLine();
            }
        } catch (final IOException e) {
            error(e.toString());
        }

        final String partie = sb.toString();
        if (partie.endsWith(" 1-0") || partie.endsWith(" 0-1")) {
            int p = 0;
            int prof = 0;
            while (p < sb.length()) {
                final char c = sb.charAt(p);
                if ((c == '(') || (c == '{')) {
                    // Supprime les commentaires, et les propositions de nul...
                    prof++;
                }
                if ((prof != 0) || (c == '+') || (c == '#')) {
                    // Supprime les marqueurs d'échecs et de mat.
                    sb.deleteCharAt(p);
                } else {
                    if (c == 'O') {
                        // Convertir les "o" majuscules en zéro...
                        sb.setCharAt(p, '0');
                    }
                    p++;
                }
                if ((c == '}') || (c == ')')) {
                    prof--;
                }
            }
            if (prof != 0) {
                S_invalid++;
            } else {
                p = 0;
                while (p < sb.length()) {
                    final char c = sb.charAt(p);
                    if (c == '.') {
                        // Supprime les numéros de coups...
                        int deb = p - 1;
                        while ((deb >= 0) && Character.isDigit(sb.charAt(deb))) {
                            deb--;
                        }
                        int fin = p + 1;
                        while ((fin < sb.length())
                                && ((" .".indexOf(sb.charAt(fin))) >= 0)) {
                            fin++;
                        }
                        sb.delete(deb + 1, fin);
                    } else if (c == '$') {
                        // Supprime les annotations numériques...
                        int fin = p + 1;
                        while ((fin < sb.length())
                                && Character.isDigit(sb.charAt(fin))) {
                            fin++;
                        }
                        sb.delete(p, fin + 1);
                    } else {
                        p++;
                    }
                }
                addGameToTemp(sb.toString());
            }
        } else if (partie.endsWith(" 1/2-1/2")) {
            S_drawn++;
        } else if (partie.endsWith(" *")) {
            S_incomplete++;
        } else {
            S_invalid++;
        }
    }

    /**
     * Interprète le fichier PGN en entrée.
     *
     * @param pChemin Chemin du fichier PGN en entrée.
     */
    private static void parsePGNFile(final String pChemin) {
        try (final FileReader ficIn = new FileReader(pChemin);
             final BufferedReader in = new BufferedReader(ficIn)) {
            String ligneLue = in.readLine();
            while (ligneLue != null) {
                final String ligne = ligneLue.trim();
                if (ligne.startsWith("1.")) {
                    parseGame(in, ligne);
                }
                ligneLue = in.readLine();
            }
            flushTemp();
            in.close();
        } catch (final IOException e) {
            error(e);
        }
    }

    /**
     * Transforme un fichier PGN en une liste épurée des parties utilisables.
     *
     * @param pFichier Chemin du fichier PGN.
     */
    private static void pgn2cln(final String pFichier) {
        LOG.info("Parsing PGN file...");
        parsePGNFile(pFichier);
        LOG.info("  Games read       = {}", S_read);
        LOG.info("  Incomplete games = {}", S_incomplete);
        LOG.info("  Drawn games      = {}", S_drawn);
        LOG.info("  Invalid games    = {}", S_invalid);
        LOG.info("  Duplicate games  = {}", S_duplicate);
        LOG.info("  => Games kept    = {}", S_temporary);
        assert S_read == (S_incomplete + S_drawn + S_invalid + S_duplicate + S_temporary);

        LOG.info("Searching duplicate games...");
        searchDuplicateGames();
        LOG.info("  Duplicate games = {}", S_duplicate);
        LOG.info("  => Unique games = {}", S_unique);
        assert S_temporary == (S_unique + S_duplicate);

        LOG.info("Searching valid mat games...");
        searchMatGames();
        LOG.info("  Invalid games  = {}", S_invalid);
        LOG.info("  Not mat games  = {}", S_notRealMat);
        LOG.info("  => Valid games = {}", S_temporary);
        assert S_unique == (S_invalid + S_notRealMat + S_temporary);
    }

    /**
     * Tri fusion des fichiers temporaires permettant d'éliminer les doublons.
     */
    private static void searchDuplicateGames() {
        S_duplicate = 0;
        if (TEMP_FILES.size() > 1) {
            S_unique = 0;
            while (TEMP_FILES.size() > 1) {
                FileReader ficIn1 = null;
                FileReader ficIn2 = null;
                try {
                    ficIn1 = new FileReader(TEMP_FILES.get(0));
                    ficIn2 = new FileReader(TEMP_FILES.get(1));
                } catch (final FileNotFoundException e) {
                    error(e.toString());
                }

                FileWriter ficOut = null;
                try {
                    final File ficTmp = File.createTempFile(
                            "obs" + TEMP_FILES.size() + "s", null);
                    ficTmp.deleteOnExit();
                    TEMP_FILES.add(ficTmp);
                    ficOut = new FileWriter(ficTmp);
                } catch (final IOException e) {
                    error(e.toString());
                }

                S_unique = 0;
                final BufferedReader in1 = new BufferedReader(ficIn1);
                final BufferedReader in2 = new BufferedReader(ficIn2);
                final BufferedWriter out = new BufferedWriter(ficOut);
                String precedente = null;
                try {
                    String ligne1 = in1.readLine();
                    String ligne2 = in2.readLine();
                    while ((ligne1 != null) || (ligne2 != null)) {
                        if (ligne1 == null) {
                            precedente = addGameToSort(out, ligne2, precedente);
                            ligne2 = in2.readLine();
                        } else if (ligne2 == null) {
                            precedente = addGameToSort(out, ligne1, precedente);
                            ligne1 = in1.readLine();
                        } else {
                            final int comp = ligne1.compareTo(ligne2);
                            if (comp < 0) {
                                precedente = addGameToSort(out, ligne1,
                                        precedente);
                                ligne1 = in1.readLine();
                            } else if (comp > 0) {
                                precedente = addGameToSort(out, ligne2,
                                        precedente);
                                ligne2 = in2.readLine();
                            } else {
                                ligne1 = in1.readLine();
                                S_duplicate++;
                            }
                        }
                    }

                    in1.close();
                    in2.close();
                    out.close();

                    TEMP_FILES.remove(1).delete();
                    TEMP_FILES.remove(0).delete();
                } catch (final IOException e) {
                    try {
                        in1.close();
                    } catch (final IOException e1) {
                        LOG.trace(e1.toString());
                    }
                    try {
                        in2.close();
                    } catch (final IOException e1) {
                        LOG.trace(e1.toString());
                    }
                    try {
                        out.close();
                    } catch (final IOException e1) {
                        LOG.trace(e1.toString());
                    }
                    error(e);
                }
            }
        }
    }

    /**
     * Recherche des parties terminées par un véritable mat.
     */
    private static void searchMatGames() {
        S_read = 0;
        S_invalid = 0;
        S_temporary = 0;

        FileReader ficIn = null;
        try {
            ficIn = new FileReader(TEMP_FILES.get(0));
        } catch (final FileNotFoundException e) {
            error(e.toString());
        }

        FileWriter ficOut = null;
        try {
            final File ficTmp = File.createTempFile("obs" + TEMP_FILES.size()
                    + "m", null);
            ficTmp.deleteOnExit();
            TEMP_FILES.add(ficTmp);
            ficOut = new FileWriter(ficTmp);
        } catch (final IOException e) {
            error(e.toString());
        }

        final BufferedReader in = new BufferedReader(ficIn);
        final BufferedWriter out = new BufferedWriter(ficOut);
        try {
            String ligne = in.readLine();
            while (ligne != null) {
                S_read++;
                if (S_read % 10000 == 0) {
                    LOG.info("  ... {} games read ({} kept) ...", S_read, S_temporary);
                }

                boolean valid = true;
                final String[] mvts = SPLITTER.split(ligne);
                MoveGenerator etat = BoardFactory.valueOf(FASTEST, STARTING);
                for (int i = 0; i < mvts.length - 1; i++) {
                    try {
                        etat = etat.derive(
                                toMove(etat, toNormalizedSAN(mvts[i])), true);
                    } catch (final SANException e) {
                        valid = false;
                        S_invalid++;
                        break;
                    }
                }
                if (valid) {
                    if (etat.getValidMoves(etat.isWhiteActive()).length == 0) {
                        out.write(ligne);
                        out.newLine();
                        S_temporary++;
                    } else {
                        S_notRealMat++;
                    }
                }
                ligne = in.readLine();
            }

            TEMP_FILES.remove(0).delete();
        } catch (final IOException e) {
            error(e.toString());
        } finally {
            try {
                in.close();
            } catch (final IOException e1) {
                LOG.trace(e1.toString());
            }
            try {
                out.close();
            } catch (final IOException e1) {
                LOG.trace(e1.toString());
            }
        }
    }

    /**
     * Crée le fichier des ouvertures au format EPD.
     *
     * @param pChemin Chemin du fichier PGN en entrée.
     */
    private static void writeOpenings(final String pChemin) {
        OutputStreamWriter ficOut = null;
        try {
            ficOut = new OutputStreamWriter(new FileOutputStream(pChemin
                    + ".epd"));
        } catch (final IOException e) {
            error(e.toString());
        }

        final BufferedWriter out = new BufferedWriter(ficOut);
        try {
            for (final Map.Entry<String, Map<String, Integer>> pos : POSITIONS
                    .entrySet()) {
                // Tri les coups possibles du meilleur au plus mauvais...
                final List<Map.Entry<String, Integer>> mvts = new ArrayList<>(
                        pos.getValue().entrySet());
                mvts.sort(BEST_MOVE_SORTER);
                // Ne conserve que les 5 premiers bons...
                final List<Map.Entry<String, Integer>> garde = new ArrayList<>();
                for (final Map.Entry<String, Integer> e : mvts) {
                    if (e.getValue() > 0) {
                        garde.add(e);
                        if (garde.size() >= 5) {
                            break;
                        }
                    }
                }
                // Tri les mouvements conservés par ordre alphanumérique
                // (contrainte du
                // format EPD).
                garde.sort(ALPHA_MOVE_SORTER);
                if (garde.size() > 0) {
                    S_openingsPositions++;
                    out.write(pos.getKey());
                    out.write(" bm");
                    for (final Map.Entry<String, Integer> mvt : garde) {
                        out.write(' ');
                        out.write(mvt.getKey());
                    }
                    out.write(';');
                    out.newLine();
                }
            }
            out.close();
        } catch (final IOException e) {
            try {
                out.close();
            } catch (final IOException e1) {
                LOG.trace(e1.toString());
            }
            error(e.toString());
        }
    }
}

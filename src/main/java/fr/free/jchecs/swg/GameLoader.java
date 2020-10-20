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
package fr.free.jchecs.swg;

import java.awt.Component;
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import fr.free.jchecs.core.Game;
import fr.free.jchecs.core.Move;
import fr.free.jchecs.core.PGNException;
import fr.free.jchecs.core.Player;

import static fr.free.jchecs.core.PGNUtils.toGame;
import static fr.free.jchecs.swg.ResourceUtils.getI18NString;
import static fr.free.jchecs.swg.ResourceUtils.getImageIcon;

/**
 * Classe prenant en charge le chargement d'une partie contenue dans un fichier
 * PGN.
 *
 * @author David Cotton
 */
final class GameLoader implements Runnable {
    /**
     * Grande icône, pour la boite de dialogue d'erreur.
     */
    private static final Icon ICON32 = getImageIcon("error32.png");

    /**
     * Référence de l'interface graphique liée.
     */
    private final UI _ui;

    /**
     * Instancie un nouveau chargeur.
     *
     * @param pUI Interface graphique mêre.
     */
    GameLoader(final UI pUI) {
        _ui = pUI;
    }

    /**
     * Affiche la boite de dialogue signalant l'impossibilité de lire le
     * fichier.
     */
    private void errorDialog() {
        JOptionPane.showMessageDialog(_ui.getMainFrame(),
                getI18NString("action.loadGame.error.message"),
                getI18NString("action.loadGame.error.title"),
                JOptionPane.ERROR_MESSAGE, ICON32);
    }

    /**
     * Réalise le chargement.
     */
    @Override
    public void run() {
        final JFileChooser dialog = new JFileChooser();
        dialog.setDialogTitle(getI18NString("action.loadGame.name"));
        dialog.addChoosableFileFilter(new FileNameExtensionFilter(
                getI18NString("action.loadGame.filter"), "pgn"));
        if (dialog.showOpenDialog(_ui.getMainFrame()) == JFileChooser.APPROVE_OPTION) {
            final File fichier = dialog.getSelectedFile();
            if (fichier != null) {
                final int ligneDebut = GameSelector.selectGame(
                        _ui.getMainFrame(), fichier);
                if (ligneDebut >= 0) {

                    BufferedReader in = null;
                    try {
                        in = new BufferedReader(new FileReader(fichier));
                    } catch (final FileNotFoundException e) {
                        errorDialog();
                    }

                    if (in != null) {
                        try {
                            // Se positionner au début de la définition de la
                            // partie
                            // sélectionnée...
                            for (int i = ligneDebut; --i > 0; /* Pré-décrémenté */) {
                                in.readLine();
                            }

                            try {
                                final Game tmp = toGame(in);
                                final Game partie = _ui.getGame();
                                final Player blancs = partie.getPlayer(true);
                                final Player noirs = partie.getPlayer(false);
                                blancs.setEngine(null);
                                noirs.setEngine(null);
                                tmp.goFirst();
                                partie.resetTo(tmp.getBoard());
                                tmp.goLast();
                                for (final Move mvt : tmp.getMovesToCurrent()) {
                                    partie.moveFromCurrent(mvt);
                                }
                                blancs.setName(tmp.getPlayer(true).getName());
                                blancs.setEngine(tmp.getPlayer(true)
                                        .getEngine());
                                noirs.setName(tmp.getPlayer(false).getName());
                                noirs.setEngine(tmp.getPlayer(false)
                                        .getEngine());
                            } catch (final PGNException e) {
                                errorDialog();
                            }

                            in.close();
                        } catch (final IOException e) {
                            errorDialog();
                            try {
                                in.close();
                            } catch (final IOException e1) {
                                // Tant pis, la fermeture de la JVM s'en
                                // chargera.
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Composant permettant de sélectionner une partie dans un fichier PGN.
     */
    private static final class GameSelector {
        /**
         * Classe utilitaire.
         */
        private GameSelector() {
            // Rien de spécifique...
        }

        /**
         * Affiche le composant et attend la sélection d'une partie.
         *
         * @param pParent  Composant parent de la boite de dialogue (peut être à
         *                 null).
         * @param pFichier Fichier PGN source.
         * @return Numéro de la ligne, dans le fichier, où commence la partie
         * sélectionnée (-1 si aucune).
         */
        static int selectGame(final Component pParent, final File pFichier) {
            int res = -1;

            final TableModel modele = new HeadersModel(pFichier);
            if (modele.getRowCount() > 0) {
                final JTable liste = new JTable(modele);
                liste.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                liste.setRowSorter(new TableRowSorter<>(modele));
                liste.setShowHorizontalLines(false);
                final TableColumnModel cols = liste.getColumnModel();
                cols.getColumn(0).setPreferredWidth(45);
                cols.getColumn(1).setPreferredWidth(140);
                cols.getColumn(2).setPreferredWidth(110);
                cols.getColumn(3).setPreferredWidth(110);
                cols.getColumn(4).setPreferredWidth(45);
                cols.getColumn(5).setPreferredWidth(90);
                liste.setDefaultRenderer(Object.class,
                        new EvenOddRowsRenderer());
                liste.setColumnSelectionAllowed(false);
                liste.setRowSelectionAllowed(true);
                liste.setRowSelectionInterval(0, 0);

                final Component fond = new JScrollPane(liste);
                fond.setPreferredSize(new Dimension(540, 175));
                if (JOptionPane
                        .showConfirmDialog(pParent, fond,
                                getI18NString("dialog.selectGame.title"),
                                JOptionPane.OK_CANCEL_OPTION,
                                JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION) {
                    res = Integer.parseInt((String) modele.getValueAt(
                            liste.getSelectedRow(), 0));
                }
            }

            return res;
        }

        /**
         * Modèle de données interne pour le tableau des en-têtes de parties.
         */
        private static final class HeadersModel extends DefaultTableModel {
            /**
             * Identifiant de la classe pour la sérialisation.
             */
            private static final long serialVersionUID = -4442746008369938816L;

            /**
             * Données des en-têtes.
             */
            private final String[][] _datas;

            /**
             * Instancie un nouveau modèle à partir d'un fichier PGN.
             *
             * @param pFichier Fichier PGN source.
             */
            HeadersModel(final File pFichier) {
                final List<String[]> parties = new ArrayList<>();

                BufferedReader in = null;
                try {
                    in = new BufferedReader(new FileReader(pFichier));
                } catch (final FileNotFoundException e) {
                    // Tant pis, on ne chargera rien...
                }

                if (in != null) {
                    int numLigne = 0;
                    String ligne;
                    String[] enreg = null;
                    try {
                        ligne = in.readLine();
                        while (ligne != null) {
                            numLigne++;
                            final String ligneEpuree = ligne.trim();
                            final int debTag = ligneEpuree.indexOf(" \"");
                            if ((debTag >= 0) && ligneEpuree.endsWith("\"]")) {
                                final String contenu = ligneEpuree.substring(
                                        debTag + 2, ligneEpuree.indexOf("\"]"));
                                if (enreg != null) {
                                    if (ligneEpuree.startsWith("[Black \"")) {
                                        enreg[3] = contenu;
                                    } else if (ligneEpuree
                                            .startsWith("[Date \"")) {
                                        enreg[5] = contenu;
                                    } else if (ligneEpuree
                                            .startsWith("[Result \"")) {
                                        enreg[4] = contenu;
                                    } else if (ligneEpuree
                                            .startsWith("[White \"")) {
                                        enreg[2] = contenu;
                                    }
                                }
                                if (ligneEpuree.startsWith("[Event \"")) {
                                    if (enreg != null) {
                                        parties.add(enreg);
                                    }
                                    enreg = new String[6];
                                    enreg[0] = Integer.toString(numLigne);
                                    enreg[1] = contenu;
                                }
                            }
                            ligne = in.readLine();
                        }
                        if (enreg != null) {
                            parties.add(enreg);
                        }
                        in.close();
                    } catch (final IOException e) {
                        // On peut quand même continuer avec ce qui a été lu
                        // jusqu'à
                        // l'erreur ...
                        try {
                            in.close();
                        } catch (final IOException e1) {
                            // Tant pis, la fermeture de la JVM s'en chargera.
                        }
                    }
                }

                _datas = parties.toArray(new String[parties.size()][]);
                setColumnCount(6);
                setRowCount(_datas.length);

                final String[] enTetes = {
                        getI18NString("dialog.selectGame.line"),
                        getI18NString("dialog.selectGame.event"),
                        getI18NString("dialog.selectGame.white"),
                        getI18NString("dialog.selectGame.black"),
                        getI18NString("dialog.selectGame.result"),
                        getI18NString("dialog.selectGame.date"),};
                setColumnIdentifiers(enTetes);
            }

            /**
             * Renvoi le contenu d'une cellule.
             *
             * @param pLigne   Indice de la ligne.
             * @param pColonne Indice de la colonne.
             * @return Contenu de la cellule correspondante.
             */
            @Override
            public Object getValueAt(final int pLigne, final int pColonne) {
                return _datas[pLigne][pColonne];
            }

            /**
             * Indique si une cellule est modifiable.
             *
             * @param pLigne   Indice de la ligne.
             * @param pColonne Indice de la colonne.
             * @return "Vrai" si la cellule est éditable (jamais dans ce cas).
             */
            @Override
            public boolean isCellEditable(final int pLigne, final int pColonne) {
                return false;
            }
        }
    }
}

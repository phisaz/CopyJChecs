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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import fr.free.jchecs.ai.Engine;
import fr.free.jchecs.ai.EngineFactory;
import fr.free.jchecs.core.Player;

import static fr.free.jchecs.swg.ResourceUtils.getI18NString;
import static fr.free.jchecs.swg.ResourceUtils.getImageIcon;

/**
 * Boite de dialogue permettant d'éditer les paramètres d'un joueur.
 *
 * @author David Cotton
 */
final class PlayerDialog {
    /**
     * Liste des définitions de types de joueurs.
     */
    private static final PlayerType[] PLAYER_TYPES;

    static {
        final List<PlayerType> liste = new ArrayList<>();
        liste.add(new PlayerType(getI18NString("label.player.human"), true));
        final String[] noms = EngineFactory.getNames();
        Arrays.sort(noms);
        for (final String n : noms) {
            liste.add(new PlayerType(n.substring(n.indexOf('.') + 1), false));
        }
        PLAYER_TYPES = liste.toArray(new PlayerType[liste.size()]);
    }

    /**
     * Classe utilitaire : ne pas instancier.
     */
    private PlayerDialog() {
        // Rien de spécifique...
    }

    /**
     * Affiche la boite de dialogue.
     *
     * @param pParent Composant parent de la boite de dialogue (peut être à null).
     * @param pJoueur Définition du joueur à éditer.
     */
    static void showAboutDialog(final Component pParent, final Player pJoueur) {
        final String icone;
        final String suffixCle;
        if (pJoueur.isWhite()) {
            suffixCle = ".white";
            icone = "white22.png";
        } else {
            suffixCle = ".black";
            icone = "black22.png";
        }

        final GridBagLayout gbl = new GridBagLayout();
        final GridBagConstraints gbc = new GridBagConstraints();
        final JPanel fond = new JPanel(gbl);

        final JLabel labType = new JLabel(getI18NString("dialog.players.type"));
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 8;
        gbl.setConstraints(labType, gbc);
        fond.add(labType);

        final JComboBox<PlayerType> comboTypes = new JComboBox<>(PLAYER_TYPES);
        comboTypes.setRenderer(new PlayerTypeRenderer());
        final String nom = pJoueur.getName();
        for (final PlayerType pt : PLAYER_TYPES) {
            if (pt.getName().equals(nom)) {
                comboTypes.setSelectedItem(pt);
                break;
            }
        }
        gbc.gridx = 1;
        gbl.setConstraints(comboTypes, gbc);
        fond.add(comboTypes);

        final JLabel labProf = new JLabel(getI18NString("dialog.players.depth"));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbl.setConstraints(labProf, gbc);
        fond.add(labProf);

        final JSlider depthSlider = new JSlider();
        depthSlider.setMajorTickSpacing(1);
        depthSlider.setPaintLabels(true);
        depthSlider.setSnapToTicks(true);
        final Engine moteur = pJoueur.getEngine();
        if (moteur == null) {
            depthSlider.setMinimum(0);
            depthSlider.setValue(0);
            depthSlider.setMaximum(0);
        } else {
            depthSlider.setMinimum(moteur.getMinimalSearchDepth());
            depthSlider.setMaximum(moteur.getMaximalSearchDepth());
            depthSlider.setValue(moteur.getSearchDepthLimit());
        }
        gbc.gridx = 1;
        gbl.setConstraints(depthSlider, gbc);
        fond.add(depthSlider);

        comboTypes.addItemListener(new ItemListener() {
            /**
             * Réagit à la sélection dans la combo des types.
             *
             * @param pEvent
             *            Evènement signalant la sélection.
             */
            @Override
            public void itemStateChanged(final ItemEvent pEvent) {
                if (pEvent.getStateChange() == ItemEvent.SELECTED) {
                    final Object item = pEvent.getItem();
                    if (item instanceof PlayerType) {
                        final PlayerType pt = (PlayerType) item;
                        final String n = pt.getName();
                        if (pt.isHuman()) {
                            pJoueur.setEngine(null);
                            depthSlider.setMinimum(0);
                            depthSlider.setValue(0);
                            depthSlider.setMaximum(0);
                        } else {
                            final Engine m = EngineFactory
                                    .newInstance("jChecs." + n);
                            depthSlider.setMinimum(m.getMinimalSearchDepth());
                            depthSlider.setMaximum(m.getMaximalSearchDepth());
                            depthSlider.setValue(m.getSearchDepthLimit());
                            pJoueur.setEngine(m);
                        }
                        pJoueur.setName(n);
                    } else {
                        assert false;
                    }
                }
            }
        });

        depthSlider.addChangeListener(new ChangeListener() {
            /**
             * Réagit aux changements de profondeur de recherche.
             *
             * @param pEvent
             *            Evènement signalant la modification.
             */
            @Override
            public void stateChanged(final ChangeEvent pEvent) {
                if (!depthSlider.getValueIsAdjusting()) {
                    final Engine m = pJoueur.getEngine();
                    if (m != null) {
                        m.setSearchDepthLimit(depthSlider.getValue());
                    }
                }
            }
        });

        final JOptionPane op = new JOptionPane(fond, JOptionPane.PLAIN_MESSAGE,
                JOptionPane.DEFAULT_OPTION, getImageIcon(icone));

        final JDialog dialog = op.createDialog(pParent,
                getI18NString("dialog.players.title" + suffixCle));
        dialog.pack();
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(pParent);
        dialog.setVisible(true);
    }

    /**
     * Description interne d'un type de joueur.
     */
    private static final class PlayerType {
        /**
         * Nom du type du joueur.
         */
        private final String _name;

        /**
         * Indicateur de type de joueur humain.
         */
        private final boolean _human;

        /**
         * Instancie une nouvelle desccription de type de joueur.
         *
         * @param pNom    Nom du type du joueur.
         * @param pHumain Indicateur à vrai si le type correspond à "Humain".
         */
        PlayerType(final String pNom, final boolean pHumain) {
            _name = pNom;
            _human = pHumain;
        }

        /**
         * Renvoi le nom du type de joueur.
         *
         * @return Nom du type de joueur.
         */
        String getName() {
            return _name;
        }

        /**
         * Indique si le typecorrespond à "Humain".
         *
         * @return Vrai si c'est un type "Humain".
         */
        boolean isHuman() {
            return _human;
        }
    }

    /**
     * Afficheur d'un type de joueur.
     */
    private static final class PlayerTypeRenderer extends
            DefaultListCellRenderer {
        /**
         * Icône des types "I.A.".
         */
        private static final Icon COMPUTER_ICON = getImageIcon("ai16.png");

        /**
         * Icône des types "Humain".
         */
        private static final Icon HUMAN_ICON = getImageIcon("human16.png");

        /**
         * Identifiant de la classe pour la sérialisation.
         */
        private static final long serialVersionUID = 3936682708234009054L;

        /**
         * Créé une nouvelle instance.
         */
        PlayerTypeRenderer() {
            // Rien de spécifique...
        }

        /**
         * Spécialisation de l'affichage des type de joueurs.
         *
         * @param pListe     Liste déroulante ciblée.
         * @param pObjet     Objet correspondant à l'élément de la liste à afficher.
         * @param pIndex     Indice de l'élément dans la liste.
         * @param pSelection A vrai si l'élément est sélectionné.
         * @param pFocus     A vrai si l'élément a le focus.
         * @return Composant graphique à afficher.
         */
        @Override
        public Component getListCellRendererComponent(final JList pListe,
                                                      final Object pObjet, final int pIndex,
                                                      final boolean pSelection, final boolean pFocus) {
            final Component res;
            if (pObjet instanceof PlayerType) {
                final PlayerType pt = (PlayerType) pObjet;
                final JLabel lab = (JLabel) super.getListCellRendererComponent(
                        pListe, pt.getName(), pIndex, pSelection, pFocus);
                if (pt.isHuman()) {
                    lab.setIcon(HUMAN_ICON);
                } else {
                    lab.setIcon(COMPUTER_ICON);
                }
                res = lab;
            } else {
                res = super.getListCellRendererComponent(pListe, pObjet,
                        pIndex, pSelection, pFocus);
            }

            return res;
        }
    }
}

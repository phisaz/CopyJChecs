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

import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

import fr.free.jchecs.core.Game;

/**
 * Composant graphique chargé d'afficher la feuille de la partie.
 *
 * @author David Cotton
 */
final class ScoreSheet {
    /**
     * Composant graphique externe.
     */
    private final JComponent _component;

    /**
     * Construit un nouveau composant d'affichage de la notation.
     *
     * @param pPartie Définition de partie liée au composant.
     */
    ScoreSheet(final Game pPartie) {
        final JEditorPane champ = new JEditorPane();
        champ.setContentType("text/html");
        champ.setEditable(false);
        final JScrollPane fond = new JScrollPane(champ,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        _component = fond;
        fond.getViewport().setPreferredSize(new Dimension(175, 250));
        pPartie.addPropertyChangeListener("position", new ScoreSheetListener(
                pPartie, champ));
    }

    /**
     * Renvoi le composant graphique affichable.
     *
     * @return Composant graphique.
     */
    JComponent getComponent() {
        return _component;
    }

    /**
     * Classe à l'écoute des évènements de la feuille de score.
     */
    private static final class ScoreSheetListener implements
            PropertyChangeListener {
        /**
         * Référence du champ affichant le score.
         */
        final JEditorPane _editorPane;

        /**
         * Référence de la partie liée à la feuille de score.
         */
        private final Game _game;

        /**
         * Instancie un nouvel écouteur.
         *
         * @param pPartie Partie liée.
         * @param pChamp  Champ affichant le score.
         */
        ScoreSheetListener(final Game pPartie, final JEditorPane pChamp) {
            _game = pPartie;
            _editorPane = pChamp;
        }

        /**
         * Réagit au changement dans la définition de la partie.
         *
         * @param pEvt Evènement signalant le changement.
         */
        @Override
        public void propertyChange(final PropertyChangeEvent pEvt) {
            final StringBuilder sb = new StringBuilder("<html>");
            final String[] sans = _game.getSANStrings();
            for (int i = 0; i < sans.length; i++) {
                final String debImp;
                final String finImp;
                if (i % 2 == 0) {
                    debImp = "<b>";
                    finImp = "</b>";
                } else {
                    debImp = "";
                    finImp = "";
                }
                final String debShow;
                final String finShow;
                if (i == _game.getCurrentMoveIndex()) {
                    debShow = "<font bgcolor=#E0E0FF><a name=\"show\">";
                    finShow = "</a></font>";
                } else {
                    debShow = "";
                    finShow = "";
                }
                sb.append(debImp).append(debShow);
                sb.append("&nbsp;").append(sans[i]);
                sb.append(finShow).append(finImp);
            }
            sb.append("</html>");
            SwingUtilities.invokeLater(new Runnable() {
                /**
                 * Lance la mise à jour dans le thread Swing, pour rester
                 * synchrone.
                 */
                @Override
                public void run() {
                    _editorPane.setText(sb.toString());
                    _editorPane.scrollToReference("show");
                }
            });
        }
    }
}

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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import static java.awt.Image.SCALE_SMOOTH;
import static java.awt.image.BufferedImage.TYPE_INT_RGB;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

import fr.free.jchecs.core.Game;
import fr.free.jchecs.core.Move;
import fr.free.jchecs.core.MoveGenerator;
import fr.free.jchecs.core.Piece;
import fr.free.jchecs.core.Square;

import static fr.free.jchecs.core.Constants.FILE_COUNT;
import static fr.free.jchecs.core.Constants.RANK_COUNT;
import static fr.free.jchecs.swg.ResourceUtils.getImageIcon;

/**
 * Composant graphique représentant un échiquier.
 *
 * @author David Cotton
 */
final class BoardUI extends JComponent {
    /**
     * Identifiant de la classe pour la sérialisation.
     */
    private static final long serialVersionUID = 6200021664655625524L;

    /**
     * Epaisseur par défaut de la bordure.
     */
    private static final int DEFAULT_BORDER_THICKNESS = 16;

    /**
     * Couleur de la case survolée par la souris, si activable.
     */
    private static final Color OVER_ENABLED_COLOR = new Color(0, 127, 0, 64);

    /**
     * Couleur de la case survolée par la souris, si non activable.
     */
    private static final Color OVER_DISABLED_COLOR = new Color(127, 0, 0, 64);

    /**
     * Couleur d'une case destination possible.
     */
    private static final Color AVAILABLE_COLOR = new Color(127, 127, 0, 64);

    /**
     * Couleur d'une case destination sélectionnée.
     */
    private static final Color SELECTED_COLOR = new Color(0, 255, 0, 64);

    /**
     * Couleur de mise en évidence d'un mouvement.
     */
    private static final Color HIGHLIGHTED_MOVE_COLOR = new Color(160, 160,
            216, 192);

    /**
     * Type de ligne pour la mise en évidence d'un mouvement.
     */
    private static final Stroke HIGHLIGHTED_MOVE_STROKE = new BasicStroke(4,
            BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

    /**
     * Liste des objets à l'écoute des mouvements déclenchés par le composant.
     */
    private final List<MoveListener> _moveListeners = new ArrayList<>(
            2);

    /**
     * Objet chargé du rendu des pièces.
     */
    private final PieceUI _pieceUI;

    /**
     * Buffer accueillant l'image composant le fond de l'échiquier.
     */
    private transient Image _boardBackground;

    /**
     * Longueur du côté d'une case.
     */
    private int _cellSideLength;

    /**
     * Epaisseur actuelle des bordures.
     */
    private int _borderThickness = DEFAULT_BORDER_THICKNESS;

    /**
     * Etat de la partie représentée par le composant.
     */
    private MoveGenerator _board;

    /**
     * Numéro de l'apparence du plateau.
     */
    private int _boardLF;

    /**
     * Numéro de l'apparence des pièces.
     */
    private int _piecesLF;

    /**
     * Case sur laquelle se trouve la souris (peut être à null).
     */
    private Square _overSquare;

    /**
     * Case sélectionnée (peut être à null).
     */
    private Square _selectedSquare;

    /**
     * Mouvement mis en évidence (peut être à null).
     */
    private Move _highLightedMove;

    /**
     * Drapeau indiquant si le dernier mouvement doit être mis en évidence.
     */
    private boolean _highlightLastMove = true;

    /**
     * Drapeau indiquant si les mouvements valides doivent être surlignés.
     */
    private boolean _highlightValids = true;

    /**
     * Drapeau indiquant si la vue doit être inversée.
     */
    private boolean _flipView;

    /**
     * Liste des cases cibles valides à partir de la case survolée ou
     * sélectionnée (peut être à null).
     */
    private Square[] _availableTargets;

    /**
     * Instancie un nouveau panneau d'affichage d'échiquier.
     *
     * @param pPartie Définition de partie liée au composant.
     * @param pRendu  Objet chargé du rendu des pièces.
     */
    BoardUI(final Game pPartie, final PieceUI pRendu) {
        _pieceUI = pRendu;
        setCellSideLength(64);

        setBoard(pPartie.getBoard());

        final MouseAdapter ma = new BoardMouseAdapter(this);
        addMouseListener(ma);
        addMouseMotionListener(ma);

        pPartie.addPropertyChangeListener("position",
                new PropertyChangeListener() {
                    /**
                     * Réagit au changement dans la définition de la partie.
                     *
                     * @param pEvt
                     *            Evènement signalant le changement.
                     */
                    @Override
                    public void propertyChange(final PropertyChangeEvent pEvt) {
                        setBoard(pPartie.getBoard());
                        setHighlightedMove(pPartie.getCurrentMove());
                    }
                });

        clearState();
    }

    /**
     * Ajoute un objet à l'écoute des mouvements déclenchés par le composant.
     *
     * @param pEcouteur Objet à l'écoute des mouvements.
     */
    void addMoveListener(final MoveListener pEcouteur) {
        if (!_moveListeners.contains(pEcouteur)) {
            _moveListeners.add(pEcouteur);
        }
    }

    /**
     * Remet à zéro les états interne du composant.
     */
    private void clearState() {
        _overSquare = null;
        _selectedSquare = null;
        _availableTargets = null;

        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        repaint();
    }

    /**
     * Emet un évènement signalant un mouvement.
     *
     * @param pMouvement Mouvement à signaler.
     */
    private void fireMoveEvent(final Move pMouvement) {
        final MoveEvent evt = new MoveEvent(this, pMouvement);
        for (final MoveListener l : _moveListeners) {
            l.moved(evt);
        }
    }

    /**
     * Renvoi l'image composant le fond de l'échiquier.
     *
     * @return Image représentant le fond de l'échiquier.
     */
    private Image getBoardBackground() {
        if (_boardBackground == null) {
            final String lf = "lf" + _boardLF + "/";
            final int dimCase = getCellSideLength();
            Image tmpImg = getImageIcon(lf + "bs.jpg").getImage();
            // getScaledInstance() est asynchrone : l'ImageIcon force une
            // synchronisation.
            final Image caseNoire = new ImageIcon(tmpImg.getScaledInstance(
                    dimCase, dimCase, SCALE_SMOOTH)).getImage();
            tmpImg = getImageIcon(lf + "ws.jpg").getImage();
            // getScaledInstance() est asynchrone : l'ImageIcon force une
            // synchronisation.
            final Image caseBlanche = new ImageIcon(tmpImg.getScaledInstance(
                    dimCase, dimCase, SCALE_SMOOTH)).getImage();
            final int cote = getSideLength();
            final BufferedImage fond = new BufferedImage(cote, cote,
                    TYPE_INT_RGB);
            final Graphics2D g2d = fond.createGraphics();
            if (_borderThickness > 0) {
                g2d.drawImage(caseNoire, _borderThickness - dimCase,
                        _borderThickness - dimCase, null);
                g2d.drawImage(caseNoire, cote - _borderThickness, cote
                        - _borderThickness, null);
                g2d.drawImage(caseBlanche, cote - _borderThickness,
                        _borderThickness - dimCase, null);
                g2d.drawImage(caseBlanche, _borderThickness - dimCase, cote
                        - _borderThickness, null);

                g2d.setColor(Color.WHITE);
                g2d.fillRect(_borderThickness, 0, cote - 2 * _borderThickness,
                        _borderThickness);
                g2d.fillRect(_borderThickness, cote - _borderThickness, cote
                        - 2 * _borderThickness, _borderThickness);
                g2d.fillRect(0, _borderThickness, _borderThickness, cote - 2
                        * _borderThickness);
                g2d.fillRect(cote - _borderThickness, _borderThickness,
                        _borderThickness, cote - 2 * _borderThickness);
            }

            boolean blanc = true;
            for (int y = _borderThickness; y < (cote - dimCase); y += dimCase) {
                for (int x = _borderThickness; x < (cote - dimCase); x += dimCase) {
                    if (blanc) {
                        g2d.drawImage(caseBlanche, x, y, null);
                    } else {
                        g2d.drawImage(caseNoire, x, y, null);
                    }
                    blanc = !blanc;
                }
                blanc = !blanc;
            }

            g2d.setColor(Color.BLACK);
            g2d.drawRect(0, 0, cote - 1, cote - 1);
            for (int i = _borderThickness; i < cote; i += dimCase) {
                g2d.drawLine(i, 0, i, cote);
                g2d.drawLine(0, i, cote, i);
            }

            if (_borderThickness > 0) {
                final FontMetrics fm = g2d.getFontMetrics();
                final int hFonte = fm.getHeight();
                final int dyCol = (_borderThickness - hFonte) / 2;
                for (int i = FILE_COUNT - 1; i >= 0; i--) {
                    final char cCol;
                    if (_flipView) {
                        cCol = (char) ('h' - i);
                    } else {
                        cCol = (char) ('a' + i);
                    }
                    final String col = Character.toString(cCol);
                    final int dxCol = _borderThickness
                            + (dimCase - fm.charWidth(cCol)) / 2;
                    g2d.drawString(col, i * dimCase + dxCol, _borderThickness
                            - dyCol - 3);
                    g2d.drawString(col, i * dimCase + dxCol, cote - dyCol - 4);
                }
                final int dyLig = _borderThickness + (dimCase - hFonte) / 2
                        + hFonte - 1;
                for (int i = RANK_COUNT - 1; i >= 0; i--) {
                    final char cLig;
                    if (_flipView) {
                        cLig = (char) ('1' + i);
                    } else {
                        cLig = (char) (('0' + RANK_COUNT) - i);
                    }
                    final String lig = Character.toString(cLig);
                    final int dxLig = (_borderThickness - fm.charWidth(cLig)) / 2;
                    g2d.drawString(lig, dxLig, i * dimCase + dyLig);
                    g2d.drawString(lig, cote - _borderThickness + dxLig, i
                            * dimCase + dyLig);
                }
            }
            g2d.dispose();

            _boardBackground = fond;
        }

        return _boardBackground;
    }

    /**
     * Renvoi la longueur d'un côté de case.
     *
     * @return Longueur du côté d'une case.
     */
    private int getCellSideLength() {
        return _cellSideLength;
    }

    /**
     * Renvoi la dimension préférée par le composant.
     *
     * @return Dimension par défaut.
     */
    @Override
    public Dimension getPreferredSize() {
        final int cote = getSideLength();
        return new Dimension(cote, cote);
    }

    /**
     * Renvoi la longueur d'un côté du composant.
     *
     * @return Longueur d'un côté du composant.
     */
    private int getSideLength() {
        return 2 * _borderThickness + RANK_COUNT * getCellSideLength() + 1;
    }

    /**
     * Indique si une case est dans les cibles possibles.
     *
     * @param pCase Case à tester.
     * @return Vrai si la case est une cible possible.
     */
    private boolean isAvailable(final Square pCase) {
        for (int i = _availableTargets.length - 1; i >= 0; i--) {
            if (_availableTargets[i] == pCase) {
                return true;
            }
        }

        return false;
    }

    /**
     * Réagit au clicks de la souris.
     *
     * @param pX Abscisse du point.
     * @param pY Ordonnée du point.
     */
    private void mouseClicked(final int pX, final int pY) {
        if (!isEnabled()) {
            return;
        }

        if ((_selectedSquare == null) && (_availableTargets != null)
                && (_availableTargets.length != 0)) {
            _selectedSquare = _overSquare;
            setCursor(_pieceUI.getCursor(_board.getPieceAt(_selectedSquare)));
            repaint();
        } else {
            if (_selectedSquare != null) {
                if ((_availableTargets != null) && (isAvailable(_overSquare))) {
                    final Piece pieceSrc = _board.getPieceAt(_selectedSquare);
                    Piece promotion = null;
                    final int rankDst = _overSquare.getRank();
                    if (((pieceSrc == Piece.WHITE_PAWN) && (rankDst == RANK_COUNT - 1))
                            || ((pieceSrc == Piece.BLACK_PAWN) && (rankDst == 0))) {
                        promotion = PromotionDialog.showPromotionDialog(this,
                                pieceSrc, _pieceUI);
                    }
                    final Move mvt = new Move(pieceSrc, _selectedSquare,
                            _overSquare, _board.getPieceAt(_overSquare),
                            promotion);
                    setBoard(_board.derive(mvt, true));
                    fireMoveEvent(mvt);
                }
                setCursor(Cursor.getDefaultCursor());
                _selectedSquare = null;
                _overSquare = null;
                mouseOver(pX, pY);
            }
        }
    }

    /**
     * Réagit au positionnement de la souris.
     *
     * @param pX Abscisse du point.
     * @param pY Ordonnée du point.
     */
    private void mouseOver(final int pX, final int pY) {
        if (!isEnabled()) {
            return;
        }

        Square cellule = null;
        if ((pX > _borderThickness) && (pY > _borderThickness)) {
            final int cote = getSideLength();
            if ((pX < cote - _borderThickness)
                    && (pY < cote - _borderThickness)) {
                final int dimCase = getCellSideLength();
                if (_flipView) {
                    cellule = Square.valueOf(FILE_COUNT
                            - (pX - _borderThickness - 1) / dimCase - 1, (pY
                            - _borderThickness - 1)
                            / dimCase);
                } else {
                    cellule = Square.valueOf((pX - _borderThickness - 1)
                            / dimCase, RANK_COUNT - (pY - _borderThickness - 1)
                            / dimCase - 1);
                }
            }
        }

        if (cellule != _overSquare) {
            _overSquare = cellule;
            if (_selectedSquare == null) {
                _availableTargets = null;
                if (_overSquare != null) {
                    final Piece p = _board.getPieceAt(_overSquare);
                    if ((p != null) && (p.isWhite() == _board.isWhiteActive())) {
                        _availableTargets = _board.getValidTargets(_overSquare);
                    }
                }
            }
            repaint();
        }
    }

    /**
     * Dessine le composant.
     *
     * @param pGraph Contexte graphique à utiliser.
     */
    @Override
    public void paint(final Graphics pGraph) {
        final Graphics2D g2d = (Graphics2D) pGraph;

        g2d.drawImage(getBoardBackground(), 0, 0, null);

        final int dimCase = getCellSideLength();
        if (_highlightLastMove && (_highLightedMove != null)) {
            g2d.setColor(HIGHLIGHTED_MOVE_COLOR);
            g2d.setStroke(HIGHLIGHTED_MOVE_STROKE);
            final Square src = _highLightedMove.getFrom();
            final Square dst = _highLightedMove.getTo();
            final int demieCase = dimCase / 2;
            if (_flipView) {
                g2d.drawLine(_borderThickness
                                + (FILE_COUNT - src.getFile() - 1) * dimCase
                                + demieCase, _borderThickness + src.getRank() * dimCase
                                + demieCase,
                        _borderThickness + (FILE_COUNT - dst.getFile() - 1)
                                * dimCase + demieCase,
                        _borderThickness + dst.getRank() * dimCase + demieCase);
            } else {
                g2d.drawLine(_borderThickness + src.getFile() * dimCase
                                + demieCase,
                        _borderThickness + (RANK_COUNT - src.getRank() - 1)
                                * dimCase + demieCase,
                        _borderThickness + dst.getFile() * dimCase + demieCase,
                        _borderThickness + (RANK_COUNT - dst.getRank() - 1)
                                * dimCase + demieCase);
            }
        }

        if (_overSquare != null) {
            if ((_selectedSquare == null) && (_availableTargets != null)
                    && (_availableTargets.length != 0)) {
                g2d.setColor(OVER_ENABLED_COLOR);
            } else if ((_selectedSquare != null) && (_availableTargets != null)
                    && (isAvailable(_overSquare))) {
                g2d.setColor(OVER_ENABLED_COLOR);
            } else {
                g2d.setColor(OVER_DISABLED_COLOR);
            }

            final int cX = _overSquare.getFile();
            final int cY = _overSquare.getRank();
            final int dx;
            final int dy;
            if (_flipView) {
                dx = _borderThickness + (FILE_COUNT - cX - 1) * dimCase + 1;
                dy = _borderThickness + cY * dimCase + 1;
            } else {
                dx = _borderThickness + cX * dimCase + 1;
                dy = _borderThickness + (RANK_COUNT - cY - 1) * dimCase + 1;
            }
            if (_borderThickness > 0) {
                final int cote = getSideLength();
                g2d.fillRect(1, dy, _borderThickness - 1, dimCase - 1);
                g2d.fillRect(cote - _borderThickness, dy, _borderThickness - 1,
                        dimCase - 1);
                g2d.fillRect(dx, 1, dimCase - 1, _borderThickness - 1);
                g2d.fillRect(dx, cote - _borderThickness, dimCase - 1,
                        _borderThickness - 1);
            }

            g2d.fillRect(dx, dy, dimCase - 1, dimCase - 1);
        }

        if (_selectedSquare != null) {
            g2d.setColor(SELECTED_COLOR);
            if (_flipView) {
                g2d.fillRect(
                        _borderThickness
                                + (FILE_COUNT - _selectedSquare.getFile() - 1)
                                * dimCase + 1, _borderThickness
                                + _selectedSquare.getRank() * dimCase + 1,
                        dimCase - 1, dimCase - 1);
            } else {
                g2d.fillRect(_borderThickness + _selectedSquare.getFile()
                        * dimCase + 1, _borderThickness
                        + (RANK_COUNT - _selectedSquare.getRank() - 1)
                        * dimCase + 1, dimCase - 1, dimCase - 1);
            }
        }

        for (final Square c : Square.values()) {
            if (_highlightValids && (_availableTargets != null)
                    && isAvailable(c)) {
                g2d.setColor(AVAILABLE_COLOR);
                if (_flipView) {
                    g2d.fillRect(_borderThickness
                                    + (FILE_COUNT - c.getFile() - 1) * dimCase + 1,
                            _borderThickness + c.getRank() * dimCase + 1,
                            dimCase - 1, dimCase - 1);
                } else {
                    g2d.fillRect(_borderThickness + c.getFile() * dimCase + 1,
                            _borderThickness + (RANK_COUNT - c.getRank() - 1)
                                    * dimCase + 1, dimCase - 1, dimCase - 1);
                }
            }
            final Piece p = _board.getPieceAt(c);
            if ((p != null) && (c != _selectedSquare)) {
                if (_flipView) {
                    g2d.drawImage(_pieceUI.getImage(p), _borderThickness
                                    + (FILE_COUNT - c.getFile() - 1) * dimCase,
                            _borderThickness + c.getRank() * dimCase, null);
                } else {
                    g2d.drawImage(_pieceUI.getImage(p),
                            _borderThickness + c.getFile() * dimCase,
                            _borderThickness + (RANK_COUNT - c.getRank() - 1)
                                    * dimCase, null);
                }
            }
        }
    }

    /**
     * Change l'état de la partie affiché par le composant.
     *
     * @param pEtat Etat de la partie à afficher.
     */
    private void setBoard(final MoveGenerator pEtat) {
        _board = pEtat;
    }

    /**
     * Alimente le numéro de l'apparence utilisée pour rendre le plateau.
     *
     * @param pNumero Numero de l'apparence (>= 0).
     */
    void setBoardLF(final int pNumero) {
        if (pNumero != _boardLF) {
            _boardLF = pNumero;
            _boardBackground = null;
            repaint();
        }
    }

    /**
     * Alimente la longueur d'un côté de case.
     *
     * @param pLongueur Longueur du côté d'une case (>= 1).
     */
    void setCellSideLength(final int pLongueur) {
        if (pLongueur != _cellSideLength) {
            _cellSideLength = pLongueur;
            _boardBackground = null;
            _pieceUI.initialize(pLongueur, "lf" + _piecesLF + "/");
        }
    }

    /**
     * Active / Désactive l'affichage des coordonnées.
     *
     * @param pAffiche A vrai si l'on veut afficher les coordonnées.
     */
    void setCoordinatesPainted(final boolean pAffiche) {
        if (pAffiche) {
            _borderThickness = DEFAULT_BORDER_THICKNESS;
        } else {
            _borderThickness = 0;
        }

        _boardBackground = null;
        repaint();
    }

    /**
     * Active/inactive le composant.
     *
     * @param pFlag Drapeau "activé".
     */
    @Override
    public void setEnabled(final boolean pFlag) {
        super.setEnabled(pFlag);

        clearState();
    }

    /**
     * Alimente le drapeau demandant l'inversion de la vue.
     *
     * @param pInverse A vrai si l'on souhaite inverser la vue.
     */
    void setFlipView(final boolean pInverse) {
        if (pInverse != _flipView) {
            _flipView = pInverse;
            _boardBackground = null;
            repaint();
        }
    }

    /**
     * Alimente le mouvement à mettre en évidence.
     *
     * @param pMouvement Mouvement à mettre en évidence (peut être à null)
     */
    private void setHighlightedMove(final Move pMouvement) {
        _highLightedMove = pMouvement;

        clearState();
    }

    /**
     * Indique si l'on souhaite mettre en évidence le dernier mouvement.
     *
     * @param pMontre A vrai pour indiquer le dernier mouvement.
     */
    void setHighlightLastMove(final boolean pMontre) {
        _highlightLastMove = pMontre;

        repaint();
    }

    /**
     * Indique si l'on souhaite surligner les mouvements valides.
     *
     * @param pSurligne A vrai pour surligner les mouvements valides.
     */
    void setHighlightValids(final boolean pSurligne) {
        _highlightValids = pSurligne;
    }

    /**
     * Alimente le numéro de l'apparence utilisée pour rendre les pièces.
     *
     * @param pNumero Numero de l'apparence (>= 0).
     */
    void setPiecesLF(final int pNumero) {
        if (pNumero != _piecesLF) {
            _piecesLF = pNumero;
            _pieceUI.initialize(_cellSideLength, "lf" + _piecesLF + "/");
            repaint();
        }
    }

    /**
     * Gestionnaire de souris lié au composant.
     */
    private static final class BoardMouseAdapter extends MouseAdapter {
        /**
         * Pointeur vers le composant graphique de l'échiquier.
         */
        private final BoardUI _boardUI;

        /**
         * Drapeau signalant que la souris est sur le composant.
         */
        private boolean _mouseOver;

        /**
         * Instancie le gestionnaire.
         *
         * @param pEchiquier Echiquier.
         */
        BoardMouseAdapter(final BoardUI pEchiquier) {
            _boardUI = pEchiquier;
        }

        /**
         * Tient compte de l'entrée de la souris sur le composant.
         *
         * @param pEvent Evènement signalant l'arrivée de la souris.
         */
        @Override
        public void mouseDragged(final MouseEvent pEvent) {
            if (_mouseOver) {
                _boardUI.mouseOver(pEvent.getX(), pEvent.getY());
            }
        }

        /**
         * Tient compte de l'entrée de la souris sur le composant.
         *
         * @param pEvent Evènement signalant l'arrivée de la souris.
         */
        @Override
        public void mouseEntered(final MouseEvent pEvent) {
            _mouseOver = true;
            _boardUI.mouseOver(pEvent.getX(), pEvent.getY());
        }

        /**
         * Tient compte de la sortie de la souris du composant.
         *
         * @param pEvent Evènement signalant la sortie de la souris.
         */
        @Override
        public void mouseExited(final MouseEvent pEvent) {
            _mouseOver = false;
            _boardUI.mouseOver(-1, -1);
        }

        /**
         * Tient compte des mouvements de la souris sur le composant.
         *
         * @param pEvent Evènement signalant un mouvement de la souris.
         */
        @Override
        public void mouseMoved(final MouseEvent pEvent) {
            _boardUI.mouseOver(pEvent.getX(), pEvent.getY());
        }

        /**
         * Tient compte de l'appui sur un bouton de la souris.
         *
         * @param pEvent Evènement signalant le click.
         */
        @Override
        public void mousePressed(final MouseEvent pEvent) {
            if (_mouseOver) {
                _boardUI.mouseClicked(pEvent.getX(), pEvent.getY());
            }
        }
    }
}

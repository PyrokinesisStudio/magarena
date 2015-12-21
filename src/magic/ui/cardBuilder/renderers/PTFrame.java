package magic.ui.cardBuilder.renderers;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import magic.model.MagicAbility;
import magic.model.MagicColor;
import magic.model.MagicType;
import magic.ui.cardBuilder.IRenderableCard;
import magic.ui.cardBuilder.ResourceManager;

public class PTFrame {

    private static final Font cardPTFont = ResourceManager.getFont("Beleren-Bold.ttf").deriveFont(Font.PLAIN, 19);
    private static final Font cardPTFontSmall = ResourceManager.getFont("Beleren-Bold.ttf").deriveFont(Font.PLAIN, 15);//scale down when triple figures
    private static final Font cardLoyaltyFont = ResourceManager.getFont("Beleren-Bold.ttf").deriveFont(Font.PLAIN, 16);

    //draw ptPanel - The only layering requirement besides frame. Ability text must wrap around ptpanel intrusion if any.
    static void drawPTPanel(BufferedImage cardImage, IRenderableCard cardDef) {
        String ptText = getPTText(cardDef);

        if (!ptText.isEmpty()) {
            BufferedImage ptImage = getPTPanelImage(cardDef);
            Graphics2D g2d = cardImage.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
            g2d.setColor(Color.BLACK);
            FontRenderContext frc2 = g2d.getFontRenderContext();

            g2d.drawImage(ptImage, 273, 466, null);

            //draw ptText
            Rectangle2D box = new Rectangle(286, 469, 60, 28); //ptText dimensions (Can't use ptPanel due to shadow distorting size)
            Point centre = new Point((int) box.getCenterX(), (int) box.getCenterY()); //Centre of box


            TextLayout layout;
            if (ptText.length() >= 6) { //power or toughness of 100+
                layout = new TextLayout(ptText, cardPTFontSmall, frc2);
            } else {
                layout = new TextLayout(ptText, cardPTFont, frc2);
            }
            Point textCentre = new Point((int) layout.getBounds().getWidth() / 2, (int) layout.getBounds().getHeight() / 2); //Centre of text

            layout.draw(g2d, (float) centre.getX() - (float) textCentre.getX(), (float) centre.getY() + (float) textCentre.getY());

            g2d.dispose();
        }
    }

    static void drawLoyaltyPanels(BufferedImage cardImage, IRenderableCard cardDef) {
        int xPos = 32;
        int width = 12;
        int height = 34;
        String loyaltyText = getLoyaltyText(cardDef);
        Graphics2D g2d = cardImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2d.setColor(Color.WHITE);
        String panelText;

        // Draw main Loyalty Panel
        if (!loyaltyText.isEmpty()) {
            BufferedImage loyaltyImage = ResourceManager.loyaltyPanel;
            g2d.drawImage(loyaltyImage, 302, 460, null);
            drawLoyaltyPanelText(g2d, new Rectangle(326, 462, width, height), loyaltyText);
        }
        // Draw activation panels
        if (OracleText.getPlaneswalkerAbilityCount(cardDef) == 3) {
            String[] activations = getPlaneswalkerActivationCosts(cardDef);
            panelText = getLoyaltyPanelText(activations[0]);
            if (panelText != "") {
                //Panel 1
                g2d.drawImage(getLoyaltyPanel(activations[0]), 18, 333, null);
                drawLoyaltyPanelText(g2d, new Rectangle(xPos, 335, width, height), panelText);
            }
            //Panel 2
            g2d.drawImage(getLoyaltyPanel(activations[1]), 18, 383, null);
            drawLoyaltyPanelText(g2d, new Rectangle(xPos, 386, width, height), getLoyaltyPanelText(activations[1]));
            //Panel 3
            g2d.drawImage(getLoyaltyPanel(activations[2]), 18, 432, null);
            drawLoyaltyPanelText(g2d, new Rectangle(xPos, 435, width, height), getLoyaltyPanelText(activations[2]));
        }
        g2d.dispose();
    }

    private static void drawLoyaltyPanelText(Graphics2D g2d, Rectangle box, String text) {
        TextLayout layout = new TextLayout(text, cardLoyaltyFont, g2d.getFontRenderContext());
        layout.draw(g2d, (float) box.getCenterX() - (float) layout.getBounds().getCenterX(), (float) box.getCenterY() - (float) layout.getBounds().getCenterY());
    }

    private static BufferedImage getLoyaltyPanel(String activation) {
        if (activation.startsWith("+")) {
            return ResourceManager.loyaltyUp;
        } else if (activation.startsWith("0")) {
            return ResourceManager.loyaltyEven;
        } else
            return ResourceManager.loyaltyDown;
    }

    private static String getLoyaltyPanelText(String string) {
        String text = string;
        if (text != "") {
            if (text.endsWith(":")) {
                text = string.substring(0, text.length() - 1);
            }
            if (text.startsWith("+")) {
                return text;
            }
            if (text.startsWith("0")) {
                return text.substring(0, 1);
            }
            return '-' + text.substring(1);
        }
        return text;
    }

    private static BufferedImage getPTPanel(MagicColor color) {
        switch (color) {
            case White:
                return ResourceManager.whitePTPanel;
            case Blue:
                return ResourceManager.bluePTPanel;
            case Black:
                return ResourceManager.blackPTPanel;
            case Red:
                return ResourceManager.redPTPanel;
            case Green:
                return ResourceManager.greenPTPanel;
            default:
                return ResourceManager.colorlessPTPanel;
        }
    }

    private static BufferedImage getPTPanelImage(IRenderableCard cardDef) {
        if (cardDef.hasAbility(MagicAbility.Devoid)) {
            return ResourceManager.colorlessPTPanel;
        }
        if (cardDef.isMulti()) {
            if (cardDef.isHybrid() || cardDef.isToken() && cardDef.getNumColors() == 2) {
                return ResourceManager.colorlessPTPanel; //Hybrid cards use colorless PT panel and banners
            } else {
                return ResourceManager.multiPTPanel;
            }
        }
        for (MagicColor color : MagicColor.values()) {
            if (cardDef.hasColor(color)) {
                return getPTPanel(color);
            }
        }
        if (cardDef.hasType(MagicType.Artifact)) {
            return ResourceManager.artifactPTPanel;
        }
        return ResourceManager.colorlessPTPanel;
    }

    private static String getPTText(IRenderableCard cardDef) {
        if (cardDef.hasType(MagicType.Creature)) {
            try {
                //For game in progress - when available
                return cardDef.getPowerToughnessText();
            } catch (NullPointerException e) {
                //Get default attributes if not in-game
                return cardDef.getPowerToughnessText();
            }
        } else {
            return "";
        }
    }

    private static String getLoyaltyText(IRenderableCard cardDef) {
        if (cardDef.hasType(MagicType.Planeswalker)) {
            try {
                //For game in progress - when available
                return Integer.toString(cardDef.getStartingLoyalty());
            } catch (NullPointerException e) {
                //Get default attributes if not in-game
                return Integer.toString(cardDef.getStartingLoyalty());
            }
        } else {
            return "";
        }
    }

    static String[] getPlaneswalkerActivationCosts(IRenderableCard cardDef) {
        String[] abilities = OracleText.getOracleAsLines(cardDef);
        String[] costs = new String[abilities.length];
        for (int i = 0; i < abilities.length; i++) {
            String ability = abilities[i].substring(0, 3);
            if (ability.matches(".*\\d+.*") || ability.contains("X")) {
                costs[i] = ability.trim();
            } else {
                costs[i] = "";
            }
        }
        return costs;
    }
}

package nl.weeaboo.vn.buildgui;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.annotation.Nullable;
import javax.swing.JPanel;

@SuppressWarnings("serial")
class ImageBackgroundPanel extends JPanel {

    private @Nullable BufferedImage image;

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int width = getWidth();
        int height = getHeight();
        if (image != null) {
            int iw = image.getWidth();
            int ih = image.getHeight();
            double scale = Math.max(width / (double)iw, height / (double)ih);
            iw = (int)Math.ceil(scale * iw);
            ih = (int)Math.ceil(scale * ih);

            g.drawImage(image, (width - iw) / 2, (height - ih) / 2, iw, ih, this);
        }
    }

    public void setImage(@Nullable BufferedImage image) {
        if (this.image != image) {
            this.image = image;
            repaint();
        }
    }

}

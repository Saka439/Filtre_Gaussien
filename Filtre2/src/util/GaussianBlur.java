package util;

import java.awt.image.BufferedImage;

public class GaussianBlur {

    private static final int[][] GAUSSIAN_KERNEL = {
        {1, 4, 7, 4, 1},
        {4, 16, 26, 16, 4},
        {7, 26, 41, 26, 7},
        {4, 16, 26, 16, 4},
        {1, 4, 7, 4, 1}
    };
    
    private static final int KERNEL_SUM = 273;

    public static BufferedImage applyGaussianBlur(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage blurredImage = new BufferedImage(width, height, image.getType());

        for (int y = 2; y < height - 2; y++) {
            for (int x = 2; x < width - 2; x++) {
                int r = 0, g = 0, b = 0;
                
                for (int ky = -2; ky <= 2; ky++) {
                    for (int kx = -2; kx <= 2; kx++) {
                        int pixel = image.getRGB(x + kx, y + ky);
                        int kernelValue = GAUSSIAN_KERNEL[ky + 2][kx + 2];
                        
                        r += ((pixel >> 16) & 0xff) * kernelValue;
                        g += ((pixel >> 8) & 0xff) * kernelValue;
                        b += (pixel & 0xff) * kernelValue;
                    }
                }
                
                r /= KERNEL_SUM;
                g /= KERNEL_SUM;
                b /= KERNEL_SUM;
                
                int newPixel = (0xff << 24) | (r << 16) | (g << 8) | b;
                blurredImage.setRGB(x, y, newPixel);
            }
        }

        return blurredImage;
    }
}

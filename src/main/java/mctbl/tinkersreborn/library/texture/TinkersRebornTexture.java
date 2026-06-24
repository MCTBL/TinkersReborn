package mctbl.tinkersreborn.library.texture;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import mctbl.tinkersreborn.TinkersReborn;
import mctbl.tinkersreborn.util.ColorUtil;

// TODO deldte?
public class TinkersRebornTexture extends TextureAtlasSprite {

    public static TextureAtlasSprite loadManually(ResourceLocation sprite) {
        return new TinkersRebornTexture(sprite.toString());
    }

    protected TinkersRebornTexture(String spriteName) {
        super(spriteName);
    }

    private ResourceLocation backupTextureLocation;

    protected TinkersRebornTexture(ResourceLocation baseTextureLocation, String spriteName) {
        this(spriteName);

        this.backupTextureLocation = baseTextureLocation;
    }

    public Collection<ResourceLocation> getDependencies() {
        return ImmutableList.of(backupTextureLocation);
    }

    @Override
    public boolean hasCustomLoader(IResourceManager manager, ResourceLocation location) {
        return true;
    }

    public boolean load(IResourceManager manager, ResourceLocation location,
        Function<ResourceLocation, TextureAtlasSprite> textureGetter) {
        this.framesTextureData = Lists.newArrayList();
        this.frameCounter = 0;
        this.tickCounter = 0;

        TextureAtlasSprite baseTexture = textureGetter.apply(backupTextureLocation);
        if (baseTexture == null || baseTexture.getFrameCount() <= 0) {
            this.width = 1; // needed so we don't crash
            this.height = 1;
            // failure
            return false;
        }

        // copy data from base texture - we have the same properties/sizes as the base

        this.copyFrom(baseTexture);
        // todo: do this for every frame for animated textures and remove the old
        // animation classes
        // get the base texture to work on - aka copy the texture data into this texture
        int[][] data;
        int[][] original = baseTexture.getFrameTextureData(0);
        data = new int[original.length][];
        data[0] = Arrays.copyOf(original[0], original[0].length);

        // do the transformation on the data for mipmap level 0
        // looks like other mipmaps are generated correctly
        processData(data[0]);

        if (this.framesTextureData.isEmpty()) {
            this.framesTextureData.add(data);
        }

        return false;
    }

    protected void processData(int[] data) {
        try {
            preProcess(data);
            // go over the base texture and color it
            for (int pxCoord = 0; pxCoord < data.length; pxCoord++) {
                data[pxCoord] = colorPixel(data[pxCoord], pxCoord);
            }
            postProcess(data);
        } catch (Exception e) {
            TinkersReborn.LOG.error("Error occured while processing: " + this.getIconName(), e);
        }
    }

    /** called before the first colorPixel */
    protected void preProcess(int[] data) {}

    /** called after the last colorPixel */
    protected void postProcess(int[] data) {}

    /**
     * need rewrite in child class
     * 
     * @param pixel
     * @param pxCoord
     * @return
     */
    protected int colorPixel(int pixel, int pxCoord) {
        return -1;
    };

    // borrowed from Shadows of Physis
    // Thanks TTFTCUTS! :)
    public static int getPerceptualBrightness(int col) {
        double r = ColorUtil.red(col) / 255.0;
        double g = ColorUtil.green(col) / 255.0;
        double b = ColorUtil.blue(col) / 255.0;

        return getPerceptualBrightness(r, g, b);
    }

    public static int getPerceptualBrightness(double r, double g, double b) {

        double brightness = Math.sqrt(0.241 * r * r + 0.691 * g * g + 0.068 * b * b);

        return (int) (brightness * 255);
    }

    protected static int mult(int c1, int c2) {
        return (int) (c1 * (c2 / 255f));
    }

    // Get coordinates from index and vice versa
    protected int getX(int pxCoord) {
        return pxCoord % width;
    }

    protected int getY(int pxCoord) {
        return pxCoord / width;
    }

    protected int coord(int x, int y) {
        return y * width + x;
    }

}

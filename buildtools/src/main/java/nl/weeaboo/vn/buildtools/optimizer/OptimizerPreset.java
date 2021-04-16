package nl.weeaboo.vn.buildtools.optimizer;

/**
 * Global quality profile for various optimizers.
 */
public enum OptimizerPreset {

    /** Only perform lossless optimizations */
    LOSSLESS,

    /** Allow lossy optimizations, optimize for quality over file size */
    QUALITY;

}
